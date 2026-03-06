package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/ModelBlockRendererMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.block.ModelBlockRenderer.class})
abstract class ModelBlockRendererMixin {
    ModelBlockRendererMixin() {}

    @org.spongepowered.asm.mixin.Overwrite
    public static void renderModel(
            com.mojang.blaze3d.vertex.PoseStack.Pose entry,
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
            net.minecraft.client.renderer.block.model.BlockStateModel model,
            float red,
            float green,
            float blue,
            int light,
            int overlay) {
        net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer.render(
                entry,
                layer -> {
                    return vertexConsumer;
                },
                model,
                red,
                green,
                blue,
                light,
                overlay,
                net.minecraft.world.level.EmptyBlockAndTintGetter.INSTANCE,
                net.minecraft.core.BlockPos.ZERO,
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
    }
}
