package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository",userRepository);
        TestUtils.injectObjects(orderController,"orderRepository",orderRepository);
    }

    @Test
    public void submit_order(){
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
        //added two items
        cart.addItem(item);
        cart.addItem(item);

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserOrder userOrder = response.getBody();
        assertThat(userOrder.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(userOrder.getItems()).hasSize(2);
        assertThat(userOrder.getTotal()).isEqualTo(new BigDecimal(20));

    }

    @Test
    public void get_order_for_user(){
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
        //added two items
        cart.addItem(item);
        cart.addItem(item);
        //make userorder from cart
        UserOrder userOrder = UserOrder.createFromCart(cart);

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Mockito.when(orderRepository.findByUser(user)).thenReturn(Lists.newArrayList(userOrder));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //  assert that whe get the order
        List<UserOrder> userOrders = response.getBody();
        assertThat(userOrders).hasSize(1);
        assertThat(userOrders.get(0).getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(userOrders.get(0).getItems()).hasSize(2);
        assertThat(userOrders.get(0).getTotal()).isEqualTo(new BigDecimal(20));
    }
}
