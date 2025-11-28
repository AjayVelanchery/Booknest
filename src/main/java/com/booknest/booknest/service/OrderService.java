package com.booknest.booknest.service;

import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.dto.CartItem;
import com.booknest.booknest.dto.OrderResponse;
import com.booknest.booknest.entity.OrderStatus;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.repository.OrderRepository;
import com.booknest.booknest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private static final String DEFAULT_CURRENCY = "INR";

    @Transactional
    public OrderResponse placeOrder(HttpSession session, String username) {

        var currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot place order.");
        }

        com.booknest.booknest.entity.Order order = new com.booknest.booknest.entity.Order();
        order.setCurrency(DEFAULT_CURRENCY);
        order.setUser(currentUser);

        List<com.booknest.booknest.entity.OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems().values()) {
            var book = bookRepository.findById(cartItem.getBookId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Book with ID " + cartItem.getBookId() + " not found."
                    ));

            if (book.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Not enough stock for the book: " + book.getTitle());
            }

            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);

            var orderItem = new com.booknest.booknest.entity.OrderItem();
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
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setOrderItems(orderItems);

        com.booknest.booknest.entity.Order savedOrder = orderRepository.save(order);
        cartService.clearCart(session);


        try {
            long amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).longValueExact();

            if (amountInPaise < 100) {
                throw new IllegalStateException("Order amount should be at least ₹1 (100 paise)");
            }


            JSONObject payload = new JSONObject();
            payload.put("amount", amountInPaise);
            payload.put("currency", DEFAULT_CURRENCY);
            payload.put("receipt", "order_rcptid_" + savedOrder.getId());
            payload.put("payment_capture", 1);

            String orderResponseJson = createRazorpayOrder(payload.toString()); // Direct API call

            JSONObject orderJson = new JSONObject(orderResponseJson);
            String razorpayOrderId = orderJson.optString("id");

            return new OrderResponse(
                    savedOrder.getId(),
                    savedOrder.getStatus(),
                    savedOrder.getTotalAmount(),
                    savedOrder.getCurrency(),
                    savedOrder.getCreatedAt(),
                    "Order placed successfully!",
                    razorpayOrderId
            );

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }


    private String createRazorpayOrder(String jsonPayload) throws IOException {
        URL url = new URL("https://api.razorpay.com/v1/orders");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String auth = razorpayKeyId + ":" + razorpayKeySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        int status = conn.getResponseCode();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        status < 400 ?
                                conn.getInputStream()
                                : conn.getErrorStream(),
                        "utf-8")))
        {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }
        conn.disconnect();
        return response.toString();
    }
}
