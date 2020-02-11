pipeline {
 environment {
   
   jobBaseName = "${env.JOB_NAME}".split("/").getAt(1..1).toString().replaceAll('\\[','').replaceAll('\\]','')
 }
 options {
  timeout(time: 10, unit: 'MINUTES') 
  } //option

  agent none
  stages {

   stage("Build and Test") {
     when {
       not {
         branch 'master'
       }
     }
     agent {
      docker {
        image 'cschockaert/docker-npm-maven:latest'
        args '-v m2_repos:/root/.m2'
                } //docker
            } // agent

            stages {

             stage('Build') {
               steps {
                echo "${jobBaseName}"
                 //sh 'mvn -f Development/pom.xml install'
                 echo "Building ChatServer"
                 sh 'mvn -f Development/pom.xml compile'
               }
   } // build
   stage('SonarQube') {
     when {
       not {
         branch 'master'
       }
     }
     steps {
      withSonarQubeEnv('SonarQube') {
        sh 'mvn -f Development/pom.xml clean install'
        sh 'mvn -f Development/pom.xml sonar:sonar -Dsonar.projectKey=${jobBaseName} -Dsonar.projectName=${jobBaseName}'
      }

      sh 'sleep 30'
      timeout(time: 10, unit: 'SECONDS') {
       retry(5) {
        script {
          def qg = waitForQualityGate()
          if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
          }
        }
      }
    }
  } //steps
} //SONAR
}
} //stage 
} // STAGES

 post {      
     success {
            slackSend (baseUrl: "https://neufse.slack.com/services/hooks/jenkins-ci/", token: "", channel: "#team-1-f19", color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME}")
            }
   failure {  
         slackSend (baseUrl: "https://neufse.slack.com/services/hooks/jenkins-ci/", token: "", channel: "#team-1-f19", color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME}")
         }
   }
} //pipeline
