package com.keville.ReBoggled.service.userService;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.keville.ReBoggled.DTO.UserInfoDTO;

public interface UserService extends UserDetailsService {

    public UserInfoDTO getUserInfoDTO();

}
