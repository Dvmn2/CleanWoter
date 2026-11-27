package net.dvmn2.cleanwoter.client.mixin;

import net.dvmn2.cleanwoter.client.Effect;
import net.dvmn2.cleanwoter.client.state.CleanWoterState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Removes the field-of-view narrowing underwater.
 *
 * <p>The FOV code narrows the view for water and lava. Reporting {@code NONE} for water skips the
 * water branch only; lava is left untouched. Abstract because a mixin class is never instantiated.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Redirect(
            method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getSubmersionType()"
                            + "Lnet/minecraft/block/enums/CameraSubmersionType;"))
    private CameraSubmersionType cleanwoter$hideWaterFov(Camera camera) {
        CameraSubmersionType type = camera.getSubmersionType();
        return type == CameraSubmersionType.WATER && CleanWoterState.isSuppressed(Effect.FOV)
                ? CameraSubmersionType.NONE
                : type;
    }
}
