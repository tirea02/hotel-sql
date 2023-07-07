package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class SignUp {

    public static User makeNewAccount(Connection connection) {
        int newId = DatabaseUtils.generateNewId(connection, "guest");
        String userId;
        String name;
        String pwd;
        String pwdCheck;

        Scanner scanner = new Scanner(System.in);

        System.out.println("**************회원가입*************");
        System.out.print("사용하실 Id를 입력하세요 : ");
        userId =scanner.nextLine();

        System.out.print("사용하실 비밀번호를 입력하세요 : ");
        pwd = scanner.nextLine();
        System.out.print("비밀번호 확인 : ");
        pwdCheck = scanner.nextLine();

        boolean isPwdValid = false;

        while(!isPwdValid){
            if(pwd.equals(pwdCheck)){
                System.out.println("비밀번호 일치");
                isPwdValid = true;
            }else{
                System.out.println("비밀번호가 일치하지 않습니다. 재입력 하십시오");
                System.out.print("사용하실 비밀번호를 입력하세요 : ");
                pwd = scanner.nextLine();
                System.out.print("비밀번호 확인 : ");
                pwdCheck = scanner.nextLine();
            }
        }

        System.out.println("고객님의 이름을 알려주세요 : ");
        name = scanner.nextLine();

        System.out.println(name+"고객님 ID : "+userId+" pwd : "+pwd+" 로 회원가입을 신청하셨습니다.");
        System.out.print("맞으시면 1번을 틀리시면 2번을 눌러주세요");

        String temp = scanner.nextLine();
        if(temp.equals("1")){
            System.out.println("회원가입을 축하드립니다. 예약 창으로 이동됩니다.");

        }else if(temp.equals(("2"))){
            return makeNewAccount(connection);
        }else{
            System.out.println("잘못된 입력입니다.");
            return makeNewAccount(connection);
        }

        User newUser = new User(newId, userId, pwd, name);

        //save logic
        addNewUser(connection, newUser);

        System.out.println(newUser);
        return newUser;

    }

    public static void addNewUser(Connection connection, User user){
        try {
            // Insert the reservation record with the generated ID
            String insertQuery = "INSERT INTO guest (id, user_id, password, name) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, user.getId());
            insertStatement.setString(2, user.getUserId());
            insertStatement.setString(3, user.getPassWord());
            insertStatement.setString(4, user.getName());
            insertStatement.executeUpdate();
            System.out.println("add new user successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to create reservation.");
            e.printStackTrace();
        }
    }

}//class SignUp END
