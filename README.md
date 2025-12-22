# LibTrack

A modern library management system with a Spring Boot REST API and React SPA frontend.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 11, Spring Boot 2.7.18, Spring Security, Spring Data JPA |
| Frontend | React 18, Vite, React Router 6, Bootstrap 5, Axios |
| Database | MySQL 8.0 (Flyway migrations) |
| Auth | JWT (stateless, HMAC-SHA256) |
| Build | Maven (backend), npm (frontend) |
| Deployment | Docker Compose (3 services) |

## Architecture

```
┌──────────┐       ┌──────────┐       ┌──────────┐
│  React   │ ────> │  Spring  │ ────> │  MySQL   │
│  :3000   │  JWT  │  Boot    │  JPA  │  :3306   │
└──────────┘       │  :8080   │       └──────────┘
                   └──────────┘
                        │
                   ┌────┴────┐
                   │  Email  │
                   │  SMTP   │
                   └─────────┘
```

- Frontend proxies `/api` and `/uploads` to the backend during development
- Auth state managed via `AuthContext` with JWT stored in `localStorage`
- Backend uses Flyway for database versioning (`ddl-auto=validate`)

## Features

### Member
- Register / Login / Forgot & reset password via email token
- Browse all books, filter by genre, search (title/author/genre/ISBN)
- View book details with stock, cover image, ratings, and recommendations
- Borrow books (respects role-based borrowing limits)
- Renew borrowed books (extends due date by 14 days)
- View active loans and borrowing history
- Pay fines online
- Add/remove wishlist items, borrow from wishlist
- Submit book purchase requests, track status
- Write reviews and star ratings
- Export transaction history to CSV

### Admin / Librarian
- Dashboard with stats: total books, members, active loans, pending requests, unpaid fines
- Full book CRUD with cover image upload
- Member management: list all members with pagination, enable/disable accounts
- Book request management: approve or reject purchase requests
- Audit log viewer with pagination
- Configurable borrowing limits per role (max books, loan duration days)
- Export all books to CSV, export all transactions to CSV
- Popular books listing (by average rating)

## API Reference

### Auth
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | - | Create account |
| POST | `/api/auth/login` | - | Login, returns JWT |
| POST | `/api/auth/forgot-password` | - | Send reset email |
| POST | `/api/auth/reset-password` | - | Reset password with token |

### Books
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/books` | - | List all books |
| GET | `/api/books/{isbn}` | - | Book details |
| GET | `/api/books/search?title=&author=&genre=&isbn=` | - | Search |
| GET | `/api/books/genre/{genre}` | - | Filter by genre |
| GET | `/api/books/popular` | - | Top rated books |
| GET | `/api/books/{isbn}/recommendations` | - | Book recommendations |
| POST | `/api/books` | Admin | Create book |
| PUT | `/api/books/{isbn}` | Admin | Update book |
| DELETE | `/api/books/{isbn}` | Admin | Delete book |

### Genres
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/genres` | - | List distinct genres |

### Transactions
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/transactions/my` | User | Current user's transactions |
| GET | `/api/transactions/my/active` | User | Active loans only |
| POST | `/api/transactions/borrow` | User | Borrow a book |
| POST | `/api/transactions/return` | Admin | Return a book |
| POST | `/api/transactions/{id}/renew` | User | Renew a loan |
| POST | `/api/transactions/{id}/pay-fine` | User | Pay fine |
| GET | `/api/transactions` | Admin | All transactions |
| GET | `/api/transactions/overdue` | Admin | Overdue transactions |
| GET | `/api/transactions/member/{id}` | Admin | By member |

### Reviews
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/reviews/book/{isbn}` | - | Reviews for a book |
| POST | `/api/reviews` | User | Add review |

### Wishlist
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/wishlist` | User | Get wishlist |
| POST | `/api/wishlist` | User | Add to wishlist |
| DELETE | `/api/wishlist/{isbn}` | User | Remove from wishlist |

### Book Requests
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/book-requests` | User | My requests |
| POST | `/api/book-requests` | User | Submit request |

### Profile
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/profile` | User | Get profile |
| POST | `/api/profile/change-password` | User | Change password |
| POST | `/api/profile/update-email` | User | Update email |

### Admin
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/admin/stats` | Admin | Dashboard stats |
| GET | `/api/admin/members?page=&size=` | Admin | List members |
| GET | `/api/admin/members/count` | Admin | Member count |
| POST | `/api/admin/members/{id}/toggle-status` | Admin | Enable/disable |
| GET | `/api/admin/book-requests?status=` | Admin | All requests |
| POST | `/api/admin/book-requests/{id}/approve` | Admin | Approve request |
| POST | `/api/admin/book-requests/{id}/reject` | Admin | Reject request |
| GET | `/api/admin/audit-logs?page=&size=` | Admin | Audit log |
| GET | `/api/admin/audit-logs/count` | Admin | Log count |
| GET | `/api/admin/borrowing-limits` | Admin | List limits |
| PUT | `/api/admin/borrowing-limits/{id}` | Admin | Update limit |
| GET | `/api/admin/popular-books` | Admin | Popular books |

### Export
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/export/books` | User | Download books CSV |
| GET | `/api/export/transactions` | Admin | Download all transactions CSV |
| GET | `/api/export/transactions/my` | User | Download my transactions CSV |

### Other
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/health` | - | Health check |
| POST | `/api/upload/cover/{isbn}` | Admin | Upload book cover |

## Quick Start

### Docker (recommended)
```bash
docker compose up --build
```
- Backend API: `http://localhost:8080`
- Frontend UI: `http://localhost:3000`

### Manual setup

**1. Database**
```sql
CREATE DATABASE libtrack;
```

**2. Backend**
```bash
cd backend
mvn clean package
java -jar target/libtrack-api.jar
```

**3. Frontend**
```bash
cd frontend
npm install
npm run dev
```

### Seed accounts
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@libtrack.app | password |
| Member | member@libtrack.app | password |

## Environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/libtrack?...` | JDBC URL |
| `DB_USERNAME` | `root` | DB user |
| `DB_PASSWORD` | `amith` | DB password |
| `JWT_SECRET` | (embedded dev secret) | HMAC key for JWT |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | - | SMTP username |
| `MAIL_PASSWORD` | - | SMTP password |

Set via shell or a `.env` file in `backend/`.

## Schedulers

The `NotificationSchedulerService` runs two background tasks:

| Task | Interval | Description |
|------|----------|-------------|
| Overdue check | Every 24h | Emails members about overdue books (deduped once per 24h) |
| Reservation check | Every 6h | Notifies members when reserved books are available |

## Database migrations

Managed by Flyway in `backend/src/main/resources/db/migration/`:

| File | Description |
|------|-------------|
| `V1__init.sql` | Core schema: members, books, transactions, reviews, etc. |
| `V2__add_overdue_notifications.sql` | Tracks sent overdue notices for dedup |

## Build & test

```bash
# Backend
cd backend
mvn clean verify            # build + test
mvn test                    # tests only

# Frontend
cd frontend
npm run build               # production build
npm run dev                 # dev server
```
