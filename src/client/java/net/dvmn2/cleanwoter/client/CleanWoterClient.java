package net.dvmn2.cleanwoter.client;

import net.dvmn2.cleanwoter.client.command.CleanWoterCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

/** Client entry point: registers the {@code /cw} command. The config is loaded lazily on first use. */
public final class CleanWoterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                CleanWoterCommand.register(dispatcher));
    }
}
