# Gaming Hub Backend

This repository contains the backend for the Gaming Hub platform, a sports facility booking and player-matching service for India.

## Features implemented in Phase 1
- Java 21 + Spring Boot 3.3.x project setup
- PostgreSQL + Flyway migration setup
- User, sport, venue, venue owner, and facility core entities
- JWT security foundation
- Swagger/OpenAPI setup
- Health endpoint and API skeleton
- Initial sports seed data

## Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 16+

## Local setup
1. Create a PostgreSQL database named `gaminghub`.
2. Copy `.env.example` to `.env` and update secrets.
3. Run:
   ```bash
   mvn clean package
   java -jar target/gaming-hub-backend-0.0.1-SNAPSHOT.jar
   ```
4. Browse Swagger UI: `http://localhost:8080/swagger-ui.html`

## Environment variables
See `.env.example`.

## Major modules to be implemented next
- Google OAuth login
- Email OTP auth
- JWT refresh and logout
- Venue search and availability
- Booking and payment system
- Game sessions and invitations
- Admin analytics and reviews
