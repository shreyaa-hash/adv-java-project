package com.bookstore.config;

import com.bookstore.model.Book;
import com.bookstore.repository.BookJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SampleData {
    @Bean
    CommandLineRunner seedBooks(BookJpaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                    new Book("Effective Java", "Joshua Bloch", new BigDecimal("650.00"), 12, "Programming"),
                    new Book("Clean Code", "Robert C. Martin", new BigDecimal("575.00"), 8, "Programming"),
                    new Book("The Hobbit", "J. R. R. Tolkien", new BigDecimal("399.00"), 15, "Fiction"),
                    new Book("A Brief History of Time", "Stephen Hawking", new BigDecimal("450.00"), 6, "Science")
                ));
            }
        };
    }
}
