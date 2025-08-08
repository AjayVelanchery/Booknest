package com.booknest.booknest.service;

import com.booknest.booknest.dto.BookRequest;
import com.booknest.booknest.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface BookService {

    Book createBook(BookRequest request);
    Book updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    Page<Book> listBooks(Pageable pageable);

    Optional<Book> getBook(Long id);
    ResponseEntity<Book> getBookResponse(Long id);

    Book uploadBookImage(Long id, MultipartFile image);
}
