package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookCrudRepository extends CrudRepository<Book, Long> { }
