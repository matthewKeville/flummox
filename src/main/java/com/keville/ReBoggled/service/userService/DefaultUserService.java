package com.keville.ReBoggled.service.userService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.keville.ReBoggled.DTO.UserInfoDTO;
import com.keville.ReBoggled.model.lobby.Lobby;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.LobbyRepository;
import com.keville.ReBoggled.repository.UserRepository;

@Component
public class DefaultUserService implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private UserRepository users;
    private LobbyRepository lobbies;

    public DefaultUserService(
        @Autowired UserRepository users,
        @Autowired LobbyRepository lobbies
        ) {
      this.users = users;
      this.lobbies = lobbies;
    }

    //UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<User> user = users.findByUsername(username);
      if ( user.isEmpty() ) {
        throw new UsernameNotFoundException("can't locate user " + username);
      }
      return user.get();
    }

    public UserInfoDTO getUserInfoDTO() {
      User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<Lobby> lobby = lobbies.findUserLobby(principal.id);
      return new UserInfoDTO(principal.id,principal.username,principal.guest,lobby.isPresent() ? lobby.get().id : null);
    }

}
