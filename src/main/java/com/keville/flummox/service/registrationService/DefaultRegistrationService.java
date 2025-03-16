package com.keville.flummox.service.registrationService;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.keville.flummox.DTO.RegisterUserRequestDTO;
import com.keville.flummox.DTO.RegisterUserResponseDTO;
import com.keville.flummox.DTO.UserVerifyRequestDTO;
import com.keville.flummox.model.user.User;
import com.keville.flummox.repository.UserRepository;
import com.keville.flummox.service.exceptions.BadRequest;
import com.keville.flummox.service.registrationService.RegistrationServiceException.RegistrationServiceError;

@Service
public class DefaultRegistrationService  implements RegistrationService {

  private UserRepository users;
  private Logger LOG = LoggerFactory.getLogger(RegistrationService.class);
  private PasswordEncoder passwordEncoder;
  private MailSender mailSender;
  private Environment env;

  public DefaultRegistrationService(
      @Autowired UserRepository users,
      @Autowired PasswordEncoder passwordEncoder,
      @Autowired JavaMailSender mailSender,
      @Autowired Environment env) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.mailSender = mailSender;
    this.env = env;
  }

  public RegisterUserResponseDTO registerUser(RegisterUserRequestDTO dto) {

    LOG.info("hit registerUser");

    try {

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
      user = users.save(user);

      // token

      String verifyToken = RandomStringUtils.randomAlphanumeric(20);
      user.verificationToken = verifyToken;
      users.save(user);


      // send verify link

      String verifyLink = env.getProperty("flummox.origin") + "/#verify?email=" + user.email + "&token=" + verifyToken;
      boolean verifyInConsole = Boolean.valueOf(env.getProperty("flummox.verifyLinkInConsole"));
      if ( verifyInConsole ) {
        LOG.info(verifyLink);
      } else {
        sendConfirmationEmail(dto.getUsername(),dto.getEmail(),verifyLink);
      }

      LOG.info("good registration");

      LOG.info("sent email");

      return RegisterUserResponseDTO.OK();
    
    } catch( RegistrationServiceException  ex) {

      LOG.info("caught failure : " + ex.error.toString());

      return buildFailResponse(ex.error);

    }

  }

  public void verifyEmail(UserVerifyRequestDTO userVerifyRequestDTO) throws BadRequest {

    Optional<User> optUser = users.findByEmail(userVerifyRequestDTO.email());
    if ( optUser.isEmpty() ) {
      throw new BadRequest("Email not found");
    }

    User user = optUser.get();
    if ( ! user.verificationToken.equals(userVerifyRequestDTO.token()) ) {
      throw new BadRequest("Bad token");
    }

    user.verified = true;
    users.save(user);
    
  }

  private RegisterUserResponseDTO buildFailResponse(RegistrationServiceException.RegistrationServiceError error) {

    RegisterUserResponseDTO response = new RegisterUserResponseDTO();

    switch ( error ) {
      case EMPTY_EMAIL:
        response.errorEmail = Optional.of("Email can not be empty");
        break;
      case EMAIL_TOO_LONG:
        response.errorEmail = Optional.of("Max length " + RegistrationService.MAX_EMAIL_LENGTH + " characters");
        break;
      case EMAIL_IN_USE:
        response.errorEmail = Optional.of("Email already in use");
        break;

      case EMTPY_USERNAME:
        response.errorUsername = Optional.of("Username can not be empty");
        break;
      case USERNAME_TOO_LONG:
        response.errorUsername = Optional.of("Max length : " + RegistrationService.MAX_USERNAME_LENGTH);
        break;
      case USERNAME_TOO_SHORT:
        response.errorUsername = Optional.of("Min length : " + RegistrationService.MIN_USERNAME_LENGTH);
        break;
      case USERNAME_IN_USE:
        response.errorUsername = Optional.of("Username already in use");
        break;

      case EMTPY_PASSWORD:
        response.errorPassword = Optional.of("Password can not be empty");
        break;
      case PASSWORD_TOO_SHORT:
        response.errorPassword = Optional.of("Min length " + RegistrationService.MIN_PASSWORD_LENGTH);
        break;
      case PASSWORD_TOO_LONG:
        response.errorPassword = Optional.of("Max length " + RegistrationService.MAX_PASSWORD_LENGTH);
        break;
      case PASSWORD_UNEQUAL:
        response.errorPassword = Optional.of("Passwords do not match");
        break;

      default:
        response.errorGeneral = Optional.of("internal error");
        break;
    }

    response.success = false;
    LOG.info("returning fail response");
    return response;

  }


  private void fail(RegistrationServiceError error) throws RegistrationServiceException {
    throw new RegistrationServiceException(error);
  }

  private void safeEmail(String email) throws RegistrationServiceException {
    if ( email.length() > MAX_EMAIL_LENGTH ) fail(RegistrationServiceError.EMAIL_TOO_LONG);
  }

  private void safeUsername(String username) throws RegistrationServiceException {

    if ( username.length() > MAX_USERNAME_LENGTH ) {
      fail(RegistrationServiceError.USERNAME_TOO_LONG);
    }

    if ( username.length() < MIN_USERNAME_LENGTH ) {
      fail(RegistrationServiceError.USERNAME_TOO_SHORT);
    }

    LOG.warn("no username profanity check");
  }

  private void safePassword(String password) throws RegistrationServiceException {
    if ( password.length() > MAX_PASSWORD_LENGTH ) fail(RegistrationServiceError.PASSWORD_TOO_LONG);
    if ( password.length() < MIN_PASSWORD_LENGTH ) fail(RegistrationServiceError.PASSWORD_TOO_SHORT);
  }

  private void  sendConfirmationEmail(String username,String email,String verifyLink) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(env.getProperty("spring.mail.username"));
    msg.setTo(email);
    msg.setText(username + " please verify your account " + verifyLink);
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      LOG.warn("Unable to send confirmation email to : " + email);
      LOG.debug(ex.getMessage());
    }
  }

}
