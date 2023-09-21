package com.kolip.findiksepeti.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.session.CartStoreServiceImpl;
import com.kolip.findiksepeti.session.SessionStoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InOrder;
import org.mockito.internal.InOrderImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {CartServiceImpl.class, CartStoreServiceImpl.class})
class CartControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private CartService cartService;

    @MockBean
    private SessionStoreService sessionStoreService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService)).build();
    }

    @Test
    public void addProduct_withValidCartItem_ShouldReturnTrue() throws Exception {
        //Initialize
        CartItem cartItem = CartGenerator.generateCartItem();
        String cartItemJson = objectMapper.writeValueAsString(cartItem);
        when(cartService.addItem(any())).thenReturn(true);

        //Run method
        mockMvc.perform(post("/cart").content(cartItemJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService).addItem(any());
    }

    @Test
    void cartItemSerialize_Test() {
        CartItem cartItem = CartGenerator.generateCartItem();
        String cartItemJson = null;
        try {
            cartItemJson = objectMapper.writeValueAsString(cartItem);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        CartItem result = null;
        try {
            result = objectMapper.readValue(cartItemJson, CartItem.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
    }

    @Test
    public void delete_WithValidId_ShouldRemoveItem() throws Exception {
        //Initialize
        String productId = "123";
        when(cartService.deleteItem(eq(Long.parseLong(productId)))).thenReturn(true);

        //Run Test
        mockMvc.perform(delete("/cart").param("productId", productId))
                .andExpect(status().isOk());

        //Verify Result
        verify(cartService).deleteItem(eq(Long.parseLong(productId)));
    }

    @Test
    public void get_WithoutParam_ShouldReturnCartItemList() throws Exception {
        //Initialize
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem1 = CartGenerator.generateCartItem();
        cartItem1.getProduct().setId(12L);
        CartItem cartItem2 = CartGenerator.generateCartItem();
        cartItem2.getProduct().setId(23L);
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);

        when(cartService.getCartItems()).thenReturn(cartItems);

        //Run Test
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        //Verify
        verify(cartService).getCartItems();
    }
}
