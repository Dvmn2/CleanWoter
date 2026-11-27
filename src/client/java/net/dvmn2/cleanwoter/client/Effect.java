package net.dvmn2.cleanwoter.client;

/**
 * Underwater visual effects that the mod can remove.
 *
 * <p>Each effect has its own switch in the config and its own command literal.
 */
public enum Effect {
    OVERLAY("overlay"),
    FOG("fog"),
    FOV("fov");

    private final String id;

    Effect(String id) {
        this.id = id;
    }

    /** Returns the config key and command literal of this effect. */
    public String getId() {
        return id;
    }

    /** Returns the translation key of the effect name. */
    public String getTranslationKey() {
        return "cleanwoter.effect." + id;
    }
}
