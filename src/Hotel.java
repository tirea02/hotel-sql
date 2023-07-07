package src;

import java.sql.*;
import java.util.Objects;

public class Hotel {
    private final Connection connection;
    private Room[][] rooms;

    Hotel(){
        this.connection = DatabaseConfig.getConnection();
        int floorCount = getFloorCountFromDatabase();
        int roomsPerFloor = getRoomsPerFloorFromDatabase();
        System.out.println(floorCount);
        System.out.println(roomsPerFloor);
        // Initialize the rooms array with the retrieved values
        this.rooms = new Room[floorCount][roomsPerFloor];

        // Populate the rooms array with Room objects
        for (int floor = 0; floor < floorCount; floor++) {
            for (int roomNumber = 0; roomNumber < roomsPerFloor; roomNumber++) {
                // Check if the room is reserved or not based on reservation data from the database
                String roomNumberString = (floor+1)+"0"+(roomNumber+1);
                Object[] result = checkReservationStatus(roomNumberString);

                boolean isReserved = (boolean) result[0];
                String userId = (String) result[1];

                rooms[floor][roomNumber] = new Room(roomNumberString, userId, isReserved);
            }
        }
    }//Hotel no args constructor end

    private int getFloorCountFromDatabase() {
        String sql  ="select * from hotel";
        try {
            Statement statement = connection != null ? connection.createStatement() : null;
            ResultSet resultSet = Objects.requireNonNull(statement).executeQuery(sql);
            if (resultSet.next()) {
                System.out.println("floor loaded , floor : " +resultSet.getInt("floor_count"));
                return resultSet.getInt("floor_count");
            }
        } catch (SQLException e) {
            System.out.println("Failed to load floor from hotel table");
            e.printStackTrace();
        }
        return -1;
    }

    // Method to retrieve the number of rooms per floor from the database
    private int getRoomsPerFloorFromDatabase() {
        String sql  ="select * from hotel";
        try {
            Statement statement = connection != null ? connection.createStatement() : null;
            ResultSet resultSet = Objects.requireNonNull(statement).executeQuery(sql);
            if (resultSet.next()) {
                System.out.println("rooms per floor loaded , rooms : " +resultSet.getInt("rooms_per_floor"));
                return resultSet.getInt("rooms_per_floor");
            }
        } catch (SQLException e) {
            System.out.println("Failed to load rooms_per_floor from hotel table");
            e.printStackTrace();
        }
        return -1;
    }

    // Method to check the reservation status for  room number
    private Object[] checkReservationStatus(String roomNumber) {
        boolean isReserved = false;
        String userId = "";
        String sql  ="select * from reservation where room_number = "+roomNumber;
        try {
            Statement statement = connection != null ? connection.createStatement() : null;
            ResultSet resultSet = Objects.requireNonNull(statement).executeQuery(sql);
            if (resultSet.next()) {
                isReserved = resultSet.getBoolean("is_reserved");
                userId = resultSet.getString("user_id");
                return new Object[] { isReserved, userId };
            }
        } catch (SQLException e) {
            System.out.println("Failed to load rooms_per_floor from hotel table");
            e.printStackTrace();
        }
        return new Object[] { isReserved, userId };
    }

    public void printAllRooms(){
        for(Room[] floor : rooms){
            for(Room room : floor){
                System.out.print(room.getRoomNumber() + "\t\t\t\t\t");
            }
            System.out.println();

            for(Room room : floor){
                if(room.isReserved()) {
                    System.out.print(room.getUserId());
                }
                System.out.print("\t\t\t\t\t");
            }
            System.out.println();
        }

    }//function printAllRooms end


    public void createReservation(String userId, String roomNumber) {
        try (PreparedStatement statement = Objects.requireNonNull(DatabaseConfig.getConnection()).prepareStatement("INSERT INTO reservation (id, user_id, room_number, is_reserved) VALUES (reservation_seq.nextval, ?, ?, ?)")) {
            statement.setString(1, userId);
            statement.setString(2, roomNumber);
            statement.setBoolean(3, true);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert reservation record.");
            e.printStackTrace();
        }
    }





}//Hotel class END
