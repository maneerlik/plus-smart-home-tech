package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Dimension {
    @Column(name = "dimension_width")
    private double width;

    @Column(name = "dimension_height")
    private double height;

    @Column(name = "dimension_depth")
    private double depth;

    public double volume() {
        return width * height * depth;
    }
}
