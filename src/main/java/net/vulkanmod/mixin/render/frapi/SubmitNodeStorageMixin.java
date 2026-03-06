package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/SubmitNodeStorageMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.SubmitNodeStorage.class})
abstract class SubmitNodeStorageMixin
        implements net.minecraft.client.renderer.SubmitNodeCollector,
                net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue {
    SubmitNodeStorageMixin() {}

    @Override // net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue
    public void submitItem(
            com.mojang.blaze3d.vertex.PoseStack matrices,
            net.minecraft.world.item.ItemDisplayContext displayContext,
            int light,
            int overlay,
            int outlineColors,
            int[] tintLayers,
            java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads,
            net.minecraft.client.renderer.rendertype.RenderType renderLayer,
            net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glintType,
            net.fabricmc.fabric.api.renderer.v1.mesh.MeshView mesh,
            net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter renderTypeGetter) {
        net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue
                accessRenderCommandQueueMethod_73529 =
                        (net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue)
                                (Object) order(0);
        accessRenderCommandQueueMethod_73529.submitItem(
                matrices,
                displayContext,
                light,
                overlay,
                outlineColors,
                tintLayers,
                quads,
                renderLayer,
                glintType,
                mesh,
                renderTypeGetter);
    }
}
