package com.example.booklibrary.service;

import com.example.booklibrary.dto.BookRequestDTO;
import com.example.booklibrary.dto.BookResponseDTO;
import com.example.booklibrary.dto.GenreStatsDTO;
import com.example.booklibrary.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {

    PageResponseDTO<BookResponseDTO> getAllBooks(Pageable pageable);

    BookResponseDTO getBookById(Long id);

    BookResponseDTO createBook(BookRequestDTO request);

    BookResponseDTO updateBook(Long id, BookRequestDTO request);

    void deleteBook(Long id);

    BookResponseDTO uploadCoverImage(Long bookId, MultipartFile file);

    PageResponseDTO<BookResponseDTO> searchBooksByKeyword(String keyword, Pageable pageable);

    PageResponseDTO<BookResponseDTO> getBooksByAuthor(Long authorId, Pageable pageable);

    List<BookResponseDTO> filterByTitleAndMinYear(String title, Integer year);

    List<BookResponseDTO> filterByMaxPriceAndGenre(Double maxPrice, String genreName);

    List<GenreStatsDTO> getGenreStatistics();
}
