package com.booknest.booknest.dto;

import com.booknest.booknest.entity.Book;
import java.util.List;

public class WishlistResponse {
    private Long wishlistId;
    private Long userId;
    private List<Book> books;

    public WishlistResponse(Long wishlistId, Long userId, List<Book> books) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.books = books;
    }

    public Long getWishlistId() { return wishlistId; }
    public Long getUserId() { return userId; }
    public List<Book> getBooks() { return books; }
}
