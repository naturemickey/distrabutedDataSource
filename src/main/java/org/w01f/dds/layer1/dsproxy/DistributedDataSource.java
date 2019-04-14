package org.w01f.dds.layer1.dsproxy;


import org.w01f.dds.layer2.sql.SqlHandler;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DistributedDataSource implements DataSource {
    public void setSqlHandler(SqlHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
    }

    private SqlHandler sqlHandler;

    @Override
    public Connection getConnection() throws SQLException {
        return new DistributedConnection(sqlHandler);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new DistributedConnection(sqlHandler);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }
}
