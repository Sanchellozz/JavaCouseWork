package com.spring.service;

import com.spring.model.Post;
import com.spring.model.Producer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {
    void savePost(Post post);

    List<Post> getPosts();

    List<Post> getPostsByMovieId(Long id);

    List<Post> getPostsByMovieIdAndStatusTrue(Long id);

    public Post findByPostId(Long id);

    public List<Post> getPostsByMovieIdAndStatusFalse(Long id);

    void deletePost(Post post);

}