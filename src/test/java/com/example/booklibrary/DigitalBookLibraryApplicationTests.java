package com.example.booklibrary;

import com.example.booklibrary.dto.BookRequestDTO;
import com.example.booklibrary.dto.BookResponseDTO;
import com.example.booklibrary.dto.GenreStatsDTO;
import com.example.booklibrary.dto.PageResponseDTO;
import com.example.booklibrary.entity.Author;
import com.example.booklibrary.entity.Book;
import com.example.booklibrary.entity.Genre;
import com.example.booklibrary.repository.AuthorRepository;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.repository.GenreRepository;
import com.example.booklibrary.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DigitalBookLibraryApplicationTests {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookService bookService;

    // Constructor Injection in Test class
    @Autowired
    public DigitalBookLibraryApplicationTests(BookRepository bookRepository,
                                             AuthorRepository authorRepository,
                                             GenreRepository genreRepository,
                                             BookService bookService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookService = bookService;
    }

    @Test
    @DisplayName("Context loads and starter entities are initialized successfully")
    void contextLoads() {
        assertTrue(genreRepository.count() >= 4, "Genres should be initialized");
        assertTrue(authorRepository.count() >= 5, "Authors should be initialized");
        assertTrue(bookRepository.count() >= 6, "Books should be initialized");
    }

    @Test
    @DisplayName("Test Method-Name Query 1: findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual")
    void testMethodNameQuery1() {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual("1984", 1900);
        assertFalse(books.isEmpty(), "Should find 1984 published after 1900");
        assertEquals("1984", books.get(0).getTitle());
    }

    @Test
    @DisplayName("Test Method-Name Query 2: findByPriceLessThanEqualAndGenreNameIgnoreCase")
    void testMethodNameQuery2() {
        List<Book> books = bookRepository.findByPriceLessThanEqualAndGenreNameIgnoreCase(20.0, "Science Fiction");
        assertFalse(books.isEmpty(), "Should find Sci-Fi books under $20");
    }

    @Test
    @DisplayName("Test Custom @Query 1: findBooksByAuthorId")
    void testCustomQuery1() {
        Author orwell = authorRepository.findByFullNameContainingIgnoreCase("Orwell").get(0);
        Page<Book> orwellBooks = bookRepository.findBooksByAuthorId(orwell.getId(), PageRequest.of(0, 10));
        assertTrue(orwellBooks.getTotalElements() >= 2, "Orwell should have at least 2 books (1984 and Animal Farm)");
    }

    @Test
    @DisplayName("Test Custom @Query 2: searchByKeyword")
    void testCustomQuery2() {
        Page<Book> searchResults = bookRepository.searchByKeyword("Arrakis", PageRequest.of(0, 10));
        assertEquals(1, searchResults.getTotalElements());
        assertEquals("Dune", searchResults.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Test Custom @Query 3: getGenreStatistics (Aggregation GROUP BY)")
    void testCustomQuery3() {
        List<GenreStatsDTO> stats = bookRepository.getGenreStatistics();
        assertFalse(stats.isEmpty(), "Genre statistics should return results");
    }

    @Test
    @DisplayName("Test Pagination and Sorting with Pageable")
    void testPaginationAndSorting() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "title"));
        PageResponseDTO<BookResponseDTO> pageResponse = bookService.getAllBooks(pageable);

        assertNotNull(pageResponse);
        assertEquals(3, pageResponse.getContent().size());
        assertEquals(0, pageResponse.getPageNumber());
        assertEquals(3, pageResponse.getPageSize());
        assertTrue(pageResponse.getTotalElements() >= 6);
    }

    @Test
    @Transactional
    @DisplayName("Test Book Creation and Many-to-Many / Many-to-One Relationships")
    void testCreateBookWithRelationships() {
        Genre genre = genreRepository.findAll().get(0);
        Author author = authorRepository.findAll().get(0);

        BookRequestDTO request = new BookRequestDTO(
                "Test Driven Spring Boot",
                "9781234567890",
                "A comprehensive guide to Spring Boot test automation.",
                2024,
                39.99,
                genre.getId(),
                Set.of(author.getId())
        );

        BookResponseDTO response = bookService.createBook(request);
        assertNotNull(response.getId());
        assertEquals("Test Driven Spring Boot", response.getTitle());
        assertEquals(genre.getName(), response.getGenre().getName());
        assertEquals(1, response.getAuthors().size());
    }

    @Test
    @Transactional
    @DisplayName("Test File Upload handling and database reflection")
    void testFileUpload() {
        Book book = bookRepository.findAll().get(0);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "cover_test.png",
                "image/png",
                "Fake Image Content".getBytes()
        );

        BookResponseDTO updatedBook = bookService.uploadCoverImage(book.getId(), mockFile);
        assertNotNull(updatedBook.getCoverImage());
        assertTrue(updatedBook.getCoverImage().endsWith("cover_test.png"));
    }
}
