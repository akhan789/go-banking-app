// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.accountmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import com.payter.service.accountmanagement.entity.Account;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteAccountManagementRepository implements AccountManagementRepository {

    private final Connection conn;

    public SQLiteAccountManagementRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTable();
    }

    public void createTable() throws SQLException {
        //@formatter:off
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_id TEXT, " +
                "account_name TEXT, " +
                "balance REAL, " +
                "status TEXT, " +
                "currency TEXT, " +
                "creation_time TIMESTAMP, " +
                "status_history TEXT" +
            ")";
        //@formatter:on
        try(PreparedStatement stmt = conn.prepareStatement(createTableQuery)) {
            stmt.execute();
        }
        catch(SQLException e) {
            throw new SQLException(
                    "Error while initializing the SQLiteAccountManagementRepository and creating the accounts table.",
                    e);
        }
    }

    @Override
    public Account save(Account account) throws SQLException {
        //@formatter:off
        String saveQuery =
            "INSERT INTO accounts (" +
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
        try(PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getAccountId());
            stmt.setString(2, account.getAccountName());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setString(4, account.getStatus());
            stmt.setString(5, account.getCurrency());
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
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    account.setId(rs.getLong(1));
                }
            }
            return account;
        }
        catch(SQLException e) {
            throw new SQLException("Error saving account: " + e.getMessage(), e);
        }
    }

    @Override
    public Account findById(long id) throws SQLException {
        String findByIdQuery = "SELECT * FROM accounts WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(findByIdQuery); ResultSet rs = stmt.executeQuery()) {
            stmt.setLong(1, id);
            if(rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setAccountId(rs.getString("account_id"));
                account.setAccountName(rs.getString("account_name"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCurrency(rs.getString("currency"));
                account.setStatus(rs.getString("status"));
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
            throw new SQLException("Account not found");
        }
    }

    @Override
    public void updateStatus(long id, String status) throws SQLException {
        String updateStatusQuery = "UPDATE accounts SET status = ? WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(updateStatusQuery)) {
            stmt.setString(1, status);
            stmt.setLong(2, id);
            if(stmt.executeUpdate() == 0) {
                throw new SQLException("Account with id " + id + " not found or status was not updated.");
            }
        }
    }
}