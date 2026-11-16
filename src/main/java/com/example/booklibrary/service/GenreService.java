package com.example.booklibrary.service;

import com.example.booklibrary.dto.GenreRequestDTO;
import com.example.booklibrary.dto.GenreResponseDTO;

import java.util.List;

public interface GenreService {

    List<GenreResponseDTO> getAllGenres();

    GenreResponseDTO getGenreById(Long id);

    GenreResponseDTO createGenre(GenreRequestDTO request);

    GenreResponseDTO updateGenre(Long id, GenreRequestDTO request);

    void deleteGenre(Long id);

    List<GenreResponseDTO> searchGenres(String keyword);
}
