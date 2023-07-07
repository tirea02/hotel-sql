package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Login {

    public static User loginUser(Connection connection) {
        String userId;
        String password;

        Scanner sc = new Scanner(System.in);

        System.out.println("**************로그인*************");
        System.out.print("아이디를 입력하세요: ");
        userId = sc.nextLine();

        System.out.print("비밀번호를 입력하세요: ");
        password = sc.nextLine();

        // Perform login validation
        // You can check the user credentials against your data store or any other validation logic


        //select from guest
        User loadUser = getPassWordFromUser(connection, userId);
        if(loadUser !=null){
            if (loadUser.getUserId().equals(userId) && loadUser.getPassWord().equals(password)) {
                System.out.println("로그인에 성공했습니다.");
                return loadUser;
            }
        }

        System.out.println("로그인에 실패했습니다. 다시 시도하세요.");
        return null;
    }

    public static User getPassWordFromUser(Connection connection, String userId) {
        String sql = "SELECT * FROM guest WHERE user_id = ?";
        try (PreparedStatement statement = Objects.requireNonNull(DatabaseConfig.getConnection()).prepareStatement(sql)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                return new User(id, userId, password, name);
            }

        } catch (SQLException e) {
            System.out.println("해당 userId를 DB에서 찾을 수 없습니다.");
            e.printStackTrace();
        }
        return null;
    }
}
