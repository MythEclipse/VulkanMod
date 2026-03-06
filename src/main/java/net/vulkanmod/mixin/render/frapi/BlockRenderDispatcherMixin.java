package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/BlockRenderDispatcherMixin.class */
@org.spongepowered.asm.mixin.Mixin({
    net.minecraft.client.renderer.block.BlockRenderDispatcher.class
})
abstract class BlockRenderDispatcherMixin {

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private net.minecraft.client.renderer.block.ModelBlockRenderer modelRenderer;

    BlockRenderDispatcherMixin() {}

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"renderBreakingTexture"},
            at = {
                @org.spongepowered.asm.mixin.injection.At(
                        value = "INVOKE_ASSIGN",
                        target =
                                "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;",
                        shift = org.spongepowered.asm.mixin.injection.At.Shift.AFTER)
            },
            cancellable = true)
    private void afterGetModel(
            net.minecraft.world.level.block.state.BlockState blockState,
            net.minecraft.core.BlockPos blockPos,
            net.minecraft.world.level.BlockAndTintGetter world,
            com.mojang.blaze3d.vertex.PoseStack matrixStack,
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci,
            @com.llamalad7.mixinextras.sugar.Local
                    net.minecraft.client.renderer.block.model.BlockStateModel model) {
        this.modelRenderer.render(
                world,
                model,
                blockState,
                blockPos,
                matrixStack,
                layer -> {
                    return vertexConsumer;
                },
                true,
                blockState.getSeed(blockPos),
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        ci.cancel();
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = {"renderSingleBlock"},
            at =
                    @org.spongepowered.asm.mixin.injection.At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/block/model/BlockStateModel;FFFII)V"))
    private void renderProxy(
            com.mojang.blaze3d.vertex.PoseStack.Pose entry,
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
            net.minecraft.client.renderer.block.model.BlockStateModel model,
            float red,
            float green,
            float blue,
            int light,
            int overlay,
            net.minecraft.world.level.block.state.BlockState state,
            com.mojang.blaze3d.vertex.PoseStack matrices,
            net.minecraft.client.renderer.MultiBufferSource vertexConsumers,
            int light1,
            int overlay1) {
        net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer.render(
                entry,
                net.fabricmc.fabric.api.renderer.v1.render.RenderLayerHelper.entityDelegate(
                        vertexConsumers),
                model,
                red,
                green,
                blue,
                light,
                overlay,
                net.minecraft.world.level.EmptyBlockAndTintGetter.INSTANCE,
                net.minecraft.core.BlockPos.ZERO,
                state);
    }
}
