package src;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    public static int generateNewId(Connection connection, String tableName) {

        try {
            // Retrieve the maximum ID from the reservation table
            String maxIdQuery = "SELECT MAX(id) FROM " + tableName;
            Statement maxIdStatement = connection.createStatement();
            ResultSet maxIdResult = maxIdStatement.executeQuery(maxIdQuery);

            int nextId;
            if (maxIdResult.next()) {
                nextId = maxIdResult.getInt(1) + 1; // Increment the maximum ID by 1
            } else {
                nextId = 1; // If no records exist, start with ID 1
            }
            return nextId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
