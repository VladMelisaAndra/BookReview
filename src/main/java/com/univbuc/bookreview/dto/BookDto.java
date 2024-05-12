package com.univbuc.bookreview.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class BookDto {
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    @Size(min = 1, max = 255)
    private String author;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date releaseDate;

    private Long categoryId;

    @Size(max = 1000)
    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
