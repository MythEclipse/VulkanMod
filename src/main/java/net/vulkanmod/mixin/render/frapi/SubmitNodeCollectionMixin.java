package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/SubmitNodeCollectionMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.SubmitNodeCollection.class})
abstract class SubmitNodeCollectionMixin implements net.minecraft.client.renderer.OrderedSubmitNodeCollector, net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue, net.vulkanmod.render.chunk.build.frapi.accessor.AccessBatchingRenderCommandQueue {

    @org.spongepowered.asm.mixin.Shadow
    private boolean wasUsed;

    @org.spongepowered.asm.mixin.Unique
    private final java.util.List<net.vulkanmod.render.chunk.build.frapi.render.MeshItemCommand> meshItemCommands = new java.util.ArrayList();

    SubmitNodeCollectionMixin() {
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = {"clear()V"}, at = {@org.spongepowered.asm.mixin.injection.At("RETURN")})
    public void clear(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.meshItemCommands.clear();
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue
    public void submitItem(com.mojang.blaze3d.vertex.PoseStack matrices, net.minecraft.world.item.ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads, net.minecraft.client.renderer.rendertype.RenderType renderLayer, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glintType, net.fabricmc.fabric.api.renderer.v1.mesh.MeshView mesh, net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter renderTypeGetter) {
        this.wasUsed = true;
        this.meshItemCommands.add(new net.vulkanmod.render.chunk.build.frapi.render.MeshItemCommand(matrices.last().copy(), displayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType, mesh, renderTypeGetter));
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.accessor.AccessBatchingRenderCommandQueue
    public java.util.List<net.vulkanmod.render.chunk.build.frapi.render.MeshItemCommand> getMeshItemCommands() {
        return this.meshItemCommands;
    }
}
