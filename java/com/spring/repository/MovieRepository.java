package com.spring.repository;

import com.spring.model.Movie;
import com.spring.model.Producer;
import com.spring.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    public List<Movie> findMoviesByUser(User user);

    public Page<Movie> findMoviesByStatusEquals(String status, Pageable pageable);

    public Page<Movie> findMoviesByNameContainingIgnoreCaseOrGenreContainingIgnoreCase(String key1, String key2, Pageable pageable);

    public Page<Movie> findMoviesByIdOrNameContainingIgnoreCase(String key1, String key2, Pageable pageable);

}
