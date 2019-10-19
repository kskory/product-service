package skory.productsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import skory.productsservice.dto.ProductDto;
import skory.productsservice.exception.DuplicateSkuException;
import skory.productsservice.service.ProductService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    public void givenProductDoesNotExist_whenFindOne_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/v1/products/111"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenProductExist_whenFindOne_thenReturnProduct() throws Exception {
        ProductDto product = ProductDto.builder().id(123L).sku("111-111").name("aaa").price(100).created(LocalDateTime.now()).build();
        when(productService.findOne(123)).thenReturn(Optional.of(product));

        ResultActions resultActions = mockMvc.perform(get("/v1/products/123"))
                .andExpect(status().isOk());

        ProductDto returned = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), ProductDto.class);
        assertThat(returned).isEqualTo(product);
    }

    @Test
    public void givenProductWithGivenSkuAlreadyExists_whenCreate_thenReturnBadRequest() throws Exception {
        when(productService.create(any())).thenThrow(DuplicateSkuException.class);

        mockMvc.perform
                (
                        post("/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ProductDto.builder().sku("111").name("aaa").price(123).build()))
                )
                .andExpect(status().isBadRequest());
    }

    //TODO remaining tests
}