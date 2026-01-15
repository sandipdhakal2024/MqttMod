# MQTT Player Health Monitor

A NeoForge 1.21.1 Minecraft mod that publishes player health data to an MQTT broker whenever a player takes damage.

## Description

This mod monitors player health in Minecraft and publishes real-time health updates to an MQTT broker using the HiveMQ MQTT client library. Every time a player is damaged, the mod sends a JSON payload containing the player's name, UUID, current health, max health, and timestamp to a configurable MQTT topic.

## Features

- 🔌 **Automatic MQTT Connection**: Connects to MQTT broker on server startup
- 📊 **Real-time Health Monitoring**: Publishes player health data when damage occurs
- ⚙️ **Configurable Settings**: Customize broker address, port, topic, and more
- 🔒 **Thread-Safe Operations**: Asynchronous MQTT publishing to avoid blocking the game thread
- 🛡️ **Graceful Error Handling**: Logs errors without crashing the game
- 🎮 **Single & Multiplayer Support**: Works in both game modes

## Installation

### Prerequisites

- Minecraft 1.21.1
- NeoForge 21.1.73 or higher
- Java 21

### Steps

1. Download the latest release of the mod from the [Releases](https://github.com/sandipdhakal2024/MqttMod/releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Start your Minecraft server or client
4. Configure the mod (see Configuration section below)

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/sandipdhakal2024/MqttMod.git
   cd MqttMod
   ```

2. Build the mod:
   ```bash
   ./gradlew build
   ```

3. The compiled mod will be in `build/libs/`

## Configuration

The mod creates a configuration file at `config/mqttmod-common.toml` with the following options:

```toml
[mqtt]
    # Enable or disable the MQTT mod
    enabled = true
    
    # MQTT broker address
    broker_address = "broker.hivemq.com"
    
    # MQTT broker port
    broker_port = 1883
    
    # MQTT topic for publishing player health data
    mqtt_topic = "minecraft/player/health"
    
    # Connection timeout in seconds
    connection_timeout = 30
```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `enabled` | boolean | `true` | Enable or disable the mod |
| `broker_address` | string | `"broker.hivemq.com"` | MQTT broker hostname or IP address |
| `broker_port` | integer | `1883` | MQTT broker port (1-65535) |
| `mqtt_topic` | string | `"minecraft/player/health"` | Topic to publish health data to |
| `connection_timeout` | integer | `30` | Connection timeout in seconds (1-300) |

## MQTT Broker Setup

### Using Public HiveMQ Broker (Default)

The mod is pre-configured to use the public HiveMQ broker at `broker.hivemq.com:1883`. No additional setup is required.

### Using Your Own Broker

1. Install an MQTT broker (e.g., [Mosquitto](https://mosquitto.org/), [EMQX](https://www.emqx.io/), or [HiveMQ CE](https://www.hivemq.com/downloads/))

2. Update the configuration file with your broker details:
   ```toml
   broker_address = "your-broker-address.com"
   broker_port = 1883
   ```

3. Restart your Minecraft server

### Testing with MQTT Client

You can subscribe to the topic using any MQTT client to see the health updates:

**Using mosquitto_sub:**
```bash
mosquitto_sub -h broker.hivemq.com -t "minecraft/player/health"
```

**Using MQTT.fx, MQTT Explorer, or other GUI clients:**
- Connect to `broker.hivemq.com:1883`
- Subscribe to topic: `minecraft/player/health`

## Message Format

When a player takes damage, the mod publishes a JSON message to the configured topic:

```json
{
  "playerName": "Steve",
  "playerUUID": "069a79f4-44e9-4726-a5be-fca90e38aaf5",
  "health": 15.5,
  "maxHealth": 20.0,
  "timestamp": 1705335600
}
```

### Fields

- `playerName`: The display name of the player
- `playerUUID`: The unique identifier of the player
- `health`: Current health after taking damage (in half-hearts, e.g., 15.5 = 7.75 hearts)
- `maxHealth`: Maximum possible health for the player
- `timestamp`: Unix timestamp (seconds since epoch)

## Example Usage

### Real-time Health Dashboard

You can create a real-time dashboard that displays player health by subscribing to the MQTT topic and visualizing the data.

### Health Alerts

Set up alerts when a player's health drops below a certain threshold:

```python
import paho.mqtt.client as mqtt
import json

def on_message(client, userdata, msg):
    data = json.loads(msg.payload)
    if data['health'] < 5.0:
        print(f"ALERT: {data['playerName']} has low health: {data['health']}")

client = mqtt.Client()
client.on_message = on_message
client.connect("broker.hivemq.com", 1883, 60)
client.subscribe("minecraft/player/health")
client.loop_forever()
```

### Data Logging

Log all health events to a database for analysis:

```javascript
const mqtt = require('mqtt');
const client = mqtt.connect('mqtt://broker.hivemq.com');

client.on('connect', () => {
  client.subscribe('minecraft/player/health');
});

client.on('message', (topic, message) => {
  const data = JSON.parse(message.toString());
  console.log(`${data.playerName}: ${data.health}/${data.maxHealth}`);
  // Save to database
});
```

## Troubleshooting

### Mod Not Publishing Messages

1. Check if the mod is enabled in the config file
2. Verify the MQTT broker is accessible from your network
3. Check the Minecraft logs for connection errors
4. Try increasing the `connection_timeout` value

### Connection Timeouts

- Ensure your firewall allows outbound connections on the MQTT port (default 1883)
- Verify the broker address is correct
- Try using a different MQTT broker

### No Messages Received

- Ensure you're subscribed to the correct topic
- Verify the player is actually taking damage in-game
- Check the Minecraft server logs for error messages

## Development

### Project Structure

```
MqttMod/
├── build.gradle                  # Gradle build configuration
├── settings.gradle               # Gradle settings
├── gradle.properties             # Minecraft & mod versions
└── src/main/
    ├── java/com/sandipdhakal/mqttmod/
    │   ├── MqttMod.java          # Main mod class
    │   ├── Config.java           # Configuration handling
    │   ├── MqttClientManager.java # MQTT client singleton
    │   └── PlayerHealthEventHandler.java # Event handler
    └── resources/
        └── META-INF/
            └── mods.toml         # Mod metadata
```

### Adding Features

To add new features or modify the mod:

1. Edit the Java source files in `src/main/java/com/sandipdhakal/mqttmod/`
2. Rebuild the mod: `./gradlew build`
3. Test your changes in a development environment: `./gradlew runClient`

## License

This project is licensed under the MIT License.

## Author

Created by [sandipdhakal2024](https://github.com/sandipdhakal2024)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have questions, please [open an issue](https://github.com/sandipdhakal2024/MqttMod/issues) on GitHub.