package skory.productsservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import skory.productsservice.model.Order;
import skory.productsservice.model.OrderItem;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void givenOrdersCreated_whenFindAll_thenReturnOrders() {
        orderRepository.save(order());
        orderRepository.save(order());

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
    }

    @Test
    public void givenOrderCreated_whenFindById_thenReturnOrder() {
        Order toSave = order();
        Order saved = orderRepository.save(toSave);

        Optional<Order> maybeOrder = orderRepository.findById(saved.getId());

        assertThat(maybeOrder).isPresent();

        maybeOrder.ifPresent(order -> {
            assertThat(order.getBuyerEmail()).isEqualTo(toSave.getBuyerEmail());
            assertThat(order.getCreated()).isEqualTo(toSave.getCreated());
            assertThat(order.getId()).isEqualTo(saved.getId());
            assertThat(order.getOrderItems()).hasSize(2);
        });
    }

    private Order order() {
        return Order.builder()
                .buyerEmail("kacper.skory@example.com")
                .created(LocalDateTime.of(2019, 3, 3, 3, 3))
                .orderItems(Arrays.asList(orderItem("111"), orderItem("222")))
                .build();
    }

    private OrderItem orderItem(String sku) {
        return OrderItem.builder().name(sku).price(100).sku(sku).build();
    }

}