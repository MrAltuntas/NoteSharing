package io.gozcu.notesharing.api;

import io.gozcu.notesharing.model.Login200Response;
import io.gozcu.notesharing.model.LoginRequest;
import io.gozcu.notesharing.model.Register200Response;
import io.gozcu.notesharing.model.RegisterRequest;
import io.gozcu.notesharing.model.ValidateToken200Response;
import io.gozcu.notesharing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<Login200Response> login(LoginRequest loginRequest) {
        Login200Response response = authService.loginUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Register200Response> register(RegisterRequest registerRequest) {
        Register200Response response = authService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ValidateToken200Response> validateToken() {
        ValidateToken200Response response = authService.validateUserToken();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}