package com.example.booklibrary.dto;

public class GenreStatsDTO {

    private String genreName;
    private Long bookCount;
    private Double averagePrice;

    public GenreStatsDTO() {
    }

    public GenreStatsDTO(String genreName, Long bookCount, Double averagePrice) {
        this.genreName = genreName;
        this.bookCount = bookCount;
        this.averagePrice = averagePrice != null ? Math.round(averagePrice * 100.0) / 100.0 : 0.0;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }
}
