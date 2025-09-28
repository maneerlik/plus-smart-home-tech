package ru.practicum.collector.model.sensor;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
})
@Getter
@Setter
@ToString
public abstract class SensorEvent {
    @NotBlank
    private String id;

    @NotBlank
    private String hubId;

    @JsonSetter(nulls = Nulls.SKIP)
    private Instant timestamp = Instant.now();


    public final SensorEventType getType() {
        String simpleName = this.getClass().getSimpleName();
        String snakeCaseName = toSnakeCase(simpleName);

        try {
            return SensorEventType.valueOf(snakeCaseName);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Cannot map event class '" + this.getClass().getName() +
                    "' to SensorEventType. Expected enum name: " + snakeCaseName, e);
        }
    }

    private static String toSnakeCase(String s) {
        return s.replaceAll("([A-Z])", "_$1").replaceAll("^_", "").toUpperCase();
    }
}
