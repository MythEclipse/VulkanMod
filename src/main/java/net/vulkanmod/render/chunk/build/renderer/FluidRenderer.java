package net.vulkanmod.render.chunk.build.renderer;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/renderer/FluidRenderer.class */
public class FluidRenderer
                implements net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.DefaultRenderer {
        private static final float MAX_FLUID_HEIGHT = 0.8888889f;
        net.vulkanmod.render.chunk.build.thread.BuilderResources resources;
        private final net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline;
        private final net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline;
        private final net.minecraft.core.BlockPos.MutableBlockPos mBlockPos = new net.minecraft.core.BlockPos.MutableBlockPos();
        private final net.vulkanmod.render.model.quad.ModelQuad modelQuad = new net.vulkanmod.render.model.quad.ModelQuad();
        private final int[] quadColors = new int[4];

        public FluidRenderer(
                        net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline,
                        net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline) {
                this.smoothLightPipeline = smoothLightPipeline;
                this.flatLightPipeline = flatLightPipeline;
        }

        public void setResources(net.vulkanmod.render.chunk.build.thread.BuilderResources resources) {
                this.resources = resources;
        }

        public void renderLiquid(
                        net.minecraft.world.level.block.state.BlockState blockState,
                        net.minecraft.world.level.material.FluidState fluidState,
                        net.minecraft.core.BlockPos blockPos) {
                net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler = net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE
                                .get(fluidState.getType());
                net.vulkanmod.render.vertex.TerrainRenderType renderType = net.vulkanmod.render.vertex.TerrainRenderType
                                .get(
                                                net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderLayer(
                                                                fluidState));
                net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder = this.resources.builderPack
                                .builder(
                                                net.vulkanmod.render.vertex.TerrainRenderType.getRemapped(
                                                                renderType))
                                .getBufferBuilder(
                                                net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal());
                if (handler == null) {
                        boolean isLava = fluidState.is(net.minecraft.tags.FluidTags.LAVA);
                        handler = net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE
                                        .get(
                                                        isLava
                                                                        ? net.minecraft.world.level.material.Fluids.LAVA
                                                                        : net.minecraft.world.level.material.Fluids.WATER);
                }
                net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.render(
                                handler,
                                this.resources.getRegion(),
                                blockPos,
                                bufferBuilder,
                                blockState,
                                fluidState,
                                this);
        }

        private boolean isFaceOccludedByState(
                        net.minecraft.world.level.BlockGetter blockGetter,
                        float h,
                        net.minecraft.core.Direction direction,
                        net.minecraft.core.BlockPos blockPos,
                        net.minecraft.world.level.block.state.BlockState blockState) {
                this.mBlockPos.set(blockPos).offset(net.minecraft.core.Direction.DOWN.getUnitVec3i());
                if (blockState.canOcclude()) {
                        net.minecraft.world.phys.shapes.VoxelShape occlusionShape = blockState.getOcclusionShape();
                        if (occlusionShape == net.minecraft.world.phys.shapes.Shapes.block()) {
                                return direction != net.minecraft.core.Direction.UP;
                        }
                        if (occlusionShape.isEmpty()) {
                                return false;
                        }
                        net.minecraft.world.phys.shapes.VoxelShape voxelShape = net.minecraft.world.phys.shapes.Shapes
                                        .box(0.0d, 0.0d, 0.0d, 1.0d, h, 1.0d);
                        return net.minecraft.world.phys.shapes.Shapes.blockOccludes(
                                        voxelShape, occlusionShape, direction);
                }
                return false;
        }

        public static boolean shouldRenderFace(
                        net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
                        net.minecraft.core.BlockPos blockPos,
                        net.minecraft.world.level.material.FluidState fluidState,
                        net.minecraft.world.level.block.state.BlockState blockState,
                        net.minecraft.core.Direction direction,
                        net.minecraft.world.level.block.state.BlockState adjBlockState) {
                if (adjBlockState.getFluidState().getType().isSame(fluidState.getType())) {
                        return false;
                }
                return (blockState.canOcclude()
                                && blockState.isFaceSturdy(blockAndTintGetter, blockPos, direction))
                                                ? false
                                                : true;
        }

        public net.minecraft.world.level.block.state.BlockState getAdjBlockState(
                        net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
                        int x,
                        int y,
                        int z,
                        net.minecraft.core.Direction dir) {
                this.mBlockPos.set(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
                return blockAndTintGetter.getBlockState(this.mBlockPos);
        }

        @Override // net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.DefaultRenderer
        public void render(
                        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler,
                        net.minecraft.world.level.BlockAndTintGetter world,
                        net.minecraft.core.BlockPos pos,
                        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer,
                        net.minecraft.world.level.block.state.BlockState blockState,
                        net.minecraft.world.level.material.FluidState fluidState) {
                render(
                                handler,
                                blockState,
                                fluidState,
                                pos,
                                (net.vulkanmod.render.vertex.TerrainBufferBuilder) vertexConsumer);
        }

        /* JADX WARN: Removed duplicated region for block: B:80:0x0690 */
        /* JADX WARN: Removed duplicated region for block: B:98:0x078c A[SYNTHETIC] */
        public void render(
                        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler,
                        net.minecraft.world.level.block.state.BlockState blockState,
                        net.minecraft.world.level.material.FluidState fluidState,
                        net.minecraft.core.BlockPos blockPos,
                        net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder) {
                net.minecraft.world.level.BlockAndTintGetter region = this.resources.getRegion();

                int color = handler.getFluidColor(region, blockPos, fluidState);
                net.minecraft.client.renderer.texture.TextureAtlasSprite[] sprites = handler.getFluidSprites(region,
                                blockPos, fluidState);

                float r = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackR(color);
                float g = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackG(color);
                float b = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackB(color);

                final int posX = blockPos.getX();
                final int posY = blockPos.getY();
                final int posZ = blockPos.getZ();

                boolean useAO = blockState.getLightEmission() == 0
                                && net.minecraft.client.Minecraft.useAmbientOcclusion();
                net.vulkanmod.render.chunk.build.light.LightPipeline lightPipeline = useAO ? this.smoothLightPipeline
                                : this.flatLightPipeline;

                net.minecraft.world.level.block.state.BlockState downState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.DOWN);
                net.minecraft.world.level.block.state.BlockState upState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.UP);
                net.minecraft.world.level.block.state.BlockState northState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.NORTH);
                net.minecraft.world.level.block.state.BlockState southState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.SOUTH);
                net.minecraft.world.level.block.state.BlockState westState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.WEST);
                net.minecraft.world.level.block.state.BlockState eastState = getAdjBlockState(region, posX, posY, posZ,
                                net.minecraft.core.Direction.EAST);

                boolean rUf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.UP,
                                upState);
                boolean rDf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.DOWN,
                                downState)
                                && !isFaceOccludedByState(
                                                region,
                                                MAX_FLUID_HEIGHT,
                                                net.minecraft.core.Direction.DOWN,
                                                blockPos,
                                                downState);
                boolean rNf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.NORTH,
                                northState);
                boolean rSf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.SOUTH,
                                southState);
                boolean rWf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.WEST,
                                westState);
                boolean rEf = shouldRenderFace(
                                region,
                                blockPos,
                                fluidState,
                                blockState,
                                net.minecraft.core.Direction.EAST,
                                eastState);

                if (!(rUf || rDf || rEf || rWf || rNf || rSf))
                        return;

                float brightnessUp = region.getShade(net.minecraft.core.Direction.UP, true);

                net.minecraft.world.level.material.Fluid fluid = fluidState.getType();
                float height = this.getHeight(region, fluid, blockPos, blockState);
                float neHeight;
                float nwHeight;
                float seHeight;
                float swHeight;
                if (height >= 1.0F) {
                        neHeight = 1.0F;
                        nwHeight = 1.0F;
                        seHeight = 1.0F;
                        swHeight = 1.0F;
                } else {
                        float s = this.getHeight(
                                        region,
                                        fluid,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.NORTH.getUnitVec3i()),
                                        northState);
                        float t = this.getHeight(
                                        region,
                                        fluid,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.SOUTH.getUnitVec3i()),
                                        southState);
                        float u = this.getHeight(
                                        region,
                                        fluid,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.EAST.getUnitVec3i()),
                                        eastState);
                        float v = this.getHeight(
                                        region,
                                        fluid,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.WEST.getUnitVec3i()),
                                        westState);
                        neHeight = this.calculateAverageHeight(
                                        region,
                                        fluid,
                                        height,
                                        s,
                                        u,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.NORTH.getUnitVec3i())
                                                        .offset(net.minecraft.core.Direction.EAST.getUnitVec3i()));
                        nwHeight = this.calculateAverageHeight(
                                        region,
                                        fluid,
                                        height,
                                        s,
                                        v,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.NORTH.getUnitVec3i())
                                                        .offset(net.minecraft.core.Direction.WEST.getUnitVec3i()));
                        seHeight = this.calculateAverageHeight(
                                        region,
                                        fluid,
                                        height,
                                        t,
                                        u,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.SOUTH.getUnitVec3i())
                                                        .offset(net.minecraft.core.Direction.EAST.getUnitVec3i()));
                        swHeight = this.calculateAverageHeight(
                                        region,
                                        fluid,
                                        height,
                                        t,
                                        v,
                                        mBlockPos
                                                        .set(blockPos)
                                                        .offset(net.minecraft.core.Direction.SOUTH.getUnitVec3i())
                                                        .offset(net.minecraft.core.Direction.WEST.getUnitVec3i()));
                }

                float x0 = (posX & 15);
                float y0 = (posY & 15);
                float z0 = (posZ & 15);

                float y = rDf ? 0.001F : 0.0F;

                this.modelQuad.setFlags(0);

                if (rUf
                                && !isFaceOccludedByState(
                                                region,
                                                java.lang.Math.min(
                                                                java.lang.Math.min(nwHeight, swHeight),
                                                                java.lang.Math.min(seHeight, neHeight)),
                                                net.minecraft.core.Direction.UP,
                                                blockPos,
                                                upState)) {
                        float u0, u1, u2, u3;
                        float v0, v1, v2, v3;

                        nwHeight -= 0.001F;
                        swHeight -= 0.001F;
                        seHeight -= 0.001F;
                        neHeight -= 0.001F;

                        net.minecraft.world.phys.Vec3 vec3 = fluidState.getFlow(region, blockPos);

                        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite;
                        if (vec3.x == 0.0 && vec3.z == 0.0) {
                                sprite = sprites[0];
                                u0 = sprite.getU(0.0F);
                                v0 = sprite.getV(0.0F);
                                u1 = u0;
                                v1 = sprite.getV(1.0F);
                                u2 = sprite.getU(1.0F);
                                v2 = v1;
                                u3 = u2;
                                v3 = v0;
                        } else {
                                sprite = sprites[1];
                                float ah = (float) net.minecraft.util.Mth.atan2(vec3.z, vec3.x) - 1.5707964F;
                                float ai = net.minecraft.util.Mth.sin(ah) * 0.25F;
                                float aj = net.minecraft.util.Mth.cos(ah) * 0.25F;
                                u0 = sprite.getU(0.5F + (-aj - ai));
                                v0 = sprite.getV(0.5F - aj + ai);
                                u1 = sprite.getU(0.5F - aj + ai);
                                v1 = sprite.getV(0.5F + aj + ai);
                                u2 = sprite.getU(0.5F + aj + ai);
                                v2 = sprite.getV(0.5F + (aj - ai));
                                u3 = sprite.getU(0.5F + (aj - ai));
                                v3 = sprite.getV(0.5F + (-aj - ai));
                        }

                        // uvShrinkRatio() removed in 1.21.11 — no UV shrinking needed

                        float brightness = brightnessUp;
                        setVertex(this.modelQuad, 0, 0.0f, nwHeight, 0.0f, u0, v0);
                        setVertex(this.modelQuad, 1, 0.0f, swHeight, 1.0f, u1, v1);
                        setVertex(this.modelQuad, 2, 1.0f, seHeight, 1.0f, u2, v2);
                        setVertex(this.modelQuad, 3, 1.0f, neHeight, 0.0f, u3, v3);

                        updateQuad(this.modelQuad, blockPos, lightPipeline, net.minecraft.core.Direction.UP);
                        updateColor(r, g, b, brightness);
                        putQuad(this.modelQuad, bufferBuilder, x0, y0, z0, false);

                        if (fluidState.shouldRenderBackwardUpFace(region, blockPos.above())) {
                                putQuad(this.modelQuad, bufferBuilder, x0, y0, z0, true);
                        }
                }

                if (rDf) {
                        float u0 = sprites[0].getU0();
                        float u1 = sprites[0].getU1();
                        float v0 = sprites[0].getV0();
                        float v1 = sprites[0].getV1();

                        float brightness = region.getShade(net.minecraft.core.Direction.DOWN, true);

                        setVertex(this.modelQuad, 0, 0.0f, y, 1.0f, u0, v1);
                        setVertex(this.modelQuad, 1, 0.0f, y, 0.0f, u0, v0);
                        setVertex(this.modelQuad, 2, 1.0f, y, 0.0f, u1, v0);
                        setVertex(this.modelQuad, 3, 1.0f, y, 1.0f, u1, v1);

                        updateQuad(this.modelQuad, blockPos, lightPipeline, net.minecraft.core.Direction.DOWN);
                        updateColor(r, g, b, brightness);
                        putQuad(this.modelQuad, bufferBuilder, x0, y0, z0, false);
                }

                this.modelQuad.setFlags(
                                net.vulkanmod.render.model.quad.ModelQuadFlags.IS_PARALLEL
                                                | net.vulkanmod.render.model.quad.ModelQuadFlags.IS_ALIGNED);

                for (net.minecraft.core.Direction direction : net.vulkanmod.render.chunk.util.Util.XZ_DIRECTIONS) {
                        float h1;
                        float h2;
                        float x1;
                        float z1;
                        float x2;
                        float z2;

                        final float E = 0.001f;
                        final float E2 = 0.999f;

                        net.minecraft.world.level.block.state.BlockState adjState;
                        switch (direction) {
                                case NORTH -> {
                                        if (!rNf)
                                                continue;
                                        h1 = nwHeight;
                                        h2 = neHeight;
                                        x1 = 0.0f;
                                        x2 = 1.0f;
                                        z1 = E;
                                        z2 = E;
                                        adjState = northState;
                                }
                                case SOUTH -> {
                                        if (!rSf)
                                                continue;
                                        h1 = seHeight;
                                        h2 = swHeight;
                                        x1 = 1.0f;
                                        x2 = 0.0f;
                                        z1 = E2;
                                        z2 = E2;
                                        adjState = southState;
                                }
                                case WEST -> {
                                        if (!rWf)
                                                continue;
                                        h1 = swHeight;
                                        h2 = nwHeight;
                                        x1 = E;
                                        x2 = E;
                                        z1 = 1.0f;
                                        z2 = 0.0f;
                                        adjState = westState;
                                }
                                case EAST -> {
                                        if (!rEf)
                                                continue;
                                        h1 = neHeight;
                                        h2 = seHeight;
                                        x1 = E2;
                                        x2 = E2;
                                        z1 = 0.0f;
                                        z2 = 1.0f;
                                        adjState = eastState;
                                }
                                default -> {
                                        continue;
                                }
                        }

                        if (isFaceOccludedByState(
                                        region, java.lang.Math.max(h1, h2), direction, blockPos, adjState))
                                continue;

                        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = sprites[1];
                        boolean isOverlay = false;

                        if (sprites.length > 2) {
                                if (net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE
                                                .isBlockTransparent(adjState.getBlock())) {
                                        sprite = sprites[2];
                                        isOverlay = true;
                                }
                        }

                        float u0 = sprite.getU(0.0F);
                        float u1 = sprite.getU(0.5F);
                        float v0 = sprite.getV((1.0F - h1) * 0.5F);
                        float v1 = sprite.getV((1.0F - h2) * 0.5F);
                        float v2 = sprite.getV(0.5F);

                        float brightness = region.getShade(direction, true);

                        setVertex(this.modelQuad, 0, x2, h2, z2, u1, v1);
                        setVertex(this.modelQuad, 1, x2, y, z2, u1, v2);
                        setVertex(this.modelQuad, 2, x1, y, z1, u0, v2);
                        setVertex(this.modelQuad, 3, x1, h1, z1, u0, v0);

                        updateQuad(this.modelQuad, blockPos, lightPipeline, direction);
                        updateColor(r, g, b, brightness);
                        putQuad(this.modelQuad, bufferBuilder, x0, y0, z0, false);

                        if (!isOverlay) {
                                putQuad(this.modelQuad, bufferBuilder, x0, y0, z0, true);
                        }
                }
        }

        /*
         * JADX INFO: renamed from:
         * net.vulkanmod.render.chunk.build.renderer.FluidRenderer$1, reason: invalid
         * class name
         */
        /*
         * JADX INFO: loaded from:
         * VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/renderer/
         * FluidRenderer$1.class
         */
        static /* synthetic */ class AnonymousClass1 {
                static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction = new int[net.minecraft.core.Direction
                                .values().length];

                static {
                        try {
                                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.NORTH
                                                .ordinal()] = 1;
                        } catch (java.lang.NoSuchFieldError e) {
                        }
                        try {
                                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.SOUTH
                                                .ordinal()] = 2;
                        } catch (java.lang.NoSuchFieldError e2) {
                        }
                        try {
                                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.WEST
                                                .ordinal()] = 3;
                        } catch (java.lang.NoSuchFieldError e3) {
                        }
                        try {
                                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.EAST
                                                .ordinal()] = 4;
                        } catch (java.lang.NoSuchFieldError e4) {
                        }
                }
        }

        private float calculateAverageHeight(
                        net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
                        net.minecraft.world.level.material.Fluid fluid,
                        float f,
                        float g,
                        float h,
                        net.minecraft.core.BlockPos blockPos) {
                if (h < 1.0f && g < 1.0f) {
                        float[] fs = new float[2];
                        if (h > 0.0f || g > 0.0f) {
                                float i = getHeight(blockAndTintGetter, fluid, blockPos);
                                if (i >= 1.0f) {
                                        return 1.0f;
                                }
                                addWeightedHeight(fs, i);
                        }
                        addWeightedHeight(fs, f);
                        addWeightedHeight(fs, h);
                        addWeightedHeight(fs, g);
                        return fs[0] / fs[1];
                }
                return 1.0f;
        }

        private void addWeightedHeight(float[] fs, float f) {
                if (f >= 0.8f) {
                        fs[0] = fs[0] + (f * 10.0f);
                        fs[1] = fs[1] + 10.0f;
                } else if (f >= 0.0f) {
                        fs[0] = fs[0] + f;
                        fs[1] = fs[1] + 1.0f;
                }
        }

        private float getHeight(
                        net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
                        net.minecraft.world.level.material.Fluid fluid,
                        net.minecraft.core.BlockPos blockPos) {
                net.minecraft.world.level.block.state.BlockState blockState = blockAndTintGetter
                                .getBlockState(blockPos);
                return getHeight(blockAndTintGetter, fluid, blockPos, blockState);
        }

        private float getHeight(
                        net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter,
                        net.minecraft.world.level.material.Fluid fluid,
                        net.minecraft.core.BlockPos blockPos,
                        net.minecraft.world.level.block.state.BlockState adjBlockState) {
                net.minecraft.world.level.material.FluidState adjFluidState = adjBlockState.getFluidState();
                if (!fluid.isSame(adjFluidState.getType())) {
                        return !adjBlockState.isSolidRender() ? 0.0f : -1.0f;
                }
                net.minecraft.world.level.block.state.BlockState blockState2 = blockAndTintGetter.getBlockState(
                                blockPos.offset(net.minecraft.core.Direction.UP.getUnitVec3i()));
                if (fluid.isSame(blockState2.getFluidState().getType())) {
                        return 1.0f;
                }
                return adjFluidState.getOwnHeight();
        }

        private void putQuad(
                        net.vulkanmod.render.model.quad.ModelQuad quad,
                        net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder,
                        float xOffset,
                        float yOffset,
                        float zOffset,
                        boolean flip) {
                net.vulkanmod.render.chunk.build.light.data.QuadLightData quadLightData = this.resources.quadLightData;
                int k = net.vulkanmod.render.model.quad.QuadUtils.getIterationStartIdx(quadLightData.br);
                bufferBuilder.ensureCapacity();
                for (int j = 0; j < 4; j++) {
                        int i = k;
                        float x = xOffset + quad.getX(i);
                        float y = yOffset + quad.getY(i);
                        float z = zOffset + quad.getZ(i);
                        bufferBuilder.vertex(
                                        x,
                                        y,
                                        z,
                                        this.quadColors[i],
                                        quad.getU(i),
                                        quad.getV(i),
                                        quadLightData.lm[i],
                                        0);
                        k = (k + (flip ? -1 : 1)) & 3;
                }
        }

        private void setVertex(
                        net.vulkanmod.render.model.quad.ModelQuad quad,
                        int i,
                        float x,
                        float y,
                        float z,
                        float u,
                        float v) {
                quad.setX(i, x);
                quad.setY(i, y);
                quad.setZ(i, z);
                quad.setU(i, u);
                quad.setV(i, v);
        }

        private void updateQuad(
                        net.vulkanmod.render.model.quad.ModelQuad quad,
                        net.minecraft.core.BlockPos blockPos,
                        net.vulkanmod.render.chunk.build.light.LightPipeline lightPipeline,
                        net.minecraft.core.Direction dir) {
                lightPipeline.calculate(quad, blockPos, this.resources.quadLightData, null, dir, false);
        }

        private void updateColor(float r, float g, float b, float brightness) {
                net.vulkanmod.render.chunk.build.light.data.QuadLightData quadLightData = this.resources.quadLightData;
                for (int i = 0; i < 4; i++) {
                        float br = quadLightData.br[i] * brightness;
                        float r1 = r * br;
                        float g1 = g * br;
                        float b1 = b * br;
                        this.quadColors[i] = net.vulkanmod.vulkan.util.ColorUtil.RGBA.pack(r1, g1, b1, 1.0f);
                }
        }
}
