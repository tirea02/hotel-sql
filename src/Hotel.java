package src;

import lombok.Getter;

import java.sql.*;
import java.util.Objects;

@Getter
public class Hotel {
    private final Connection connection;
    private Room[][] rooms; //update rooms with database update, plz note to sync rooms and database manually

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
    }//function checkReservationStatus end

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

    public void printUserReservedRooms(User user){
        String sql = "SELECT * FROM reservation WHERE user_id = ?";
        try (PreparedStatement statement = Objects.requireNonNull(DatabaseConfig.getConnection()).prepareStatement(sql)) {
            statement.setString(1, user.getUserId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String roomNumber = resultSet.getString("room_number");
                System.out.println(roomNumber);
            }
        }catch (SQLException e) {
            System.out.println("해당 userId를 DB에서 찾을 수 없습니다.");
            e.printStackTrace();
        }

    }


    public void createReservation(String userId, String roomNumber) {
        try {
            // Retrieve the maximum ID from the reservation table

            int nextId = DatabaseUtils.generateNewId(connection, "reservation");

            // Insert the reservation record with the generated ID
            String insertQuery = "INSERT INTO reservation (id, user_id, room_number, is_reserved) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, nextId);
            insertStatement.setString(2, userId);
            insertStatement.setString(3, roomNumber);
            insertStatement.setBoolean(4, true);
            insertStatement.executeUpdate();
            updateRoomStatus(userId, roomNumber, true);

            System.out.println("Reservation created successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to create reservation.");
            e.printStackTrace();
        }
    }//function createReservation end

    public void cancelReservation(String userId, String roomNumber){
        try {
            String deleteQuery = "delete from reservation where user_id = ? and room_number = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, userId);
            deleteStatement.setString(2, roomNumber);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Reservation canceled successfully.");
                updateRoomStatus("", roomNumber, false);
            } else {
                System.out.println("No reservation found for the specified user and room.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to cancel reservation.");
            e.printStackTrace();
        }
    }//function cancelReservation end

    public void updateRoomStatus(String userId, String roomNumber, boolean isReserved) {
        int floor = Integer.parseInt(roomNumber.substring(0, 1)) - 1;
        int room = Integer.parseInt(roomNumber.substring(1)) - 1;
        if (floor >= 0 && floor < rooms.length && room >= 0 && room < rooms[floor].length) {
            Room currentRoom = rooms[floor][room];
            currentRoom.setReserved(isReserved);
            currentRoom.setUserId(userId);
        } else {
            System.out.println("Invalid room number.");
        }
    }//function updateRoomStatus end

    public  void signOutUser(String userId) {
        PreparedStatement selectReservationStatement = null;
        try (Connection connection = DatabaseConfig.getConnection()) {
            Objects.requireNonNull(connection).setAutoCommit(true); // Enable auto-commit

            // Set isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // Retrieve the reservations for the user
            String selectReservationQuery = "SELECT room_number FROM reservation WHERE user_id = ?";
            selectReservationStatement = connection.prepareStatement(selectReservationQuery);
            selectReservationStatement.setString(1, userId);
            ResultSet resultSet = selectReservationStatement.executeQuery();

            // Cancel all reservations before signOut
            while (resultSet.next()) {
                String roomNumber = resultSet.getString("room_number");
                cancelReservation(userId, roomNumber);
            }
            System.out.println("Deleted all reservations successfully.");

            // Delete user from guest table
            String deleteUserQuery = "DELETE FROM guest WHERE user_id = ?";
            PreparedStatement deleteGuestStatement = connection.prepareStatement(deleteUserQuery);
            deleteGuestStatement.setString(1, userId);
            deleteGuestStatement.executeUpdate();
            System.out.println("Deleted user successfully.");

        } catch (SQLException e) {
            System.out.println("Failed to delete user.");
            e.printStackTrace();
        } finally {
            // Close prepared statement
            try {
                if (selectReservationStatement != null) {
                    selectReservationStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





}//class Hotel END
