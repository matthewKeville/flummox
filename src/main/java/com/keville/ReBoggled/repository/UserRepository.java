package com.keville.ReBoggled.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.keville.ReBoggled.model.user.User;

public interface UserRepository extends CrudRepository<User, Integer> {

  @Query("""
  SELECT  user.* FROM user where user.username  = :username
  """)
  Optional<User> findByUsername(String username);

  // existance

  @Query("""
  SELECT  count(*) FROM user where user.username  = :username
  """)
  boolean existsByUsername(String username);

  @Query("""
  SELECT  count(*) FROM user where user.email  = :email
  """)
  boolean existsByEmail(String email);

}
