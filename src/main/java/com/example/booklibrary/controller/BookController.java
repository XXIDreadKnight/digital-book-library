package com.example.booklibrary.controller;

import com.example.booklibrary.dto.BookRequestDTO;
import com.example.booklibrary.dto.BookResponseDTO;
import com.example.booklibrary.dto.GenreStatsDTO;
import com.example.booklibrary.dto.PageResponseDTO;
import com.example.booklibrary.service.BookService;
import com.example.booklibrary.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoints for managing books, pagination, queries, and cover uploads")
public class BookController {

    private final BookService bookService;
    private final FileStorageService fileStorageService;

    // Explicit Constructor Injection (mandatory)
    public BookController(BookService bookService, FileStorageService fileStorageService) {
        this.bookService = bookService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Requirement: Server-side pagination & sorting using Pageable & Sort.
     * Example: GET /api/books?page=0&size=5&sort=title,asc
     */
    @GetMapping
    @Operation(summary = "Get all books with server-side pagination and sorting")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getAllBooks(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponseDTO<BookResponseDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Requirement: @PathVariable for extracting ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get single book by ID")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * Requirement: @PostMapping, @RequestBody, and JSR-303 @Valid.
     */
    @PostMapping
    @Operation(summary = "Create a new book with JSR-303 validation")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO createdBook = bookService.createBook(request);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    /**
     * Requirement: @PutMapping, @PathVariable, @RequestBody, and @Valid.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Requirement: @DeleteMapping and @PathVariable.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by ID")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Requirement: File Upload using MultipartFile, enctype="multipart/form-data",
     * stored physically on local filesystem and path reflected in database.
     */
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and attach a cover image to a book")
    public ResponseEntity<BookResponseDTO> uploadCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        BookResponseDTO updatedBook = bookService.uploadCoverImage(id, file);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Serves/downloads the physically stored cover image file.
     */
    @GetMapping("/{id}/cover")
    @Operation(summary = "View or download the book's cover image")
    public ResponseEntity<Resource> getCoverImage(@PathVariable Long id, HttpServletRequest request) {
        BookResponseDTO book = bookService.getBookById(id);
        if (book.getCoverImage() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileStorageService.loadFileAsResource(book.getCoverImage());

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default to octet-stream
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Requirement: Complex query using @Query (searchByKeyword) + @RequestParam + Pageable.
     */
    @GetMapping("/search")
    @Operation(summary = "Search books by keyword across title/description using custom @Query")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> searchBooks(
            @RequestParam("keyword") String keyword,
            @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable) {
        PageResponseDTO<BookResponseDTO> results = bookService.searchBooksByKeyword(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Requirement: Complex query using @Query joining Many-to-Many authors + @PathVariable + Pageable.
     */
    @GetMapping("/by-author/{authorId}")
    @Operation(summary = "Get books by author ID using custom @Query join")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getBooksByAuthor(
            @PathVariable Long authorId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        PageResponseDTO<BookResponseDTO> results = bookService.getBooksByAuthor(authorId, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Requirement: Method-Name Query 1 (findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual).
     */
    @GetMapping("/filter/title-year")
    @Operation(summary = "Filter books by title and minimum publication year (Method-Name Query)")
    public ResponseEntity<List<BookResponseDTO>> filterByTitleAndYear(
            @RequestParam("title") String title,
            @RequestParam("minYear") Integer minYear) {
        List<BookResponseDTO> books = bookService.filterByTitleAndMinYear(title, minYear);
        return ResponseEntity.ok(books);
    }

    /**
     * Requirement: Method-Name Query 2 (findByPriceLessThanEqualAndGenreNameIgnoreCase).
     */
    @GetMapping("/filter/price-genre")
    @Operation(summary = "Filter books by maximum price and genre name (Method-Name Query)")
    public ResponseEntity<List<BookResponseDTO>> filterByPriceAndGenre(
            @RequestParam("maxPrice") Double maxPrice,
            @RequestParam("genre") String genre) {
        List<BookResponseDTO> books = bookService.filterByMaxPriceAndGenre(maxPrice, genre);
        return ResponseEntity.ok(books);
    }

    /**
     * Requirement: Complex query using @Query (Aggregation GROUP BY).
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get genre statistics (book count and average price) using custom @Query aggregation")
    public ResponseEntity<List<GenreStatsDTO>> getGenreStatistics() {
        List<GenreStatsDTO> stats = bookService.getGenreStatistics();
        return ResponseEntity.ok(stats);
    }
}
