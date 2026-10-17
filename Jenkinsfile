// Alternativa a GitHub Actions: el mismo pipeline en Jenkins (requiere JDK 25 y Maven configurados
// en "Global Tool Configuration" con los nombres jdk25 y maven3, Node 22 y Docker en el agente).
pipeline {
    agent any
    tools {
        jdk 'jdk25'
        maven 'maven3'
    }
    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Compilar y probar') {
            steps { sh 'mvn -B -ntp clean verify' }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/*/target/surefire-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true,
                            artifacts: 'backend/*/target/site/jacoco/**, backend/catalog-service/target/optimizacion-catalogo.md'
                }
            }
        }
        stage('Frontend Angular') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm test'
                    sh 'npm run build'
                }
            }
            post {
                success { archiveArtifacts artifacts: 'frontend/dist/frontend/browser/**' }
            }
        }
        stage('Imágenes Docker') {
            when { branch 'main' }
            steps {
                script {
                    def servicios = ['discovery-server', 'api-gateway', 'auth-service', 'catalog-service', 'sales-service',
                                     'design-service', 'production-service', 'inventory-service',
                                     'notification-service', 'report-service']
                    servicios.each { s ->
                        sh "docker build --build-arg SERVICE=${s} -t sigip/${s}:${env.BUILD_NUMBER} ."
                    }
                    sh "docker build -t sigip/frontend:${env.BUILD_NUMBER} frontend"
                }
            }
        }
    }
}
