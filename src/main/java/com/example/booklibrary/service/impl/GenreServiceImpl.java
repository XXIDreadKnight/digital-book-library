package com.example.booklibrary.service.impl;

import com.example.booklibrary.dto.GenreRequestDTO;
import com.example.booklibrary.dto.GenreResponseDTO;
import com.example.booklibrary.entity.Genre;
import com.example.booklibrary.exception.BadRequestException;
import com.example.booklibrary.exception.ResourceNotFoundException;
import com.example.booklibrary.repository.GenreRepository;
import com.example.booklibrary.service.GenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    // Explicit Constructor Injection (mandatory)
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponseDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));
        return mapToDTO(genre);
    }

    @Override
    @Transactional
    public GenreResponseDTO createGenre(GenreRequestDTO request) {
        if (genreRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Genre with name '" + request.getName().trim() + "' already exists.");
        }

        Genre genre = new Genre();
        genre.setName(request.getName().trim());
        genre.setDescription(request.getDescription());

        Genre savedGenre = genreRepository.save(genre);
        return mapToDTO(savedGenre);
    }

    @Override
    @Transactional
    public GenreResponseDTO updateGenre(Long id, GenreRequestDTO request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));

        String newName = request.getName().trim();
        if (!genre.getName().equalsIgnoreCase(newName) && genreRepository.existsByNameIgnoreCase(newName)) {
            throw new BadRequestException("Genre with name '" + newName + "' already exists.");
        }

        genre.setName(newName);
        genre.setDescription(request.getDescription());

        Genre updatedGenre = genreRepository.save(genre);
        return mapToDTO(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id));
        if (genre.getBooks() != null && !genre.getBooks().isEmpty()) {
            throw new BadRequestException("Cannot delete genre '" + genre.getName() + "' because it has associated books.");
        }
        genreRepository.delete(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponseDTO> searchGenres(String keyword) {
        return genreRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private GenreResponseDTO mapToDTO(Genre genre) {
        int bookCount = genre.getBooks() != null ? genre.getBooks().size() : 0;
        return new GenreResponseDTO(genre.getId(), genre.getName(), genre.getDescription(), bookCount);
    }
}
