package com.example.booklibrary.repository;

import com.example.booklibrary.dto.GenreStatsDTO;
import com.example.booklibrary.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // ==============================================================
    // Method-Name Queries (Mandatory requirement: at least 2)
    // ==============================================================

    /**
     * Method-Name Query 1: Finds books where title contains given substring (case-insensitive)
     * and publication year is greater than or equal to the specified year.
     */
    List<Book> findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual(String title, Integer publicationYear);

    /**
     * Method-Name Query 2: Finds books with price less than or equal to a threshold
     * belonging to a genre matched by name (case-insensitive).
     */
    List<Book> findByPriceLessThanEqualAndGenreNameIgnoreCase(Double maxPrice, String genreName);

    /**
     * Method-Name Query 3: Finds books by genre ID with pagination and sorting.
     */
    Page<Book> findByGenreId(Long genreId, Pageable pageable);

    // ==============================================================
    // Complex / Custom Queries using @Query (Mandatory requirement: at least 2)
    // ==============================================================

    /**
     * Complex @Query 1: Joins the many-to-many relationship to find books authored
     * by a specific author ID with pagination and sorting.
     */
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    Page<Book> findBooksByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * Complex @Query 2: Performs case-insensitive search across title or description
     * matching the given keyword with pagination.
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Complex @Query 3: Aggregation query calculating book count and average price
     * grouped by genre.
     */
    @Query("SELECT new com.example.booklibrary.dto.GenreStatsDTO(g.name, COUNT(b), AVG(b.price)) " +
           "FROM Genre g LEFT JOIN g.books b GROUP BY g.name")
    List<GenreStatsDTO> getGenreStatistics();
}
