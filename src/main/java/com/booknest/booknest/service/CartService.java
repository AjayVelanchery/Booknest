package com.booknest.booknest.service;

import com.booknest.booknest.dto.Cart;
import com.booknest.booknest.dto.CartItem;
import com.booknest.booknest.entity.Book;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "CART";

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addItem(HttpSession session, Book book, int quantity) {
        Cart cart = getCart(session);
        CartItem item = new CartItem(
                book.getId(),
                book.getTitle(),
                book.getPrice(),
                quantity
        );
        cart.addItem(item);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeItem(HttpSession session, Long bookId) {
        Cart cart = getCart(session);
        cart.removeItem(bookId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateItemQuantity(HttpSession session, Long bookId, int quantity) {
        Cart cart = getCart(session);
        cart.updateQuantity(bookId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.getItems().clear();
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
