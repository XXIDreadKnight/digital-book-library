package com.example.booklibrary.service.impl;

import com.example.booklibrary.dto.AuthorRequestDTO;
import com.example.booklibrary.dto.AuthorResponseDTO;
import com.example.booklibrary.entity.Author;
import com.example.booklibrary.exception.BadRequestException;
import com.example.booklibrary.exception.ResourceNotFoundException;
import com.example.booklibrary.repository.AuthorRepository;
import com.example.booklibrary.service.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    // Explicit Constructor Injection (mandatory)
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        return mapToDTO(author);
    }

    @Override
    @Transactional
    public AuthorResponseDTO createAuthor(AuthorRequestDTO request) {
        if (authorRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Author with email '" + request.getEmail() + "' already exists.");
        }

        Author author = new Author();
        author.setFullName(request.getFullName().trim());
        author.setEmail(request.getEmail().trim().toLowerCase());
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());

        Author savedAuthor = authorRepository.save(author);
        return mapToDTO(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!author.getEmail().equalsIgnoreCase(newEmail) && authorRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Author with email '" + newEmail + "' already exists.");
        }

        author.setFullName(request.getFullName().trim());
        author.setEmail(newEmail);
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());

        Author updatedAuthor = authorRepository.save(author);
        return mapToDTO(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new BadRequestException("Cannot delete author '" + author.getFullName() + "' because they have associated books.");
        }
        authorRepository.delete(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDTO> searchAuthors(String name) {
        return authorRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AuthorResponseDTO mapToDTO(Author author) {
        int bookCount = author.getBooks() != null ? author.getBooks().size() : 0;
        return new AuthorResponseDTO(
                author.getId(),
                author.getFullName(),
                author.getEmail(),
                author.getBiography(),
                author.getBirthDate(),
                bookCount
        );
    }
}
