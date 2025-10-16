// src/main/java/com/kaiburr/taskapi/TaskRepository.java
package com.kaiburr.task_api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    // Spring Data automatically implements this method based on its name.
    // It will find all Task documents where the 'name' field contains the given string.
    List<Task> findByNameContaining(String name); // [cite: 74, 75]
}