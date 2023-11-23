package com.keville.ReBoggled.repository;

import org.springframework.data.repository.CrudRepository;

import com.keville.ReBoggled.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
