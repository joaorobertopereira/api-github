pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '1'))
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
                        sh 'docker tag ${DOCKER_USERNAME}/api-github:latest ${AWS_ECR_IMAGE_REPO_URL}:latest'
                        sh 'docker push ${AWS_ECR_IMAGE_REPO_URL}:latest'
                    }
                }
            }
        }


        stage('Create ECS task definition') {
            steps {
                script {
                    def task_definition = """{
                        \"family\": \"${AWS_TASK_DEFINITION_NAME}\",
                        \"containerDefinitions\": [
                            {
                                \"name\": \"${AWS_CONTAINER_NAME}\",
                                \"image\": \"${AWS_ECR_IMAGE_REPO_URL}:latest\",
                                \"cpu\": \"0\",
                                \"memory\": \"512\",
                                \"memoryReservation\": \"512\",

                                \"portMappings\": [
                                    {
                                        \"containerPort\": ${CONTAINER_PORT},
                                        \"hostPort\": ${CONTAINER_PORT},
                                        \"protocol\": \"tcp\"
                                    }
                                ]
                                \"essential\": true,
                                \"environment\": [],
                                \"mountPoints\": [],
                                \"volumesFrom\": []
                            }
                        ],
                        \"requiresCompatibilities\": [
                            \"EC2\"
                        ],
                        \"cpu\": \"256\",
                        \"memory\": \"512\"
                    }"""
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-access']]) {
                        def task_definition_arn = sh(
                            script: """
                            echo '${task_definition}' > /tmp/task.json
                            aws ecs register-task-definition \
                                --cli-input-json file:///tmp/task.json \
                                --query 'taskDefinition.taskDefinitionArn' \
                                --output text""",
                            returnStdout: true
                        ).trim()

                        env.TASK_DEFINITION_ARN = task_definition_arn
                    }
                }
            }
        }

        stage('Run ECS task on EC2') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-access']]) {
                        try {
                            def task_definition_arn = sh(
                                script: """
                                aws ecs register-task-definition \
                                    --family ${AWS_TASK_DEFINITION_NAME} \
                                    --requires-compatibilities EC2 \
                                    --cpu '256' \
                                    --memory '512' \
                                    --container-definitions '[
                                        {
                                            \"name\": \"${AWS_CONTAINER_NAME}\",
                                            \"image\": \"${AWS_ECR_IMAGE_REPO_URL}:latest\",
                                            \"essential\": true,
                                            \"portMappings\": [
                                                {
                                                    \"containerPort\": ${CONTAINER_PORT},
                                                    \"hostPort\": ${CONTAINER_PORT},
                                                    \"protocol\": \"tcp\"
                                            }
                                        ],
                                        \"logConfiguration\": {
                                            \"logDriver\": \"awslogs\",
                                            \"options\": {
                                                \"awslogs-group\": \"ecs-logs\",
                                                \"awslogs-region\": \"${AWS_DEFAULT_REGION}\",
                                                \"awslogs-stream-prefix\": \"my-container\"
                                            }
                                        }
                                    }
                                ]' \
                                --output text \
                                --query 'taskDefinition.taskDefinitionArn'""",
                            returnStdout: true
                        ).trim()

                            echo "Task definition created: ${task_definition_arn}"

                            // Create a EC2 task
                            def task_response = sh(
                                script: """
                                aws ecs run-task \
                                    --cluster ${AWS_CLUSTER_NAME} \
                                    --launch-type EC2 \
                                    --task-definition ${task_definition_arn} \
                                    --output json""",
                                returnStdout: true
                            ).trim()

                            def task_id = sh(
                                script: "echo '${task_response}' | jq -r '.tasks[0].taskArn' | cut -d/ -f2",
                                returnStdout: true
                            ).trim()

                            echo "EC2 task started: ${task_id}"

                            // Wait for the task to start running
                            timeout(time: 5, unit: 'MINUTES') {
                                def task_status = sh(
                                    script: """
                                    aws ecs describe-tasks \
                                        --cluster ${AWS_CLUSTER_NAME} \
                                        --tasks ${task_id} \
                                        --query 'tasks[0].lastStatus' \
                                        --output text""",
                                    returnStdout: true
                                ).trim()

                                if (task_status != 'RUNNING') {
                                    error "EC2 task failed to start: ${task_status}"
                                }
                            }

                            // Get the public IP address of the task
                            def task_response_json = readJSON text: task_response
                            def eni_id = task_response_json.tasks[0].attachments[0].details.find { it.name == 'networkInterfaceId' }?.value
                            def public_ip = sh(
                                script: """
                                aws ec2 describe-network-interfaces \
                                    --network-interface-ids ${eni_id} \
                                    --query 'NetworkInterfaces[0].Association.PublicIp' \
                                    --output text""",
                                returnStdout: true
                            ).trim()

                            echo "Task public IP: ${public_ip}"

                            /* Commented out: Terminate the task

                                // Terminate the task
                            sh(
                                script: """
                                aws ecs stop-task \
                                    --cluster ${AWS_CLUSTER_NAME} \
                                    --task ${task_id} \
                                    --output text \
                                    --query 'task.taskArn'""",
                                returnStdout: true
                            ).trim()

                            echo "EC2 task terminated: ${task_id}"
                            */

                        } catch (Exception e) {
                            error "Failed to run EC2 task: ${e.message}"
                        }
                    }
                }
            }
        }

//         //Deploy Amazon ECS task definition
//         stage('Deploy Amazon ECS task definition') {
//             steps {
//                 script {
//                     withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-access']]) {
//                         sh 'aws ecs describe-task-definition --task-definition ${AWS_TASK_DEFINITION_NAME} --query taskDefinition > ./ecs/task-definition.json'
//                         def ecsTaskDefinition = readJSON file: AWS_ECS_TASK_DEFINITION_PATH, returnPojo: true
//                         echo "Revision Number TaskDefinition: ${ecsTaskDefinition.revision}"
//                         //sh 'aws ecs update-service --cluster ${AWS_CLUSTER_NAME} --service ${AWS_SERVICE_NAME} --task-definition ${AWS_TASK_DEFINITION_NAME}:${ecsTaskDefinition.revision} --desired-count 1'
//                     }
//                 }
//             }
//         }

        //Push to Docker Hub Container registry
        stage('Push to Docker Hub Container registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-access', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                    sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
                    sh "docker push ${DOCKER_USERNAME}/api-github:latest"
                }
            }
        }
    }
}
