package com.spring.service.impl;

import com.spring.model.Post;
import com.spring.model.Producer;
import com.spring.repository.PostRepository;
import com.spring.repository.ProducerRepository;
import com.spring.service.PostService;
import com.spring.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public void savePost(Post post) {
        postRepository.save(post);

    }


    @Override
    public List<Post> getPosts() {
        return postRepository.findAll();
    }


    @Override
    public List<Post> getPostsByMovieId(Long id) {
        return postRepository.findByMovie_IdOrderByMoneyRequiredDesc(id);
    }

    @Override
    public List<Post> getPostsByMovieIdAndStatusTrue(Long id) {
        return postRepository.findAllByMovie_IdAndStatusTrueOrderByMoneyRequiredDesc(id);
    }

    @Override
    public Post findByPostId(Long id) {
        return postRepository.findByPostId(id);
    }

    @Override
    public List<Post> getPostsByMovieIdAndStatusFalse(Long id) {
        return postRepository.findAllByMovie_IdAndStatusFalseOrderByMoneyRequiredDesc(id);
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

}