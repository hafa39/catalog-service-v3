package com.example.catalogservice.domain;

import com.example.catalogservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class BookRepositoryJdbcTests {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findBookByIsbnWhenExisting(){
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        jdbcAggregateTemplate.insert(book);
        Optional<Book> byIsbn = bookRepository.findByIsbn(bookIsbn);
        assertThat(byIsbn).isPresent();
        assertThat(byIsbn.get().isbn()).isEqualTo(bookIsbn);

    }

    @Test
    void findBookByIsbnWhenNotExisting(){
        var bookIsbn = "1234561237";
        Optional<Book> byIsbn = bookRepository.findByIsbn(bookIsbn);
        assertThat(byIsbn).isNotPresent();
    }

    @Test
    void existsByIsbnWhenExisting(){
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        jdbcAggregateTemplate.insert(book);
        boolean existsByIsbn = bookRepository.existsByIsbn(bookIsbn);
        assertThat(existsByIsbn).isTrue();
    }

    @Test
    void existsByIsbnWhenNotExisting(){
        var bookIsbn = "1234561237";
        boolean existsByIsbn = bookRepository.existsByIsbn(bookIsbn);
        assertThat(existsByIsbn).isFalse();
    }

    @Test
    void deleteByIsbn(){
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        Book persistedBook = jdbcAggregateTemplate.insert(book);
        bookRepository.deleteByIsbn(bookIsbn);
        assertThat(jdbcAggregateTemplate.findById(persistedBook.id(),Book.class)).isNull();
    }
}
