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
            com.mojang.blaze3d.buffers.Std140Builder.intoBuffer(org.lwjgl.system.MemoryUtil.memSlice(byteBuffer, i + (n * uboSize), uboSize)).putMat4f(new org.joml.Matrix4f().ortho2D(0.0f, width >> n, height >> n, 0.0f)).putMat4f(new org.joml.Matrix4f().translate(this.x >> n, this.y >> n, 0.0f).scale((this.contents.width() + (this.padding * 2)) >> n, (this.contents.height() + (this.padding * 2)) >> n, 1.0f)).putFloat(this.padding / this.contents.width()).putFloat(this.padding / this.contents.height()).putInt(n);
        }
    }
}
