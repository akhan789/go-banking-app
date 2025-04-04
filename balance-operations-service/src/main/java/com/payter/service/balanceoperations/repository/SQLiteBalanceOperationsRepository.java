// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.payter.service.balanceoperations.entity.BalanceOperation;
import com.payter.service.balanceoperations.entity.BalanceOperation.Type;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteBalanceOperationsRepository implements BalanceOperationsRepository {

    private final Connection conn;

    public SQLiteBalanceOperationsRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTable();
    }

    public void createTable() throws SQLException {
        //@formatter:off
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS balance_operations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_id INTEGER, " +
                "amount NUMERIC, " +
                "type TEXT, "+
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP"+
            ")";
        //@formatter:on
        try(Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
        }
        catch(SQLException e) {
            throw new SQLException("Error while creating the balance_operations table.", e);
        }
    }

    @Override
    public BalanceOperation save(BalanceOperation balanceOperation) throws SQLException {
        //@formatter:off
        String saveQuery =
            "INSERT INTO balance_operations (" +
                "account_id, " +
                "amount, " +
                "type, " +
                "timestamp" +
            ") " +
            "VALUES (?, ?, ?, ?)";
        //@formatter:on
        try(PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, balanceOperation.getAccountId());
            if(balanceOperation.getAmount() != null) {
                stmt.setBigDecimal(2, balanceOperation.getAmount());
            }
            else {
                stmt.setNull(2, Types.NUMERIC);
            }
            if(balanceOperation.getType() != null) {
                stmt.setString(3, balanceOperation.getType().name());
            }
            else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if(balanceOperation.getTimestamp() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(balanceOperation.getTimestamp()));
            }
            else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    balanceOperation.setId(rs.getLong(1));
                }
            }
        }
        return balanceOperation;
    }

    @Override
    public List<BalanceOperation> findByAccountId(String accountId) throws SQLException {
        String findByAccountId = "SELECT * FROM balance_operations WHERE account_id = ?";  // Ensure table name is correct
        List<BalanceOperation> balanceOperations = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(findByAccountId)) {
            stmt.setString(1, accountId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    BalanceOperation balanceOperation = new BalanceOperation();
                    balanceOperation.setId(rs.getLong("id"));
                    balanceOperation.setAccountId(rs.getString("account_id"));
                    balanceOperation.setAmount(rs.getBigDecimal("amount"));
                    balanceOperation.setType(Type.valueOf(rs.getString("type")));
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    if(timestamp != null) {
                        balanceOperation.setTimestamp(timestamp.toLocalDateTime());
                    }
                    else {
                        balanceOperation.setTimestamp(null);  // Or handle as needed
                    }
                    balanceOperations.add(balanceOperation);
                }
            }
        }
        return balanceOperations;
    }

    @Override
    public BigDecimal calculateBalance(String accountId) throws SQLException {
        String calculateBalanceQuery = "SELECT amount, type FROM balance_operations WHERE account_id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(calculateBalanceQuery)) {
            stmt.setString(1, accountId);
            try(ResultSet rs = stmt.executeQuery()) {
                BigDecimal balance = BigDecimal.ZERO;
                while(rs.next()) {
                    BigDecimal amount = rs.getBigDecimal("amount");
                    if(amount != null) {
                        Type type = Type.valueOf(rs.getString("type"));
                        if(type == Type.CREDIT) {
                            balance = balance.add(amount);
                        }
                        else if(type == Type.DEBIT) {
                            balance = balance.subtract(amount);
                        }
                        // TODO: support TRANSFER?
                        else {
                            System.err.println("Unexpected type: " + type);
                        }
                    }
                }
                return balance;
            }
        }
    }
}