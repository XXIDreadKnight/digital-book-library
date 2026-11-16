package com.example.booklibrary.controller;

import com.example.booklibrary.dto.GenreRequestDTO;
import com.example.booklibrary.dto.GenreResponseDTO;
import com.example.booklibrary.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "Endpoints for managing book genres")
public class GenreController {

    private final GenreService genreService;

    // Explicit Constructor Injection (mandatory)
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @Operation(summary = "Get all genres")
    public ResponseEntity<List<GenreResponseDTO>> getAllGenres() {
        List<GenreResponseDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID")
    public ResponseEntity<GenreResponseDTO> getGenreById(@PathVariable Long id) {
        GenreResponseDTO genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    @Operation(summary = "Create a new genre with validation")
    public ResponseEntity<GenreResponseDTO> createGenre(@Valid @RequestBody GenreRequestDTO request) {
        GenreResponseDTO createdGenre = genreService.createGenre(request);
        return new ResponseEntity<>(createdGenre, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a genre")
    public ResponseEntity<GenreResponseDTO> updateGenre(
            @PathVariable Long id,
            @Valid @RequestBody GenreRequestDTO request) {
        GenreResponseDTO updatedGenre = genreService.updateGenre(id, request);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre by ID")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search genres by name")
    public ResponseEntity<List<GenreResponseDTO>> searchGenres(@RequestParam("name") String name) {
        List<GenreResponseDTO> results = genreService.searchGenres(name);
        return ResponseEntity.ok(results);
    }
}
