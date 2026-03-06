package net.vulkanmod.render.chunk.build.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/VulkanModRenderer.class */
public class VulkanModRenderer implements net.fabricmc.fabric.api.renderer.v1.Renderer {
    public static final net.vulkanmod.render.chunk.build.frapi.VulkanModRenderer INSTANCE =
            new net.vulkanmod.render.chunk.build.frapi.VulkanModRenderer();

    private VulkanModRenderer() {}

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public net.fabricmc.fabric.api.renderer.v1.mesh.MutableMesh mutableMesh() {
        return new net.vulkanmod.render.chunk.build.frapi.mesh.MutableMeshImpl();
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public void render(
            net.minecraft.client.renderer.block.ModelBlockRenderer modelBlockRenderer,
            net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
            net.minecraft.client.renderer.block.model.BlockStateModel blockStateModel,
            net.minecraft.world.level.block.state.BlockState blockState,
            net.minecraft.core.BlockPos blockPos,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider
                    blockVertexConsumerProvider,
            boolean cull,
            long seed,
            int overlay) {
        net.vulkanmod.render.chunk.build.frapi.render.BlockRenderContext.POOL
                .get()
                .render(
                        blockAndTintGetter,
                        blockStateModel,
                        blockState,
                        blockPos,
                        poseStack,
                        blockVertexConsumerProvider,
                        cull,
                        seed,
                        overlay);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public void render(
            com.mojang.blaze3d.vertex.PoseStack.Pose pose,
            net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider
                    blockVertexConsumerProvider,
            net.minecraft.client.renderer.block.model.BlockStateModel blockStateModel,
            float v,
            float v1,
            float v2,
            int i,
            int i1,
            net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
            net.minecraft.core.BlockPos blockPos,
            net.minecraft.world.level.block.state.BlockState blockState) {
        net.vulkanmod.render.chunk.build.frapi.render.SimpleBlockRenderContext.POOL
                .get()
                .bufferModel(
                        pose,
                        blockVertexConsumerProvider,
                        blockStateModel,
                        v,
                        v1,
                        v2,
                        i,
                        i1,
                        blockAndTintGetter,
                        blockPos,
                        blockState);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public void renderBlockAsEntity(
            net.minecraft.client.renderer.block.BlockRenderDispatcher blockRenderDispatcher,
            net.minecraft.world.level.block.state.BlockState state,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource,
            int light,
            int overlay,
            net.minecraft.world.level.BlockAndTintGetter blockView,
            net.minecraft.core.BlockPos pos) {
        net.minecraft.world.level.block.RenderShape blockRenderType = state.getRenderShape();
        if (blockRenderType != net.minecraft.world.level.block.RenderShape.INVISIBLE) {
            net.minecraft.client.renderer.block.model.BlockStateModel model =
                    blockRenderDispatcher.getBlockModel(state);
            int tint =
                    ((net.fabricmc.fabric.mixin.client.indigo.renderer
                                            .BlockRenderDispatcherAccessor)
                                    blockRenderDispatcher)
                            .getBlockColors()
                            .getColor(
                                    state,
                                    (net.minecraft.world.level.BlockAndTintGetter) null,
                                    (net.minecraft.core.BlockPos) null,
                                    0);
            float red = ((tint >> 16) & 255) / 255.0f;
            float green = ((tint >> 8) & 255) / 255.0f;
            float blue = (tint & 255) / 255.0f;
            net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer.render(
                    poseStack.last(),
                    net.fabricmc.fabric.api.renderer.v1.render.RenderLayerHelper.entityDelegate(
                            bufferSource),
                    model,
                    red,
                    green,
                    blue,
                    light,
                    overlay,
                    blockView,
                    pos,
                    state);
        }
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter getLayerRenderStateEmitter(
            net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState layer) {
        return ((net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState) layer)
                .getMutableMesh()
                .emitter();
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.Renderer
    public void setLayerRenderTypeGetter(
            net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState layer,
            net.fabricmc.fabric.api.renderer.v1.render.ItemRenderTypeGetter renderTypeGetter) {
        ((net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState) layer)
                .setRenderTypeGetter(renderTypeGetter);
    }
}
