package com.booknest.booknest.repository;

import com.booknest.booknest.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
    public interface BookRepository extends JpaRepository<Book,Long>{


}
