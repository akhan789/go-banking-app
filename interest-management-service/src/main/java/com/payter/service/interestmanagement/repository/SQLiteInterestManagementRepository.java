// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.payter.service.interestmanagement.entity.InterestManagement;
import com.payter.service.interestmanagement.entity.InterestManagement.CalculationFrequency;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteInterestManagementRepository implements InterestManagementRepository {

    private final Connection conn;

    public SQLiteInterestManagementRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTable();
    }

    private void createTable() throws SQLException {
        try(Statement stmt = conn.createStatement()) {
            //@formatter:off
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS interest_management (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "daily_rate REAL, " +
                    "calculation_frequency TEXT, " +
                    "created_at DATETIME" +
                ")");
            //@formatter:on
            // Insert default values only if table is empty
            try(ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM interest_management")) {
                if(rs.next() && rs.getInt(1) == 0) {
                    //@formatter:off
                    stmt.execute(
                        "INSERT OR IGNORE INTO interest_management (" +
                            "id, " +
                            "daily_rate, " +
                            "calculation_frequency, " +
                            "created_at" +
                        ") " +
                        "VALUES (1, 0.001, 'DAILY', '1971-01-01 00:00:00')");
                    //@formatter:on
                }
            }
        }
    }

    @Override
    public InterestManagement save(InterestManagement interestManagement) throws SQLException {
        String saveQuery = "INSERT INTO interest_management (daily_rate, calculation_frequency, created_at) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, interestManagement.getDailyRate());
            stmt.setString(2, interestManagement.getCalculationFrequency().name());
            stmt.setTimestamp(3, Timestamp.valueOf(interestManagement.getCreatedAt()));
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    interestManagement.setId(rs.getLong(1));
                }
            }
            return interestManagement;
        }
    }

    @Override
    public InterestManagement findLatest() throws SQLException {
        String findLatestQuery = "SELECT * FROM interest_management ORDER BY id DESC LIMIT 1";
        try(PreparedStatement stmt = conn.prepareStatement(findLatestQuery); ResultSet rs = stmt.executeQuery()) {
            if(rs.next()) {
                InterestManagement config = new InterestManagement();
                config.setId(rs.getLong("id"));
                config.setDailyRate(BigDecimal.valueOf(rs.getDouble("daily_rate")));
                config.setCalculationFrequency(CalculationFrequency.valueOf(rs.getString("calculation_frequency")));
                return config;
            }
            return null;
        }
    }
}