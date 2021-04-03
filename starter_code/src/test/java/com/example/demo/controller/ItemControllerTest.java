package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepository);
    }

    @Test
    public void get_Item_By_Id(){

        Item item = new Item();
        item.setId(1L);
        item.setName("Chocolate");
        item.setPrice(BigDecimal.TEN);

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(item.getId());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Item returned_item = response.getBody();
        assertThat(returned_item.getId()).isEqualTo(item.getId());
        assertThat(returned_item.getName()).isEqualTo(item.getName());
    }

    @Test
    public void get_Item_By_Name(){

        Item item = new Item();
        item.setId(1L);
        item.setName("Chocolate");
        item.setPrice(BigDecimal.TEN);

        Mockito.when(itemRepository.findByName(item.getName())).thenReturn(Lists.newArrayList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Item> returned_items = response.getBody();
        assertThat(returned_items).hasSize(1);
        assertThat(returned_items.get(0).getId()).isEqualTo(item.getId());
        assertThat(returned_items.get(0).getName()).isEqualTo(item.getName());
    }
}
