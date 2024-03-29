package skory.productsservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skory.productsservice.dto.OrderDto;
import skory.productsservice.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> findOne(@PathVariable long orderId) {
        return ResponseEntity.of(orderService.findOne(orderId));
    }

    @PostMapping()
    public OrderDto create(@RequestBody OrderDto order) {
        return orderService.create(order);
    }

}
