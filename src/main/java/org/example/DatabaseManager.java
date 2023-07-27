package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;

    public List<Long> chatID = new ArrayList<>();

    public DatabaseManager(String url) {
        try {
            // Устанавливаем соединение с базой данных SQLite
            connection = DriverManager.getConnection(url);
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        PreparedStatement statement = null;

        try {
            String createTableSql = "CREATE TABLE IF NOT EXISTS ListOfID (id INTEGER PRIMARY KEY, chatID INTEGER)";
            statement = connection.prepareStatement(createTableSql);
            statement.executeUpdate();

            System.out.println("Таблица успешно создана или уже существует.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    //statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Long> getData() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM ListOfID";  //WHERE id = ?";
            statement = connection.prepareStatement(sql);

            // Устанавливаем значение параметра
            //statement.setLong(1, value);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Получаем значения столбцов
                Long id = resultSet.getLong("id");
                //int chatID = resultSet.getInt("chatID");

                chatID.add(id);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return chatID;
    }

    public void insertData(Long values) {
        PreparedStatement statement = null;

        try {
            // Создаем SQL-запрос INSERT с параметрами
            String sql = "INSERT INTO ListOfID (id, chatID) VALUES (?, ?)";

            // Создаем объект PreparedStatement для выполнения запроса
            statement = connection.prepareStatement(sql);

            // Добавляем значения в запрос INSERT с использованием цикла или итерации по коллекции
            statement.setLong(1, values);

            // Выполняем запрос INSERT для каждого значения
            statement.executeUpdate();

            System.out.println("Новые данные успешно добавлены в базу данных.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Закрываем statement
                if (statement != null) {
                    //statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            // Закрываем соединение с базой данных
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
