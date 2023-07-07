package src;

import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Getter
public class Room {
    private String roomNumber;
    private String userId;
    private boolean isReserved;

    public Room(String roomNumber, String userId, boolean isReserved) {
        this.roomNumber = roomNumber;
        this.userId = userId;
        this.isReserved = isReserved;
    }


    public static Room findRoomByRoomNumber(String roomNumber) {
        try (PreparedStatement statement = Objects.requireNonNull(DatabaseConfig.getConnection()).prepareStatement("SELECT * FROM guest WHERE room_number = ?")) {
            statement.setString(1, roomNumber);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String userId = resultSet.getString("user_id");
                boolean isReserved = resultSet.getBoolean("is_reserved");

                return new Room(roomNumber, userId, isReserved);
            }
        } catch (SQLException e) {
            System.out.println("Failed to find room by room number .");
            e.printStackTrace();
        }
        return null;
    }

}
