package com.spring.model;

import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    @Column(name = "content", nullable = true, columnDefinition = "TEXT")
    private String content;
    private double moneyRequired;
    private double moneyTotal;
    private boolean status;

    @ManyToOne
    private Movie movie;

    public Post() {
    }

    public Post(long postId, String title, String content, double moneyRequired, double moneyTotal, boolean status, Movie movie) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.moneyRequired = moneyRequired;
        this.moneyTotal = moneyTotal;
        this.status = status;
        this.movie = movie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getMoneyRequired() {
        return moneyRequired;
    }

    public void setMoneyRequired(double moneyRequired) {
        this.moneyRequired = moneyRequired;
    }

    public double getMoneyTotal() {
        return moneyTotal;
    }

    public void setMoneyTotal(double moneyTotal) {
        this.moneyTotal = moneyTotal;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}