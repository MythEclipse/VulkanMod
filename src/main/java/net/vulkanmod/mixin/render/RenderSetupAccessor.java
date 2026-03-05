package net.vulkanmod.mixin.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/RenderSetupAccessor.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.rendertype.RenderSetup.class})
public interface RenderSetupAccessor {
    @org.spongepowered.asm.mixin.gen.Accessor("pipeline")
    com.mojang.blaze3d.pipeline.RenderPipeline pipeline();

    @org.spongepowered.asm.mixin.gen.Accessor("layeringTransform")
    net.minecraft.client.renderer.rendertype.LayeringTransform layeringTransform();

    @org.spongepowered.asm.mixin.gen.Accessor("outputTarget")
    net.minecraft.client.renderer.rendertype.OutputTarget outputTarget();

    @org.spongepowered.asm.mixin.gen.Accessor("textureTransform")
    net.minecraft.client.renderer.rendertype.TextureTransform textureTransform();
}
