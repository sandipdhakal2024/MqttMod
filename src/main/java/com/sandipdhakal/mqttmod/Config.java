package com.sandipdhakal.mqttmod;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.ConfigValue<String> BROKER_ADDRESS;
    public static final ModConfigSpec.IntValue BROKER_PORT;
    public static final ModConfigSpec.ConfigValue<String> MQTT_TOPIC;
    public static final ModConfigSpec.IntValue CONNECTION_TIMEOUT;

    static {
        BUILDER.comment("MQTT Player Health Monitor Configuration").push("mqtt");

        ENABLED = BUILDER
                .comment("Enable or disable the MQTT mod")
                .define("enabled", true);

        BROKER_ADDRESS = BUILDER
                .comment("MQTT broker address")
                .define("broker_address", "broker.hivemq.com");

        BROKER_PORT = BUILDER
                .comment("MQTT broker port")
                .defineInRange("broker_port", 1883, 1, 65535);

        MQTT_TOPIC = BUILDER
                .comment("MQTT topic for publishing player health data")
                .define("mqtt_topic", "minecraft/player/health");

        CONNECTION_TIMEOUT = BUILDER
                .comment("Connection timeout in seconds")
                .defineInRange("connection_timeout", 30, 1, 300);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
