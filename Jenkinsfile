node {
    git 'https://github.com/andersraberg/AdventOfCode2019.git'
    stage('Build') {
        sh './gradlew clean build'
    }
    
    stage('Sonar') {
        withSonarQubeEnv() {
            sh './gradlew sonarqube -Dsonar.projectKey=AdventofCode2019'
        }
    }

    stage('Report') {
        junit 'build/test-results/**/*.xml'
    }

    stage('Run') {
        sh './gradlew run'
    }
    
}
