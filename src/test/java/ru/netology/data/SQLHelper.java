package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLDataException;

public class SQLHelper {
    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    public static Connection getConnection() throws SQLDataException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/traveldb", "user", "mypassword");
    }

    @SneakyThrows
    public static void cleanData() {
        var sqlOrderEntity = "DELETE FROM order_entity;";
        var sqlPaymentEntity = "DELETE FROM payment_entity;";
        var sqlCreditEntity = "DELETE FROM credit_request_entity;";
        try (var conn = getConnection()) {
            runner.update(conn, sqlOrderEntity);
            runner.update(conn, sqlPaymentEntity);
            runner.update(conn, sqlCreditEntity);
        }
    }

    @SneakyThrows
    public static String getPaymentStatus() {
        var sqlData = "SELECT status FROM payment_entity;";
        try (var conn = getConnection()) {
            return runner.query(conn, sqlData, new ScalarHandler<String>());
        }
    }
}
