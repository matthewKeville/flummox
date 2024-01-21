package com.keville.ReBoggled.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("userinfo")
public class User {

    @Id
    public Integer id;

    public String email;
    public String username;
    public Boolean verified;
    public Boolean guest;

    public User() {}

    public static User createUser(String email, String username) {
      User user = new User();
      user.email = email;
      user.username = username;
      user.verified = false;
      user.guest = false;
      return user;
    }

    public String toString() {
      return
        "email :      " + this.email    + "\n" + 
        "username :   " + this.username + "\n" + 
        "verified :   " + this.verified + "\n" + 
        "guest :      " + this.guest;
    }

}
