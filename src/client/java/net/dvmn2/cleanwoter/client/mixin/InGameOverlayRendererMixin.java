package net.dvmn2.cleanwoter.client.mixin;

import net.dvmn2.cleanwoter.client.Effect;
import net.dvmn2.cleanwoter.client.state.CleanWoterState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips the blue full-screen overlay drawn while the camera is underwater.
 *
 * <p>Abstract because a mixin class is never instantiated.
 */
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    @Inject(
            method = "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;"
                    + "Lnet/minecraft/client/util/math/MatrixStack;"
                    + "Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void cleanwoter$skipUnderwaterOverlay(MinecraftClient client, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (CleanWoterState.isSuppressed(Effect.OVERLAY)) {
            ci.cancel();
        }
    }
}
