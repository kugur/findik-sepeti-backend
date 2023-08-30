package com.kolip.findiksepeti.cart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CartControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CartController()).build();
    }

    @Test
    public void addProduct_withValidCartItems_ShouldReturnCart() {

    }

    @Test
    void addTest_deneme() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(get("/carts").session(session)).andExpect(status().isOk());

        mockMvc.perform(get("/cartsResult").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1234));
    }

}