// Copyright (c) 2025, Payter and/or its affiliates. All rights reserved.
package com.payter.service.auditlogging.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.payter.service.auditlogging.entity.AuditLogging;

/**
 * 
 * 
 * @author Abid Khan
 * @since 0.0.1_SNAPSHOT
 * @version $Revision$
 */
public class SQLiteAuditLoggingRepository implements AuditLoggingRepository {

    private final Connection conn;

    public SQLiteAuditLoggingRepository(Connection conn) throws SQLException {
        this.conn = conn;
        try(Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS audit_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, timestamp DATETIME)");
        }
    }

    @Override
    public void save(AuditLogging log) throws SQLException {
        try(PreparedStatement stmt = conn
                .prepareStatement("INSERT INTO audit_logs (message, timestamp) VALUES (?, ?)")) {
            stmt.setString(1, log.getMessage());
            stmt.setTimestamp(2, Timestamp.valueOf(log.getTimestamp()));
            stmt.executeUpdate();
        }
    }
}