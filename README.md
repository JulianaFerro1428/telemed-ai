# TeleMed IA — MVP

## 🔗 Repository

[TeleMed IA — MVP](https://github.com/JulianaFerro1428/telemed-ai)

## Telemedicine Platform with Intelligent Agent

TeleMed IA is an academic telemedicine project developed for the **Distributed Systems – Group 2** course.

This repository contains the **Minimum Viable Product (MVP)** corresponding to the **first-cut delivery**. The MVP represents a portion of the complete TeleMed IA project and aims to demonstrate a set of priority functionalities within the scope defined for this first delivery.

> **Important:** This repository corresponds only to the MVP. It does not represent the entirety of the final project.

---

## 👥 Team Members

- **María Sofia Aljure Herrera**
- **María Juliana Ferro Bonilla**
- **José Miguel Vera Garzón**

**Course:** Distributed Systems – Group 2
**Instructor:** Gonzalez Bonilla Jesus Ariel
**Program:** Systems Engineering
**Institution:** Corporación Universitaria del Huila – Corhuila
**Code:** 82739
**First cut — 2026-2**

---

## 📌 About the MVP

The MVP was developed as a first functional version of TeleMed IA, selecting a subset of the functionalities proposed for the final project.

The complete project envisions a progressive evolution toward a distributed architecture based on microservices. For this first delivery, the MVP uses a **modular monolith**, keeping internal responsibilities separated to facilitate its future evolution.

The proposed relationship is:

```text
MVP
  ↓
Modular monolith
  ↓
Separation by domains
  ↓
Microservices
  ↓
TeleMed IA final project
```

## 🎯 Objective

Demonstrate a functional healthcare management flow through an integrated frontend and backend, including authentication, user management, professionals, profiles, and appointments.

## ✨ Main Features

The MVP mainly includes:

- Patient registration.
- Login and authentication via JWT.
- Role-based access control.
- Patient profile management.
- Professional management by the administrator.
- Appointment viewing and management.
- Appointment cancellation and rescheduling.
- Professional agenda.
- Medical attention management.
- Clinical summary review.
- Simulated notifications.
- REST API documented via Swagger/OpenAPI.
- Information persistence in PostgreSQL.
- Database migrations via Flyway.

The main implemented roles are:

| Role | Main Functionality |
|------|----------------------|
| PATIENT | Registration, authentication, profile, and appointment management |
| PROFESSIONAL | Agenda, appointment review, and medical attention |
| ADMIN | User and professional management |

## ⚠️ Simulated or Pending Features

Since this is a first-cut MVP, some functionalities of the final project are not yet fully implemented.

**Password recovery**

The password recovery option is visually available in the interface, but it is a simulated functionality.

The actual recovery process via email is not implemented end-to-end.

**Intelligent Agent**

The pre-consultation experience is available within the MVP, but integration with a real artificial intelligence provider is still pending.

**Notifications**

Notifications are handled in a simulated manner. Actual email delivery via SMTP is still pending.

**Other future features**

- Full availability via time slots.
- Generation and download of PDF summaries.
- Automated unit and integration testing.
- Implementation of independent microservices.
- Full evolution of the conversational AI agent.

These features are part of the subsequent evolution of the final project.

## 🏗️ Architecture

The MVP uses a modular monolith architecture.

```
┌──────────────────────────────────────┐
│              FRONTEND                │
│      Angular 17 + Ionic 7            │
│                                      │
│  Patient | Professional | Admin      │
└──────────────────┬───────────────────┘
                   │
                HTTP/REST
                   │
                   ▼
┌──────────────────────────────────────┐
│               BACKEND                │
│        Spring Boot 3.3.13            │
│                                      │
│ Interfaces → Application → Domain    │
│              Infrastructure          │
└──────────────────┬───────────────────┘
                   │
                  JPA
                   │
                   ▼
┌──────────────────────────────────────┐
│           PostgreSQL 16              │
│                                      │
│              Flyway                  │
└──────────────────────────────────────┘
```

The modular structure is based on the domains defined for TeleMed IA, allowing them to later evolve into independent services.  [Documentacion MVP TeleMed](Documentacion_MVP_TeleMed_IA.pdf)

## 🛠️ Main Technologies

### Backend

- Java 17
- Spring Boot 3.3.13
- Spring Security
- JWT
- BCrypt
- JPA / Hibernate
- Maven

### Frontend

- Angular 17+
- Ionic 7+
- Capacitor
- RxJS
- Reactive Forms
- SCSS

### Database and Tools

- PostgreSQL 16
- Flyway
- OpenAPI / Swagger
- Docker
- Docker Compose
- Git / GitHub

The technical documentation contains the full detail of the technologies and tools used. [Documentacion MVP TeleMed](Documentacion_MVP_TeleMed_IA.pdf)

## 📁 Repository Structure

```
telemed-ai/
│
├── telemedai-backend/
│   └── Spring Boot Backend
│
├── telemedai-frontend/
│   └── Angular + Ionic Frontend
│
├── docker-compose.yml
├── .env
└── README.md
```

The project is mainly divided into two applications: backend and frontend.
- [Backend](telemedai-backend)
- [Frontend](telemedai-frontend)


## 🚀 Running with Docker

The project includes configuration via Docker Compose to facilitate running the MVP.

### Requirements

- Docker
- Docker Compose

### Clone the repository

```bash
git clone https://github.com/JulianaFerro1428/telemed-ai.git
cd telemed-ai
```

### Run

```bash
docker compose up --build
```

Once the services are started, the system will be available locally according to the project configuration.

### Main Services

| Service | Port |
|---------|------|
| Frontend | 4200 |
| Backend | 8080 |
| PostgreSQL | 5432 |

## 🔌 API

The backend exposes a REST API used by the frontend.

**API base**
`http://localhost:8080/api`

**Swagger UI**
`http://localhost:8080/swagger-ui/index.html`

Swagger allows you to view and execute the main available endpoints of the MVP.

## 🗄️ Database

The MVP uses PostgreSQL 16 as the database management system and Flyway to control schema creation and evolution through versioned migrations.

[Documentacion MVP TeleMed](Documentacion_MVP_TeleMed_IA.pdf)

Among the main entities are:

- Users
- Patients
- Professionals
- Agendas
- Availability
- Appointments
- Conversations
- Messages
- Summaries
- Notifications
- Authentication tokens

## 🔐 Security

Authentication uses Spring Security and JWT, with differentiation of roles:

- PATIENT
- PROFESSIONAL
- ADMIN

The frontend adapts the available functionalities according to the authenticated role, and the backend manages the corresponding authentication and authorization.

## 🧪 Evidence

The MVP's functionality was verified through:

- Backend execution.
- PostgreSQL.
- Flyway migrations.
- Swagger UI.
- Patient registration.
- Authentication.
- Administrative management.
- Professional registration and access.
- Information persistence.

The corresponding evidence and screenshots are developed in the MVP's technical documentation.

## 📚 Documentation

The complete technical documentation of the MVP can be found in the document:

*Technical Documentation of the MVP – TeleMed IA*

This README presents only the main project information. The technical documentation contains the details of the scope, requirements, user stories, architecture, domains, database, API, testing, evidence, limitations, and future work.

The complete technical documentation of this MVP is available here:

📄 [Technical Documentation — TeleMed IA MVP](Documentacion_MVP_TeleMed_IA.pdf)

For the broader TeleMed IA project documentation:

🔗 [TeleMed IA — Project Documentation](https://github.com/code-corhuila/telemed-ia-docs)

## 🔮 Future Work

The MVP constitutes the foundation for continuing the development of the TeleMed IA project.

Among the main lines of evolution are:

- Full integration of the conversational AI agent.
- Integration of AI with pre-consultation and appointment management.
- Implementation of email communication.
- PDF document generation.
- Expansion of automated and security testing.
- Improvement of notifications and reminders.
- Progressive incorporation of the remaining functionalities.
- Evolution of the modular monolith toward a distributed architecture.
- Separation of domains into independent microservices.

## 📌 Project Status

- **Version:** MVP — First cut
- **Status:** Functional academic delivery
- **Current architecture:** Modular monolith
- **Planned architecture:** Microservices
- **Project:** TeleMed IA

## 📄 License

Project developed for academic purposes for the Distributed Systems – Group 2 course at Corporación Universitaria del Huila – Corhuila.