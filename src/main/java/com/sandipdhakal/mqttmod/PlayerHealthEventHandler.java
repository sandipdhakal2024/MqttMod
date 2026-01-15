package com.sandipdhakal.mqttmod;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerHealthEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerHealthEventHandler.class);

    @SubscribeEvent
    public void onPlayerDamage(LivingDamageEvent.Post event) {
        // Check if the damaged entity is a player
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Check if the mod is enabled
        if (!Config.ENABLED.get()) {
            return;
        }

        try {
            // Get player health information
            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            String playerName = player.getName().getString();
            String playerUUID = player.getUUID().toString();
            long timestamp = System.currentTimeMillis() / 1000;

            // Create JSON payload
            JsonObject json = new JsonObject();
            json.addProperty("playerName", playerName);
            json.addProperty("playerUUID", playerUUID);
            json.addProperty("health", health);
            json.addProperty("maxHealth", maxHealth);
            json.addProperty("timestamp", timestamp);

            String message = json.toString();
            String topic = Config.MQTT_TOPIC.get();

            LOGGER.debug("Player {} took damage. Health: {}/{}", playerName, health, maxHealth);

            // Publish to MQTT asynchronously
            MqttClientManager.getInstance().publishMessage(topic, message);

        } catch (Exception e) {
            LOGGER.error("Error while processing player damage event", e);
        }
    }
}
