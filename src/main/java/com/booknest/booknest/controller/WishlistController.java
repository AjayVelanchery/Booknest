package com.booknest.booknest.controller;

import com.booknest.booknest.dto.WishlistResponse;
import com.booknest.booknest.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<WishlistResponse> getMyWishlist(Authentication authentication) {
        WishlistResponse response = wishlistService.getMyWishlist(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<WishlistResponse> addBookToWishlist(@PathVariable Long bookId,
                                                              Authentication authentication) {
        WishlistResponse response = wishlistService.addBookToWishlist(authentication.getName(), bookId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<WishlistResponse> removeBookFromWishlist(@PathVariable Long bookId,
                                                                   Authentication authentication) {
        WishlistResponse response = wishlistService.removeBookFromWishlist(authentication.getName(), bookId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/order")
    public ResponseEntity<WishlistResponse> orderFromWishlist(Authentication authentication,
                                                              HttpSession session) {
        WishlistResponse response = wishlistService.moveWishlistToCart(authentication.getName(), session);
        return ResponseEntity.ok(response);
    }
}
