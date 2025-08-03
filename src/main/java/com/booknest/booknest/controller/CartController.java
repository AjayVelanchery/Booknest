package com.booknest.booknest.controller;

import com.booknest.booknest.dto.AddToCartRequest;
import com.booknest.booknest.dto.UpdateCartRequest;
import com.booknest.booknest.entity.Book;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final BookRepository bookRepository;


    @GetMapping
    public ResponseEntity<Cart> getCart(HttpSession session) {
        Cart cart = cartService.getCart(session);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(HttpSession session, @RequestBody @Valid AddToCartRequest request) {
        Book book = bookRepository.findById(request.getBookId()).orElse(null);
        if (book == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Book not found"));
        }
        cartService.addItem(session, book, request.getQuantity());
        return ResponseEntity.ok(Collections.singletonMap("message", "Item added to cart"));
    }

    @PutMapping("/items/{bookId}")
    public ResponseEntity<?> updateCartItem(
            HttpSession session,
            @PathVariable Long bookId,
            @RequestBody @Valid UpdateCartRequest request) {
        if (!bookId.equals(request.getBookId())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Book ID in path and body must match"));
        }
        cartService.updateItemQuantity(session, bookId, request.getQuantity());
        return ResponseEntity.ok(Collections.singletonMap("message", "Cart updated"));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<?> removeFromCart(HttpSession session, @PathVariable Long bookId) {
        cartService.removeItem(session, bookId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok(Collections.singletonMap("message", "Cart cleared"));
    }
}
