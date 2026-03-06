package net.vulkanmod.mixin.texture;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/texture/TextureAtlasSpriteMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.texture.TextureAtlasSprite.class})
public class TextureAtlasSpriteMixin {

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.client.renderer.texture.SpriteContents contents;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private int padding;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private int x;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private int y;

    @org.spongepowered.asm.mixin.Overwrite
    public void uploadSpriteUbo(java.nio.ByteBuffer byteBuffer, int i, int maxMipLevel, int width, int height, int uboSize) {
        for (int n = 0; n <= maxMipLevel; n++) {
            int mipWidth = java.lang.Math.max(1, width >> n);
            int mipHeight = java.lang.Math.max(1, height >> n);
            int spriteWidth = java.lang.Math.max(1, (this.contents.width() + (this.padding * 2)) >> n);
            int spriteHeight = java.lang.Math.max(1, (this.contents.height() + (this.padding * 2)) >> n);
            float paddingU = this.contents.width() == 0 ? 0.0f : ((float) this.padding) / ((float) this.contents.width());
            float paddingV = this.contents.height() == 0 ? 0.0f : ((float) this.padding) / ((float) this.contents.height());

            com.mojang.blaze3d.buffers.Std140Builder.intoBuffer(org.lwjgl.system.MemoryUtil.memSlice(byteBuffer, i + (n * uboSize), uboSize))
                    .putMat4f(new org.joml.Matrix4f().ortho2D(0.0f, mipWidth, mipHeight, 0.0f))
                    .putMat4f(new org.joml.Matrix4f().translate(this.x >> n, this.y >> n, 0.0f).scale(spriteWidth, spriteHeight, 1.0f))
                    .putFloat(paddingU)
                    .putFloat(paddingV)
                    .putInt(n);
        }
    }
}
