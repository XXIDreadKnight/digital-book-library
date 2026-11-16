package com.example.booklibrary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AuthorRequestDTO {

    @NotBlank(message = "Author full name is mandatory")
    @Size(min = 2, max = 150, message = "Author full name must be between 2 and 150 characters")
    private String fullName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Size(max = 2000, message = "Biography cannot exceed 2000 characters")
    private String biography;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    public AuthorRequestDTO() {
    }

    public AuthorRequestDTO(String fullName, String email, String biography, LocalDate birthDate) {
        this.fullName = fullName;
        this.email = email;
        this.biography = biography;
        this.birthDate = birthDate;
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
}
