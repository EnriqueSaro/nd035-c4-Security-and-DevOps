package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController,"userRepository",userRepository);
        TestUtils.injectObjects(cartController,"cartRepository",cartRepository);
        TestUtils.injectObjects(cartController,"itemRepository",itemRepository);
    }

    @Test
    public void add_Item_to_Cart(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Item item = new Item();
        item.setId(1L);
        item.setName("Chocolate");
        item.setPrice(BigDecimal.TEN);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        user.setCart(cart);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(1);
        cartRequest.setUsername(user.getUsername());

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Cart returned_cart = response.getBody();

        //assert that the item that we added is part of the cart
        assertThat(returned_cart.getUser().getId()).isEqualTo(user.getId());
        assertThat(returned_cart.getItems()).hasSize(cartRequest.getQuantity());
        assertThat(returned_cart.getItems().get(0).getName()).isEqualTo(item.getName());
        assertThat(returned_cart.getItems().get(0).getId()).isEqualTo(item.getId());


    }

    @Test
    public void remove_Item_from_Cart_BadRequest(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Item item = new Item();
        item.setId(1L);
        item.setName("Chocolate");
        item.setPrice(BigDecimal.TEN);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);

        user.setCart(cart);
        cart.setItems(Lists.newArrayList(item,item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(1);
        cartRequest.setUsername(user.getUsername());

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Cart returned_cart = response.getBody();

        //Assert that only one item of two was removed from items list
        assertThat(returned_cart.getUser()).isEqualTo(user);
        assertThat(returned_cart.getItems()).hasSize(1);
        assertThat(returned_cart.getItems().get(0).getId()).isEqualTo(item.getId());

    }
}
