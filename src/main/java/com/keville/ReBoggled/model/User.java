package com.keville.ReBoggled.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERINFO")
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
      user.setEmail(email);
      user.setUsername(username);
      user.setVerified(false);
      user.setGuest(false);
      return user;
    }


    public Integer getId() {
      return id;
    }

    public String getUsername() {
      return username;
    }

    public String getEmail() {
      return email;
    }

    public boolean isGuest() {
      return this.guest;
    }

    public void setVerified(boolean verified) {
      this.verified = verified;
    }

    public void setGuest(boolean guest) {
      this.guest = guest;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String toString() {
      return
        "email :      " + this.email    + "\n" + 
        "username :   " + this.username + "\n" + 
        "verified :   " + this.verified + "\n" + 
        "guest :      " + this.guest;
    }

}
