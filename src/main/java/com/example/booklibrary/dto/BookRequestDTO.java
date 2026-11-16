package com.example.booklibrary.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public class BookRequestDTO {

    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;

    @NotBlank(message = "ISBN is mandatory")
    @Pattern(regexp = "^(97[89])?\\d{9}[\\dX]$", message = "ISBN must be a valid 10-digit or 13-digit format")
    private String isbn;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Publication year is mandatory")
    @Min(value = 1450, message = "Publication year must be 1450 or later")
    @Max(value = 2100, message = "Publication year must be realistic (<= 2100)")
    private Integer publicationYear;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private Double price;

    @NotNull(message = "Genre ID is mandatory")
    private Long genreId;

    @NotEmpty(message = "At least one author ID must be associated with the book")
    private Set<Long> authorIds;

    public BookRequestDTO() {
    }

    public BookRequestDTO(String title, String isbn, String description, Integer publicationYear, Double price, Long genreId, Set<Long> authorIds) {
        this.title = title;
        this.isbn = isbn;
        this.description = description;
        this.publicationYear = publicationYear;
        this.price = price;
        this.genreId = genreId;
        this.authorIds = authorIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Set<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(Set<Long> authorIds) {
        this.authorIds = authorIds;
    }
}
