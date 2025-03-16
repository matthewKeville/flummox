package com.keville.flummox.service.userService;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.keville.flummox.DTO.UserInfoDTO;

public interface UserService extends UserDetailsService {

    public UserInfoDTO getUserInfoDTO();
    public void updateUserSessionActivityChecker();

}
