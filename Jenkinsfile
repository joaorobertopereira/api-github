pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
        timestamps()
    }

    environment {
        AWS_ECS_TASK_DEFINITION_PATH = './ecs/task-definition.json'
        AWS_ECR_IMAGE_REPO_URL = '745703739258.dkr.ecr.us-east-1.amazonaws.com/api-github-ecr'
        AWS_DEFAULT_REGION = 'us-east-1'
        AWS_TASK_DEFINITION_NAME = 'ApiGitHubTaskDefinition'
        AWS_SERVICE_NAME = 'ApiGithubService'
        AWS_CLUSTER_NAME = 'ApiGithubCluster'
        AWS_CONTAINER_NAME = 'ApiGithubContainer'
        DOCKER_USERNAME = 'joaoroberto'
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven 'M3'
        jdk 'Java17'
        dockerTool 'Docker'
    }

    stages {

        //Build Application Jar
        stage('Build Application Jar') {
            steps {
                sh "mvn clean package -DskipTests -f pom.xml"
            }
        }

        //Docker Compose Build
        stage('Docker Compose Build') {
            steps {
                sh 'docker compose build'
            }
        }

        //Tag and push image to Amazon ECR
        stage('Tag and push image to Amazon ECR') {
            agent any
            steps {
                withCredentials([<object of type com.cloudbees.jenkins.plugins.awscredentials.AmazonWebServicesCredentialsBinding>]) {
                    sh "docker tag ${DOCKER_USERNAME}/api-github:latest ${AWS_ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}"
                    sh "docker tag ${DOCKER_USERNAME}/api-github:latest ${AWS_ECR_IMAGE_REPO_URL}:latest"

                    sh "docker push ${AWS_ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}"
                    sh "docker push ${AWS_ECR_IMAGE_REPO_URL}:latest"
                }
            }
        }

        //Deploy Amazon ECS task definition
        stage('Deploy Amazon ECS task definition') {
            steps {
            withCredentials([<object of type com.cloudbees.jenkins.plugins.awscredentials.AmazonWebServicesCredentialsBinding>]) {
                script {
                    updateContainerDefinitionJsonWithImageVersion()
                    def taskRevision = sh(script: "aws ecs describe-task-definition --task-definition ${AWS_TASK_DEFINITION_NAME} --query taskDefinition | egrep \"revision\" | tr \"/\" \" \" | awk '{print \$2}' | sed 's/\"\$//'", returnStdout: true)
                    sh("aws ecs update-service --cluster ${AWS_CLUSTER_NAME} --service ${AWS_SERVICE_NAME} --task-definition ${AWS_TASK_DEFINITION_NAME}:${taskRevision}")
                }
            }
        }

        //Push to Docker Hub Container registry
        stage('Push to Docker Hub Container registry') {
            agent any
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-access', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                    sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"

                    sh "docker tag ${DOCKER_USERNAME}/api-github:latest ${DOCKER_USERNAME}/api-github:${BUILD_NUMBER}"
                    sh "docker push ${DOCKER_USERNAME}/api-github:${BUILD_NUMBER}"
                    sh "docker push ${DOCKER_USERNAME}/api-github:latest"
                }
            }
        }
    }
}

def updateContainerDefinitionJsonWithImageVersion() {
    def containerDefinitionJson = readJSON file: AWS_ECS_TASK_DEFINITION_PATH, returnPojo: true
    containerDefinitionJson[0]['image'] = "${AWS_ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}".inspect()
    echo "task definiton json: ${containerDefinitionJson}"
    writeJSON file: AWS_ECS_TASK_DEFINITION_PATH, json: containerDefinitionJson
}