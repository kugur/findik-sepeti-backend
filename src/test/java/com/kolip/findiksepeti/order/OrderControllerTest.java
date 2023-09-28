package com.kolip.findiksepeti.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    public OrderService orderService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
    }

    @Test
    public void createOrder_withValidInput_ShouldCallOrderService() throws Exception {
        //Initialize
        Order order = OrderGenerator.createOder(2);
        when(orderService.createOrder(any())).thenReturn(OrderStatus.ORDER_CREATED);


        //Run Test
        mockMvc.perform(
                        post("/order").content(objectMapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$").value(OrderStatus.ORDER_CREATED.name()));


        verify(orderService).createOrder(
                argThat(argument -> argument.getOrderItems().size() == order.getOrderItems().size() &&
                        order.getPayment().getCreditCardNumber().equals(argument.getPayment().getCreditCardNumber())));
    }
}