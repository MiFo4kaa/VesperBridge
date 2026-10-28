# VesperBridge - Minecraft server plugin for VesperPy bot integration

[![Discord](https://img.shields.io/discord/947609711845462116?label=Discord&color=5865F2)](https://discord.gg/rgxqrmSbJN)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-blue.svg)](https://wiki.vespera.fun)

---

## 📖 Overview

**VesperBridge** - an open-source Minecraft server plugin designed specifically to establish a secure API bridge between your Minecraft server and the VesperPy Discord bot.
• API Endpoint - provides a secure web server for handling incoming requests from VesperPy
• Whitelist Sync - processes commands from Discord tickets to dynamically manage the server whitelist
• Premium role sync - bridges players roles with luckperms groups

## 📁 Project Structure

```
VesperBridge/
├── src/main/
│   ├── java/fun/vespera/vesperBridge
│   │   ├── api/             # HTTP server and request handlers
│   │   ├── commands/        # In-game administration commands
│   │   ├── whitelistUtils/       # whitelist hooks
│   │   ├── MessageManager.java
│   │   └── VesperBridge.java # Main plugin class and lifecycle management
│   └── resources/
│       ├── config.yml       # Main configuration file (ports, keys, channels)
│       ├── messages.yml     # plugin messages config
│       └── plugin.yml       # Spigot/Paper plugin metadata file
└── pom.xml                  # Maven dependencies and project configuration
```

---

## 📝 License

[MIT License.](https://github.com/MiFo4kaa/VesperBridge/blob/main/LICENSE)
