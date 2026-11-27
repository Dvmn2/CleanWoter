# CleanWoter

Fabric client mod that removes underwater visual effects: the blue overlay, the fog and the field-of-view narrowing.

Type: Fabric client mod. Companion plugin: none.

## Requirements
- Minecraft 1.21.11, Java 21
- Fabric Loader >= 0.18.5, Fabric API (client command API)
- Companion plugin: None.

## Build and run
```bash
./gradlew build      # build/libs/CleanWoter-<version>.jar
./gradlew runClient  # Minecraft 1.21.11 dev client
```

## Architecture
```text
net.dvmn2.cleanwoter.client
├── CleanWoterClient          // ClientModInitializer
├── CleanWoterConfig          // config/cleanwoter.json
├── Effect                    // OVERLAY, FOG, FOV
├── command/CleanWoterCommand // /cw tree
├── state/CleanWoterState     // "is this effect removed now?" + failure isolation
└── mixin/                    // three minimal hooks
```

| Class | Responsibility |
|---|---|
| `CleanWoterClient` | registers the `/cw` command; nothing else |
| `CleanWoterConfig` | public-field Gson config, lazy synchronized `get()`, atomic `save()` |
| `Effect` | the three removable effects; id is both config key and command literal |
| `CleanWoterState` | combines the master switch and the per-effect switch; turns an effect off for the session after an error |
| `CleanWoterCommand` | `/cw on`, `/cw off`, `/cw status`, `/cw <effect> on\|off` |
| `FogRendererMixin` | `FogRenderer#getCameraSubmersionType`: reports air instead of water |
| `GameRendererMixin` | `GameRenderer#getFov`: redirects `Camera#getSubmersionType` so water is not seen as water |
| `InGameOverlayRendererMixin` | `InGameOverlayRenderer#renderUnderwaterOverlay`: cancelled at `HEAD` |

Threads: all code runs on the client (render) thread. Config reads and writes are a few hundred bytes and are done synchronously.

Lifecycle: the entry point registers the command; the config file is created on first access (first command or first underwater frame).

## Public API
None.

## Protocol
None. The mod sends and receives nothing.

## Data and files
- `config/cleanwoter.json`: player config. Keys `enabled`, `overlay`, `fog`, `fov` (all boolean, default `true`). Missing keys keep defaults, unknown keys are ignored. A file over 16 KiB or with invalid JSON is ignored with a warning and defaults are used. Writes are atomic (temporary file, then move).
- No cache and no downloaded data.

## Conventions and constraints
- Fog: the water fog modifier is not rejected directly. The fog colour (also the sky clear colour) comes from the first applicable modifier that provides one, so rejecting the water modifier would leave no colour source. Instead the fog type is changed from water to atmospheric, and the normal air fog applies.
- FOV: the vanilla code narrows the view for water and for lava through one `getSubmersionType` call. The redirect hides only water.
- All mixin injectors use `defaultRequire: 1`. If a game update changes a target, the client fails at startup instead of silently losing the effect.
- Deviation from STYLE.md §5.5: there is no intensity limiter and no "no flicker" mode. The mod only removes effects and never adds motion or brightness changes; the per-effect switches and `/cw off` serve as the limiter.
- Deviation from STYLE.md §2.5: mixin classes are `abstract`, not `final`, because the Mixin framework does not instantiate them.
- No state is cleared on disconnect: the state comes from the player's config, not from a server.
- Shader packs (Iris) and other rendering replacements can draw their own fog and overlay; the hooks cannot affect those.

## License
All rights reserved.
