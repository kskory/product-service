package skory.productsservice.service;

import org.springframework.stereotype.Service;
import skory.productsservice.dto.OrderDto;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.exception.ProductNotFoundException;
import skory.productsservice.model.Order;
import skory.productsservice.model.OrderItem;
import skory.productsservice.repository.OrderRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private Clock clock;
    private OrderRepository orderRepository;
    private ProductService productService;

    public OrderService(Clock clock, OrderRepository orderRepository, ProductService productService) {
        this.clock = clock;
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<OrderDto> findOne(long orderId) {
        return orderRepository.findById(orderId).map(this::toDto);
    }

    public OrderDto create(OrderDto order) {
        List<OrderItem> orderItems = order.getProducts().stream()
                .map(productDto -> productService.findOne(productDto.getId()).orElseThrow(ProductNotFoundException::new))
                .map(this::toOrderItem)
                .collect(Collectors.toList());

        Order toSave = Order.builder()
                .buyerEmail(order.getBuyerEmail())
                .orderItems(orderItems)
                .created(LocalDateTime.now(clock))
                .build();

        return toDto(orderRepository.save(toSave));
    }

    private OrderItem toOrderItem(ProductDto product) {
        return OrderItem.builder()
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .build();
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .buyerEmail(order.getBuyerEmail())
                .products(order.getOrderItems().stream().map(this::toDto).collect(Collectors.toList()))
                .created(order.getCreated())
                .totalPrice(getTotalPrice(order.getOrderItems()))
                .build();
    }

    private ProductDto toDto(OrderItem orderItem) {
        return ProductDto.builder()
                .sku(orderItem.getSku())
                .name(orderItem.getName())
                .price(orderItem.getPrice())
                .build();
    }

    private long getTotalPrice(List<OrderItem> products) {
        return products.stream().mapToLong(OrderItem::getPrice).sum();
    }
}
