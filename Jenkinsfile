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
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven 'M3'
        jdk 'Java17'
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
                script {
                    sh "docker system prune -a"
                    sh "docker-compose build"
                }
            }
        }

        //Tag and push image to Amazon ECR
        stage('Tag and push image to Amazon ECR') {
            steps {
                withEnv(["AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}", "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}", "AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION}"]) {
                    script {
                        sh 'docker login -u AWS -p $(aws ecr get-login-password --region ${AWS_DEFAULT_REGION}) ${AWS_ECR_URL}'
                        sh 'docker tag ${DOCKER_USERNAME}/api-github:latest ${AWS_ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}'
                        sh 'docker tag ${DOCKER_USERNAME}/api-github:latest ${AWS_ECR_IMAGE_REPO_URL}:latest'
                        sh 'docker push ${AWS_ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}'
                        sh 'docker push ${AWS_ECR_IMAGE_REPO_URL}:latest'
                    }
                }
            }
        }

        //Deploy Amazon ECS task definition
        stage('Deploy Amazon ECS task definition') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-access']]) {
                        sh 'aws ecs describe-task-definition --task-definition ${AWS_TASK_DEFINITION_NAME} --query taskDefinition > ./ecs/task-definition.json'
                        def ecsTaskDefinition = readJSON file: AWS_ECS_TASK_DEFINITION_PATH, returnPojo: true
                        echo "Revision Number TaskDefinition: ${ecsTaskDefinition.revision}"
                        //sh 'aws ecs update-service --cluster ${AWS_CLUSTER_NAME} --service ${AWS_SERVICE_NAME} --task-definition ${AWS_TASK_DEFINITION_NAME}:${ecsTaskDefinition.revision} --desired-count 1'
                    }
                }
            }
        }

        //Push to Docker Hub Container registry
        stage('Push to Docker Hub Container registry') {
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
