package com.spring.service;

import java.util.List;


import com.spring.model.Producer;
import org.springframework.stereotype.Service;

@Service
public interface ProducerService {
    void saveProducer(Producer producer);

    public List<Producer> getProducersByName(String name);

    List<Producer> getProducers();

    void deleteProducer(Producer producer);

    Producer getProducerById(Long id) throws Exception;

}
