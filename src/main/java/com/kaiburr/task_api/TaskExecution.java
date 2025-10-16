// src/main/java/com/kaiburr/taskapi/TaskExecution.java
package com.kaiburr.task_api;

import java.util.Date; // <-- ADD THIS IMPORT

import com.fasterxml.jackson.annotation.JsonFormat;

public class TaskExecution {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "IST")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "IST")
    private Date endTime;
    
    private String output;

    // Getters and Setters... (no changes needed here)
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
}