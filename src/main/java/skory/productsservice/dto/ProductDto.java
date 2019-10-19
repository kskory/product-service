package skory.productsservice.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Value
@Builder
public class ProductDto {

    private Long id;

    @NotEmpty
    private String sku;

    @NotEmpty
    private String name;

    @Min(0)
    private long price;

    private LocalDateTime created;
}
