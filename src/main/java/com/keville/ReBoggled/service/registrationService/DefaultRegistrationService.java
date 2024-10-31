package com.keville.ReBoggled.service.registrationService;

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

import com.keville.ReBoggled.DTO.RegisterUserDTO;
import com.keville.ReBoggled.model.user.User;
import com.keville.ReBoggled.repository.UserRepository;
import com.keville.ReBoggled.service.registrationService.RegistrationServiceException.RegistrationServiceError;

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
      return;
    }

    sendConfirmationEmail(dto.getUsername(),dto.getEmail(),verifyLink);

  }

  public void verifyEmail(String email,String token) throws RegistrationServiceException {

    Optional<User> optUser = users.findByEmail(email);
    if ( optUser.isEmpty() ) {
      throw new RegistrationServiceException(RegistrationServiceError.EMAIL_NOT_FOUND);
    }

    User user = optUser.get();
    if ( ! user.verificationToken.equals(token) ) {
      throw new RegistrationServiceException(RegistrationServiceError.BAD_VERIFICATION_TOKEN);
    }

    user.verified = true;
    users.save(user);
    
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
