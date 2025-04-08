// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.payter.service.auditlogging.entity.AuditLogging;
import com.payter.service.auditlogging.entity.AuditLogging.EventType;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteAuditLoggingRepository implements AuditLoggingRepository {

    private final String dbUrl;

    public SQLiteAuditLoggingRepository(String dbUrl) throws SQLException {
        this.dbUrl = dbUrl;
        //@formatter:off
        try(Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "eventType TEXT, " +
                    "details TEXT, " +
                    "timestamp DATETIME" +
                ")");
        }
        //@formatter:on
    }

    @Override
    public void writeLogEntry(AuditLogging log) throws SQLException {
        //@formatter:off
        try(Connection conn = DriverManager.getConnection(dbUrl);
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO audit_logs (" +
                        "eventType, " +
                        "details, " +
                        "timestamp" +
                    ") " +
                    "VALUES (?, ?, ?)")) {
            //@formatter:on
            stmt.setString(1, log.getEventType().name());
            stmt.setString(2, log.getDetails());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    @Override
    public List<AuditLogging> getLogs(long afterId) throws SQLException {
        List<AuditLogging> logs = new ArrayList<>();
        //@formatter:off
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT " +
                     "id, "+
                     "eventType, "+
                     "details, "+
                     "timestamp "+
                 "FROM audit_logs "+
                 "WHERE id > ? ORDER BY id ASC")) {
            //@formatter:on
            stmt.setLong(1, afterId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    long id = Long.valueOf(rs.getLong("id"));
                    EventType eventType = EventType.valueOf(rs.getString("eventType"));
                    String details = rs.getString("details");
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    logs.add(new AuditLogging(id, eventType, details, timestamp));
                }
            }
        }
        return logs;
    }
}