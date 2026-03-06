package net.vulkanmod.mixin.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/MinecraftMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.Minecraft.class})
public class MinecraftMixin {

    @org.spongepowered.asm.mixin.Shadow public boolean noRender;

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    public net.minecraft.client.Options options;

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"<init>"},
            at = {@org.spongepowered.asm.mixin.injection.At("RETURN")})
    private void forceGraphicsMode(
            net.minecraft.client.main.GameConfig gameConfig,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.minecraft.client.OptionInstance<net.minecraft.client.GraphicsPreset>
                graphicsModeOption = this.options.graphicsPreset();
        if (graphicsModeOption.get() == net.minecraft.client.GraphicsPreset.FABULOUS) {
            net.vulkanmod.Initializer.LOGGER.error(
                    "Fabulous graphics mode not supported, forcing Fancy.");
            graphicsModeOption.set(net.minecraft.client.GraphicsPreset.FANCY);
        }
        if (((java.lang.Boolean) this.options.improvedTransparency().get()).booleanValue()) {
            net.vulkanmod.Initializer.LOGGER.error(
                    "Improved transparency currently not supported, forcing it off.");
            this.options.improvedTransparency().set(false);
        }
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"runTick"},
            at = {
                @org.spongepowered.asm.mixin.injection.At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/Minecraft;tick()V")
            })
    private void redirectResourceTick(
            boolean bl,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci,
            @com.llamalad7.mixinextras.sugar.Local(ordinal = 0) int i,
            @com.llamalad7.mixinextras.sugar.Local(ordinal = 1) int j) {
        int n = java.lang.Math.min(10, i) - 1;
        boolean doUpload = j == n;
        net.vulkanmod.render.texture.SpriteUpdateUtil.setDoUpload(doUpload);
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"close"},
            at = {@org.spongepowered.asm.mixin.injection.At("HEAD")})
    public void close(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.vulkanmod.vulkan.Vulkan.waitIdle();
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"close"},
            at = {
                @org.spongepowered.asm.mixin.injection.At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/renderer/VirtualScreen;close()V")
            })
    public void close2(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.vulkanmod.vulkan.Vulkan.cleanUp();
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"resizeDisplay"},
            at = {@org.spongepowered.asm.mixin.injection.At("HEAD")})
    public void onResolutionChanged(
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.vulkanmod.vulkan.Renderer.scheduleSwapChainUpdate();
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = {"setScreen"},
            at =
                    @org.spongepowered.asm.mixin.injection.At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/Minecraft;noRender:Z",
                            opcode = org.lwjgl.vulkan.VK10.VK_FORMAT_ASTC_12x10_UNORM_BLOCK))
    private void keepVar(net.minecraft.client.Minecraft instance, boolean value) {}
}
