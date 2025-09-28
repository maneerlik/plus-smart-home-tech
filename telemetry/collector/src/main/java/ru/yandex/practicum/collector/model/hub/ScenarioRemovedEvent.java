package ru.practicum.collector.model.hub;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @Size(min = 3, message = "name must contain at least 3 characters")
    private String name;
}
