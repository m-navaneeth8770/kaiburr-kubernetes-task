package com.kaiburr.task_api;

// NEW IMPORTS FOR KUBERNETES CLIENT
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import java.util.concurrent.TimeUnit;
// ---

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            Optional<Task> task = taskRepository.findById(id);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with ID " + id + " not found.");
            }
        } else {
            List<Task> tasks = taskRepository.findAll();
            return ResponseEntity.ok(tasks);
        }
    }

    @GetMapping("/findByName")
    public ResponseEntity<List<Task>> findTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findByNameContaining(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @PutMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // TODO: Add command validation as required by the assessment
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with ID " + id + " not found.");
        }
        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task with ID " + id + " deleted successfully.");
    }

    // This method now creates a Kubernetes pod to run the command.
    @PutMapping("/{id}/executions")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with ID " + id + " not found.");
        }
    
        Task task = taskOptional.get();
        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());
    
        // Use a try-with-resources block to ensure the client is properly closed.
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            // Create a unique name for the pod for this specific execution.
            final String podName = "task-runner-" + task.getId() + "-" + System.currentTimeMillis();
    
            // Define the pod using the fluent PodBuilder API.
            Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withName(podName)
                .endMetadata()
                .withNewSpec()
                    .withRestartPolicy("Never") 
                    .addNewContainer()
                        .withName("task-runner-container")
                        .withImage("busybox")
                        .withCommand("sh", "-c", task.getCommand())
                    .endContainer()
                .endSpec()
                .build();
    
            // Create the pod in the 'default' namespace in the cluster.
            client.pods().inNamespace("default").create(pod);
    
            // Wait for the pod to complete (either Succeeded or Failed).
            client.pods().inNamespace("default").withName(podName).waitUntilCondition(
                p -> p != null && ("Succeeded".equals(p.getStatus().getPhase()) || "Failed".equals(p.getStatus().getPhase())),
                5, TimeUnit.MINUTES);
    
            // Retrieve the logs from the completed pod to get the command's output.
            String podLogs = client.pods().inNamespace("default").withName(podName).getLog();
            execution.setOutput(podLogs);
    
            // Clean up by deleting the pod from the cluster.
            client.pods().inNamespace("default").withName(podName).delete();
    
        } catch (Exception e) {
            execution.setOutput("Failed to execute in Kubernetes pod: " + e.getMessage());
            e.printStackTrace();
        }
    
        execution.setEndTime(new Date());
    
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }
        task.getTaskExecutions().add(execution);
        Task updatedTask = taskRepository.save(task);
    
        return ResponseEntity.ok(updatedTask);
    }
}