package com.keville.ReBoggled.service.userService;

import java.util.List;

import com.keville.ReBoggled.model.user.User;

public interface UserService {

    public Iterable<User> getUsers();

    public User getUser(int id);

    public List<User> getUsers(List<Integer> ids);

    public void addLobby(User user);

}
