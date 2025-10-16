# Kaiburr Assessment - Task 2: Kubernetes Deployment & Cloud-Native Execution

## 🚀 Project Overview

This repository demonstrates a complete, cloud-native workflow, taking a Java Spring Boot application from source code to a fully operational deployment on Kubernetes. This project fulfills all requirements for Task 2 of the Kaiburr assessment.

The core achievement of this task is the transformation of the application's "execute" functionality. Instead of running shell commands locally (as in Task 1), the application now functions as a controller within the cluster. It leverages the Kubernetes API to programmatically spin up new, isolated `busybox` pods to run commands on-demand. This showcases a powerful, scalable, and secure pattern for executing containerized jobs.

---

## 🏛️ Architectural Workflow

The final deployment operates on a sophisticated, event-driven workflow:

1.  **API Request:** A user sends a `PUT` request to the `/tasks/{id}/executions` endpoint of the running application.
2.  **Java Application Logic:** The Spring Boot application receives the request.
3.  **Kubernetes API Call:** Using the Fabric8 Kubernetes client library, the application authenticates with the Kubernetes API Server (via its `ServiceAccount`) and issues a command to create a new `Pod`.
4.  **Pod Creation:** The Kubernetes scheduler finds a node and creates a new `busybox` pod based on the application's definition.
5.  **Command Execution:** The new pod starts, executes the shell command specified in the original `Task` object, and then terminates.
6.  **Log Retrieval & Cleanup:** The main application waits for the pod to complete, retrieves its logs (the command's output), and then sends a `delete` command to the Kubernetes API to clean up the completed pod.
7.  **API Response:** The captured output is saved to MongoDB and returned to the user in the final API response.

---

## 🛠️ Technology Stack

* **Application:** Java 17, Spring Boot
* **Containerization:** Docker
* **Orchestration:** Kubernetes (via Minikube)
* **Package Management:** Helm (for MongoDB deployment)
* **Kubernetes Client:** Fabric8 Java Client

---

## 📋 Step-by-Step Deployment Guide

To replicate this deployment, please follow the steps below.

### 1. Prerequisites
Ensure Docker Desktop, Minikube, `kubectl`, and Helm are installed on your system.

### 2. Clone the Repository
```bash
git clone [https://github.com/m-navaneeth8770/kaiburr-kubernetes-task.git](https://github.com/m-navaneeth8770/kaiburr-kubernetes-task.git)
cd kaiburr-kubernetes-task
```

### 3. Start the Kubernetes Cluster
```bash
minikube start
```

### 4. Configure Docker Environment
This crucial step points your local Docker CLI to the Docker daemon inside Minikube, making the image available to the cluster without a remote registry.
```powershell
# For PowerShell
minikube docker-env | Invoke-Expression
```

### 5. Build the Application Image
Build the Docker image that contains the Kubernetes-aware Java application.
```bash
docker build -t m-navaneeth8770/kaiburr-task-app:2.0 .
```

### 6. Deploy MongoDB
Use the Bitnami Helm chart to deploy a production-ready MongoDB instance with persistence enabled.
```bash
helm repo add bitnami [https://charts.bitnami.com/bitnami](https://charts.bitnami.com/bitnami)
helm install mongodb bitnami/mongodb --set persistence.enabled=true
```
Wait for the `mongodb-0` pod to enter the `Running` state (`kubectl get pods`).

### 7. Deploy the Application
Apply the Role-Based Access Control (RBAC) permissions and the application deployment manifests.
```bash
# Apply permissions allowing the app to create other pods
kubectl apply -f rbac.yaml

# Deploy the application, service, and environment variables
kubectl apply -f deployment.yaml
```

---

## ✅ How to Test

1.  **Access the Service:** Open a terminal and run `minikube service kaiburr-app-service --url`. This creates a tunnel and provides the accessible URL. **This terminal must remain open.**
2.  **Use Postman:**
    * **Create a task:** Send a `PUT` request to the `/tasks` endpoint of the provided URL.
    * **Execute the task:** Send a `PUT` request to `/tasks/{id}/executions`. Observe a new `task-runner-...` pod being created and completed by running `kubectl get pods -w` in a separate terminal.

---

## 📸 Evidence of Completion

The following screenshots provide definitive proof that all requirements for Task 2 have been successfully met.

### 1. Successful Deployment Status
This screenshot confirms that both the application and MongoDB pods are running successfully in the Kubernetes cluster.

![Deployment Status](screenshots/screenshot-1-status.png)

### 2. Live Pod Creation in Real-Time
This is the core demonstration of the task's main requirement. The screenshot captures the `task-runner-...` pod being created in the terminal, triggered directly by the Postman API call.

![Live Pod Creation](screenshots/screenshot-2-live-creation.png)

### 3. Final Result with Pod Output
This screenshot shows the successful `200 OK` response from the execution endpoint. The response body includes the output captured from the ephemeral pod's logs, confirming the end-to-end workflow is complete.

![Execution Result](screenshots/screenshot-3-final-result.png)