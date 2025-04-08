// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.balanceoperations.repository;

import java.sql.Connection;
import java.sql.DriverManager;
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

    private final String dbUrl;

    public SQLiteBalanceOperationsRepository(final String dbUrl) throws SQLException {
        this.dbUrl = dbUrl;
        createTable();
    }

    public void createTable() throws SQLException {
        //@formatter:off
        String createTableQuery = 
            "CREATE TABLE IF NOT EXISTS balance_operations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_id TEXT NOT NULL, " +
                "to_account_id TEXT, " +
                "amount NUMERIC, " +
                "type TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "related_balance_operation_id INTEGER" +
            ")";
        //@formatter:on
        try(Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
        }
        catch(SQLException e) {
            throw new SQLException("Error while creating the balance_operations table.", e);
        }
    }

    @Override
    public BalanceOperation save(BalanceOperation balanceOperation) throws SQLException {
        try(Connection conn = DriverManager.getConnection(dbUrl)) {
            return save(balanceOperation, conn);
        }
    }

    private BalanceOperation save(BalanceOperation balanceOperation, Connection conn) throws SQLException {
        //@formatter:off
        String saveQuery =
            "INSERT INTO balance_operations (" +
                "account_id, " +
                "to_account_id, " +
                "amount, " +
                "type, " +
                "timestamp, " +
                "related_balance_operation_id" +
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        //@formatter:on
        try(PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, balanceOperation.getAccountId());
            stmt.setString(2, balanceOperation.getToAccountId());
            if(balanceOperation.getAmount() != null) {
                stmt.setBigDecimal(3, balanceOperation.getAmount());
            }
            else {
                stmt.setNull(3, Types.NUMERIC);
            }
            if(balanceOperation.getType() != null) {
                stmt.setString(4, balanceOperation.getType().name());
            }
            else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if(balanceOperation.getTimestamp() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(balanceOperation.getTimestamp()));
            }
            else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            if(balanceOperation.getRelatedBalanceOperationId() != 0) { // Adjusted from -1L to 0
                stmt.setLong(6, balanceOperation.getRelatedBalanceOperationId());
            }
            else {
                stmt.setNull(6, Types.INTEGER);
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
        String findByAccountIdQuery = "SELECT * FROM balance_operations WHERE account_id = ?";
        List<BalanceOperation> balanceOperations = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(findByAccountIdQuery)) {
            stmt.setString(1, accountId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    BalanceOperation balanceOperation = new BalanceOperation();
                    balanceOperation.setId(rs.getLong("id"));
                    balanceOperation.setAccountId(rs.getString("account_id"));
                    balanceOperation.setToAccountId(rs.getString("to_account_id"));
                    balanceOperation.setAmount(rs.getBigDecimal("amount"));
                    balanceOperation.setType(Type.valueOf(rs.getString("type")));
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    if(timestamp != null) {
                        balanceOperation.setTimestamp(timestamp.toLocalDateTime());
                    }
                    Long relatedId = rs.getObject("related_balance_operation_id") != null
                            ? rs.getLong("related_balance_operation_id")
                            : 0L;
                    balanceOperation.setRelatedBalanceOperationId(relatedId);
                    balanceOperations.add(balanceOperation);
                }
            }
        }
        return balanceOperations;
    }

    @Override
    public void saveTransfer(BalanceOperation debit, BalanceOperation credit) throws SQLException {
        try(Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.setAutoCommit(false);
            try {
                // Save debit
                BalanceOperation savedDebit = save(debit, conn);
                credit.setRelatedBalanceOperationId(savedDebit.getId());

                // Save credit
                BalanceOperation savedCredit = save(credit, conn);
                savedDebit.setRelatedBalanceOperationId(savedCredit.getId());

                // Update related ID for debit
                try(PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE balance_operations SET related_balance_operation_id = ? WHERE id = ?")) {
                    stmt.setLong(1, savedCredit.getId());
                    stmt.setLong(2, savedDebit.getId());
                    stmt.executeUpdate();
                }
                conn.commit();
            }
            catch(SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}