package com.sandipdhakal.mqttmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MqttMod.MODID)
public class MqttMod {
    public static final String MODID = "mqttmod";
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttMod.class);

    public MqttMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing MQTT Player Health Monitor Mod");
        
        // Register configuration
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        // Register the common setup method
        modEventBus.addListener(this::commonSetup);
        
        // Register server stopping event
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        
        // Register player damage event handler
        NeoForge.EVENT_BUS.register(new PlayerHealthEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MQTT Mod common setup");
        
        // Initialize MQTT client on a separate thread to avoid blocking
        event.enqueueWork(() -> {
            if (Config.ENABLED.get()) {
                LOGGER.info("MQTT Mod is enabled, initializing MQTT client");
                MqttClientManager.getInstance().connect();
            } else {
                LOGGER.info("MQTT Mod is disabled in config");
            }
        });
    }

    private void onServerStopping(final ServerStoppingEvent event) {
        LOGGER.info("Server stopping, disconnecting MQTT client");
        MqttClientManager.getInstance().disconnect();
    }
}
