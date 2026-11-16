package com.example.booklibrary.service;

import com.example.booklibrary.dto.AuthorRequestDTO;
import com.example.booklibrary.dto.AuthorResponseDTO;

import java.util.List;

public interface AuthorService {

    List<AuthorResponseDTO> getAllAuthors();

    AuthorResponseDTO getAuthorById(Long id);

    AuthorResponseDTO createAuthor(AuthorRequestDTO request);

    AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request);

    void deleteAuthor(Long id);

    List<AuthorResponseDTO> searchAuthors(String name);
}
