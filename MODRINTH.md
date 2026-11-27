# CleanWoter

Client mod that makes underwater look like above water: no blue screen tint, no underwater fog and no narrowing of the view.

## Features
- Removes the blue overlay that covers the screen underwater.
- Removes the underwater fog, so you see as far as on land.
- Removes the field-of-view narrowing when you dive.
- Each effect can be switched on or off separately.
- Works on any server and in single player. Nothing is required on the server.

## Requirements
- Minecraft 1.21.11
- Fabric Loader 0.18.5 or newer
- Fabric API
- No server plugin is needed.

## Installation
1. Install Fabric Loader for Minecraft 1.21.11.
2. Put `CleanWoter-<version>.jar` and Fabric API into the `mods` folder.
3. Start the game. The mod works right away.

## Commands
| Command | Description |
|---|---|
| `/cw off` | Disable all mod effects immediately |
| `/cw on` | Enable the mod again |
| `/cw status` | Show the state of the mod and of each effect |
| `/cw <effect> on` | Remove this effect (`overlay`, `fog` or `fov`) |
| `/cw <effect> off` | Keep the vanilla version of this effect |

Changes are saved to the config at once.

## Configuration
File: `config/cleanwoter.json`

| Key | Type | Default | Range | Description |
|---|---|---|---|---|
| `enabled` | boolean | `true` | `true`, `false` | Master switch for the whole mod |
| `overlay` | boolean | `true` | `true`, `false` | Remove the blue overlay |
| `fog` | boolean | `true` | `true`, `false` | Remove the underwater fog |
| `fov` | boolean | `true` | `true`, `false` | Remove the field-of-view narrowing |

```json
{
  "enabled": true,
  "overlay": true,
  "fog": true,
  "fov": true
}
```

The file is created the first time the mod needs it. If it is damaged, the mod uses the defaults and writes a warning to the game log.

## Controls
None.

## Privacy and safety
- The mod changes only what you see on your own screen. It does not use the network and does not send or read any server data.
- It does not download or change files outside its own config.
- Seeing clearly underwater can be an advantage. Some servers do not allow this, so check the rules of the server you play on.
- Type `/cw off` at any time to bring back all vanilla underwater effects.

## Troubleshooting
- **Fog or tint is still there with a shader pack.** Shader packs and some rendering mods draw their own underwater look. Turn off the shader pack's underwater effects or use it without them.
- **The game does not start after a Minecraft update.** The mod is built for Minecraft 1.21.11 only. Install a version made for your game version.
- **An effect stopped working and `/cw status` shows it turned off after an error.** Restart the game and check the log for lines from `cleanwoter`.
- **Settings are not kept.** Check that the `config` folder of the game is writable.

## License
All rights reserved.
