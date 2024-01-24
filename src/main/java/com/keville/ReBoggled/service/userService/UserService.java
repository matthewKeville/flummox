package com.keville.ReBoggled.service.userService;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import com.keville.ReBoggled.model.user.User;

public interface UserService extends UserDetailsManager {

    public User getUser(int id);
    public User getUserByUsername(String username) throws UsernameNotFoundException;

    // does this belong here?
    public void addLobby(User user);

}
