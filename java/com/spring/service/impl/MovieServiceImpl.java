package com.spring.service.impl;

import java.util.List;

import com.spring.model.User;
import com.spring.repository.MovieRepository;
import com.spring.model.Movie;
import com.spring.service.MovieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    @Override
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    @Override
    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    @Override
    public Movie getMovieById(Long id) throws Exception {
        return movieRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Movie id: " + id));
    }

    @Override
    public List<Movie> findMoviesByUser(User user) {
       return movieRepository.findMoviesByUser(user);
    }

    @Override
    public Page<Movie> findMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection, String searchKey){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if(searchKey.equals("")){
            return this.movieRepository.findAll(pageable);
        }else {
            return movieRepository.findMoviesByNameContainingIgnoreCaseOrGenreContainingIgnoreCase(searchKey,searchKey,pageable);

        }

    }

    @Override
    public Page<Movie> findFinancingSearchMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.movieRepository.findMoviesByStatusEquals("Ищет финансирования", pageable);
    }

    @Override
    public Page<Movie> findProductionMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.movieRepository.findMoviesByStatusEquals("В процессе", pageable);
    }

    @Override
    public Page<Movie> findEndedMovieWithPagination(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.movieRepository.findMoviesByStatusEquals("Готов", pageable);
    }

}
