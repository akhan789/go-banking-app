# Go Banking Application

This project is developed in Java as part of the Payter Technical Assessment. It simulates a modular banking system with services for account management, balance operations, interest management, and audit logging. The architecture is cleanly separated by modules to promote scalability and maintainability and as much of the SOLID principles have been attempted as time permitted.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Modules](#modules)
- [Getting Started](#getting-started)
- [Technologies Used](#technologies-used)

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