package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/ItemRendererAccessor.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.entity.ItemRenderer.class})
public interface ItemRendererAccessor {
    @org.spongepowered.asm.mixin.gen.Invoker("getSpecialFoilBuffer")
    static com.mojang.blaze3d.vertex.VertexConsumer getSpecialFoilBuffer(
            net.minecraft.client.renderer.MultiBufferSource provider,
            net.minecraft.client.renderer.rendertype.RenderType layer,
            com.mojang.blaze3d.vertex.PoseStack.Pose entry) {
        throw new java.lang.AssertionError();
    }
}
