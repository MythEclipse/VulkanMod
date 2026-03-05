package net.vulkanmod.mixin.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/RenderSystemMixin.class */
@org.spongepowered.asm.mixin.Mixin({com.mojang.blaze3d.systems.RenderSystem.class})
public abstract class RenderSystemMixin {

    @org.spongepowered.asm.mixin.Shadow
    @org.jetbrains.annotations.Nullable
    private static java.lang.Thread renderThread;

    @org.spongepowered.asm.mixin.Shadow
    @org.jetbrains.annotations.Nullable
    private static com.mojang.blaze3d.systems.GpuDevice DEVICE;

    @org.spongepowered.asm.mixin.Shadow
    @org.jetbrains.annotations.Nullable
    private static net.minecraft.client.renderer.DynamicUniforms dynamicUniforms;

    @org.spongepowered.asm.mixin.Shadow
    private static com.mojang.blaze3d.systems.SamplerCache samplerCache;

    @org.spongepowered.asm.mixin.Shadow
    private static java.lang.String apiDescription;

    @org.spongepowered.asm.mixin.Overwrite(remap = false)
    public static void initRenderer(long l, int i, boolean bl, com.mojang.blaze3d.shaders.ShaderSource shaderSource, boolean bl2) {
        renderThread.setPriority(7);
        net.vulkanmod.vulkan.VRenderSystem.initRenderer();
        DEVICE = new net.vulkanmod.render.engine.VkGpuDevice(l, i, bl, shaderSource, bl2);
        apiDescription = com.mojang.blaze3d.systems.RenderSystem.getDevice().getImplementationInformation();
        net.vulkanmod.vulkan.Renderer.initRenderer();
        dynamicUniforms = new net.minecraft.client.renderer.DynamicUniforms();
        samplerCache.initialize();
    }
}
