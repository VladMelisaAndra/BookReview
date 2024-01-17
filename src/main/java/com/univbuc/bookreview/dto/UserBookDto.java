package com.univbuc.bookreview.dto;

public class UserBookDto {

    private Long userId;
    private Long bookId;

    public UserBookDto() {}

    public UserBookDto(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}

