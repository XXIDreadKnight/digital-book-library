package com.example.booklibrary.repository;

import com.example.booklibrary.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Author> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > 0")
    List<Author> findAuthorsWithBooks();
}
