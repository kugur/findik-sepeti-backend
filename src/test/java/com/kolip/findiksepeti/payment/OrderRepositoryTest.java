package com.kolip.findiksepeti.payment;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.categories.CategoryRepository;
import com.kolip.findiksepeti.order.Order;
import com.kolip.findiksepeti.order.OrderGenerator;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryTest {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public CategoryRepository categoryRepository;

    @Test
    public void save_WithValidOrder_PersistSuccessfully() {
        //Initialize
        Order toBePersistedOrder = generateOrderWithPersistedProducts(2);

        //Run Test
        Order persistedOrder = orderRepository.save(toBePersistedOrder);

        //Verify Result
        assertEquals(toBePersistedOrder.getOrderItems().size(), persistedOrder.getOrderItems().size());
        assertNotNull(persistedOrder.getId());
        assertNotNull(persistedOrder.getShipping().getId());
    }

    private Order generateOrderWithPersistedProducts(int orderItemCount) {
        Order order = OrderGenerator.createOder(2);
        order.getOrderItems().forEach(orderItem -> {
            orderItem.setProduct(persistProduct(orderItem.getProduct()));
        });
        return order;
    }

    private Product persistProduct(Product product) {
        Category toBePersistedCategory = product.getCategory();
        toBePersistedCategory.setId(null);
        Category persistedCategory = categoryRepository.saveAndFlush(toBePersistedCategory);
        product.setCategory(persistedCategory);
        product.setId(null);
        return productRepository.saveAndFlush(product);
    }

}