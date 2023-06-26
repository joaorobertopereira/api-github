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
                withCredentials([string(credentialsId: 'api-github-aws', variable: 'ECR_IMAGE_REPO_URL')]) {
                    withAWS(region: "${AWS_DEFAULT_REGION}", credentials: 'api-github-aws') {
                        sh "docker tag ${DOCKER_USERNAME}/api-github:latest ${ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}"
                        sh "docker tag ${DOCKER_USERNAME}/api-github:latest ${ECR_IMAGE_REPO_URL}:latest"

                        sh "docker push ${ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}"
                        sh "docker push ${ECR_IMAGE_REPO_URL}:latest"
                    }
                }
            }
        }

        //Deploy Amazon ECS task definition
        stage('Deploy Amazon ECS task definition') {
            steps {
                withCredentials([string(credentialsId: 'AWS_EXECUTION_ROL_SECRET', variable: 'AWS_ECS_EXECUTION_ROL'),string(credentialsId: 'AWS_REPOSITORY_URL_SECRET', variable: 'AWS_ECR_URL')]) {
                    script {
                        updateContainerDefinitionJsonWithImageVersion()
                        //sh("aws ecs register-task-definition --region ${AWS_ECR_REGION} --family ${AWS_ECS_TASK_DEFINITION} --execution-role-arn ${AWS_ECS_EXECUTION_ROL} --requires-compatibilities ${AWS_ECS_COMPATIBILITY} --network-mode ${AWS_ECS_NETWORK_MODE} --cpu ${AWS_ECS_CPU} --memory ${AWS_ECS_MEMORY} --container-definitions file://${AWS_ECS_TASK_DEFINITION_PATH}")
                        def taskRevision = sh(script: "aws ecs describe-task-definition --task-definition ${TASK_DEFINITION} --query taskDefinition | egrep \"revision\" | tr \"/\" \" \" | awk '{print \$2}' | sed 's/\"\$//'", returnStdout: true)
                        sh("aws ecs update-service --cluster ${CLUSTER_NAME} --service ${SERVICE_NAME} --task-definition ${TASK_DEFINITION}:${taskRevision}")
                    }
                }
            }
        }
        //Push to Docker Hub Container registry
        stage('Push to Docker Hub Container registry') {
            agent any
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
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
    containerDefinitionJson[0]['image'] = "${ECR_IMAGE_REPO_URL}:${BUILD_NUMBER}".inspect()
    echo "task definiton json: ${containerDefinitionJson}"
    writeJSON file: AWS_ECS_TASK_DEFINITION_PATH, json: containerDefinitionJson
}