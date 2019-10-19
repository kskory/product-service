package skory.productsservice.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class OrderDto {

    private long id;

    @Email
    private String buyerEmail;

    private List<ProductDto> products;

    private long totalPrice;

    private LocalDateTime created;
}
