// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.interestmanagement.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.payter.common.util.Util;
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

    private final String dbUrl;

    public SQLiteInterestManagementRepository(String dbUrl) throws SQLException {
        this.dbUrl = dbUrl;
        createTable();
    }

    private void createTable() throws SQLException {
        try(Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
            //@formatter:off
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS interest_management (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "daily_rate REAL, " +
                    "calculation_frequency TEXT, " +
                    "created_at DATETIME, " +
                    "last_applied_at DATETIME, " +
                    "next_application_at DATETIME" +
                ")");
           //@formatter:on
            try(ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM interest_management")) {
                if(rs.next() && rs.getInt(1) == 0) {
                    //@formatter:off
                    String insertSQL =
                        "INSERT OR IGNORE INTO interest_management (" +
                            "id, " +
                            "daily_rate, " +
                            "calculation_frequency, " +
                            "created_at, " +
                            "last_applied_at, " +
                            "next_application_at" +
                        ") " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                    //@formatter:on
                    try(PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                        pstmt.setLong(1, 1L);
                        pstmt.setBigDecimal(2, BigDecimal.ZERO);
                        pstmt.setString(3, "DAILY");
                        pstmt.setTimestamp(4, Timestamp.valueOf("1971-01-01 00:00:00"));
                        pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                        pstmt.setTimestamp(6, null);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }

    @Override
    public InterestManagement save(InterestManagement interestManagement) throws SQLException {
        //@formatter:off
        String saveQuery =
            "INSERT INTO interest_management ("
                + "daily_rate, "
                + "calculation_frequency, "
                + "created_at, "
                + "last_applied_at, "
                + "next_application_at"
            + ") "
            + "VALUES (?, ?, ?, ?, ?)";
        //@formatter:on
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, interestManagement.getDailyRate());
            stmt.setString(2, interestManagement.getCalculationFrequency().name());
            LocalDateTime createdAt = interestManagement.getCreatedAt();
            if(createdAt == null) {
                createdAt = LocalDateTime.now();
                interestManagement.setCreatedAt(createdAt);
            }
            stmt.setTimestamp(3, Timestamp.valueOf(createdAt));
            LocalDateTime lastApplied = interestManagement.getLastAppliedAt();
            stmt.setTimestamp(4, lastApplied != null ? Timestamp.valueOf(lastApplied) : null);
            LocalDateTime nextApplication = interestManagement.getNextApplicationAt();
            stmt.setTimestamp(5, nextApplication != null ? Timestamp.valueOf(nextApplication) : null);
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
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(findLatestQuery);
                ResultSet rs = stmt.executeQuery()) {
            if(rs.next()) {
                InterestManagement config = new InterestManagement();
                config.setId(rs.getLong("id"));
                config.setDailyRate(BigDecimal.valueOf(rs.getDouble("daily_rate")));
                config.setCalculationFrequency(CalculationFrequency.valueOf(rs.getString("calculation_frequency")));
                config.setCreatedAt(Util.toLocalDateTime(rs.getTimestamp("created_at")));
                config.setLastAppliedAt(Util.toLocalDateTime(rs.getTimestamp("last_applied_at")));
                config.setNextApplicationAt(Util.toLocalDateTime(rs.getTimestamp("next_application_at")));
                return config;
            }
            return null;
        }
    }
}