package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ProductPageDto {
    private List<ProductDto> content;
    private List<SortDto> sort;

    @Data
    @AllArgsConstructor
    public static class SortDto {
        private String property;
        private String direction;
    }
}
