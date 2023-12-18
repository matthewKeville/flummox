package com.keville.ReBoggled.repository;

import org.springframework.data.repository.CrudRepository;

import com.keville.ReBoggled.model.user.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
