package com.kolip.findiksepeti.cart;

import com.kolip.findiksepeti.session.CartStoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CartServiceImplTest.class)
class CartServiceImplTest {

    private CartService instanceUnderTest;

    @MockBean
    private CartStoreServiceImpl sessionStoreService;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new CartServiceImpl(sessionStoreService);
    }

    @Test
    public void addItem_WithValidItem_ShouldCallSessionStorage() {
        //initialize
        CartItem cartItem = CartGenerator.generateCartItem();
        cartItem.setId(11L);

        //Run Test
        boolean result = instanceUnderTest.addItem(cartItem);

        //Verify Result
        verify(sessionStoreService).store(eq(cartItem.getId()), eq(cartItem));
        assertTrue(result);
    }

    @Test
    public void addItem_WithNotValidItem_ShouldReturnFalse() {
        //Initialize
        CartItem cartItem = CartGenerator.generateCartItem();
        cartItem.setProduct(null);

        //Run Test
        boolean result = instanceUnderTest.addItem(cartItem);

        //Verify Result
        verify(sessionStoreService, times(0)).store(any(), any());
        assertFalse(result);
    }

    @Test
    public void delete_WithValidId_ShouldCallStoreService() {
        //Initialize
        Long cartItemId = 123L;

        //Run Test
        boolean result = instanceUnderTest.deleteItem(cartItemId);

        //Verify Result
        verify(sessionStoreService).delete(eq(cartItemId));
        assertTrue(result);
    }

    @Test
    public void getCartItems_WithTwoItemsOnList_ShouldReturnList() {
        //Initialize
        CartItem cartItem1 = CartGenerator.generateCartItem();
        cartItem1.getProduct().setId(1L);
        cartItem1.setId(111L);
        CartItem cartItem2 = CartGenerator.generateCartItem();
        cartItem2.getProduct().setId(2L);
        cartItem2.setId(112L);
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);
        when(sessionStoreService.getAll()).thenReturn(cartItems);

        //Run Test
        List<CartItem> result = instanceUnderTest.getCartItems();

        //Verify
        assertEquals(cartItems.size(), result.size());
        verify(sessionStoreService).getAll();
    }
}