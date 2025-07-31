package com.booknest.booknest.service;

import com.booknest.booknest.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book createBook(Book book);
    Book updateBook(Long id,Book book);
    void deleteBook(Long id);
   Optional<Book> getBook(Long id);
   Page<Book> listBooks(Pageable pageable);
}
