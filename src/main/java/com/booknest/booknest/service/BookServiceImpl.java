package com.booknest.booknest.service;

import com.booknest.booknest.entity.Book;
import com.booknest.booknest.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService{
    @Autowired
    private BookRepository bookRepository;


    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id,Book book){
        book.setId(id);
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id){
        bookRepository.deleteById(id);
    }

    @Override
    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Page<Book> listBooks(Pageable pageable) {
        return  bookRepository.findAll(pageable);
    }


}
