node {
    stage('Build') {
        dir('AdventOfCode2019') {
            sh './gradlew build'
        }
    }
    
    stage('Sonar') {
        dir('AdventOfCode2019') {
	    withSonarQubeEnv() { // Will pick the global server connection you have configured
                sh './gradlew sonarqube -Dsonar.projectKey=AdventofCode2019'
            }
        }
    }

    stage('Run') {
        dir('AdventOfCode2019') {
            sh './gradlew run'
        }
    }
}
