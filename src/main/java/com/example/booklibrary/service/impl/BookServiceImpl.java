package com.example.booklibrary.service.impl;

import com.example.booklibrary.dto.*;
import com.example.booklibrary.entity.Author;
import com.example.booklibrary.entity.Book;
import com.example.booklibrary.entity.Genre;
import com.example.booklibrary.exception.BadRequestException;
import com.example.booklibrary.exception.ResourceNotFoundException;
import com.example.booklibrary.repository.AuthorRepository;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.repository.GenreRepository;
import com.example.booklibrary.service.BookService;
import com.example.booklibrary.service.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final FileStorageService fileStorageService;

    // Explicit Constructor Injection (mandatory)
    public BookServiceImpl(BookRepository bookRepository,
                           GenreRepository genreRepository,
                           AuthorRepository authorRepository,
                           FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookResponseDTO> getAllBooks(Pageable pageable) {
        Page<Book> booksPage = bookRepository.findAll(pageable);
        return PageResponseDTO.fromPage(booksPage.map(this::mapToDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return mapToDTO(book);
    }

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {
        if (bookRepository.existsByIsbn(request.getIsbn().trim())) {
            throw new BadRequestException("Book with ISBN '" + request.getIsbn().trim() + "' already exists.");
        }

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", request.getGenreId()));

        Set<Author> authors = findAuthorsByIds(request.getAuthorIds());

        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setIsbn(request.getIsbn().trim());
        book.setDescription(request.getDescription());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setGenre(genre);
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        String newIsbn = request.getIsbn().trim();
        if (!book.getIsbn().equalsIgnoreCase(newIsbn) && bookRepository.existsByIsbn(newIsbn)) {
            throw new BadRequestException("Book with ISBN '" + newIsbn + "' already exists.");
        }

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", request.getGenreId()));

        Set<Author> authors = findAuthorsByIds(request.getAuthorIds());

        book.setTitle(request.getTitle().trim());
        book.setIsbn(newIsbn);
        book.setDescription(request.getDescription());
        book.setPublicationYear(request.getPublicationYear());
        book.setPrice(request.getPrice());
        book.setGenre(genre);
        book.setAuthors(authors);

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        // Delete associated cover image file if present
        if (book.getCoverImage() != null) {
            fileStorageService.deleteFile(book.getCoverImage());
        }

        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public BookResponseDTO uploadCoverImage(Long bookId, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        // Delete old cover image if it exists
        if (book.getCoverImage() != null) {
            fileStorageService.deleteFile(book.getCoverImage());
        }

        // Physically store file on disk
        String storedFileName = fileStorageService.storeFile(file);

        // Reflect file name in the database
        book.setCoverImage(storedFileName);
        Book updatedBook = bookRepository.save(book);

        return mapToDTO(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookResponseDTO> searchBooksByKeyword(String keyword, Pageable pageable) {
        Page<Book> resultPage = bookRepository.searchByKeyword(keyword, pageable);
        return PageResponseDTO.fromPage(resultPage.map(this::mapToDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BookResponseDTO> getBooksByAuthor(Long authorId, Pageable pageable) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author", "id", authorId);
        }
        Page<Book> resultPage = bookRepository.findBooksByAuthorId(authorId, pageable);
        return PageResponseDTO.fromPage(resultPage.map(this::mapToDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> filterByTitleAndMinYear(String title, Integer year) {
        return bookRepository.findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual(title, year)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> filterByMaxPriceAndGenre(Double maxPrice, String genreName) {
        return bookRepository.findByPriceLessThanEqualAndGenreNameIgnoreCase(maxPrice, genreName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreStatsDTO> getGenreStatistics() {
        return bookRepository.getGenreStatistics();
    }

    private Set<Author> findAuthorsByIds(Set<Long> authorIds) {
        Set<Author> authors = new HashSet<>();
        for (Long authorId : authorIds) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
            authors.add(author);
        }
        return authors;
    }

    private BookResponseDTO mapToDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setPrice(book.getPrice());
        dto.setCoverImage(book.getCoverImage());

        if (book.getCoverImage() != null) {
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/books/")
                    .path(book.getId().toString())
                    .path("/cover")
                    .toUriString();
            dto.setCoverImageUrl(downloadUrl);
        }

        if (book.getGenre() != null) {
            Genre g = book.getGenre();
            dto.setGenre(new GenreResponseDTO(g.getId(), g.getName(), g.getDescription(), 0));
        }

        if (book.getAuthors() != null) {
            Set<AuthorResponseDTO> authorDTOs = book.getAuthors().stream()
                    .map(a -> new AuthorResponseDTO(a.getId(), a.getFullName(), a.getEmail(), a.getBiography(), a.getBirthDate(), 0))
                    .collect(Collectors.toSet());
            dto.setAuthors(authorDTOs);
        }

        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
}
