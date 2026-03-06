package net.vulkanmod.render.chunk.build.frapi.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/render/SimpleBlockRenderContext.class */
public class SimpleBlockRenderContext
        extends net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext {
    public static final java.lang.ThreadLocal<
                    net.vulkanmod.render.chunk.build.frapi.render.SimpleBlockRenderContext>
            POOL =
                    java.lang.ThreadLocal.withInitial(
                            net.vulkanmod.render.chunk.build.frapi.render.SimpleBlockRenderContext
                                    ::new);
    private final net.minecraft.util.RandomSource random = net.minecraft.util.RandomSource.create();
    private net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider vertexConsumers;
    private net.minecraft.client.renderer.chunk.ChunkSectionLayer defaultRenderLayer;
    private float red;
    private float green;
    private float blue;
    private int light;

    @org.jetbrains.annotations.Nullable
    private net.minecraft.client.renderer.chunk.ChunkSectionLayer lastRenderLayer;

    @org.jetbrains.annotations.Nullable
    private com.mojang.blaze3d.vertex.VertexConsumer lastVertexConsumer;

    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext
    protected void bufferQuad(
            net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer;
        net.minecraft.client.renderer.chunk.ChunkSectionLayer quadRenderLayer = quad.renderLayer();
        net.minecraft.client.renderer.chunk.ChunkSectionLayer renderLayer =
                quadRenderLayer == null ? this.defaultRenderLayer : quadRenderLayer;
        if (renderLayer == this.lastRenderLayer) {
            vertexConsumer = this.lastVertexConsumer;
        } else {
            com.mojang.blaze3d.vertex.VertexConsumer buffer =
                    this.vertexConsumers.getBuffer(renderLayer);
            vertexConsumer = buffer;
            this.lastVertexConsumer = buffer;
            this.lastRenderLayer = renderLayer;
        }
        tintQuad(quad);
        shadeQuad(quad, quad.emissive());
        bufferQuad(quad, vertexConsumer);
    }

    private void tintQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        if (quad.tintIndex() != -1) {
            float red = this.red;
            float green = this.green;
            float blue = this.blue;
            for (int i = 0; i < 4; i++) {
                quad.color(i, net.minecraft.util.ARGB.scaleRGB(quad.color(i), red, green, blue));
            }
        }
    }

    private void shadeQuad(
            net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad,
            boolean emissive) {
        if (emissive) {
            for (int i = 0; i < 4; i++) {
                quad.lightmap(i, 15728880);
            }
            return;
        }
        int light = this.light;
        for (int i2 = 0; i2 < 4; i2++) {
            quad.lightmap(
                    i2,
                    net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.maxLight(
                            quad.lightmap(i2), light));
        }
    }

    public void bufferModel(
            com.mojang.blaze3d.vertex.PoseStack.Pose entry,
            net.fabricmc.fabric.api.renderer.v1.render.BlockVertexConsumerProvider vertexConsumers,
            net.minecraft.client.renderer.block.model.BlockStateModel model,
            float red,
            float green,
            float blue,
            int light,
            int overlay,
            net.minecraft.world.level.BlockAndTintGetter blockView,
            net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state) {
        this.matrices = entry;
        this.overlay = overlay;
        this.vertexConsumers = vertexConsumers;
        this.defaultRenderLayer =
                net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(state);
        this.red = net.minecraft.util.Mth.clamp(red, 0.0f, 1.0f);
        this.green = net.minecraft.util.Mth.clamp(green, 0.0f, 1.0f);
        this.blue = net.minecraft.util.Mth.clamp(blue, 0.0f, 1.0f);
        this.light = light;
        this.random.setSeed(42L);
        model.emitQuads(
                getEmitter(),
                blockView,
                pos,
                state,
                this.random,
                cullFace -> {
                    return false;
                });
        this.matrices = null;
        this.vertexConsumers = null;
        this.lastRenderLayer = null;
        this.lastVertexConsumer = null;
    }
}
