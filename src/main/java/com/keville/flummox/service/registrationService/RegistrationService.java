package com.keville.flummox.service.registrationService;

import com.keville.flummox.DTO.RegisterUserRequestDTO;
import com.keville.flummox.DTO.RegisterUserResponseDTO;
import com.keville.flummox.DTO.UserVerifyRequestDTO;
import com.keville.flummox.service.exceptions.BadRequest;

public interface RegistrationService {

    public static final int MIN_USERNAME_LENGTH     = 3;
    public static final int MAX_USERNAME_LENGTH     = 24;
    public static final int MAX_EMAIL_LENGTH        = 255;
    public static final int MAX_PASSWORD_LENGTH     = 80;
    public static final int MIN_PASSWORD_LENGTH     = 8;

    public RegisterUserResponseDTO registerUser(RegisterUserRequestDTO dto);
    public void verifyEmail(UserVerifyRequestDTO userVerifyRequestDTO) throws BadRequest;

}
