package net.vulkanmod.mixin.texture;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/texture/SpriteContentsAnimatedTextureM.class */
@org.spongepowered.asm.mixin.Mixin(
        targets =
                "net.minecraft.client.renderer.texture.SpriteContents$AnimatedTexture")
public class SpriteContentsAnimatedTextureM {
    @org.spongepowered.asm.mixin.injection.ModifyArg(
            method = {"createAnimationState"},
            at =
                    @org.spongepowered.asm.mixin.injection.At(
                            value = "INVOKE",
                            target =
                                    "Lcom/mojang/blaze3d/systems/GpuDevice;createTexture(Ljava/util/function/Supplier;ILcom/mojang/blaze3d/textures/TextureFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;"),
            index = 6)
    private int fixMipLevels(int mipLevels) {
        return mipLevels - 1;
    }
}
