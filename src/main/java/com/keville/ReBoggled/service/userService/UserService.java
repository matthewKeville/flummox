package com.keville.ReBoggled.service.userService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.keville.ReBoggled.model.user.User;

public interface UserService extends UserDetailsService {

    public User getUser(int id);
    public User getUserByUsername(String username) throws UsernameNotFoundException;
    public void addLobby(User user);


}
