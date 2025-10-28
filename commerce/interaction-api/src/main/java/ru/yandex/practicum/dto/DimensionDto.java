package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @NotNull
    @Positive
    private double width;

    @NotNull
    @Positive
    private double height;

    @NotNull
    @Positive
    private double depth;
}
