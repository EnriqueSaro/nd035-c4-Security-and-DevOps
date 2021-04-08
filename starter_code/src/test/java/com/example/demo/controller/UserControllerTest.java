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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@AutoConfigureMockMvc
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
    @Test
    public void createUser_bad_request(){
        //Mockito.when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedValue");

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("length");
        userRequest.setConfirmPassword("length");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        //assert that not creating a user with a password length less than 7
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPasswordNotEqual");

        final ResponseEntity<User> response2 = userController.createUser(userRequest);
        //assert that not creating a user with different password and confirmPassword
        assertThat(response2).isNotNull();
        assertThat(response2.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void findUser_by_Username_Or_Id(){
        User user_returned = new User();
        user_returned.setId(1L);
        user_returned.setUsername("test");

        Mockito.when(userRepository.findByUsername("test")).thenReturn(user_returned);
        Mockito.when(userRepository.findById(user_returned.getId())).thenReturn(Optional.of(user_returned));

        //assert that findById works
        ResponseEntity<User> response = userController.findById(user_returned.getId());
        User user = response.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(user.getId()).isEqualTo(user_returned.getId());

        //assert that findByUsername works
        response = userController.findByUserName("test");
        user = response.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        assertThat(user.getId()).isEqualTo(user_returned.getId());
        assertThat(user.getUsername()).isEqualTo(user_returned.getUsername());

        response = userController.findById(100000L);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());

        response = userController.findByUserName("UNKNOWN_USERNAME");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }
}
