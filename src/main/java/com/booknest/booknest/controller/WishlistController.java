package com.booknest.booknest.controller;

import com.booknest.booknest.dto.ApiResponse;
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
        return ResponseEntity.ok(wishlistService.getMyWishlist(authentication.getName()));
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse> addBookToWishlist(@PathVariable Long bookId,
                                                         Authentication authentication) {
        wishlistService.addBookToWishlist(authentication.getName(), bookId);
        return ResponseEntity.ok(new ApiResponse(true,"Book added to wishlist"));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse> removeBookFromWishlist(@PathVariable Long bookId,
                                                              Authentication authentication) {
        wishlistService.removeBookFromWishlist(authentication.getName(), bookId);
        return ResponseEntity.ok(new ApiResponse(true,"Book removed from wishlist"));
    }

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> orderFromWishlist(Authentication authentication,
                                                         HttpSession session) {
        wishlistService.moveWishlistToCart(authentication.getName(), session);
        return ResponseEntity.ok(new ApiResponse(true,"Wishlist items moved to cart. Proceed to checkout."));
    }
}
