package com.keville.ReBoggled.service.userService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.keville.ReBoggled.model.user.User;

//formerly UserDetailsManager (but the contract was superfluous)
public interface UserService extends UserDetailsService {

    public User createUser(User user);
    public User getUser(int id);
    public User getUserByUsername(String username) throws UsernameNotFoundException;
    public void addLobby(User user);


}
