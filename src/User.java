package src;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Setter
@ToString
@Getter
@EqualsAndHashCode
public class User {
    private int id;
    private String userId;
    private String passWord;
    private String name;

    public User(int id, String userId, String passWord, String name) {
        this.id = id;
        this.userId = userId;
        this.passWord = passWord;
        this.name = name;
    }



}
