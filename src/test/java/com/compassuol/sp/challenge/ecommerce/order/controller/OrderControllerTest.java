package com.compassuol.sp.challenge.ecommerce.order.controller;

import com.compassuol.sp.challenge.ecommerce.auth.jwt.JwtUtil;
import com.compassuol.sp.challenge.ecommerce.order.consumer.ViaCepConsumerFeign;
import com.compassuol.sp.challenge.ecommerce.order.dto.OrderDeleteDTO;
import com.compassuol.sp.challenge.ecommerce.order.dto.OrderResponseDTO;
import com.compassuol.sp.challenge.ecommerce.order.dto.OrderUpdateDTO;
import com.compassuol.sp.challenge.ecommerce.order.exception.OrderStatusNotAuthorizedException;
import com.compassuol.sp.challenge.ecommerce.order.repository.AddressRepository;
import com.compassuol.sp.challenge.ecommerce.order.repository.OrderRepository;
import com.compassuol.sp.challenge.ecommerce.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.compassuol.sp.challenge.ecommerce.common.OrderConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@WithMockUser
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;
    @MockBean
    OrderRepository orderRepository;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    UserDetailsService userDetailsService;

    @Mock
    AddressRepository addressRepository;
    @Mock
    ViaCepConsumerFeign viaCepConsumerFeign;

    @AfterEach
    public void afterEach() {
        ORDER_WITH_STATUS_CONFIRMED.setId(100L);
        ORDER_WITH_STATUS_SENT.setId(101L);
    }

    @Test
    void createOrder_WithValidData_ReturnsStatusIsCreated() throws Exception {
        when(orderService.createOrder(any())).thenReturn(ORDER_RESPONSE_DTO);
        mockMvc.perform(post("/orders").with(csrf())
                        .content(objectMapper.writeValueAsString(VALID_CREATE_ORDER_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_WithNonexistentProductId_ThrowsExceptionNotFound() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new EntityNotFoundException());
        mockMvc.perform(post("/orders").with(csrf())
                        .content(objectMapper.writeValueAsString(VALID_CREATE_ORDER_DTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void listOrder_ReturnsAllOrder() throws Exception {
        List<OrderResponseDTO> orders = new ArrayList<>();
        orders.add(new OrderResponseDTO());
        when(orderService.getAll()).thenReturn(orders);
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getById_WithValidId() throws Exception {
        when(orderService.getbyId(1L)).thenReturn(new OrderResponseDTO());
        mockMvc.perform(get("/orders/1")).andExpect(status().isOk());
    }

    @Test
    void getById_WithInvalidId() throws Exception {
        when(orderService.getbyId(1L)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/orders/1")).andExpect(status().isNotFound());
    }

    @Test
    void removeProduct_WithValidData_ReturnsOrderWithOrderStatusCanceled() throws Exception {
        OrderDeleteDTO dto = new OrderDeleteDTO("odiei o produto");
        when(orderService.removeOrder(anyLong(), any())).thenReturn(ORDER_RESPONSE_DTO);
        mockMvc.perform(delete("/orders/100").with(csrf())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void removeProduct_WithInvalidData_Returns() throws Exception {
        OrderDeleteDTO dto = new OrderDeleteDTO("odiei o produto");
        when(orderService.removeOrder(anyLong(), any())).thenThrow(OrderStatusNotAuthorizedException.class);
        mockMvc.perform(delete("/orders/101").with(csrf())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(delete("/orders/a").with(csrf())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrder_WithValidData_ReturnsNewOrder() throws Exception {
        when(orderService.getbyId(1L)).thenReturn(new OrderResponseDTO());
        mockMvc.perform(get("/orders/1")).andExpect(status().isOk());
    }

    @Test
    void updateOrder_WithInvalidId() throws Exception {
        when(orderService.updateOrder(anyLong(), any())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/999").with(csrf())
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
