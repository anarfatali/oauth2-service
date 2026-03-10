# OAuth2 Service

This repository contains a robust backend service built with Spring Boot that provides comprehensive authentication and authorization functionalities. It supports both traditional email/password registration and social login via OAuth2 with providers like Google, GitHub, and Discord. The service uses JSON Web Tokens (JWT) for stateless session management and implements role-based access control.

## Features

*   **Local Authentication**: Standard email and password registration and login.
*   **OAuth2 Social Login**: Seamless integration with Google, GitHub, and Discord.
*   **JWT-Based Security**: Stateless authentication using Access and Refresh tokens.
*   **Role-Based Access Control (RBAC)**: Differentiates between `USER` and `ADMIN` roles with protected endpoints.
*   **Token Management**: Secure sign-out functionality by blacklisting JWTs until they expire. A scheduled job cleans up the expired tokens from the blacklist.
*   **Admin Dashboard**: Endpoints for administrators to manage users, including listing all users, promoting users to admin, and disabling user accounts.
*   **RESTful API**: A well-defined API for user profiles, authentication, and administration.
*   **Custom Exception Handling**: Centralized and consistent error responses for API clients.
*   **API Documentation**: Integrated Swagger UI for easy API exploration and testing.
*   **Data Seeding**: Automatically populates user roles (`ROLE_USER`, `ROLE_ADMIN`) on application startup.

## Technologies Used

*   **Framework**: Spring Boot 3
*   **Language**: Java 17
*   **Security**: Spring Security (Core, OAuth2 Client, JWT)
*   **Database**: Spring Data JPA (Hibernate) with PostgreSQL
*   **Build Tool**: Gradle
*   **API Documentation**: Springdoc (Swagger UI)
*   **Utilities**: Lombok

### Prerequisites

*   JDK 17 or later
*   Gradle 9.2 or compatible
*   PostgreSQL database instance

### Configuration

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/anarfatali/oauth2-service.git
    cd oauth2-service
    ```

2.  **Database Setup:**
    Create a PostgreSQL database named `auth_db`. Update the `spring.datasource` properties in `src/main/resources/application.yaml` with your database credentials if they differ from the defaults.

    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/auth_db
        username: postgres
        password: 1234
    ```

3.  **Environment Variables:**
    This application requires several environment variables for OAuth2 client credentials and JWT configuration. Set the following variables in your environment or IDE run configuration:

    *   `GOOGLE_CLIENT_ID`: Your Google OAuth2 Client ID.
    *   `GOOGLE_CLIENT_SECRET`: Your Google OAuth2 Client Secret.
    *   `GITHUB_CLIENT_ID`: Your GitHub OAuth2 Client ID.
    *   `GITHUB_CLIENT_SECRET`: Your GitHub OAuth2 Client Secret.
    *   `DISCORD_CLIENT_ID`: Your Discord OAuth2 Client ID.
    *   `DISCORD_CLIENT_SECRET`: Your Discord OAuth2 Client Secret.
    *   `JWT_SECRET`: A Base64-encoded secret key for signing JWTs. You can generate one using an online tool.

### Running the Application

You can run the application using the Gradle wrapper:

```bash
./gradlew bootRun
```

The service will start on `http://localhost:8080`.

## API Endpoints

The API is documented using Swagger. Once the application is running, you can access the Swagger UI at:
**`http://localhost:8080/swagger-ui.html`**

### Authentication

*   `POST /api/auth/register`: Register a new user with name, email, and password.
*   `POST /api/auth/login`: Log in with email and password to receive JWTs.
*   `GET /oauth2/authorize/{provider}`: Redirects to the OAuth2 provider's login page (e.g., `google`, `github`, `discord`).
*   `GET /api/auth/me`: Retrieves the profile of the currently authenticated user.
*   `POST /api/auth/signout`: Log out the user by blacklisting the provided access and refresh tokens.

### User

*   `GET /api/user/profile`: A sample protected endpoint for users with `ROLE_USER`.
*   `GET /api/user/list`: A sample protected endpoint for users with `ROLE_ADMIN`.

### Admin

*   `GET /api/admin/users`: (`ROLE_ADMIN`) Lists all registered users.
*   `PATCH /api/admin/users/{id}/promote`: (`ROLE_ADMIN`) Promotes a user to the `ADMIN` role.
*   `PATCH /api/admin/users/{id}/disable`: (`ROLE_ADMIN`) Disables a user's account.
