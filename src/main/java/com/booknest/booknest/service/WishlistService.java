package com.booknest.booknest.service;

import com.booknest.booknest.dto.WishlistResponse;
import jakarta.servlet.http.HttpSession;

public interface WishlistService {


    WishlistResponse getMyWishlist(String principalId);


    WishlistResponse addBookToWishlist(String principalId, Long bookId);


    WishlistResponse removeBookFromWishlist(String principalId, Long bookId);


    WishlistResponse moveWishlistToCart(String principalId, HttpSession session);
}
