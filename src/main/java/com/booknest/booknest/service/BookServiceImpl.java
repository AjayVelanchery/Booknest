package com.booknest.booknest.service;

import com.booknest.booknest.dto.BookRequest;
import com.booknest.booknest.entity.Book;
import com.booknest.booknest.exception.ResourceNotFoundException;
import com.booknest.booknest.repository.BookRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final Cloudinary cloudinary;

    public BookServiceImpl(BookRepository bookRepository, Cloudinary cloudinary) {
        this.bookRepository = bookRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Book createBook(BookRequest request) {
        Book book = new Book();
        copyFromRequest(request, book);
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        copyFromRequest(request, book);
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        bookRepository.delete(book);
    }

    @Override
    public Page<Book> listBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book uploadBookImage(Long id, MultipartFile image) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));

        try {
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());

            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            book.setImageUrl(imageUrl);
            book.setImagePublicId(publicId);

            return bookRepository.save(book);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public ResponseEntity<Book> getBookResponse(Long id) {
        return getBook(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    private void copyFromRequest(BookRequest request, Book book) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setPublisher(request.getPublisher());
        book.setPublicationDate(request.getPublicationDate());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
    }
}
