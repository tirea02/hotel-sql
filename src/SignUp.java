package src;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class SignUp {

    public static void makeNewAccount(Connection connection) {
        int newId = DatabaseUtils.generateNewId(connection, "guest");
        String userId;
        String name;
        String pwd;
        String pwdCheck;


    }
}//class SignUp END
