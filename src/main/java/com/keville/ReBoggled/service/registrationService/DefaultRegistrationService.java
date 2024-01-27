package com.keville.ReBoggled.service.registrationService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keville.ReBoggled.DTO.RegisterUserDTO;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.registrationService.RegistrationServiceException.RegistrationServiceError;
import com.keville.ReBoggled.service.userService.UserService;
@Service
public class DefaultRegistrationService  implements RegistrationService {

  private final int MAX_USERNAME_LENGTH     = 40;
  private final int MAX_EMAIL_LENGTH        = 255;
  private final int MAX_PASSWORD_LENGTH     = 80;
  private final int MIN_PASSWORD_LENGTH     = 8;

  private UserRepository users;
  private Logger LOG = LoggerFactory.getLogger(RegistrationService.class);
  private PasswordEncoder passwordEncoder;

  public DefaultRegistrationService(@Autowired UserRepository users,
      @Autowired PasswordEncoder passwordEncoder) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
  }

  public void registerUser(RegisterUserDTO dto) throws RegistrationServiceException {

    //prelim

    if ( StringUtils.isBlank(dto.getUsername()) ) {
      fail(RegistrationServiceError.EMTPY_USERNAME);
    }
    if ( StringUtils.isBlank(dto.getEmail()) ) {
      fail(RegistrationServiceError.EMPTY_EMAIL);
    }
    if ( StringUtils.isBlank(dto.getPassword()) ) {
      fail(RegistrationServiceError.EMTPY_PASSWORD);
    }

    //implicit constraints

    safeEmail(dto.getEmail());
    safeUsername(dto.getUsername());
    safePassword(dto.getPassword());
 
    //everything else
    
    if ( !dto.getPassword().equals(dto.getPasswordConfirmation()) ) {
      throw new RegistrationServiceException(RegistrationServiceError.PASSWORD_UNEQUAL);
    }

    if ( users.existsByEmail(dto.getEmail()) ) {
      throw new RegistrationServiceException(RegistrationServiceError.EMAIL_IN_USE);
    }

    if ( users.existsByUsername(dto.getUsername()) ) { 
      throw new RegistrationServiceException(RegistrationServiceError.USERNAME_IN_USE);
    }

    // OKAY!

    String encodedPassword = passwordEncoder.encode(dto.getPassword());
    User user = new User(dto.getUsername(),dto.getEmail(),encodedPassword);
    users.save(user);

  }

  private void fail(RegistrationServiceError error) throws RegistrationServiceException {
    throw new RegistrationServiceException(error);
  }

  private void safeEmail(String email) throws RegistrationServiceException {
    if ( email.length() > MAX_EMAIL_LENGTH ) fail(RegistrationServiceError.EMAIL_TOO_LONG);

    //TODO : Find Robust Email Validator
    LOG.warn(" skipping thorough email validation ");
  }

  private void safeUsername(String username) throws RegistrationServiceException {
    if ( username.length() > MAX_USERNAME_LENGTH ) fail(RegistrationServiceError.USERNAME_TOO_LONG);

    //TODO : Find Existing Username screener (Profanities, Hate Speech)
    LOG.warn(" skipping thorough username screening ");
  }

  private void safePassword(String password) throws RegistrationServiceException {
    if ( password.length() > MAX_PASSWORD_LENGTH ) fail(RegistrationServiceError.PASSWORD_TOO_LONG);
    if ( password.length() < MIN_PASSWORD_LENGTH ) fail(RegistrationServiceError.PASSWORD_TOO_SHORT);
  }

}
