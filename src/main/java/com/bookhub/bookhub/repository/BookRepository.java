package com.bookhub.bookhub.repository;

import com.bookhub.bookhub.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthor(String Author);

    List<Book> findByTitleAndAuthor(String title, String author);

    List<Book> findByPublicationYearGreaterThan(Integer year);

    List<Book> findByTitleContaining(String keyword);

    long countByAuthor(String author);

    boolean existsByIsbn(String isbn);

    void deleteByIsbn(String isbn);
}
