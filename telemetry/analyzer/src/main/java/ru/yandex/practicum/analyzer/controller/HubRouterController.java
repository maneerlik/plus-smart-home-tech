package ru.yandex.practicum.analyzer.controller;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Service
public class HubRouterController {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterController(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient
    ) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(DeviceActionRequest deviceActionRequest) {
        try {
            log.info("Hub request: {}", deviceActionRequest);
            hubRouterClient.handleDeviceAction(deviceActionRequest);
        } catch (Exception e) {
            log.error("Could not send data");
        }
    }
}
