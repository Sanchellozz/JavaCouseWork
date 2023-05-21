package com.spring.service;

import java.util.List;

import com.spring.model.Movie;

import com.spring.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface MovieService {
    void saveMovie(Movie movie);

    List<Movie> getMovies();

    void deleteMovie(Movie movie);

    Movie getMovieById(Long id) throws Exception;

    public List<Movie> findMoviesByUser(User user);

    Page<Movie> findMovieWithPagination(int offset, int pageSize, String sortField, String sortDirection, String search);

    public Page<Movie> findFinancingSearchMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection);


    public Page<Movie> findProductionMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection);

    public Page<Movie> findEndedMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection);

}
