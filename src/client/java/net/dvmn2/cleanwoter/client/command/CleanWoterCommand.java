package net.dvmn2.cleanwoter.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dvmn2.cleanwoter.client.CleanWoterConfig;
import net.dvmn2.cleanwoter.client.Effect;
import net.dvmn2.cleanwoter.client.state.CleanWoterState;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Client command tree of the mod. {@code /cw off} is the emergency switch that restores all vanilla effects.
 * <pre>
 * /cw
 * ├── on
 * ├── off
 * ├── status
 * ├── overlay (on | off)
 * ├── fog     (on | off)
 * └── fov     (on | off)
 * </pre>
 * Every change is saved to the config immediately. Runs on the client thread.
 */
public final class CleanWoterCommand {

    private static final String ROOT = "cw";
    private static final String LITERAL_ON = "on";
    private static final String LITERAL_OFF = "off";
    private static final String LITERAL_STATUS = "status";

    private static final String KEY_ENABLED = "cleanwoter.enabled";
    private static final String KEY_DISABLED = "cleanwoter.disabled";
    private static final String KEY_EFFECT_CHANGED = "cleanwoter.effect.changed";
    private static final String KEY_STATE_ON = "cleanwoter.state.on";
    private static final String KEY_STATE_OFF = "cleanwoter.state.off";
    private static final String KEY_STATUS_TITLE = "cleanwoter.status.title";
    private static final String KEY_STATUS_MOD = "cleanwoter.status.mod";
    private static final String KEY_STATUS_EFFECT = "cleanwoter.status.effect";
    private static final String KEY_STATUS_FAILED = "cleanwoter.status.failed";

    private CleanWoterCommand() {
    }

    /** Registers the {@code /cw} tree in the client command dispatcher. */
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal(ROOT)
                .then(ClientCommandManager.literal(LITERAL_ON)
                        .executes(ctx -> setEnabled(ctx.getSource(), true)))
                .then(ClientCommandManager.literal(LITERAL_OFF)
                        .executes(ctx -> setEnabled(ctx.getSource(), false)))
                .then(ClientCommandManager.literal(LITERAL_STATUS)
                        .executes(ctx -> status(ctx.getSource())));
        for (Effect effect : Effect.values()) {
            root.then(effectNode(effect));
        }
        dispatcher.register(root);
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> effectNode(Effect effect) {
        return ClientCommandManager.literal(effect.getId())
                .then(ClientCommandManager.literal(LITERAL_ON)
                        .executes(ctx -> setEffect(ctx.getSource(), effect, true)))
                .then(ClientCommandManager.literal(LITERAL_OFF)
                        .executes(ctx -> setEffect(ctx.getSource(), effect, false)));
    }

    private static int setEnabled(FabricClientCommandSource source, boolean enabled) {
        CleanWoterConfig.get().enabled = enabled;
        CleanWoterConfig.save();
        source.sendFeedback(Text.translatable(enabled ? KEY_ENABLED : KEY_DISABLED)
                .formatted(enabled ? Formatting.GREEN : Formatting.YELLOW));
        return Command.SINGLE_SUCCESS;
    }

    private static int setEffect(FabricClientCommandSource source, Effect effect, boolean value) {
        CleanWoterConfig.get().setEffectEnabled(effect, value);
        CleanWoterConfig.save();
        source.sendFeedback(Text.translatable(KEY_EFFECT_CHANGED,
                Text.translatable(effect.getTranslationKey()), stateText(value)).formatted(Formatting.GRAY));
        return Command.SINGLE_SUCCESS;
    }

    private static int status(FabricClientCommandSource source) {
        CleanWoterConfig config = CleanWoterConfig.get();
        source.sendFeedback(Text.translatable(KEY_STATUS_TITLE).formatted(Formatting.GOLD));
        source.sendFeedback(Text.translatable(KEY_STATUS_MOD, stateText(config.enabled)).formatted(Formatting.GRAY));
        for (Effect effect : Effect.values()) {
            source.sendFeedback(effectLine(config, effect));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static Text effectLine(CleanWoterConfig config, Effect effect) {
        Text name = Text.translatable(effect.getTranslationKey());
        if (CleanWoterState.hasFailed(effect)) {
            return Text.translatable(KEY_STATUS_FAILED, name).formatted(Formatting.RED);
        }
        return Text.translatable(KEY_STATUS_EFFECT, name, stateText(config.isEffectEnabled(effect)))
                .formatted(Formatting.GRAY);
    }

    private static Text stateText(boolean on) {
        return Text.translatable(on ? KEY_STATE_ON : KEY_STATE_OFF).formatted(on ? Formatting.GREEN : Formatting.RED);
    }
}
