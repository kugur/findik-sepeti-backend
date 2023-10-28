package com.kolip.findiksepeti.payment;

import com.kolip.findiksepeti.AbstractTest;
import com.kolip.findiksepeti.categories.Category;
import com.kolip.findiksepeti.categories.CategoryRepository;
import com.kolip.findiksepeti.order.Order;
import com.kolip.findiksepeti.order.OrderGenerator;
import com.kolip.findiksepeti.order.OrderRepository;
import com.kolip.findiksepeti.order.OrderStatus;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductRepository;
import com.kolip.findiksepeti.user.CustomUser;
import com.kolip.findiksepeti.user.Gender;
import com.kolip.findiksepeti.user.Role;
import com.kolip.findiksepeti.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest extends AbstractTest {
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
    @Disabled
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

    @Test
    public void getOrders_WithUser_ShouldReturnOnlyRelatedOrders() {
        //Initialize
        CustomUser user1 = persistUser();
        Order toBePersistedOrderByUser1 = generateOrderWithPersistedProducts(2);
        toBePersistedOrderByUser1.setUser(user1);
        orderRepository.saveAndFlush(toBePersistedOrderByUser1);
        toBePersistedOrderByUser1 = generateOrderWithPersistedProducts(3);
        toBePersistedOrderByUser1.setUser(user1);
        Order orderByUser1 = orderRepository.saveAndFlush(toBePersistedOrderByUser1);

        CustomUser user2 = persistUser();
        Order toBePersistedOrderByUser2 = generateOrderWithPersistedProducts(3);
        toBePersistedOrderByUser2.setUser(user2);
        Order orderByUser2 = orderRepository.saveAndFlush(toBePersistedOrderByUser2);
        System.out.println("----------------- Inserts are finished.!!");
        //Run Test
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> resultForUser1 = orderRepository.findByUserId(pageRequest, user1.getId());
        Page<Order> resultForUser2 = orderRepository.findByUserId(pageRequest, user2.getId());


        //Verify Result
        assertNotEquals(orderByUser1.getId(), orderByUser2.getId());
        assertEquals(2, resultForUser1.getTotalElements());
        assertEquals(orderByUser1.getId(), resultForUser1.stream().findFirst().get().getId());

        assertEquals(orderByUser2.getId(), resultForUser2.get().findFirst().get().getId());
    }

    @Test
    public void updateStatus_WithValidValues_ShouldUpdateStatus() {
        //Initialize
        OrderStatus newStatus = OrderStatus.INVALID_ORDER;
        Order order = generateOrderWithPersistedProducts(2);
        order = orderRepository.saveAndFlush(order);
        entityManager.clear();

        //Run Test
        orderRepository.updateStatusById(newStatus, order.getId());

        //Verify Result
        Optional<Order> updatedOrder = orderRepository.findById(order.getId());
        assertFalse(updatedOrder.isEmpty());
        assertEquals(newStatus, updatedOrder.get().getStatus());
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
                Set.of(new Role("ROLE_USER")));
        user = userRepository.saveAndFlush(user);
        return user;
    }

}