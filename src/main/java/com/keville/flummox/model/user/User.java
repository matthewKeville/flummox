package com.keville.flummox.model.user;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table("user")
public class User implements UserDetails {

    @Id
    public Integer id;

    public String password;
    public String username;
    public String email;
    public Boolean verified = false;
    public String verificationToken = "";
    public Boolean guest = true;
    public Boolean deactivated = false;

    public User() {};

    public User(String username, String email, String password) {
      this.email = email;
      this.username = username;
      this.password = password;
      this.verified = false;
      this.deactivated = false;
      this.guest = false;
    }

    @Deprecated
    public static User createUser(String email, String username) {
      User user = new User(username,email,"{noop}password");
      return user;
    }

    public String toString() {
      return
        "email :      " + this.email    + "\n" + 
        "username :   " + this.username + "\n" + 
        "verified :   " + this.verified + "\n" + 
        "guest :      " + this.guest;
    }

    @Override
    public String getPassword() {
      return this.password;
    }
    @Override
    public String getUsername() {
      return this.username;
    }

    // Required for UserDetails interface ( but not used by me )
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return new HashSet<GrantedAuthority>();
    }
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }
    @Override
    public boolean isEnabled() {
      return true;
    }

}
