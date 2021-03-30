package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepository);
        TestUtils.injectObjects(userController,"cartRepository",cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",bCryptPasswordEncoder);
    }

    @Test
    public void createUser_happy_path(){
        Mockito.when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedValue");

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        User user = response.getBody();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(0);
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getPassword()).isEqualTo("hashedValue");
    }
}
