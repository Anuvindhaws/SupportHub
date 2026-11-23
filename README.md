# SupportHub - Enterprise HelpDesk Platform

A real-time IT HelpDesk application built using Spring Boot, Angular, and AWS.

## Project Features

- User authentication with JWT
- Role-based access control
- Create and manage support tickets
- Ticket assignment to technicians
- Ticket priority and status management
- Real-time ticket notifications
- Admin dashboard and reports

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- REST APIs
- WebSocket

### Frontend
- Angular
- TypeScript
- HTML
- CSS

### Database
- PostgreSQL
- Flyway

### Testing
- JUnit 5
- Mockito
- Testcontainers
- Playwright

### DevOps
- Git
- GitHub Actions
- Docker
- AWS
- Terraform
- Amazon ECR
- CloudWatch
- Prometheus
- Grafana

## Environments

1. Local Development
2. Testing / Staging
3. Production

## CI/CD Workflow

Developer -> GitHub -> GitHub Actions -> Automated Tests -> Docker Build -> Amazon ECR -> Test Deployment -> Approval -> Production Deployment

## Project Structure

- backend: Spring Boot application
- frontend: Angular application
- infrastructure: Docker and Terraform configurations
- tests: API and end-to-end tests
- docs: Project documentation
- .github/workflows: GitHub Actions pipelines

## Author

DevOps Engineering Project
