package com.kolip.findiksepeti.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.pagination.PageRequestConverter;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductGenerator;
import com.kolip.findiksepeti.shipping.Shipping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PageRequestConverter.class})
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    public OrderService orderService;
    private PageRequestConverter pageRequestConverter;


    @BeforeEach
    public void setUp() {
        pageRequestConverter = new PageRequestConverter();
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, pageRequestConverter)).build();
    }

    @Test
    public void createOrder_withValidInput_ShouldCallOrderService() throws Exception {
        //Initialize
        Order order = OrderGenerator.createOder(2);
        System.out.println(objectMapper.writeValueAsString(order));
        when(orderService.createOrder(any())).thenReturn(OrderStatus.ORDER_CREATED);


        //Run Test
        mockMvc.perform(
                        post("/order").content(objectMapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$").value(OrderStatus.ORDER_CREATED.name()));


        verify(orderService).createOrder(
                argThat(argument -> argument.getOrderItems().size() == order.getOrderItems().size() &&
                        order.getPayment().getCreditCardNumber().equals(argument.getPayment().getCreditCardNumber())));
    }

    @Test
    public void getOrders_withPageRequest_ShouldCallOrderService() throws Exception {
        //Initialize
        int pageNumber = 2;
        int pageSize = 5;
        String pageRequest = "{\"page\": " + pageNumber + ", \"size\":" + pageSize + "}";
        List<Order> orders = createOrders();
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        when(orderService.getProducts(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/order").param("pageInfo", pageRequest)).andExpect(status().isOk());

        //Verify Result
        verify(orderService).getProducts(any());
        assertEquals(pageNumber, pageRequestArgument.getValue().getPageNumber());
        assertEquals(pageSize, pageRequestArgument.getValue().getPageSize());
    }

    @Test
    public void getOrders_withoutPageRequest_ShouldCallFirstOrderPage() throws Exception {
        //Initialize
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        List<Order> orders = createOrders();
        when(orderService.getProducts(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/order")).andExpect(status().isOk());
        assertEquals(0, pageRequestArgument.getValue().getPageNumber());
        assertEquals(10, pageRequestArgument.getValue().getPageSize());
    }

    @Test
    public void getOrders_withoutOrderParam_ShouldCallIdDesc() throws Exception {
        //Initialize
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        List<Order> orders = createOrders();
        when(orderService.getProducts(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/order")).andExpect(status().isOk());
        assertTrue(Objects.requireNonNull(pageRequestArgument.getValue().getSort().getOrderFor("id")).isDescending());
    }

    private List<Order> createOrders() {
        Order order = new Order();
        order.setId(11L);
        order.setStatus(OrderStatus.ORDER_CREATED);

        Shipping shipping = new Shipping();
        shipping.setAddress("adres alanai vs ");
        shipping.setName("uur kol");
        shipping.setNote("note alani");
        order.setShipping(shipping);

        OrderItem orderItem = new OrderItem();
        Product product = ProductGenerator.createProduct(1L);
        orderItem.setProduct(product);
        orderItem.setQuantity(4);
        order.setOrderItems(List.of(orderItem));

        return List.of(order);
    }
}