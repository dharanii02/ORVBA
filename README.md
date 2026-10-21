# ORVBA – On Road Vehicle Breakdown Assistance

A full-stack web application for managing roadside vehicle breakdown assistance, mechanic coordination, repair quotations, and service progress.

## About the Project

ORVBA (On Road Vehicle Breakdown Assistance) is a web-based application designed to help users obtain assistance during unexpected vehicle breakdowns. It connects users with administrator-approved mechanics and provides a structured workflow for managing service requests, repair quotations, and service progress.

The application provides separate functionalities for users, mechanics, and administrators. It supports vehicle management, mechanic verification, service request processing, diagnosis and quotation management, pricing control, billing information, payment status management, and feedback.

## Key Features

### User Module
- User registration and login.
- Add and manage vehicle details.
- Create roadside assistance service requests.
- Track service progress through status updates.
- Review and approve or reject repair quotations.
- View billing information and payment status.
- Submit feedback after service completion.

### Mechanic Module
- Register and submit mechanic details for administrator approval.
- Access mechanic functionalities after approval.
- Manage availability.
- View and accept service requests.
- Update service progress, including travel, arrival, and completion statuses.
- Submit vehicle diagnoses and repair quotations.

### Administrator Module
- Approve or reject mechanic registrations.
- Manage service details and pricing limits.
- Review quotations exceeding configured maximum prices.
- Manage service-related information.

### Authentication and Security
- Spring Security.
- JWT-based authentication.
- Role-based authorization for users, mechanics, and administrators.
- Restricted access to role-specific operations.

## Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript, Bootstrap |
| Backend | Java 21, Spring Boot 3.5.7 |
| API Communication | REST APIs |
| Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, JSON Web Token (JWT) |
| Database | MySQL |
| Build Tool | Apache Maven |

## System Architecture

ORVBA follows a client-server architecture. The frontend provides interfaces for users, mechanics, and administrators. The Spring Boot backend processes requests, implements business logic, enforces access control, and exposes REST APIs. Spring Data JPA and Hibernate handle database persistence operations, while MySQL stores application data.

## Service Workflow

1. The user registers and logs in to the application.
2. The user adds vehicle details and creates a breakdown service request.
3. An administrator-approved mechanic views and accepts the request.
4. The mechanic updates the service status while travelling to and arriving at the service location.
5. The mechanic inspects the vehicle and submits a diagnosis and repair quotation.
6. The user approves or rejects the proposed quotation.
7. Quotations exceeding configured maximum prices can undergo administrative review according to the application's workflow.
8. After completing the repair, the mechanic updates the service status.
9. Billing information, payment status, and user feedback are managed through the application.

## Prerequisites

Install the following software before running the application:

- **Java Development Kit (JDK) 21**
- **Apache Maven**
- **MySQL Server**
- **Modern web browser**
- **Java IDE (optional):** IntelliJ IDEA, Eclipse, or Spring Tool Suite

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/dharanii02/ORVBA.git
cd ORVBA
```

### 2. Configure the Application

- Ensure MySQL is running in your local environment.
- Create and configure the database required by the application.
- Configure the necessary application settings locally.
- Keep database credentials and JWT secrets private and out of version control.

### 3. Run the Backend

Open a terminal in the backend application's directory and execute:

```bash
mvn spring-boot:run
```

Alternatively, open the backend project in your Java IDE and run the main Spring Boot application class.

### 4. Run the Frontend

Open the frontend using a local development server or browser, depending on the project's configuration.

Ensure the frontend API endpoints point to the running backend.

### 5. Access the Application

Open the frontend in your browser and register or log in to access the functionalities available for your role.

*The exact local URL, port, and frontend startup method depend on your application configuration.*

## Configuration

Before running the application, verify that:

- JDK 21 is configured in your IDE and terminal.
- Maven dependencies are resolved successfully.
- MySQL is running and the required database is configured.
- Local application settings are correct.
- Frontend API endpoints match the backend address.
- Sensitive credentials and secrets are excluded from GitHub.

**Security:** Never publish database passwords, JWT secrets, access tokens, or other sensitive configuration values in a public repository.

## Testing

The following functional areas can be tested:

- **Authentication:** Registration, login, and role-based access.
- **Mechanic Management:** Registration, administrator approval, and availability.
- **Vehicle Management:** Adding and updating vehicle details.
- **Service Requests:** Request creation, mechanic acceptance, and status updates.
- **Quotation Management:** Diagnosis submission, quotation approval or rejection, and administrative review.
- **Service and Pricing Management:** Service information and pricing limits.
- **Post-Service Operations:** Billing information, payment status, and feedback.
- **Security:** Restrictions on user, mechanic, and administrator operations.

To run the automated tests configured in the backend project, execute the following command from the backend application directory:

```bash
mvn clean test
```

The results depend on the automated tests implemented in the project.

## Troubleshooting

| Issue | Suggested Solution |
|---|---|
| Java version error | Verify that JDK 21 is selected in the IDE and terminal. |
| Backend startup failure | Check application logs and local configuration. |
| Database connection failure | Verify that MySQL is running and local settings are correct. |
| Frontend API error | Ensure the backend is running and API endpoints are correct. |
| Authentication failure | Check credentials, JWT configuration, and role permissions. |
| Maven build failure | Review dependency resolution and compilation errors. |

## Future Enhancements

Potential future improvements include:

- GPS-based discovery of nearby mechanics.
- Live mechanic location tracking.
- Automated notifications for service status changes.
- Online payment gateway integration.
- A dedicated mobile application.
- Enhanced service history and reporting.

These are proposed enhancements and are not represented as currently implemented features.

## Project Objective

The objective of ORVBA is to simplify roadside assistance by organizing service requests, mechanic coordination, repair quotation approval, and service progress tracking within a single web-based platform. The application aims to improve coordination, pricing transparency, and accountability between users, mechanics, and administrators.


