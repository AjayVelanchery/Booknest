package com.booknest.booknest.service;

import com.booknest.booknest.dto.WishlistResponse;
import jakarta.servlet.http.HttpSession;

public interface WishlistService {
    WishlistResponse getMyWishlist(String principalId);
    void addBookToWishlist(String principalId, Long bookId);
    void removeBookFromWishlist(String principalId, Long bookId);
    void moveWishlistToCart(String principalId, HttpSession session);
}
