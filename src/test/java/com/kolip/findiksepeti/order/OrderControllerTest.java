package com.kolip.findiksepeti.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolip.findiksepeti.config.LibraryConfiguration;
import com.kolip.findiksepeti.filters.Filter;
import com.kolip.findiksepeti.filters.FilterConverter;
import com.kolip.findiksepeti.pagination.PageRequestConverter;
import com.kolip.findiksepeti.products.Product;
import com.kolip.findiksepeti.products.ProductGenerator;
import com.kolip.findiksepeti.shipping.Shipping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PageRequestConverter.class, FilterConverter.class})
@ContextConfiguration(classes = {LibraryConfiguration.class})
class OrderControllerTest {

    private MockMvc mockMvc;
    //    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    public OrderService orderService;
    @Autowired
    private PageRequestConverter pageRequestConverter;

    @Autowired
    private FilterConverter filterConverter;


    @BeforeEach
    public void setUp() {
        String javaVersion = System.getProperty("java.version");
        System.out.println("Java Version: " + javaVersion);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, pageRequestConverter, filterConverter)).build();
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
        when(orderService.getOrders(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/order").param("pageInfo", pageRequest)).andExpect(status().isOk());

        //Verify Result
        verify(orderService).getOrders(any());
        assertEquals(pageNumber, pageRequestArgument.getValue().getPageNumber());
        assertEquals(pageSize, pageRequestArgument.getValue().getPageSize());
    }

    @Test
    public void getAllOrders_withPageRequest_ShouldCallGetAllOrders() throws Exception {
        //Initialize
        int pageNumber = 2;
        int pageSize = 5;
        String pageRequest = "{\"page\": " + pageNumber + ", \"size\":" + pageSize + "}";
        List<Order> orders = createOrders();
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        when(orderService.getAllOrders(pageRequestArgument.capture(), any())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/orderAll").param("pageInfo", pageRequest)).andExpect(status().isOk());

        //Verify Result
        verify(orderService).getAllOrders(any(), any());
        assertEquals(pageNumber, pageRequestArgument.getValue().getPageNumber());
        assertEquals(pageSize, pageRequestArgument.getValue().getPageSize());
    }

    @Test
    public void getAllOrders_WithFilter_ShouldCallWithFilterObject() throws Exception {
        //Initialize
        String filterName = "status";
        String filterValue = OrderStatus.ORDER_CREATED.name();
        String filterJson = "[{\"name\":\"" + filterName + "\", \"operation\":\"EQUAL\" , \"value\":\"" + filterValue + "\"}]";
        List<Order> orders = createOrders();
        ArgumentCaptor<List<Filter>> filterArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(orderService.getAllOrders(any(), filterArgumentCaptor.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/orderAll").param("filters", filterJson)).andExpect(status().isOk());

        //Verify Result
        verify(orderService).getAllOrders(any(), any());
        assertEquals(1, filterArgumentCaptor.getValue().size());
        assertEquals(filterName, filterArgumentCaptor.getValue().get(0).getName());
        assertEquals(filterValue, filterArgumentCaptor.getValue().get(0).getValue());
    }

    @Test
    public void getOrders_withoutPageRequest_ShouldCallFirstOrderPage() throws Exception {
        //Initialize
        ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);
        List<Order> orders = createOrders();
        when(orderService.getOrders(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

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
        when(orderService.getOrders(pageRequestArgument.capture())).thenReturn(new PageImpl<>(orders));

        //Run Test
        mockMvc.perform(get("/order")).andExpect(status().isOk());
        assertTrue(Objects.requireNonNull(pageRequestArgument.getValue().getSort().getOrderFor("id")).isDescending());
    }

    @Test
    public void updateStatus_WithStatus_ShouldCallUpdateService() throws Exception {
        //Initialize
        String status = OrderStatus.PLACE_ORDERED.name();
        String orderId = "123";
        when(orderService.updateStatus(eq(Long.parseLong(orderId)), eq(OrderStatus.PLACE_ORDERED))).thenReturn(true);

        //Run Test
        mockMvc.perform(put("/orderStatus/" + orderId).param("status", status)).andExpect(status().isOk());

        //Verify
        verify(orderService).updateStatus(eq(Long.parseLong(orderId)), eq(OrderStatus.valueOf(status)));
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