package net.vulkanmod.render.chunk.build.renderer;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/renderer/BlockRenderer.class */
public class BlockRenderer extends net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext {
    private org.joml.Vector3f pos;
    private net.vulkanmod.render.chunk.build.thread.BuilderResources resources;
    private net.vulkanmod.render.vertex.TerrainBuilder terrainBuilder;
    final boolean backFaceCulling = net.vulkanmod.Initializer.CONFIG.backFaceCulling;
    private net.vulkanmod.render.vertex.TerrainRenderType renderType;

    public void setResources(net.vulkanmod.render.chunk.build.thread.BuilderResources resources) {
        this.resources = resources;
    }

    public BlockRenderer(net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline, net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline) {
        setupLightPipelines(flatLightPipeline, smoothLightPipeline);
        this.random = new net.minecraft.world.level.levelgen.SingleThreadedRandomSource(42L);
    }

    public void renderBlock(net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, org.joml.Vector3f pos) {
        this.pos = pos;
        this.blockPos = blockPos;
        this.blockState = blockState;
        this.random.setSeed(blockState.getSeed(blockPos));
        net.vulkanmod.render.vertex.TerrainRenderType renderType = net.vulkanmod.render.vertex.TerrainRenderType.getRemapped(net.vulkanmod.render.vertex.TerrainRenderType.get(net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(blockState)));
        this.renderType = renderType;
        this.terrainBuilder = this.resources.builderPack.builder(renderType);
        this.terrainBuilder.setBlockAttributes(blockState);
        net.minecraft.client.renderer.block.model.BlockStateModel model = net.minecraft.client.Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        net.minecraft.world.level.BlockAndTintGetter renderRegion = this.renderRegion;
        net.minecraft.world.phys.Vec3 offset = blockState.getOffset(blockPos);
        pos.add((float) offset.x, (float) offset.y, (float) offset.z);
        prepareForBlock(blockState, blockPos, blockState.getLightEmission() == 0);
        model.emitQuads(getEmitter(), renderRegion, blockPos, blockState, this.random, this::isFaceCulled);
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext
    protected com.mojang.blaze3d.vertex.VertexConsumer getVertexConsumer(net.minecraft.client.renderer.chunk.ChunkSectionLayer layer) {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext
    protected void endRenderQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) throws java.lang.MatchException {
        net.fabricmc.fabric.api.util.TriState aoMode = quad.ambientOcclusion();
        boolean ao = this.useAO && (aoMode == net.fabricmc.fabric.api.util.TriState.TRUE || (aoMode == net.fabricmc.fabric.api.util.TriState.DEFAULT && this.defaultAO));
        boolean emissive = quad.emissive();
        boolean vanillaShade = quad.shadeMode() == net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode.VANILLA;
        net.vulkanmod.render.vertex.TerrainBuilder terrainBuilder = getBufferBuilder(quad.renderLayer());
        net.vulkanmod.render.chunk.build.light.LightPipeline lightPipeline = ao ? this.smoothLightPipeline : this.flatLightPipeline;
        tintQuad(quad);
        shadeQuad(quad, lightPipeline, emissive, vanillaShade);
        bufferQuad(terrainBuilder, this.pos, quad, this.quadLightData);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private net.vulkanmod.render.vertex.TerrainBuilder getBufferBuilder(net.minecraft.client.renderer.chunk.ChunkSectionLayer layer) throws java.lang.MatchException {
        if (layer == null) {
            return this.terrainBuilder;
        }
        net.vulkanmod.render.vertex.TerrainRenderType renderType = net.vulkanmod.render.vertex.TerrainRenderType.get(layer);
        net.vulkanmod.render.vertex.TerrainBuilder bufferBuilder = this.resources.builderPack.builder(net.vulkanmod.render.vertex.TerrainRenderType.getRemapped(renderType));
        bufferBuilder.setBlockAttributes(this.blockState);
        return bufferBuilder;
    }

    public void bufferQuad(net.vulkanmod.render.vertex.TerrainBuilder terrainBuilder, org.joml.Vector3f pos, net.vulkanmod.render.model.quad.ModelQuadView quad, net.vulkanmod.render.chunk.build.light.data.QuadLightData quadLightData) {
        net.vulkanmod.render.chunk.cull.QuadFacing quadFacing = quad.getQuadFacing();
        if (this.renderType == net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT || !this.backFaceCulling) {
            quadFacing = net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED;
        }

        net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder = terrainBuilder.getBufferBuilder(quadFacing.ordinal());
        int packedNormal = quad.getNormal();
        float[] brightnessArr = quadLightData.br;
        int[] lights = quadLightData.lm;
        int idx = net.vulkanmod.render.model.quad.QuadUtils.getIterationStartIdx(brightnessArr, lights);
        bufferBuilder.ensureCapacity();
        byte b = 0;
        while (true) {
            byte i = b;
            if (i < 4) {
                float x = pos.x() + quad.getX(idx);
                float y = pos.y() + quad.getY(idx);
                float z = pos.z() + quad.getZ(idx);
                int quadColor = quad.getColor(idx);
                int color = net.vulkanmod.vulkan.util.ColorUtil.ARGB.toRGBA(quadColor);
                int light = lights[idx];
                float u = quad.getU(idx);
                float v = quad.getV(idx);
                bufferBuilder.vertex(x, y, z, color, u, v, light, packedNormal);
                idx = (idx + 1) & 3;
                b = (byte) (i + 1);
            } else {
                return;
            }
        }
    }
}
