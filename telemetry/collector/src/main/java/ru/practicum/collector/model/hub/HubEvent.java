package ru.practicum.collector.model.hub;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.collector.model.hub.enums.HubEventType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED"),
})
@Getter
@Setter
@ToString
public class HubEvent {
    @NotBlank
    private String hubId;

    @JsonSetter(nulls = Nulls.SKIP)
    private Instant timestamp;


    public final HubEventType getType() {
        String simpleName = this.getClass().getSimpleName();
        simpleName = simpleName.substring(0, simpleName.length() - "Event".length());
        String snakeCaseName = toSnakeCase(simpleName);

        try {
            return HubEventType.valueOf(snakeCaseName);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Cannot map event class '" + this.getClass().getName() +
                    "' to SensorEventType. Expected enum name: " + snakeCaseName, e);
        }
    }

    private static String toSnakeCase(String s) {
        return s.replaceAll("([A-Z])", "_$1").replaceAll("^_", "").toUpperCase();
    }
}
