package net.vulkanmod.mixin.texture.update;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/texture/update/GameRendererM.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.GameRenderer.class})
public abstract class GameRendererM {

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    net.minecraft.client.Minecraft minecraft;
}
