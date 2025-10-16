// src/main/java/com/kaiburr/taskapi/Task.java
package com.kaiburr.task_api;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks") // Maps this class to the "tasks" collection in MongoDB
public class Task {

    @Id
    private String id; // This will be the unique identifier

    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions;

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public List<TaskExecution> getTaskExecutions() {
        return taskExecutions;
    }
    public void setTaskExecutions(List<TaskExecution> taskExecutions) {
        this.taskExecutions = taskExecutions;
    }
}