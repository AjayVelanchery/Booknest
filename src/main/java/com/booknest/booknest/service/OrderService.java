package com.booknest.booknest.service;

import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.dto.CartItem;
import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.entity.*;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.repository.OrderRepository;
import com.booknest.booknest.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;       // Inject UserRepository here
    private final CartService cartService;

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
        order.setStatus(OrderStatus.PAID);
        order.setOrderItems(orderItems);


        Order savedOrder = orderRepository.save(order);


        cartService.clearCart(session);


        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getCurrency(),
                savedOrder.getCreatedAt(),
                "Order placed successfully!"
        );
    }
}
