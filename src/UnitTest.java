package src;

import static src.DatabaseConfig.getConnection;

public class UnitTest {


    public static void connectionTest(){
        System.out.println(getConnection());
    }


}
