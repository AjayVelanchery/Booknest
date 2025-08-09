package com.booknest.booknest.service;

import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.dto.CartItem;
import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.entity.*;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.repository.OrderRepository;
import com.booknest.booknest.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Value("${stripe.api.key.secret}")
    private String stripeSecretKey;

    private static final String DEFAULT_CURRENCY = "USD";

    @Transactional
    public OrderResponse placeOrder(HttpSession session, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot place order.");
        }

        Order order = new Order();
        order.setCurrency(DEFAULT_CURRENCY);
        order.setUser(currentUser);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems().values()) {
            Book book = bookRepository.findById(cartItem.getBookId())
                    .orElseThrow(() -> new IllegalStateException("Book with ID " + cartItem.getBookId() + " not found."));

            if (book.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for book: " + book.getTitle());
            }

            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(cartItem.getBookId());
            orderItem.setBookTitle(cartItem.getBookTitle());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(cartItem.getTotalPrice());
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(session);


        Stripe.apiKey = stripeSecretKey;

        String paymentUrl = null;
        try {
            long amountCents = totalAmount.movePointRight(2).longValueExact();
            long minimumAmount = 50L;
            if (amountCents < minimumAmount) {
                throw new IllegalStateException("Total amount must be at least $0.50 USD.");
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://yourdomain.com/payment/success?session_id={CHECKOUT_SESSION_ID}") // CHANGE THIS
                    .setCancelUrl("https://yourdomain.com/payment/cancel") // CHANGE THIS
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(DEFAULT_CURRENCY.toLowerCase())
                                                    .setUnitAmount(amountCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Order #" + savedOrder.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("order_id", savedOrder.getId().toString())
                    .build();

            Session checkoutSession = Session.create(params);
            paymentUrl = checkoutSession.getUrl();

        } catch (StripeException e) {
            throw new IllegalStateException("Stripe payment gateway error: " + e.getMessage(), e);
        }
        // ---------------------------------------

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),  // Should be PENDING
                savedOrder.getTotalAmount(),
                savedOrder.getCurrency(),
                savedOrder.getCreatedAt(),
                "Order placed successfully!",
                paymentUrl     // <----- Add this field to your OrderResponse DTO!
        );
    }
}
