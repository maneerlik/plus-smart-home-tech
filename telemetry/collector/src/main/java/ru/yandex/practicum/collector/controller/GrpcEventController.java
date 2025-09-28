package ru.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.collector.exception.UnknownEventTypeException;
import ru.practicum.collector.service.handler.hub.HubEventHandler;
import ru.practicum.collector.service.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class GrpcEventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public GrpcEventController(
            List<SensorEventHandler> sensorEventHandlers,
            List<HubEventHandler> hubEventHandlers
    ) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }


    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();

            // проверить, есть ли обработчик для полученного события
            if (sensorEventHandlers.containsKey(payloadCase)) {
                // если обработчик найден, передать событие ему на обработку
                sensorEventHandlers.get(payloadCase).handle(request);
            } else {
                throw new UnknownEventTypeException("Unknown sensor event type: " + payloadCase);
            }

            // после обработки события возвращает ответ клиенту
            responseObserver.onNext(Empty.getDefaultInstance());

            // завершает обработку запроса
            responseObserver.onCompleted();
        } catch (Exception e) {
            // в случае исключения отправляем ошибку клиенту
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();

            if (hubEventHandlers.containsKey(payloadCase)) {
                hubEventHandlers.get(payloadCase).handle(request);
            } else {
                throw new UnknownEventTypeException("Unknown hub event type: " + payloadCase);
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}
