package net.vulkanmod.mixin.vertex;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/vertex/DefaultVertexFormatM.class */
@org.spongepowered.asm.mixin.Mixin({com.mojang.blaze3d.vertex.DefaultVertexFormat.class})
public class DefaultVertexFormatM {
    @org.spongepowered.asm.mixin.injection.Redirect(method = {"<clinit>"}, at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexFormat$Builder;build()Lcom/mojang/blaze3d/vertex/VertexFormat;", ordinal = 14))
    private static com.mojang.blaze3d.vertex.VertexFormat fixMissingPaddingFormat(com.mojang.blaze3d.vertex.VertexFormat.Builder instance) {
        return com.mojang.blaze3d.vertex.VertexFormat.builder().add("Position", com.mojang.blaze3d.vertex.VertexFormatElement.POSITION).add("Color", com.mojang.blaze3d.vertex.VertexFormatElement.COLOR).add("Normal", com.mojang.blaze3d.vertex.VertexFormatElement.NORMAL).padding(1).add("LineWidth", com.mojang.blaze3d.vertex.VertexFormatElement.LINE_WIDTH).build();
    }
}
