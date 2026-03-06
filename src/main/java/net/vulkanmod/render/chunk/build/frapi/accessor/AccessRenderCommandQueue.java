package net.vulkanmod.render.chunk.build.frapi.accessor;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/accessor/AccessRenderCommandQueue.class */
public interface AccessRenderCommandQueue {
    void submitItem(
            com.mojang.blaze3d.vertex.PoseStack class_4587Var,
            net.minecraft.world.item.ItemDisplayContext class_811Var,
            int i,
            int i2,
            int i3,
            int[] iArr,
            java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> list,
            net.minecraft.client.renderer.rendertype.RenderType class_1921Var,
            net.minecraft.client.renderer.item.ItemStackRenderState.FoilType class_10445Var,
            net.fabricmc.fabric.api.renderer.v1.mesh.MeshView meshView,
            net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter itemRenderTypeGetter);
}
