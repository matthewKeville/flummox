package com.keville.flummox.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.keville.flummox.model.user.User;

public interface UserRepository extends CrudRepository<User, Integer> {

  @Query("""
  SELECT  user.* FROM user where user.username  = :username
  """)
  Optional<User> findByUsername(String username);

  @Query("""
  SELECT  user.* FROM user where user.email  = :email
  """)
  Optional<User> findByEmail(String email);

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
