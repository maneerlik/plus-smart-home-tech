package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.analyzer.model.enumeration.ActionType;

@Entity
@Getter
@Setter
@ToString
@Table(name = "actions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private ActionType type;
    private int value;

    @Transient
    private String sensorId;
}
