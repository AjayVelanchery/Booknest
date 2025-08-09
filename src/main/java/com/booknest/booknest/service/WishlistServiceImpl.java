package com.booknest.booknest.service;

import com.booknest.booknest.dto.WishlistResponse;
import com.booknest.booknest.entity.Book;
import com.booknest.booknest.entity.User;
import com.booknest.booknest.entity.Wishlist;
import com.booknest.booknest.exception.ResourceNotFoundException;
import com.booknest.booknest.repository.BookRepository;
import com.booknest.booknest.repository.UserRepository;
import com.booknest.booknest.repository.WishlistRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               UserRepository userRepository,
                               BookRepository bookRepository,
                               CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
    }

    private User getCurrentUser(String principalId) {
        return userRepository.findByUsername(principalId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principalId));
    }

    @Override
    public WishlistResponse getMyWishlist(String principalId) {
        User user = getCurrentUser(principalId);
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().user(user).build()));
        return new WishlistResponse(wishlist.getId(), user.getId(), wishlist.getBooks());
    }

    @Override
    public void addBookToWishlist(String principalId, Long bookId) {
        User user = getCurrentUser(principalId);
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().user(user).build()));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (!wishlist.getBooks().contains(book)) {
            wishlist.getBooks().add(book);
            wishlistRepository.save(wishlist);
        }
    }

    @Override
    public void removeBookFromWishlist(String principalId, Long bookId) {
        User user = getCurrentUser(principalId);
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        wishlist.getBooks().removeIf(book -> book.getId().equals(bookId));
        wishlistRepository.save(wishlist);
    }

    @Override
    public void moveWishlistToCart(String principalId, HttpSession session) {
        User user = getCurrentUser(principalId);
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        for (Book book : wishlist.getBooks()) {
            cartService.addItem(session, book, 1);
        }

        wishlistRepository.save(wishlist);
    }
}
