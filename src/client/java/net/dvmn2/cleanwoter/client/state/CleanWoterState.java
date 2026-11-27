package net.dvmn2.cleanwoter.client.state;

import net.dvmn2.cleanwoter.client.CleanWoterConfig;
import net.dvmn2.cleanwoter.client.Effect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;

/**
 * Answers whether an underwater effect must be removed right now.
 *
 * <p>Called from render hooks on the client thread. If a lookup fails, the affected effect is turned
 * off for the rest of the session (the vanilla behaviour returns) and the error is logged once.
 * Nothing here depends on the server, so there is nothing to clear on disconnect.
 */
public final class CleanWoterState {

    private static final Logger LOGGER = LoggerFactory.getLogger("cleanwoter");
    private static final Set<Effect> FAILED = EnumSet.noneOf(Effect.class);

    private CleanWoterState() {
    }

    /** Returns true if the effect must be removed: mod enabled, effect enabled and no earlier failure. */
    public static boolean isSuppressed(Effect effect) {
        if (FAILED.contains(effect)) {
            return false;
        }
        try {
            CleanWoterConfig config = CleanWoterConfig.get();
            return config.enabled && config.isEffectEnabled(effect);
        } catch (RuntimeException ex) {
            // Render hook boundary: a broken lookup must not crash the client.
            if (FAILED.add(effect)) {
                LOGGER.error("Effect {} was turned off after an error", effect.getId(), ex);
            }
            return false;
        }
    }

    /** Returns true if the effect was turned off after an error during this session. */
    public static boolean hasFailed(Effect effect) {
        return FAILED.contains(effect);
    }
}
