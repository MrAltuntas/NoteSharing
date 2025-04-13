package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.Login200Response;
import io.gozcu.notesharing.model.LoginRequest;
import io.gozcu.notesharing.model.Register200Response;
import io.gozcu.notesharing.model.RegisterRequest;
import io.gozcu.notesharing.model.ValidateToken200Response;

public interface AuthService {
    Register200Response registerUser(RegisterRequest registerRequest);
    Login200Response loginUser(LoginRequest loginRequest);
    ValidateToken200Response validateUserToken();
}