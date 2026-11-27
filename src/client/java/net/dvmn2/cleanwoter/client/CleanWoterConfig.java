package net.dvmn2.cleanwoter.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Player-side config stored in {@code config/cleanwoter.json}.
 * <pre>
 * {
 *   "enabled": true,
 *   "overlay": true,
 *   "fog": true,
 *   "fov": true
 * }
 * </pre>
 * {@code enabled} is the master switch. Every other key controls one {@link Effect}; {@code true} means
 * that the effect is removed. Missing keys keep their defaults and unknown keys are ignored.
 * Used from the client thread only.
 */
public final class CleanWoterConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("cleanwoter");
    private static final String FILE_NAME = "cleanwoter.json";

    /** Upper bound for the config file size, bytes. A real file is well under 1 KiB. */
    private static final long MAX_FILE_BYTES = 16 * 1024;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static CleanWoterConfig instance;

    public boolean enabled = true;
    public boolean overlay = true;
    public boolean fog = true;
    public boolean fov = true;

    /** Returns the config, loading it on first call. Never returns {@code null}. */
    public static synchronized CleanWoterConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    /** Writes the current config to disk. A failure is logged and does not throw. */
    public static synchronized void save() {
        write(get());
    }

    /** Returns true if the given effect is switched on in the config (ignores the master switch). */
    public boolean isEffectEnabled(Effect effect) {
        return switch (effect) {
            case OVERLAY -> overlay;
            case FOG -> fog;
            case FOV -> fov;
        };
    }

    /** Switches the given effect on or off in memory; call {@link #save()} to persist. */
    public void setEffectEnabled(Effect effect, boolean value) {
        switch (effect) {
            case OVERLAY -> overlay = value;
            case FOG -> fog = value;
            case FOV -> fov = value;
        }
    }

    private static CleanWoterConfig load() {
        Path path = path();
        if (!Files.isRegularFile(path)) {
            CleanWoterConfig defaults = new CleanWoterConfig();
            write(defaults);
            return defaults;
        }
        try {
            if (Files.size(path) > MAX_FILE_BYTES) {
                LOGGER.warn("Config file {} is larger than {} bytes; using defaults", path, MAX_FILE_BYTES);
                return new CleanWoterConfig();
            }
            CleanWoterConfig loaded = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8),
                    CleanWoterConfig.class);
            // All values are booleans, so there is nothing to clamp after loading.
            return loaded == null ? new CleanWoterConfig() : loaded;
        } catch (IOException | JsonParseException ex) {
            LOGGER.warn("Could not read config file {}; using defaults", path, ex);
            return new CleanWoterConfig();
        }
    }

    private static void write(CleanWoterConfig config) {
        Path target = path();
        try {
            Files.createDirectories(target.getParent());
            Path tmp = target.resolveSibling(FILE_NAME + ".tmp");
            Files.writeString(tmp, GSON.toJson(config), StandardCharsets.UTF_8);
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            LOGGER.warn("Could not write config file {}", target, ex);
        }
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }
}
