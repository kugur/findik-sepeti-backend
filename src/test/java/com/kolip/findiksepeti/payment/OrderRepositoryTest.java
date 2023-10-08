package com.kolip.findiksepeti.payment;

import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.categories.CategoryRepository;
import com.kolip.findiksepeti.order.Order;
import com.kolip.findiksepeti.order.OrderGenerator;
import com.kolip.findiksepeti.order.OrderRepository;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductRepository;
import com.kolip.findiksepeti.user.CustomUser;
import com.kolip.findiksepeti.user.Gender;
import com.kolip.findiksepeti.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryTest {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public CategoryRepository categoryRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    public void save_WithValidOrder_PersistSuccessfully() {
        //Initialize
        Order toBePersistedOrder = generateOrderWithPersistedProducts(10);

        //Run Test
        Order persistedOrder = orderRepository.save(toBePersistedOrder);

        //Verify Result
        assertEquals(toBePersistedOrder.getOrderItems().size(), persistedOrder.getOrderItems().size());
        assertNotNull(persistedOrder.getId());
        assertNotNull(persistedOrder.getShipping().getAddress());
        assertNotNull(persistedOrder.getCreatedDate());
        assertEquals(LocalDateTime.now().withSecond(0).withNano(0),
                     persistedOrder.getCreatedDate().withSecond(0).withNano(0));
    }

    @Test
    public void save_WithUser_PersistSuccessfully() {
        //Initialize
        Order toBePersistedOrder = generateOrderWithPersistedProducts(10);
        CustomUser user = persistUser();
        toBePersistedOrder.setUser(user);

        //Run Test
        Order persistedOrder = orderRepository.save(toBePersistedOrder);

        //Verify Result
        assertNotNull(persistedOrder.getUser());
        assertEquals(user.getId(), persistedOrder.getUser().getId());
    }

    @Test
    public void save_WithUpdatedUser_ShouldNotUpdateTheUser() {
        //Initialize
        Order toBePersistedOrder = generateOrderWithPersistedProducts(10);
        CustomUser user = persistUser();
        user.setFirstName("New Name");
        entityManager.flush();
        entityManager.clear();
        toBePersistedOrder.setUser(user);

        System.out.println("--------- Persisted completed.-------------");

        //Run Test
       orderRepository.save(toBePersistedOrder);
        Optional<CustomUser> userOnDb = userRepository.findById(user.getId());

        //Verify Result
        assertFalse(userOnDb.isEmpty());
        assertNotEquals(user.getFirstName(), userOnDb.get().getFirstName());
    }

    @Test
    public void get_WithValidPageRequest_ShouldReturnResult() {
        //Initialize
        int itemCount = 10;
        int orderCount = 3;
        for (int i = 0; i < orderCount; i++) {
            Order toBePersistedOrder = generateOrderWithPersistedProducts(itemCount);
            orderRepository.saveAndFlush(toBePersistedOrder);
        }
        orderRepository.flush();
        entityManager.flush();
        entityManager.clear();

        System.out.println("--------- Persisted completed.-------------");

        //Run Test
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> result = orderRepository.findAll(pageRequest);


        //Verify Result
        assertEquals(orderCount, result.getTotalElements());
        assertNotNull(result.get().findFirst().get().getShipping().getAddress());
        assertEquals(itemCount, result.get().findFirst().get().getOrderItems().size());
        assertNotNull(result.get().findFirst().get().getOrderItems().get(0).getProduct().getId());
    }

    private Order generateOrderWithPersistedProducts(int orderItemCount) {
        Order order = OrderGenerator.createOder(orderItemCount);
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

    private CustomUser persistUser() {
        CustomUser user = new CustomUser("uur", "klp", "1234", "uur@gmail.com", "adres alani", Gender.MALE,
                                         List.of(new SimpleGrantedAuthority("ROLE_USER")));
        user = userRepository.saveAndFlush(user);
        return user;
    }

}