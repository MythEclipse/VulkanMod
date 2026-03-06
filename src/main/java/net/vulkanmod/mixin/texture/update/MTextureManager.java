package net.vulkanmod.mixin.texture.update;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/texture/update/MTextureManager.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.texture.TextureManager.class})
public abstract class MTextureManager {

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private java.util.Set<net.minecraft.client.renderer.texture.TickableTexture> tickableTextures;

    @org.spongepowered.asm.mixin.Overwrite
    public void tick() {
        if (net.vulkanmod.vulkan.Renderer.skipRendering
                || !net.vulkanmod.Initializer.CONFIG.textureAnimations) {
            return;
        }
        for (net.minecraft.client.renderer.texture.TickableTexture tickable :
                this.tickableTextures) {
            tickable.tick();
        }
        net.vulkanmod.render.texture.SpriteUpdateUtil.transitionLayouts();
    }
}
