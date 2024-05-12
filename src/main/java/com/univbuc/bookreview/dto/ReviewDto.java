package com.univbuc.bookreview.dto;

import java.util.Date;

public class ReviewDto {
    private Long bookId;
    private Long userId;
    private int stars;
    private String comment;
    private Date date;

    // Getters and Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Integer getUserId() { return Math.toIntExact(userId); }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
