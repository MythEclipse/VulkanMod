package net.vulkanmod.mixin.render;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.vulkanmod.vulkan.VRenderSystem;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private void onSetupFog(Camera camera, int i, DeltaTracker deltaTracker, float f,
                            ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir) {
        Vector4f fogColor = cir.getReturnValue();
        if (fogColor == null) {
            return;
        }
        float alpha = fogColor.w() <= 0.0f ? 1.0f : fogColor.w();
        VRenderSystem.setShaderFogColor(fogColor.x(), fogColor.y(), fogColor.z(), alpha);
    }

    @Inject(method = "updateBuffer", at = @At("HEAD"))
    private static void onUpdateBuffer(java.nio.ByteBuffer buffer, int position, Vector4f fogColor,
                                       float environmentalStart, float environmentalEnd,
                                       float renderDistanceStart, float renderDistanceEnd,
                                       float skyEnd, float cloudEnd, CallbackInfo ci) {
        FogData fogData = VRenderSystem.fogData;
        fogData.environmentalStart = environmentalStart;
        fogData.environmentalEnd = environmentalEnd;
        fogData.renderDistanceStart = renderDistanceStart;
        fogData.renderDistanceEnd = renderDistanceEnd;
        fogData.skyEnd = skyEnd;
        fogData.cloudEnd = cloudEnd;

        float alpha = fogColor.w() <= 0.0f ? 1.0f : fogColor.w();
        VRenderSystem.setShaderFogColor(fogColor.x(), fogColor.y(), fogColor.z(), alpha);
    }
}
