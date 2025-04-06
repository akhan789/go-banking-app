// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import com.payter.service.accountmanagement.entity.Account;
import com.payter.service.accountmanagement.entity.Account.Currency;
import com.payter.service.accountmanagement.entity.Account.Status;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteAccountManagementRepository implements AccountManagementRepository {

    private final String dbUrl;

    public SQLiteAccountManagementRepository(final String dbUrl) throws SQLException {
        this.dbUrl = dbUrl;
        createTable();
    }

    public void createTable() throws SQLException {
        //@formatter:off
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS account_management (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_id TEXT UNIQUE, " +
                "account_name TEXT, " +
                "balance NUMERIC, " +
                "status TEXT, " +
                "currency TEXT, " +
                "creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status_history TEXT" +
            ")";
        //@formatter:on
        try(Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
        }
        catch(SQLException e) {
            throw new SQLException("Error while creating the account_management table.", e);
        }
    }

    @Override
    public Account save(Account account) throws SQLException {
        //@formatter:off
        String saveQuery =
            "INSERT INTO account_management (" +
                "account_id, " +
                "account_name, " +
                "balance, " +
                "status, " +
                "currency, " +
                "creation_time, " +
                "status_history" +
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        //@formatter:on
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getAccountId());
            stmt.setString(2, account.getAccountName());
            if(account.getBalance() != null) {
                stmt.setBigDecimal(3, account.getBalance());
            }
            else {
                stmt.setNull(3, Types.NUMERIC);
            }
            if(account.getStatus() != null) {
                stmt.setString(4, account.getStatus().name());
            }
            else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if(account.getCurrency() != null) {
                stmt.setString(5, account.getCurrency().name());
            }
            else {
                stmt.setNull(5, Types.VARCHAR);
            }
            if(account.getCreationTime() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(account.getCreationTime()));
            }
            else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            if(account.getStatusHistory() != null && !account.getStatusHistory().isEmpty()) {
                stmt.setString(7, String.join("|", account.getStatusHistory()));
            }
            else {
                stmt.setNull(7, Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("Saving account failed, no rows affected.");
            }

            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    long generatedId = rs.getLong(1);
                    if(generatedId > 0) {
                        account.setId(generatedId);
                    }
                    else {
                        throw new SQLException("Saving account failed, no ID obtained.");
                    }
                }
            }
            return account;
        }
        catch(SQLException e) {
            throw new SQLException("Error saving account: " + e.getMessage(), e);
        }
    }

    @Override
    public Account findByAccountId(String accountId) throws SQLException {
        String findByAccountIdQuery = "SELECT * FROM account_management WHERE account_id = ?";
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(findByAccountIdQuery)) {
            stmt.setString(1, accountId);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    Account account = new Account();
                    account.setId(rs.getLong("id"));
                    account.setAccountId(rs.getString("account_id"));
                    account.setAccountName(rs.getString("account_name"));
                    account.setBalance(rs.getBigDecimal("balance"));
                    try {
                        account.setCurrency(Currency.valueOf(rs.getString("currency")));
                    }
                    catch(IllegalArgumentException | NullPointerException e) {
                        throw new SQLException("Invalid currency value in database", e);
                    }
                    try {
                        account.setStatus(Status.valueOf(rs.getString("status")));
                    }
                    catch(IllegalArgumentException | NullPointerException e) {
                        throw new SQLException("Invalid status value in database", e);
                    }
                    Timestamp creationTime = rs.getTimestamp("creation_time");
                    if(creationTime != null) {
                        account.setCreationTime(creationTime.toLocalDateTime());
                    }
                    String statusHistoryString = rs.getString("status_history");
                    if(statusHistoryString != null && !statusHistoryString.trim().isEmpty()) {
                        List<String> statusHistory = Arrays.asList(statusHistoryString.split("\\|"));
                        account.setStatusHistory(statusHistory);
                    }
                    return account;
                }
            }
        }
        return null;
    }

    @Override
    public void updateStatus(String accountId, Status status) throws SQLException {
        if(accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("accountId cannot be null or empty.");
        }
        if(status == null) {
            throw new IllegalArgumentException("status cannot be null.");
        }
        //@formatter:off
        String updateStatusQuery = 
            "UPDATE account_management " +
            "SET " +
                "status = ?, " +
                "status_history = COALESCE(status_history || '|', '') || ? " +
            "WHERE " +
                "account_id = ?";
        //@formatter:on
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(updateStatusQuery)) {
            stmt.setString(1, status.name());
            stmt.setString(2, status.name()); // Append to status_history
            stmt.setString(3, accountId);
            if(stmt.executeUpdate() == 0) {
                throw new SQLException("Account with accountId " + accountId + " not found or status was not updated.");
            }
        }
    }

    @Override
    public void updateBalance(String accountId, BigDecimal balance) throws SQLException {
        if(accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("accountId cannot be null or empty.");
        }
        if(balance == null) {
            throw new IllegalArgumentException("balance cannot be null.");
        }
        String updateBalanceQuery = "UPDATE account_management SET balance = ? WHERE account_id = ?";
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(updateBalanceQuery)) {
            stmt.setBigDecimal(1, balance);
            stmt.setString(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0) {
                throw new SQLException(
                        "Account with accountId " + accountId + " not found or balance was not updated.");
            }
        }
    }
}