package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.repository.BookCrudRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books/crud")
public class BookCrudController {
    private final BookCrudRepository repository;

    public BookCrudController(BookCrudRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        repository.findAll().forEach(books::add);
        return books;
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) {
        book.setId(null);
        return repository.save(book);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book changed) {
        Book book = repository.findById(id).orElseThrow(() -> notFound(id));
        copyFields(changed, book);
        return repository.save(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) throw notFound(id);
        repository.deleteById(id);
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + id + " was not found");
    }

    static void copyFields(Book source, Book target) {
        target.setTitle(source.getTitle());
        target.setAuthor(source.getAuthor());
        target.setPrice(source.getPrice());
        target.setQuantity(source.getQuantity());
        target.setCategory(source.getCategory());
    }
}
