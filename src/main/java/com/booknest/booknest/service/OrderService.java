package com.booknest.booknest.service;

import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.dto.CartItem;
import com.booknest.booknest.entity.Book;
import com.booknest.booknest.entity.Order;
import com.booknest.booknest.entity.OrderItem;
import com.booknest.booknest.entity.OrderStatus;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    private static final String DEFAULT_CURRENCY = "USD";

    @Transactional
    public Order placeOrder(HttpSession session) {
        Cart cart = cartService.getCart(session);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot place order.");
        }

        Order order = new Order();
        order.setCurrency(DEFAULT_CURRENCY);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems().values()) {
            Optional<Book> optionalBook = bookRepository.findById(cartItem.getBookId());
            if (optionalBook.isEmpty()) {
                throw new IllegalStateException("Book with ID " + cartItem.getBookId() + " not found.");
            }

            Book book = optionalBook.get();

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

        return savedOrder;
    }
}
