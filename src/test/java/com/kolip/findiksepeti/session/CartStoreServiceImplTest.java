package com.kolip.findiksepeti.session;

import com.kolip.findiksepeti.cart.CartGenerator;
import com.kolip.findiksepeti.cart.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CartStoreServiceImpl.class)
class CartStoreServiceImplTest {

    private CartStoreServiceImpl instanceUnderTest;

    @BeforeEach
    public void setup() {
        instanceUnderTest = new CartStoreServiceImpl();
    }

    @Test
    public void store_WithCartItem_ShouldStoreTheValue() {
        //Initialize
        CartItem cartItem = CartGenerator.generateCartItem();
        cartItem.getProduct().setId(123L);
        cartItem.setId(11L);
        //Run Test
        instanceUnderTest.store(cartItem.getId(), cartItem);
        CartItem storedCartItem = instanceUnderTest.getValue(cartItem.getId());

        //Verify Result
        assertEquals(cartItem, storedCartItem);
    }

    @Test
    public void getValue_WithNotExistValue_ShouldReturnNull() {
        //Initialize
        Long notExistKey = 123L;

        //Run Test
        CartItem result = instanceUnderTest.getValue(notExistKey);

        //Verify Result
        assertNull(result);
    }

    @Test
    public void store_WithExistKey_ShouldReplace() {
        //Initialize
        Long existId = 123L;
        CartItem existCartItem = CartGenerator.generateCartItem();
        existCartItem.setQuantity(2);
        existCartItem.getProduct().setId(1111L);
        existCartItem.setId(existId);

        CartItem newCartItem = CartGenerator.generateCartItem();
        newCartItem.setQuantity(12);
        newCartItem.getProduct().setId(1111L);
        newCartItem.setId(existId);

        //Run Test
        instanceUnderTest.store(existId, existCartItem);
        instanceUnderTest.store(existId, newCartItem);
        CartItem result = instanceUnderTest.getValue(existId);

        //Verify Result
        assertEquals(newCartItem, result);
    }

    @Test
    public void delete_WithExistKey_ShouldRemove() {
        //Initialize
        Long existId = 123L;
        CartItem cartItem = CartGenerator.generateCartItem();
        cartItem.setId(existId);
        instanceUnderTest.store(existId, cartItem);

        //Run Test
        instanceUnderTest.delete(existId);
        CartItem existCartItem = instanceUnderTest.getValue(existId);

        //Verify Result
        assertNull(existCartItem);
    }

    @Test
    public void getAll_TwoItemStoredBefore_ShouldReturnTwoItems() {
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

        //Run Test
        cartItems.forEach(cartItem -> instanceUnderTest.store(cartItem.getId(), cartItem));
        List<CartItem> result = instanceUnderTest.getAll();

        //Verify Result
        assertEquals(cartItems.size(), result.size());
    }

    @Test
    public void getAll_NotStoredBefore_ShouldReturnEmptyList() {
        //Run Test
        List<CartItem> result = instanceUnderTest.getAll();

        //Verify Result
        assertEquals(0, result.size());
    }
}