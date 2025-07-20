# Digital Wallet API

This project is a RESTful web service that allows users to create digital wallets, view balances, deposit or withdraw funds, and securely manage all wallet-related operations.

##  Features

- Role-based access control for Customers and Employees
- JWT-based authentication
- Wallet creation, listing, deposit and withdrawal transactions
- Transaction approval mechanism
- Spring Security-based authorization
- Global exception handling
- Data conversion utilities

---

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- H2 
- JWT (JSON Web Token)
- Lombok
- Maven
- JUnit & Mockito

---

## 📦 Getting Started

### 1. Clone the repository

git clone https://github.com/selinatss/digital-wallet.git
cd digital-wallet

### 2. Run the application

./mvnw spring-boot:run


### 3. Project Structure
```json

src/
├── controller/       -> REST API controllers
├── service/          -> Business logic layer
├── repository/       -> JPA repositories for data access
├── config/           -> Security configuration, JWT setup.
├── model/
│   ├── request/      -> DTOs for incoming requests
│   └── response/     -> DTOs for outgoing responses
├── entity/           -> JPA entity classes (mapped to DB tables)
├── filter/           -> JWT authentication filter
├── utils/            -> Converter classes and helper utilities
└── exception/        -> Global exception handlers
```

### 4. API Documentation
#### 1- POST /api/v1/auth/register

Registers a new user (Customer or Employee) into the system.

**Request Headers:**
Bearer token is required for registration.

**Request Body:**
```json
{
  "userName": "testUser",
  "password": "1234",
  "name": "test",
  "surname": "test",
  "tckn": "12345678901",
  "role": "CUSTOMER"
}
```

**Response Body:**
```json
{
  "customerId": 1,
  "userName": "testUser",
  "name": "test",
  "surname": "test",
  "role": "CUSTOMER",
  "message": "successfully registered"
}
```

#### 2- POST /api/v1/auth/authenticate

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "userName": "testUser",
  "password": "1234"
}
```

**Response Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3- POST /api/v1/wallet/create

Creates a new wallet for a customer.

**Request Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "walletName": "My Wallet",
  "currency": "USD",
  "activeForShopping": true,
  "activeForWithdrawal": true,
  "customerId": "123456"
}
```

**Response Body:**
```json
{
  "id": 1,
  "walletName": "My Wallet",
  "currency": "USD",
  "activeForShopping": true,
  "activeForWithdrawal": true,
  "balance": 0.0,
  "usableBalance": 0.0
}
```

### 4- GET /api/v1/wallet/list/{customerId}

Retrieves all wallets belonging to the specified customer.

**Request Headers:**
- `Authorization: Bearer <token>`

**Path Parameters:**
- `customerId` (Long): The ID of the customer whose wallets you want to retrieve.

**Example Request:**
  GET http://localhost:8080/api/v1/wallet/list/1

**Response Body:**
```json
[
  {
    "id": 1,
    "walletName": "My Wallet",
    "currency": "USD",
    "activeForShopping": true,
    "activeForWithdrawal": true,
    "balance": 100.0,
    "usableBalance": 90.0,
    "customerId": 1
  },
  {
    "id": 2,
    "walletName": "My Wallet2",
    "currency": "EUR",
    "activeForShopping": false,
    "activeForWithdrawal": true,
    "balance": 250.0,
    "usableBalance": 250.0,
    "customerId": 1
  }
]
```
### 5- POST /api/v1/transaction/deposit

Retrieves all wallets belonging to the specified customer.

**Request Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "walletId": "1",
  "amount": "1001.0",
  "oppositePartyType": "PAYMENT"
}
```

**Response Body:**
```json
{
  "id": 1,
  "walletId": 1,
  "transactionType": "DEPOSIT",
  "transactionStatus": "PENDING",
  "usableBalance": 0.00,
  "balance": 1001.00,
  "createdAt": "2025-07-20T21:31:45.5114956"
}
```

### 6- POST /api/v1/transaction/withdraw

Retrieves all wallets belonging to the specified customer.

**Request Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "walletId": "1",
  "amount": "500.0",
  "oppositePartyType": "PAYMENT"
}
```

**Response Body:**
```json
{
  "id": 2,
  "walletId": 1,
  "transactionType": "WITHDRAWAL",
  "transactionStatus": "APPROVED",
  "usableBalance": 501.00,
  "balance": 501.00,
  "createdAt": "2025-07-20T21:40:23.6135437"
}
```

### 7- GET /api/v1/transaction/list/{id}

Retrieves all transactions belonging to the specified wallet.

**Request Headers:**
- `Authorization: Bearer <token>`

**Path Parameters:**
- `walletId` (Long): The ID of the wallet whose transactions you want to retrieve.

**Request Body:**
GET http://localhost:8080/api/v1/wallet/list/1

**Response Body:**
```json
[
  {
    "id": 1,
    "walletId": 1,
    "transactionType": "DEPOSIT",
    "transactionStatus": "PENDING",
    "usableBalance": 0.00,
    "balance": 1001.00,
    "createdAt": "2025-07-20T21:31:45.511496"
  }
]
```

### 6- POST /api/v1/transaction/status

Changes the status of a transaction.

**Request Headers:**
- `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "transactionId": "1",
  "status": "APPROVED"
}
```

**Response Body:**
```json
{
  "id": 1,
  "walletId": 1,
  "transactionType": "DEPOSIT",
  "transactionStatus": "APPROVED",
  "usableBalance": 1001.00,
  "balance": 1001.00,
  "createdAt": "2025-07-20T21:31:45.511496"
}
```

