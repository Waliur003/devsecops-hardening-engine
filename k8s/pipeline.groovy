pipeline {
    agent any

    environment {
        IMAGE_NAME = 'sun003/secure-backend'
        IMAGE_TAG  = 'v1.0'
        REGISTRY   = 'docker.io'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build Container Image') {
            steps {
                dir('app') {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Static Security Scan (Trivy Config)') {
            steps {
                script {
                    echo 'Scanning Kubernetes Manifests for Misconfigurations...'
                    sh "trivy config ./k8s/deployment.yaml --exit-code 1 --severity HIGH,CRITICAL"
                }
            }
        }

        stage('Container Image Security Scan (Trivy Image)') {
            steps {
                script {
                    echo 'Scanning Built Container Image for Vulnerabilities...'
                    sh "trivy image ${IMAGE_NAME}:${IMAGE_TAG} --exit-code 1 --severity HIGH,CRITICAL"
                }
            }
        }

        stage('Deploy Hardened Manifests to Kubernetes') {
            steps {
                script {
                    echo 'Applying Hardened Infrastructure Manifests...'
                    sh "kubectl apply -f ./k8s/rbac.yaml"
                    sh "kubectl apply -f ./k8s/network-policy.yaml"
                    sh "kubectl apply -f ./k8s/deployment.yaml"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'DevSecOps Hardening Pipeline Executed Successfully!'
        }
        failure {
            echo 'Pipeline Failed: Security Gate Policy Violation Intercepted!'
        }
    }
}