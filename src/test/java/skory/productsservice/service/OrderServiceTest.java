package skory.productsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skory.productsservice.dto.OrderDto;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.exception.ProductNotFoundException;
import skory.productsservice.model.Order;
import skory.productsservice.model.OrderItem;
import skory.productsservice.repository.OrderRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;

    private Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    private OrderService orderService;

    @BeforeEach
    public void setup() {
        orderService = new OrderService(clock, orderRepository, productService);
    }

    @Test
    void findAll() {
    }

    @Test
    void findOne() {
    }

    @Test
    void givenProductsExists_whenCreateOrder_thenReturnCreatedOrder() {
        OrderDto toSave = OrderDto.builder()
                .buyerEmail("hello@a.com")
                .products(asList(
                        ProductDto.builder().id(1L).build(),
                        ProductDto.builder().id(2L).build()
                ))
                .build();
        when(productService.findOne(1L)).thenReturn(of(ProductDto.builder().id(1L).price(10).sku("111").build()));
        when(productService.findOne(2L)).thenReturn(of(ProductDto.builder().id(2L).price(30).sku("222").build()));
        when(orderRepository.save(any())).thenReturn(
                Order.builder()
                        .id(123)
                        .buyerEmail(toSave.getBuyerEmail())
                        .created(LocalDateTime.now(clock))
                        .orderItems(asList(
                                OrderItem.builder().price(10).sku("111").build(),
                                OrderItem.builder().price(30).sku("222").build()
                        ))
                        .build()
        );

        OrderDto orderDto = orderService.create(toSave);

        verify(orderRepository).save(
                Order.builder()
                        .buyerEmail(toSave.getBuyerEmail())
                        .created(LocalDateTime.now(clock))
                        .orderItems(asList(
                                OrderItem.builder().price(10).sku("111").build(),
                                OrderItem.builder().price(30).sku("222").build()
                        ))
                        .build()
        );
        assertThat(orderDto.getBuyerEmail()).isEqualTo("hello@a.com");
        assertThat(orderDto.getCreated()).isEqualTo(LocalDateTime.now(clock));
        assertThat(orderDto.getProducts()).hasSize(2);
        assertThat(orderDto.getTotalPrice()).isEqualTo(40);
    }

    @Test
    void givenProductsDoesNotExist_whenCreateOrder_thenThrowExcpetion() {
        OrderDto toSave = OrderDto.builder()
                .buyerEmail("hello@a.com")
                .products(asList(
                        ProductDto.builder().id(1L).build()
                ))
                .build();
        when(productService.findOne(1L)).thenReturn(empty());

        assertThrows(ProductNotFoundException.class, () ->
                orderService.create(toSave)
        );
    }
}