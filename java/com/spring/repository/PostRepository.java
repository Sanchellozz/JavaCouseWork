package com.spring.repository;

import com.spring.model.Post;
import com.spring.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public List<Post> findByMovie_IdOrderByMoneyRequiredDesc(Long id);

    public List<Post> findAllByMovie_IdAndStatusTrueOrderByMoneyRequiredDesc(Long id);

    public Post findByPostId(Long id);

    public List<Post> findAllByMovie_IdAndStatusFalseOrderByMoneyRequiredDesc(Long id);

    public List<Post> getAllByStatusFalse();

    public List<Post> getAllByStatusTrue();

}
