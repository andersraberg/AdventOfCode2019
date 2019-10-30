node {
    git 'https://github.com/andersraberg/AdventOfCode2019.git'
    stage('Build') {
        sh './gradlew build'
    }
    
    stage('Sonar') {
        withSonarQubeEnv() {
            sh './gradlew sonarqube -Dsonar.projectKey=AdventofCode2019'
        }
    }

    stage('Run') {
        sh './gradlew run'
    }
    
    stage('Report') {
        junit 'unitTests'
    }
}
