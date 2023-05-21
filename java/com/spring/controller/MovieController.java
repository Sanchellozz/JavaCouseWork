package com.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spring.model.*;
import com.spring.repository.UserRepository;
import com.spring.security.MyUserDetails;
import com.spring.service.*;
import com.spring.utility.FileUpload;
import com.spring.utility.constants.ImageType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private UserRepository userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PostService postService;

    @RequestMapping("/movies")
    public String getHomeMovies(Model model, @RequestParam(defaultValue = "") String searchKey) {
        return getMoviesWithPagination(1,"rating", "asc", searchKey, model);
    }

    @RequestMapping("/movies/financing_search")
    public String getFinancingSearchMovies(Model model) {
        return getFinancingSearchMoviesWithPagination(1,"rating", "asc", model);
    }

    @RequestMapping("/movies/production_stage")
    public String getProductionMovies(Model model) {
        return getProductionMoviesWithPagination(1,"rating", "asc", model);
    }

    @RequestMapping("/movies/graduated_films")
    public String getEndedMovies(Model model) {
        return getEndedMoviesWithPagination(1,"rating", "asc", model);
    }

    @RequestMapping("/movies/{id}")
    public String getMoviePreview(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("id") long id, Model model) {
        try {
            User user = authService.profile(principal);
            Movie movie = movieService.getMovieById(id);
            double z = 0, n = 0, req = 0, tot = 0;
            List<Review> list = movie.getReviews();
            for (Review r : list) {
                z = z + r.getMovieRating();
                n = z / list.size();
            }
            List<Post> posts = postService.getPostsByMovieId(id);
            for (Post p : posts) {
                req = req + p.getMoneyRequired();
                tot = tot + p.getMoneyTotal();
            }
            movie.setRating(n);
            model.addAttribute("total", tot);
            model.addAttribute("required", req);
            model.addAttribute("movie", movie);
            model.addAttribute("currentUser", user);
            model.addAttribute("new_review", new Review());
            movieService.saveMovie(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/movie/movie_preview";
    }

//    @RequestMapping("/admin/movies")
//    public String getMovies(Model model) {
//        List<Movie> movies = movieService.getMovies();
//        model.addAttribute("movies", movies);
//        return "admin/movie/movies";
//    }

    @RequestMapping("/admin/movies")
    public String getAdminMovies(Model model, @RequestParam(defaultValue = "") String searchKey) {
        return getAdminMoviesWithPagination(1,"rating", "asc", searchKey, model);
    }


    @RequestMapping("/admin/userMovies")
    public String getUserMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        User user = authService.profile(principal);
        List<Movie> movies = movieService.findMoviesByUser(user);
        model.addAttribute("movies", movies);
        return "admin/movie/movies";
    }

    @RequestMapping(value = "/admin/movies/add", method = RequestMethod.GET)
    public String addMovieForm(@ModelAttribute("movie") Movie movie, Model model) {
        List<Producer> producers = producerService.getProducers();
        model.addAttribute("producers", producers);
        return "admin/movie/movie_form";
    }

    @RequestMapping(value = "/admin/movies/add", method = RequestMethod.POST)
    public String addMovie(@AuthenticationPrincipal MyUserDetails principal, @RequestParam(value = "file") MultipartFile file, @Valid Movie movie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            List<Producer> producers = producerService.getProducers();
            model.addAttribute("producers", producers);
            return "admin/movie/movie_form";
        }
        try {
            User user = authService.profile(principal);
            String path = FileUpload.saveImage(ImageType.MOVIE_POSTER, movie.getName(), file);
            movie.setPoster(path);
            movie.setUser(user);
            movieService.saveMovie(movie);
        } catch (Exception e) {
            return "admin/movie/movie_form";
        }
        return "redirect:/admin/userMovies";
    }

    @RequestMapping(value = "/admin/movies/edit/{id}", method = RequestMethod.GET)
    public String updateMovieForm(@PathVariable("id") long id, Model model) {
        List<Producer> producers = producerService.getProducers();
        model.addAttribute("producers", producers);
        try {
            Movie movie = movieService.getMovieById(id);
            model.addAttribute("movieForm", movie);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return "admin/movie/movie_update";
    }

    @RequestMapping(value = "/admin/movies/edit/{id}", method = RequestMethod.POST)
    public String updateMovie(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("id") long id, Movie movie, BindingResult result, Model model,
                              @RequestParam(value = "file", required = false) MultipartFile file) {
        if (result.hasErrors()) {
            return "redirect:/admin/movie/movie_form";
        }
        try {
            if (!file.isEmpty()) {
                String path = FileUpload.saveImage(ImageType.MOVIE_POSTER, movie.getName(), file);
                movie.setPoster(path);
            } else {
                movie.setPoster(movieService.getMovieById(id).getPoster());
            }
            User user = authService.profile(principal);
            movie.setUser(user);
            movieService.saveMovie(movie);
        } catch (Exception e) {
            return "admin/movie/movie_form";
        }
        return "redirect:/admin/userMovies";
    }

    @RequestMapping("/admin/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") long id, Model model) {
        try {
            Movie movie = movieService.getMovieById(id);
            Set<User> fav = movie.getFavouriteMovieUsers();
            Set<User> wat = movie.getWatchlistUsers();
            List<Review> reviews = movie.getReviews();
            Set <User> users;
            for (Review r: reviews) {
                users = r.getLikedReviewsUsers();
                for (User u: users) {
                    u.removeLikedReviews(r.getReviewId());
                    reviewService.deleteReview(r.getReviewId());
                }
            }
            for (User u:fav) {
                u.removeMovieFromFavourite(id);
            }
            for (User u:wat) {
                u.removeMovieFromWatchlist(id);
            }

            movieService.deleteMovie(movie);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return "redirect:/admin/movies/";
    }

    @RequestMapping(value = "/movies/{movie_id}/add-to-watchlist", method = RequestMethod.GET)
    public String addMovieToWatchlist(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.addMovieToWatchlist(movie);
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/{movie_id}/remove-from-watchlist", method = RequestMethod.GET)
    public String removeMovieFromWatchlist(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.removeMovieFromWatchlist(movie.getId());
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/watch-listed-movies/clear-all", method = RequestMethod.GET)
    public String clearWatchList(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            user.setWatchListedMovies(new HashSet<>());
            userService.save(user);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/watch-listed-movies", method = RequestMethod.GET)
    public String showWatchListedMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "watchlist";
    }

    @RequestMapping(value = "/movies/{movie_id}/add-to-favourite", method = RequestMethod.GET)
    public String addMovieToFavourite(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.addMovieToFavourite(movie);
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }


    @RequestMapping(value = "/movies/{movie_id}/remove-from-favourite", method = RequestMethod.GET)
    public String removeMovieFromFavourite(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.removeMovieFromFavourite(movie.getId());
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }

    @RequestMapping(value = "/movies/favourite-movies/clear-all", method = RequestMethod.GET)
    public String clearFavouriteMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            user.setFavouriteMovies(new HashSet<>());
            userService.save(user);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }

    @RequestMapping(value = "/movies/favourite-movies", method = RequestMethod.GET)
    public String showFavouriteMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            Set<Movie> movies = user.getFavouriteMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "favourite";
    }

    @RequestMapping("/user_reviews/")
    public String getUserReviews(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        User user = authService.profile(principal);
        Set<Review>  reviews = user.getReview();
        model.addAttribute("reviews", reviews);
        return "/user/movie/review/user_reviews";
    }

    @GetMapping("/plantNamesAutocomplete")
    @ResponseBody
    public List<LabelValue> plantNamesAutocomplete(@RequestParam(value="term", required = false, defaultValue="") String term, Model model) {
        List<LabelValue> allProducerNames = new ArrayList<LabelValue>();
        List<Producer> producers = producerService.getProducersByName(term);
        for (Producer producer: producers){
            LabelValue labelValue = new LabelValue();
            labelValue.setLabel(producer.getName());
            labelValue.setValue(producer.getId());
            allProducerNames.add(labelValue);
        }
        return allProducerNames;
    }

    @RequestMapping("/admin/page/{pageNo}")
    public String getAdminMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDir") String sortDir,
                                          @RequestParam(defaultValue = "") String searchKey,
                                          Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findMovieWithPagination(pageNo,pageSize,sortField,sortDir,searchKey);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");
        model.addAttribute("searchKey", searchKey);

        if(sortField.equals("rating") && sortDir.equals("asc")){
            model.addAttribute("ratAsk", true);
        }else if(sortField.equals("rating") && sortDir.equals("desc")){
            model.addAttribute("ratDesk", true);
        }

        if(sortField.equals("name") && sortDir.equals("asc")){
            model.addAttribute("nameAsk", true);
        }else if(sortField.equals("name") && sortDir.equals("desc")){
            model.addAttribute("nameDesk", true);
        }

        if(sortField.equals("releaseDate") && sortDir.equals("asc")){
            model.addAttribute("dateAsk", true);
        }else if(sortField.equals("releaseDate") && sortDir.equals("desc")){
            model.addAttribute("dateDesk", true);
        }

        if(sortField.equals("genre") && sortDir.equals("asc")){
            model.addAttribute("genreAsk", true);
        }else if(sortField.equals("genre") && sortDir.equals("desc")){
            model.addAttribute("genreDesk", true);
        }

        model.addAttribute("movies", movies);
        return "admin/movie/movies";
    }

    @RequestMapping("/page/{pageNo}")
    public String getMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDir") String sortDir,
                                          @RequestParam(defaultValue = "") String searchKey,
                                          Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findMovieWithPagination(pageNo,pageSize,sortField,sortDir,searchKey);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");
        model.addAttribute("searchKey", searchKey);

        if(sortField.equals("rating") && sortDir.equals("asc")){
            model.addAttribute("ratAsk", true);
        }else if(sortField.equals("rating") && sortDir.equals("desc")){
            model.addAttribute("ratDesk", true);
        }

        if(sortField.equals("name") && sortDir.equals("asc")){
            model.addAttribute("nameAsk", true);
        }else if(sortField.equals("name") && sortDir.equals("desc")){
            model.addAttribute("nameDesk", true);
        }

        if(sortField.equals("releaseDate") && sortDir.equals("asc")){
            model.addAttribute("dateAsk", true);
        }else if(sortField.equals("releaseDate") && sortDir.equals("desc")){
            model.addAttribute("dateDesk", true);
        }

        if(sortField.equals("genre") && sortDir.equals("asc")){
            model.addAttribute("genreAsk", true);
        }else if(sortField.equals("genre") && sortDir.equals("desc")){
            model.addAttribute("genreDesk", true);
        }

        model.addAttribute("movies", movies);
        return "user/home";
    }

    @RequestMapping("/page2/{pageNo}")
    public String getFinancingSearchMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDir") String sortDir,
                                          Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findFinancingSearchMovieWithPagination(pageNo,pageSize,sortField,sortDir);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");

        if(sortField.equals("rating") && sortDir.equals("asc")){
            model.addAttribute("ratAsk", true);
        }else if(sortField.equals("rating") && sortDir.equals("desc")){
            model.addAttribute("ratDesk", true);
        }

        if(sortField.equals("name") && sortDir.equals("asc")){
            model.addAttribute("nameAsk", true);
        }else if(sortField.equals("name") && sortDir.equals("desc")){
            model.addAttribute("nameDesk", true);
        }

        if(sortField.equals("releaseDate") && sortDir.equals("asc")){
            model.addAttribute("dateAsk", true);
        }else if(sortField.equals("releaseDate") && sortDir.equals("desc")){
            model.addAttribute("dateDesk", true);
        }

        if(sortField.equals("genre") && sortDir.equals("asc")){
            model.addAttribute("genreAsk", true);
        }else if(sortField.equals("genre") && sortDir.equals("desc")){
            model.addAttribute("genreDesk", true);
        }

        model.addAttribute("movies", movies);
        return "user/financing_search_movies";
    }

    @RequestMapping("/page3/{pageNo}")
    public String getProductionMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDir") String sortDir,
                                          Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findProductionMovieWithPagination(pageNo,pageSize,sortField,sortDir);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");

        if(sortField.equals("rating") && sortDir.equals("asc")){
            model.addAttribute("ratAsk", true);
        }else if(sortField.equals("rating") && sortDir.equals("desc")){
            model.addAttribute("ratDesk", true);
        }

        if(sortField.equals("name") && sortDir.equals("asc")){
            model.addAttribute("nameAsk", true);
        }else if(sortField.equals("name") && sortDir.equals("desc")){
            model.addAttribute("nameDesk", true);
        }

        if(sortField.equals("releaseDate") && sortDir.equals("asc")){
            model.addAttribute("dateAsk", true);
        }else if(sortField.equals("releaseDate") && sortDir.equals("desc")){
            model.addAttribute("dateDesk", true);
        }

        if(sortField.equals("genre") && sortDir.equals("asc")){
            model.addAttribute("genreAsk", true);
        }else if(sortField.equals("genre") && sortDir.equals("desc")){
            model.addAttribute("genreDesk", true);
        }


        model.addAttribute("movies", movies);
        return "user/production_movies";
    }

    @RequestMapping("/page1/{pageNo}")
    public String getEndedMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                                    @RequestParam("sortField") String sortField,
                                                    @RequestParam("sortDir") String sortDir,
                                                    Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findEndedMovieWithPagination(pageNo,pageSize,sortField,sortDir);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");

        if(sortField.equals("rating") && sortDir.equals("asc")){
            model.addAttribute("ratAsk", true);
        }else if(sortField.equals("rating") && sortDir.equals("desc")){
            model.addAttribute("ratDesk", true);
        }

        if(sortField.equals("name") && sortDir.equals("asc")){
            model.addAttribute("nameAsk", true);
        }else if(sortField.equals("name") && sortDir.equals("desc")){
            model.addAttribute("nameDesk", true);
        }

        if(sortField.equals("releaseDate") && sortDir.equals("asc")){
            model.addAttribute("dateAsk", true);
        }else if(sortField.equals("releaseDate") && sortDir.equals("desc")){
            model.addAttribute("dateDesk", true);
        }

        if(sortField.equals("genre") && sortDir.equals("asc")){
            model.addAttribute("genreAsk", true);
        }else if(sortField.equals("genre") && sortDir.equals("desc")){
            model.addAttribute("genreDesk", true);
        }


        model.addAttribute("movies", movies);
        return "user/ended_movies";
    }


}
