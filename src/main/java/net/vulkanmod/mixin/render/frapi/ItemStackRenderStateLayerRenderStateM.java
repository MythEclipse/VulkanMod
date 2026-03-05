package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: compiled from: ItemRenderStateLayerRenderStateM.java */
/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/ItemStackRenderStateLayerRenderStateM.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState.class})
abstract class ItemStackRenderStateLayerRenderStateM implements net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState, net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState {

    @org.spongepowered.asm.mixin.Unique
    private final net.vulkanmod.render.chunk.build.frapi.mesh.MutableMeshImpl mutableMesh = new net.vulkanmod.render.chunk.build.frapi.mesh.MutableMeshImpl();

    @org.spongepowered.asm.mixin.Unique
    private net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter renderTypeGetter = null;

    ItemStackRenderStateLayerRenderStateM() {
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = {"clear()V"}, at = {@org.spongepowered.asm.mixin.injection.At("RETURN")})
    private void onReturnClear(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.mutableMesh.clear();
        this.renderTypeGetter = null;
    }

    @org.spongepowered.asm.mixin.injection.Redirect(method = {"submit"}, at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitItem(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemDisplayContext;III[ILjava/util/List;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V"))
    private void submitItemProxy(net.minecraft.client.renderer.SubmitNodeCollector commandQueue, com.mojang.blaze3d.vertex.PoseStack matrices, net.minecraft.world.item.ItemDisplayContext displayContext, int light, int overlay, int outlineColor, int[] tints, java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads, net.minecraft.client.renderer.rendertype.RenderType layer, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint) {
        if (this.mutableMesh.size() > 0 && (commandQueue instanceof net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue)) {
            net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue access = (net.vulkanmod.render.chunk.build.frapi.accessor.AccessRenderCommandQueue) commandQueue;
            access.submitItem(matrices, displayContext, light, overlay, outlineColor, tints, quads, layer, glint, this.mutableMesh, this.renderTypeGetter);
        } else {
            commandQueue.submitItem(matrices, displayContext, light, overlay, outlineColor, tints, quads, layer, glint);
        }
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState
    public net.vulkanmod.render.chunk.build.frapi.mesh.MutableMeshImpl getMutableMesh() {
        return this.mutableMesh;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState, net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState
    public void setRenderTypeGetter(net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter renderTypeGetter) {
        this.renderTypeGetter = renderTypeGetter;
    }
}
