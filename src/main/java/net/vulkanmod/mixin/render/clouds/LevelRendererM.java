package net.vulkanmod.mixin.render.clouds;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.phys.Vec3;
import net.vulkanmod.render.profiling.Profiler;
import net.vulkanmod.render.sky.CloudRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererM {

    @Shadow @Final private LevelTargetBundle targets;

    @Unique private CloudRenderer cloudRenderer;

    @Inject(method = "addCloudsPass", at = @At("HEAD"), cancellable = true)
    public void addCloudsPass(FrameGraphBuilder frameGraphBuilder, CloudStatus cloudStatus, Vec3 camPos, long gameTime, float partialTicks, int cloudColor, float cloudHeight, CallbackInfo ci) {
        if (this.cloudRenderer == null) {
            this.cloudRenderer = new CloudRenderer();
        }

        FramePass framePass = frameGraphBuilder.addPass("clouds");
        if (this.targets.clouds != null) {
            this.targets.clouds = framePass.readsAndWrites(this.targets.clouds);
        } else {
            this.targets.main = framePass.readsAndWrites(this.targets.main);
        }

        framePass.executes(() -> {
            Profiler profiler = Profiler.getMainProfiler();
            profiler.push("Clouds");

            this.cloudRenderer.renderClouds(cloudHeight, cloudColor, camPos.x(), camPos.y(), camPos.z(), gameTime, partialTicks);

            profiler.pop();
        });

        ci.cancel();
    }

    @Inject(method = "allChanged", at = @At("RETURN"))
    private void onAllChanged(CallbackInfo ci) {
        if (this.cloudRenderer != null) {
            this.cloudRenderer.resetBuffer();
        }
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void onReload(ResourceManager resourceManager, CallbackInfo ci) {
        if (this.cloudRenderer != null) {
            this.cloudRenderer.loadTexture();
        }
    }

}
