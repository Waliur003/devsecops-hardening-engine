pipeline {
    agent any
    stages {
        stage('Static Application Security Testing') {
            steps {
                echo 'Auditing Kubernetes manifests for security compliance misconfigurations...'
                // Scan manifests directory using Trivy tool execution commands
                sh 'trivy config ./k8s'
            }
        }
        stage('Container Image Scan') {
            steps {
                echo 'Auditing container image layers for known vulnerability exploits...'
                sh 'trivy image yourdockerhubusername/secure-backend:v1.0'
            }
        }
        stage('Least-Privilege Cluster Deployment') {
            steps {
                echo 'Applying hardened manifests to zero-trust cluster topologies...'
                sh 'kubectl apply -f k8s/rbac.yaml'
                sh 'kubectl apply -f k8s/network-policy.yaml'
                sh 'kubectl apply -f k8s/deployment.yaml'
            }
        }
    }
}