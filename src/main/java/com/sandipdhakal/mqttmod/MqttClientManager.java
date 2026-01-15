package com.sandipdhakal.mqttmod;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MqttClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttClientManager.class);
    private static MqttClientManager instance;
    
    private Mqtt3AsyncClient mqttClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    private MqttClientManager() {
    }

    public static synchronized MqttClientManager getInstance() {
        if (instance == null) {
            instance = new MqttClientManager();
        }
        return instance;
    }

    public void connect() {
        if (connected.get()) {
            LOGGER.warn("MQTT client is already connected");
            return;
        }

        try {
            String brokerAddress = Config.BROKER_ADDRESS.get();
            int brokerPort = Config.BROKER_PORT.get();
            int timeout = Config.CONNECTION_TIMEOUT.get();
            
            String clientId = "minecraft-client-" + UUID.randomUUID().toString();
            
            LOGGER.info("Connecting to MQTT broker at {}:{} with client ID: {}", 
                       brokerAddress, brokerPort, clientId);

            mqttClient = Mqtt3Client.builder()
                    .identifier(clientId)
                    .serverHost(brokerAddress)
                    .serverPort(brokerPort)
                    .buildAsync();

            mqttClient.connectWith()
                    .cleanSession(true)
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            LOGGER.error("Failed to connect to MQTT broker: {}", throwable.getMessage());
                            connected.set(false);
                        } else {
                            LOGGER.info("Successfully connected to MQTT broker");
                            connected.set(true);
                        }
                    })
                    .orTimeout(timeout, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        LOGGER.error("Connection timeout or error: {}", throwable.getMessage());
                        connected.set(false);
                        return null;
                    });

        } catch (Exception e) {
            LOGGER.error("Exception while connecting to MQTT broker", e);
            connected.set(false);
        }
    }

    public void disconnect() {
        if (mqttClient != null && connected.get()) {
            try {
                LOGGER.info("Disconnecting from MQTT broker");
                mqttClient.disconnect()
                        .whenComplete((unused, throwable) -> {
                            if (throwable != null) {
                                LOGGER.error("Error during disconnect: {}", throwable.getMessage());
                            } else {
                                LOGGER.info("Successfully disconnected from MQTT broker");
                            }
                            connected.set(false);
                        });
            } catch (Exception e) {
                LOGGER.error("Exception while disconnecting from MQTT broker", e);
            }
        }
    }

    public void publishMessage(String topic, String message) {
        if (!connected.get() || mqttClient == null) {
            LOGGER.warn("Cannot publish message - MQTT client is not connected");
            return;
        }

        try {
            mqttClient.publishWith()
                    .topic(topic)
                    .payload(message.getBytes(StandardCharsets.UTF_8))
                    .send()
                    .whenComplete((mqtt3Publish, throwable) -> {
                        if (throwable != null) {
                            LOGGER.error("Failed to publish message to topic {}: {}", 
                                       topic, throwable.getMessage());
                        } else {
                            LOGGER.debug("Successfully published message to topic: {}", topic);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Exception while publishing message", e);
        }
    }

    public boolean isConnected() {
        return connected.get();
    }
}
