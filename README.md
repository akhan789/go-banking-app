# Go Banking Application

This project is developed in Java as part of the Payter Technical Assessment. It simulates a modular banking system with services for account management, balance operations, interest management, and audit logging. The architecture is cleanly separated by modules to promote scalability and maintainability and as much of the SOLID principles have been attempted as time permitted.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Modules](#modules)
- [Getting Started](#getting-started)
- [Technologies Used](#technologies-used)
- [Request/Response Examples](#requestresponse-examples)

---

## 🧾 Overview

The **Go Banking Application** models essential features of a core banking system. It includes standalone Java services that communicate over HTTP to simulate real-world service interactions. It also provides a Swing-based GUI for interacting with the system in a desktop environment.

---

## 🚀 Features

- 🏦 **Account Management:** Open, close, suspend, and reactivate accounts.
- 💰 **Balance Operations:** Deposit, withdraw, and transfer funds between accounts.
- 📈 **Interest Management:** Apply interest rules to account balances.
- 📝 **Audit Logging:** Track operations and transaction histories.
- 🧭 **Service Gateway:** Acts as a routing layer for all inter-service communication.
- 🖥️ **Swing UI:** Desktop interface for interacting with the banking app.

---

## 🧩 Modules

Each module in the repository has a distinct responsibility:

| Module             | Description                                               |
|--------------------|-----------------------------------------------------------|
| `account-service`  | Manages account creation, updates, suspensions, closures. |
| `balance-service`  | Handles deposits, withdrawals, and transfers.             |
| `interest-service` | Applies interest calculations to eligible accounts.       |
| `audit-service`    | Logs all operations and transactions.                     |
| `service-gateway`  | Central entry point and dispatcher for HTTP requests.     |
| `swing-ui`         | Java Swing-based desktop user interface.                  |
| `common`           | Shared utilities and data models.                         |

---

## 🧪 Technologies Used

- **Java 17+**
- **Maven** – Project build & dependency management
- **Java Swing** – GUI development
- **Custom HTTP Services** – Lightweight communication layer (no frameworks)
- **Modular Architecture** – Clean separation of service responsibilities

> 💡 *This application is designed for simulation and demonstration purposes. It does not persist data and is not production-ready.*

---

## 📡 Request/Response Examples

Below are examples of HTTP requests and responses for key operations in the Go Banking Application. Each example includes the request details, response payload, and corresponding audit log entry (if applicable). All requests use the header `X-API-Key: default_api_key` for authentication.

### Account Management
#### Create Account
- **Request**:
  ```plaintext
  POST http://localhost:8000/accountmanagement
  Content-Type: application/json
  Body: {"accountName":"test","balance":1.0,"currency":"GBP"}
  
- **Response**:
  ```json
  {
    "id": 1,
    "accountId": "0090eb02",
    "toAccountId": null,
    "amount": 1.0,
    "type": "CREDIT",
    "timestamp": "2025-04-09T13:16:53.0736848",
    "relatedBalanceOperationId": 0
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:29:14.693: CREATE] Created account: 24a766e4

#### Suspend Account
- **Request**:
  ```plaintext
  PUT http://localhost:8000/accountmanagement/0090eb02/suspend
  Content-Type: application/json
  Body: <no body>

- **Response**:
  ```json
  {
    "id": 9,
    "accountId": "0090eb02",
    "accountName": null,
    "balance": 1,
    "status": "SUSPENDED",
    "currency": "GBP",
    "creationTime": "2025-04-09T13:05:55.347",
    "statusHistory": ["SUSPENDED"]
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:32:17: UPDATE] Account id: 24a766e4 status has been updated to SUSPENDED

#### Reactivate Account
- **Request**:
  ```plaintext
  PUT http://localhost:8000/accountmanagement/0090eb02/reactivate
  Content-Type: application/json
  Body: <no body>
  
- **Response**:
  ```json
  {
    "id": 9,
    "accountId": "0090eb02",
    "accountName": null,
    "balance": 1,
    "status": "ACTIVE",
    "currency": "GBP",
    "creationTime": "2025-04-09T13:05:55.347",
    "statusHistory": ["SUSPENDED", "ACTIVE"]
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:32:37.354: UPDATE] Account id: 24a766e4 status has been updated to ACTIVE

#### Close Account
- **Request**:
  ```plaintext
  PUT http://localhost:8000/accountmanagement/0090eb02/close
  Content-Type: application/json
  Body: <no body>
  
- **Response**:
  ```json
  {
    "id": 9,
    "accountId": "0090eb02",
    "accountName": null,
    "balance": 1,
    "status": "CLOSED",
    "currency": "GBP",
    "creationTime": "2025-04-09T13:05:55.347",
    "statusHistory": ["SUSPENDED", "ACTIVE", "CLOSED"]
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:32:52.283: UPDATE] Account id: 24a766e4 status has been updated to CLOSED

### Balance Operations
#### Get Account Balance
- **Request**:
  ```plaintext
  GET http://localhost:8000/balanceoperations/balance/0090eb02
  Content-Type: application/json
  
- **Response**:
  ```json
  {
    "balance": 1
  }

- **Audit Log**:
  ```plaintext
  Not logged.

#### Credit Account
- **Request**:
  ```plaintext
  POST http://localhost:8000/balanceoperations/credit
  Content-Type: application/json
  Body: {"accountId":"0090eb02","amount":1.0}

- **Response**:
  ```json
  {
    "id": 1,
    "accountId": "0090eb02",
    "toAccountId": null,
    "amount": 1.0,
    "type": "CREDIT",
    "timestamp": "2025-04-09T13:16:53.0736848",
    "relatedBalanceOperationId": 0
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:33:21.624: UPDATE] Credit funds to account id: 24a766e4, amount: 1.0
  [2025-04-09T13:33:21.631: CREATE] Balance Operation CREDIT saved, details: Account ID: 24a766e4, amount: 1.0

#### Debit Account
- **Request**:
  ```plaintext
  POST http://localhost:8000/balanceoperations/debit
  Content-Type: application/json
  Body: {"accountId":"0090eb02","amount":1.0}
  
- **Response**:
  ```json
  {
  "id": 2,
    "accountId": "0090eb02",
    "toAccountId": null,
    "amount": 1.0,
    "type": "DEBIT",
    "timestamp": "2025-04-09T13:18:03.0279654",
    "relatedBalanceOperationId": 0
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:33:56.667: UPDATE] Debited funds from account id: 24a766e4, amount: 1.0
  [2025-04-09T13:33:56.672: CREATE] Balance Operation DEBIT saved, details: Account ID: 24a766e4, amount: 1.0

#### Transfer from Account to Account
- **Request**:
  ```plaintext
  POST http://localhost:8000/balanceoperations/transfer
  Content-Type: application/json
  Body: {"fromAccountId":"0090eb02","toAccountId":"7cd78caa","amount":1.0}
  
- **Response**:
  ```json
  {
    "id": 3,
    "accountId": "0090eb02",
    "toAccountId": "7cd78caa",
    "amount": 1.0,
    "type": "TRANSFER",
    "timestamp": "2025-04-09T13:19:25.2387212",
    "relatedBalanceOperationId": 4
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:34:51.654: UPDATE] Debited funds from account id: 24a766e4, amount: 1.0
[2025-04-09T13:34:51.665: UPDATE] Credit funds to account id: ad75b1c7, amount: 1.0
[2025-04-09T13:34:51.669: UPDATE] Balance Operation TRANSFER saved, Transferred funds from account id: 24a766e4 to account id: ad75b1c7, amount: 1.0

### Interest Management
#### Configure Interest Daily Rate
- **Request**:
  ```plaintext
  POST http://localhost:8000/interestmanagement/rate
  Content-Type: application/json
  Body: {"dailyRate":5.0,"calculationFrequency":null}

- **Response**:
  ```json
  {
    "id": 4,
    "dailyRate": 5.0,
    "calculationFrequency": "WEEKLY",
    "createdAt": "1971-01-01T00:00:00"
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:35:15.276: UPDATE] Interest management updated: dailyRate=5.0

#### Configure Interest Calculation Frequency
- **Request**:
  ```plaintext
  POST http://localhost:8000/interestmanagement/calculationfrequency
  Content-Type: application/json
  Body: {"dailyRate":null,"calculationFrequency":"MONTHLY"}
  
- **Response**:
  ```json
  {
    "id": 5,
    "dailyRate": 5.0,
    "calculationFrequency": "MONTHLY",
    "createdAt": "1971-01-01T00:00:00"
  }

- **Audit Log**:
  ```plaintext
  [2025-04-09T13:35:31.626: UPDATE] Interest management updated: calculationFrequency=DAILY