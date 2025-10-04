package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.analyzer.model.enumeration.ConditionOperation;
import ru.yandex.practicum.analyzer.model.enumeration.ConditionType;

@Entity
@Getter
@Setter
@ToString
@Table(name = "conditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    private ConditionOperation operation;
    private int value;

    @Transient
    private String sensorId;
}
