package com.spring.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity(name = "movie")
@Table(name = "movie", uniqueConstraints = @UniqueConstraint(name = "movie_name_unique", columnNames = "name"))

public class Movie {

    @Id

    @SequenceGenerator(name = "movie_id_sequence", sequenceName = "movie_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_id_sequence")
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Заполните название")
    private String name;

    @Column(name = "release_date", nullable = false, columnDefinition = "DATE")
    @NotNull(message = "Заполните дату")
    @Temporal(javax.persistence.TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date releaseDate;

    // only for users to see
    @Column(name = "rating", nullable = true, columnDefinition = "Decimal(3,1)")
    private Double rating;

    @Column(name = "genre", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Заполните жанр")
    private String genre;

    @Column(name = "status", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Укажите статус")
    private String status;

    @Column(name = "poster", nullable = true, columnDefinition = "TEXT")
    private String poster;

    @OneToMany(mappedBy = "movie", cascade = { CascadeType.MERGE, CascadeType.REMOVE})
    private List<Review> reviews;

    @OneToMany(mappedBy = "movie", cascade = { CascadeType.MERGE, CascadeType.REMOVE})
    private List<Post> posts;

    @Column(name = "description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_finance", nullable = true, columnDefinition = "Decimal(3,1)")
    private double requiredFinance;

    @Column(name = "total_finance", nullable = true, columnDefinition = "Decimal(3,1)")
    private double totalFinance;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @NotEmpty(message = "Укажите продюссера")
    @JoinTable(name = "movie_producer", joinColumns = { @JoinColumn(name = "movie_id") }, inverseJoinColumns = {
            @JoinColumn(name = "producer_id") })
    private Set<Producer> producers;

    @Column(name = "filmCrew", nullable = false, columnDefinition = "TEXT")
    private String filmCrew;

    @ManyToMany(mappedBy = "watchListedMovies")
    private Set<User> watchlistUsers = new HashSet<>();

    @ManyToMany(mappedBy = "favouriteMovies")
    private Set<User> favouriteMovieUsers = new HashSet<>();

    @ManyToOne
    private User user;




    public Movie() {
    }

    public Movie(String name, Date releaseDate, Double rating, String genre, String poster, String description, String filmCrew, String status,
            Set<Producer> producers,Set<User>watchlistUsers) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.genre = genre;
        this.status = status;
        this.poster = poster;
        this.description = description;
        this.producers = producers;
        this.filmCrew = filmCrew;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Producer> getProducers() {
        return producers;
    }

    public void setProducers(Set<Producer> producers) {
        this.producers = producers;
    }

    public String getFilmCrew() {
        return filmCrew;
    }

    public void setFilmCrew(String filmCrew) {
        this.filmCrew = filmCrew;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }


    public Set<User> getWatchlistUsers() {
        return watchlistUsers;
    }
    public void setWatchlistUsers(Set<User> watchlistUsers) {
        this.watchlistUsers = watchlistUsers;
    }
    public void addUserToWatchListedMovie(User user) { watchlistUsers.add(user); }
    public boolean isUserAvailableInMovieWatchlist(Long user_id){
        Set<User>watchlistUser = this.getWatchlistUsers();
        for(User user:watchlistUser){
            if(user.getId() == user_id)
                return true;
        }
        return false;
    }

    public Set<User> getFavouriteMovieUsers() {
        return favouriteMovieUsers;
    }
    public void setFavouriteMovieUsers(Set<User> favouriteMovieUsers) { this.favouriteMovieUsers = favouriteMovieUsers; }
    public void addUserToFavouriteMovie(User user) { favouriteMovieUsers.add(user); }
    public boolean isUserAvailableInFavouriteMovie(Long user_id){
        Set<User>favouriteMovieUsers = this.getFavouriteMovieUsers();
        for(User user:favouriteMovieUsers){
            if(user.getId() == user_id)
                return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getRequiredFinance() {
        return requiredFinance;
    }

    public void setRequiredFinance(double requiredFinance) {
        this.requiredFinance = requiredFinance;
    }

    public double getTotalFinance() {
        return totalFinance;
    }

    public void setTotalFinance(double totalFinance) {
        this.totalFinance = totalFinance;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", name=" + name + ", releaseDate=" + releaseDate + ", rating=" + rating + ", genre="
                + genre + ", poster=" + poster + ", description=" + description + ", produsers=" + producers + "]";
    }
}
