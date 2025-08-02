package com.booknest.booknest.controller;

import com.booknest.booknest.entity.Book;
import com.booknest.booknest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
    @RequestMapping("/api/books")
   public class BookController{

        @Autowired private BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<Book> createBook(@RequestBody Book book){

            return ResponseEntity.ok(bookService.createBook(book));
        }

        @GetMapping("/{id}")
    public ResponseEntity<Book>getBook(@PathVariable Long id){
            Optional<Book>book=bookService.getBook(id);

            return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

        }
    @PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}")
public ResponseEntity<Book>updateBook(@PathVariable Long id,@RequestBody Book book){

            return ResponseEntity.ok(bookService.updateBook(id,book));
}
    @PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteBook(@PathVariable Long id){
    bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
        }




        @GetMapping
    public Page<Book> listBooks(Pageable pageable){
            return bookService.listBooks(pageable);
        }
}


