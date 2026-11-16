package com.example.booklibrary.dto;

import java.time.LocalDate;

public class AuthorResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String biography;
    private LocalDate birthDate;
    private int bookCount;

    public AuthorResponseDTO() {
    }

    public AuthorResponseDTO(Long id, String fullName, String email, String biography, LocalDate birthDate, int bookCount) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.biography = biography;
        this.birthDate = birthDate;
        this.bookCount = bookCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }
}
