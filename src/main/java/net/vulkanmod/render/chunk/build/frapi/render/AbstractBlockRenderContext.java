package net.vulkanmod.render.chunk.build.frapi.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/render/AbstractBlockRenderContext.class */
public abstract class AbstractBlockRenderContext
        extends net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext {
    protected final net.vulkanmod.render.chunk.build.color.BlockColorRegistry blockColorRegistry;
    protected net.minecraft.world.level.block.state.BlockState blockState;
    protected net.minecraft.core.BlockPos blockPos;
    protected net.minecraft.client.renderer.chunk.ChunkSectionLayer defaultLayer;
    protected net.minecraft.world.level.BlockAndTintGetter renderRegion;
    protected net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline;
    protected net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline;
    protected boolean useAO;
    protected boolean defaultAO;
    protected net.minecraft.util.RandomSource random;
    protected int cullCompletionFlags;
    protected int cullResultFlags;
    private final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl editorQuad = new net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl() { // from
                                                                                                                                                                       // class:
        // net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.1
        {
            this.data = new int[net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE];
            clear();
        }

        @Override // net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl
        public void emitDirectly() {
            net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.this
                    .renderQuad(this);
        }
    };
    protected net.minecraft.core.BlockPos.MutableBlockPos tempPos = new net.minecraft.core.BlockPos.MutableBlockPos();
    protected final it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap<net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.ShapePairKey> occlusionCache = new it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap<net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.ShapePairKey>(
            2048, 0.25f) { // from class:
        // net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.2
        protected void rehash(int i) {
        }
    };
    protected final net.vulkanmod.render.chunk.build.light.data.QuadLightData quadLightData = new net.vulkanmod.render.chunk.build.light.data.QuadLightData();
    protected boolean enableCulling = true;

    protected abstract com.mojang.blaze3d.vertex.VertexConsumer getVertexConsumer(
            net.minecraft.client.renderer.chunk.ChunkSectionLayer class_11515Var);

    protected AbstractBlockRenderContext() {
        this.occlusionCache.defaultReturnValue((byte) 127);
        net.minecraft.client.color.block.BlockColors blockColors = net.minecraft.client.Minecraft.getInstance()
                .getBlockColors();
        this.blockColorRegistry = net.vulkanmod.interfaces.color.BlockColorsExtended.from(blockColors)
                .getColorResolverMap();
    }

    protected void setupLightPipelines(
            net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline,
            net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline) {
        this.flatLightPipeline = flatLightPipeline;
        this.smoothLightPipeline = smoothLightPipeline;
    }

    public void prepareForWorld(
            net.minecraft.world.level.BlockAndTintGetter blockView, boolean enableCulling) {
        this.renderRegion = blockView;
        this.enableCulling = enableCulling;
    }

    public void prepareForBlock(
            net.minecraft.world.level.block.state.BlockState blockState,
            net.minecraft.core.BlockPos blockPos,
            boolean modelAo) {
        this.blockPos = blockPos;
        this.blockState = blockState;
        this.defaultLayer = net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(blockState);
        this.useAO = net.minecraft.client.Minecraft.useAmbientOcclusion();
        this.defaultAO = this.useAO && modelAo && blockState.getLightEmission() == 0;
        this.cullCompletionFlags = 0;
        this.cullResultFlags = 0;
    }

    public boolean isFaceCulled(
            @org.jetbrains.annotations.Nullable net.minecraft.core.Direction face) {
        return !shouldRenderFace(face);
    }

    public boolean shouldRenderFace(net.minecraft.core.Direction face) {
        if (face == null || !this.enableCulling) {
            return true;
        }
        int mask = 1 << face.get3DDataValue();
        if ((this.cullCompletionFlags & mask) != 0) {
            return (this.cullResultFlags & mask) != 0;
        }
        this.cullCompletionFlags |= mask;
        if (faceNotOccluded(this.blockState, face)) {
            this.cullResultFlags |= mask;
            return true;
        }
        return false;
    }

    public boolean faceNotOccluded(
            net.minecraft.world.level.block.state.BlockState blockState,
            net.minecraft.core.Direction face) {
        net.minecraft.world.level.block.state.BlockState adjBlockState = this.renderRegion
                .getBlockState(this.tempPos.setWithOffset(this.blockPos, face));
        if (blockState.skipRendering(adjBlockState, face)) {
            return false;
        }
        if (adjBlockState.canOcclude()) {
            net.minecraft.world.phys.shapes.VoxelShape shape = blockState.getFaceOcclusionShape(face);
            if (shape.isEmpty()) {
                return true;
            }
            net.minecraft.world.phys.shapes.VoxelShape adjShape = adjBlockState
                    .getFaceOcclusionShape(face.getOpposite());
            if (adjShape.isEmpty()) {
                return true;
            }
            if (shape == net.minecraft.world.phys.shapes.Shapes.block()
                    && adjShape == net.minecraft.world.phys.shapes.Shapes.block()) {
                return false;
            }
            net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.ShapePairKey blockStatePairKey = new net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext.ShapePairKey(
                    shape, adjShape);
            byte b = this.occlusionCache.getAndMoveToFirst(blockStatePairKey);
            if (b != 127) {
                return b != 0;
            }
            boolean bl = net.minecraft.world.phys.shapes.Shapes.joinIsNotEmpty(
                    shape, adjShape, net.minecraft.world.phys.shapes.BooleanOp.ONLY_FIRST);
            if (this.occlusionCache.size() == 2048) {
                this.occlusionCache.removeLastByte();
            }
            this.occlusionCache.putAndMoveToFirst(blockStatePairKey, (byte) (bl ? 1 : 0));
            return bl;
        }
        return true;
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext
    public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter getEmitter() {
        this.editorQuad.clear();
        return this.editorQuad;
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext
    protected void bufferQuad(
            net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quadView) {
        renderQuad(quadView);
    }

    private void renderQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        if (isFaceCulled(quad.cullFace())) {
            return;
        }
        endRenderQuad(quad);
    }

    protected void endRenderQuad(
            net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
    }

    protected void tintQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        int tintIndex = quad.tintIndex();
        if (tintIndex != -1) {
            int blockColor = getBlockColor(this.renderRegion, tintIndex);
            for (int i = 0; i < 4; i++) {
                quad.color(
                        i,
                        net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.multiplyColor(
                                blockColor, quad.color(i)));
            }
        }
    }

    private int getBlockColor(net.minecraft.world.level.BlockAndTintGetter region, int colorIndex) {
        net.minecraft.client.color.block.BlockColor blockColor = this.blockColorRegistry
                .getBlockColor(this.blockState.getBlock());
        int color = blockColor != null
                ? blockColor.getColor(this.blockState, region, this.blockPos, colorIndex)
                : -1;
        return (-16777216) | color;
    }

    protected void shadeQuad(
            net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad,
            net.vulkanmod.render.chunk.build.light.LightPipeline lightPipeline,
            boolean emissive,
            boolean vanillaShade) {
        net.vulkanmod.render.chunk.build.light.data.QuadLightData data = this.quadLightData;
        lightPipeline.calculate(
                quad, this.blockPos, data, quad.cullFace(), quad.lightFace(), quad.diffuseShade());
        if (emissive) {
            for (int i = 0; i < 4; i++) {
                quad.color(
                        i,
                        net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.multiplyRGB(
                                quad.color(i), data.br[i]));
                data.lm[i] = 15728880;
            }
            return;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            quad.color(
                    i2,
                    net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.multiplyRGB(
                            quad.color(i2), data.br[i2]));
            data.lm[i2] = net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.maxBrightness(
                    quad.lightmap(i2), data.lm[i2]);
        }
    }

    public net.minecraft.client.renderer.chunk.ChunkSectionLayer effectiveRenderLayer(
            @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.chunk.ChunkSectionLayer quadRenderLayer) {
        return quadRenderLayer == null ? this.defaultLayer : quadRenderLayer;
    }

    public void emitVanillaBlockQuads(
            net.minecraft.client.renderer.block.model.BlockStateModel model,
            @org.jetbrains.annotations.Nullable net.minecraft.world.level.block.state.BlockState state,
            java.util.function.Supplier<net.minecraft.util.RandomSource> randomSupplier,
            java.util.function.Predicate<net.minecraft.core.Direction> cullTest) {
        net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad = this.editorQuad;
        for (int i = 0; i <= 6; i++) {
            net.minecraft.core.Direction cullFace = net.fabricmc.fabric.api.renderer.v1.model.ModelHelper
                    .faceFromIndex(i);
            if (!cullTest.test(cullFace)) {
                java.util.List<net.minecraft.client.renderer.block.model.BlockModelPart> parts = model
                        .collectParts(this.random);
                int partCount = parts.size();
                for (int j = 0; j < partCount; j++) {
                    parts.get(j).emitQuads(quad, cullTest);
                }
            }
        }
    }

    static record ShapePairKey(
            net.minecraft.world.phys.shapes.VoxelShape first,
            net.minecraft.world.phys.shapes.VoxelShape second) {

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof ShapePairKey)) {
                return false;
            }
            ShapePairKey shapePairKey = (ShapePairKey) object;
            return this.first == shapePairKey.first && this.second == shapePairKey.second;
        }

        @Override
        public int hashCode() {
            return (System.identityHashCode(this.first) * 31)
                    + System.identityHashCode(this.second);
        }
    }
}
