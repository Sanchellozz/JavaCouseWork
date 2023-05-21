package com.spring.controller;

import com.spring.model.Movie;
import com.spring.model.Post;
import com.spring.model.Producer;
import com.spring.model.User;
import com.spring.security.MyUserDetails;
import com.spring.service.AuthService;
import com.spring.service.MovieService;
import com.spring.service.PostService;
import com.spring.utility.FileUpload;
import com.spring.utility.constants.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    MovieService movieService;
    @Autowired
    private AuthService authService;

//    @GetMapping({"/movies/posts/{movie_id}"})
//    public String getPosts(@PathVariable(value = "movie_id") Long movie_id, Model model) {
//        model.addAttribute("posts", postService.getPostsByMovieId(movie_id));
//        return "user/movie/posts";
//    }

    @GetMapping({"/movies/posts/{movie_id}"})
    public String getPosts(@AuthenticationPrincipal MyUserDetails principal, @PathVariable(value = "movie_id") Long movie_id, Model model) {
        try {
            User user = authService.profile(principal);
            Movie movie = movieService.getMovieById(movie_id);
            model.addAttribute("posts", postService.getPostsByMovieIdAndStatusTrue(movie_id));
            model.addAttribute("currentUser", user);
            model.addAttribute("movieUser", movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/movie/posts";
    }

    @GetMapping({"/movies/postsfalse/{movie_id}"})
    public String getPostsFalse(@AuthenticationPrincipal MyUserDetails principal, @PathVariable(value = "movie_id") Long movie_id, Model model) {
        try {
            User user = authService.profile(principal);
            Movie movie = movieService.getMovieById(movie_id);
            System.out.println(user.getId() + " " + movie.getUser().getId());
            model.addAttribute("posts", postService.getPostsByMovieIdAndStatusFalse(movie_id));
            model.addAttribute("currentUser", user);
            model.addAttribute("movieUser", movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/movie/posts_false";
    }

    @GetMapping("/movies/posts/add/{movie_id}")
    public String addPostForm(@PathVariable(value = "movie_id") Long movie_id, @ModelAttribute("post") Post post) {
        return "user/movie/posts_form";
    }

    @PostMapping("/movies/posts/add/{movie_id}")
    public String addPost(@PathVariable(value = "movie_id") Long movie_id, @Valid Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "user/movie/posts_form";
        }
        try {
                post.setMovie(movieService.getMovieById(movie_id));
                post.setMoneyTotal(0);
                post.setStatus(true);
                postService.savePost(post);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "user/movie/posts_form";
        }
        return  "redirect:/movies/posts/{movie_id}";
    }

    @RequestMapping(value = "/movies/posts/{movie_id}/financing/{post_id}", method = RequestMethod.POST)
    public String financingPost(@AuthenticationPrincipal MyUserDetails principal, @PathVariable(value = "movie_id") Long movie_id, @PathVariable(value = "post_id") Long post_id, @RequestParam(value = "finance", required = false) double finance, Model model ) throws Exception {
        model.addAttribute("finance", finance);
        System.out.println(finance);
        Post post = postService.findByPostId(post_id);
        double money = post.getMoneyTotal();
        if(finance > post.getMoneyRequired()){
            finance = post.getMoneyRequired() - post.getMoneyTotal();
        }
        User user = authService.profile(principal);
        if(user.getMoney() < finance){
            model.addAttribute("error",true);
            return  "redirect:/movies/posts/{movie_id}";
        }
        user.setMoney(user.getMoney() - finance);
        post.setMoneyTotal(money + finance);
        if(post.getMoneyTotal() >= post.getMoneyRequired()){
            post.setStatus(false);
            Movie movie = movieService.getMovieById(movie_id);
            User movieUser = movie.getUser();
            movieUser.setMoney(movieUser.getMoney() + post.getMoneyTotal());
            System.out.println(movieUser.getMoney());
        }
        postService.savePost(post);
        return  "redirect:/movies/posts/{movie_id}";
    }


}