package net.dvmn2.cleanwoter.client.mixin;

import net.dvmn2.cleanwoter.client.Effect;
import net.dvmn2.cleanwoter.client.state.CleanWoterState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes the fog renderer treat water as open air.
 *
 * <p>The fog type is replaced instead of rejecting the water fog modifier: some fog modifier must still apply,
 * because the fog colour (also used as the sky clear colour) comes from the first modifier that provides one.
 * Abstract because a mixin class is never instantiated.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(
            method = "getCameraSubmersionType(Lnet/minecraft/client/render/Camera;)"
                    + "Lnet/minecraft/block/enums/CameraSubmersionType;",
            at = @At("RETURN"),
            cancellable = true)
    private void cleanwoter$treatWaterAsAir(Camera camera, CallbackInfoReturnable<CameraSubmersionType> cir) {
        if (cir.getReturnValue() == CameraSubmersionType.WATER && CleanWoterState.isSuppressed(Effect.FOG)) {
            cir.setReturnValue(CameraSubmersionType.ATMOSPHERIC);
        }
    }
}
