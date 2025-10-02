## Collector Service

Сервис **Collector** предназначен для приема событий от устройств умного дома через gRPC-интерфейс и их передачи 
в **Apache Kafka** для дальнейшей обработки, шлюз между устройствами умного дома и системой аналитики

---

### Функциональность

Сервис реализует gRPC-сервер с двумя методами:

- **`CollectSensorEvent`** - принимает события от подключённых к хабу устройств (датчиков температуры, влажности, 
освещенности и т.д.)
- **`CollectHubEvent`** - принимает события, связанные с хабом или логикой умного дома (н.п. добавление устройства, 
срабатывание сценария и пр.)

Все входящие данные передаются в бинарном формате **Protocol Buffers (Protobuf)**

После приёма и валидации событие преобразуется в формат **Apache Avro** и отправляется в соответствующий топик **Kafka**:

| Тип события       | Kafka-топик               |
|-------------------|---------------------------|
| События датчиков  | `telemetry.sensors.v1`    |
| События хаба      | `telemetry.hubs.v1`       |

---

### Архитектура

- Обработчики событий (интерфейсы `SensorEventHandler`, `HubEventHandler`) регистрируются как Spring-компоненты
- Каждый обработчик отвечает за конкретный тип события (определённый через `PayloadCase` в сгенерированном стабе 
Protobuf)
- Маппинг из Protobuf в Avro выполняется в абстрактных базовых классах (`BaseSensorEventHandler`, 
`BaseHubEventHandler`)
- Отправка в Kafka реализована через класс `EventProducer`

При получении неизвестного типа события сервис возвращает ошибку `UnknownEventTypeException`

---

### Стек

- **gRPC** - для RPC-взаимодействия
- **Protocol Buffers** - для сериализации входящих данных
- **Apache Avro** - для внутреннего представления событий
- **Apache Kafka** - брокер очередей для централизованной передачи
- **Spring Boot** - DI и конфигурация

---

### Пример использования

#### Отправка события климатического датчика

```protobuf
// SensorEventProto
{
  id: "sensor-123",
  hub_id: "hub-456",
  timestamp: { seconds: 1712345678, nanos: 0 },
  climate_sensor: {
    temperature_c: 22.5,
    humidity: 45.0,
    co2_level: 600
  }
}
```

-> Преобразуется в `ClimateSensorAvro` -> Отправляется в топик `telemetry.sensors.v1` с ключом `hub-456`

#### Отправка события добавления устройства

```protobuf
// HubEventProto
{
  hub_id: "hub-456",
  timestamp: { seconds: 1712345680, nanos: 0 },
  device_added: {
    id: "device-789",
    type: LIGHT
  }
}
```

-> Преобразуется в `DeviceAddedEventAvro` -> Отправляется в топик `telemetry.hubs.v1` с ключом `hub-456`

---

### Запуск

Сервис запускается как обычное Spring Boot-приложение. Требуется настройка:

- gRPC-порта
- подключения к Kafka (bootstrap servers, сериализаторы Avro)
- имён топиков (через `KafkaTopicsProperties`)

Пример конфигурации в `application.yml`:

```yaml
grpc:
  port: 9090

kafka:
  bootstrap-servers: localhost:9092
  topics:
    telemetry-sensors-v1: telemetry.sensors.v1
    telemetry-hubs-v1: telemetry.hubs.v1
```

---

### Расширение

Добавить поддержку нового типа события:

1. Создать новый Protobuf-тип в `SensorEventProto` или `HubEventProto`
2. Реализовать обработчик, унаследовав `BaseSensorEventHandler` или `BaseHubEventHandler`
3. Пометить класс аннотацией `@Component`
4. Соответствующий Avro-класс существует и поддерживается `AvroMapper`

Сервис автоматически зарегистрирует новый обработчик при старте используя регистрацию обработчиков, реализованную 
через паттерн **Стратегия**

---
