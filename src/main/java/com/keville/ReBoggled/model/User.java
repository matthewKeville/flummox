package com.keville.ReBoggled.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERDETAIL")
public class User {

    @Id
    private Integer id;

    private String email;
    private Boolean verified;

    public User() {}
    public User(String email, boolean verified) {
      this.email = email;
      this.verified = verified;
    }


    public Integer getId() {
      return id;
    }

    /*
    public void setId(Integer id) {
      this.id = id;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public Boolean getVerified() {
      return verified;
    }
    public void setVerified(Boolean verified) {
      this.verified = verified;
    }

    */

}
