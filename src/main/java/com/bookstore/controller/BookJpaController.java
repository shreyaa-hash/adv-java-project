package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.repository.BookJpaRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/books/jpa")
public class BookJpaController {
    private final BookJpaRepository repository;

    public BookJpaController(BookJpaRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Book> findAll(@RequestParam(defaultValue = "title") String sort) {
        String safeSort = List.of("title", "author", "price", "quantity", "id").contains(sort) ? sort : "title";
        return repository.findAll(Sort.by(Sort.Direction.ASC, safeSort));
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id) { return repository.findById(id).orElseThrow(() -> notFound(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody Book book) {
        book.setId(null);
        return repository.saveAndFlush(book);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book changed) {
        Book book = repository.findById(id).orElseThrow(() -> notFound(id));
        BookCrudController.copyFields(changed, book);
        return repository.saveAndFlush(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) throw notFound(id);
        repository.deleteById(id);
        repository.flush();
    }

    @GetMapping("/count")
    public long count() { return repository.count(); }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + id + " was not found");
    }
}
