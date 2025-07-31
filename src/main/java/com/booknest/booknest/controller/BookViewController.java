package com.booknest.booknest.controller;

import com.booknest.booknest.entity.Book;
import com.booknest.booknest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookViewController {

    @Autowired
    private BookService bookService;


    @GetMapping("/books")
    public String viewBooks(@RequestParam (defaultValue = "0")int page, Model model){
        if (page < 0) {
            page = 0;
        }
        Page<Book> books=bookService.listBooks(PageRequest.of(page,10));
        model.addAttribute("books",books);
        return "books";
    }
}
