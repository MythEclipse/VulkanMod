package net.vulkanmod.render.chunk.build.renderer;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/renderer/FluidRenderer.class */
public class FluidRenderer implements net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.DefaultRenderer {
    private static final float MAX_FLUID_HEIGHT = 0.8888889f;
    net.vulkanmod.render.chunk.build.thread.BuilderResources resources;
    private final net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline;
    private final net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline;
    private final net.minecraft.core.BlockPos.MutableBlockPos mBlockPos = new net.minecraft.core.BlockPos.MutableBlockPos();
    private final net.vulkanmod.render.model.quad.ModelQuad modelQuad = new net.vulkanmod.render.model.quad.ModelQuad();
    private final int[] quadColors = new int[4];

    public FluidRenderer(net.vulkanmod.render.chunk.build.light.LightPipeline flatLightPipeline, net.vulkanmod.render.chunk.build.light.LightPipeline smoothLightPipeline) {
        this.smoothLightPipeline = smoothLightPipeline;
        this.flatLightPipeline = flatLightPipeline;
    }

    public void setResources(net.vulkanmod.render.chunk.build.thread.BuilderResources resources) {
        this.resources = resources;
    }

    public void renderLiquid(net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.world.level.material.FluidState fluidState, net.minecraft.core.BlockPos blockPos) {
        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler = net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE.get(fluidState.getType());
        net.vulkanmod.render.vertex.TerrainRenderType renderType = net.vulkanmod.render.vertex.TerrainRenderType.get(net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderLayer(fluidState));
        net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder = this.resources.builderPack.builder(net.vulkanmod.render.vertex.TerrainRenderType.getRemapped(renderType)).getBufferBuilder(net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal());
        if (handler == null) {
            boolean isLava = fluidState.is(net.minecraft.tags.FluidTags.LAVA);
            handler = net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE.get(isLava ? net.minecraft.world.level.material.Fluids.LAVA : net.minecraft.world.level.material.Fluids.WATER);
        }
        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.render(handler, this.resources.getRegion(), blockPos, bufferBuilder, blockState, fluidState, this);
    }

    private boolean isFaceOccludedByState(net.minecraft.world.level.BlockGetter blockGetter, float h, net.minecraft.core.Direction direction, net.minecraft.core.BlockPos blockPos, net.minecraft.world.level.block.state.BlockState blockState) {
        this.mBlockPos.set(blockPos).offset(net.minecraft.core.Direction.DOWN.getUnitVec3i());
        if (blockState.canOcclude()) {
            net.minecraft.world.phys.shapes.VoxelShape occlusionShape = blockState.getOcclusionShape();
            if (occlusionShape == net.minecraft.world.phys.shapes.Shapes.block()) {
                return direction != net.minecraft.core.Direction.UP;
            }
            if (occlusionShape.isEmpty()) {
                return false;
            }
            net.minecraft.world.phys.shapes.VoxelShape voxelShape = net.minecraft.world.phys.shapes.Shapes.box(0.0d, 0.0d, 0.0d, 1.0d, h, 1.0d);
            return net.minecraft.world.phys.shapes.Shapes.blockOccludes(voxelShape, occlusionShape, direction);
        }
        return false;
    }

    public static boolean shouldRenderFace(net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, net.minecraft.core.BlockPos blockPos, net.minecraft.world.level.material.FluidState fluidState, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.Direction direction, net.minecraft.world.level.block.state.BlockState adjBlockState) {
        if (adjBlockState.getFluidState().getType().isSame(fluidState.getType())) {
            return false;
        }
        return (blockState.canOcclude() && blockState.isFaceSturdy(blockAndTintGetter, blockPos, direction)) ? false : true;
    }

    public net.minecraft.world.level.block.state.BlockState getAdjBlockState(net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, int x, int y, int z, net.minecraft.core.Direction dir) {
        this.mBlockPos.set(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
        return blockAndTintGetter.getBlockState(this.mBlockPos);
    }

    @Override // net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering.DefaultRenderer
    public void render(net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler, net.minecraft.world.level.BlockAndTintGetter world, net.minecraft.core.BlockPos pos, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.world.level.material.FluidState fluidState) {
        render(handler, blockState, fluidState, pos, (net.vulkanmod.render.vertex.TerrainBufferBuilder) vertexConsumer);
    }

    /* JADX WARN: Removed duplicated region for block: B:80:0x0690  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x078c A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void render(net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler r10, net.minecraft.world.level.block.state.BlockState r11, net.minecraft.world.level.material.FluidState r12, net.minecraft.core.BlockPos r13, net.vulkanmod.render.vertex.TerrainBufferBuilder r14) {
        /*
            Method dump skipped, instruction units count: 1939
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: net.vulkanmod.render.chunk.build.renderer.FluidRenderer.render(net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.material.FluidState, net.minecraft.core.BlockPos, net.vulkanmod.render.vertex.TerrainBufferBuilder):void");
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.chunk.build.renderer.FluidRenderer$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/renderer/FluidRenderer$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction = new int[net.minecraft.core.Direction.values().length];

        static {
            try {
                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.NORTH.ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.SOUTH.ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.WEST.ordinal()] = 3;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$net$minecraft$core$Direction[net.minecraft.core.Direction.EAST.ordinal()] = 4;
            } catch (java.lang.NoSuchFieldError e4) {
            }
        }
    }

    private float calculateAverageHeight(net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, net.minecraft.world.level.material.Fluid fluid, float f, float g, float h, net.minecraft.core.BlockPos blockPos) {
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

    private float getHeight(net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, net.minecraft.world.level.material.Fluid fluid, net.minecraft.core.BlockPos blockPos) {
        net.minecraft.world.level.block.state.BlockState blockState = blockAndTintGetter.getBlockState(blockPos);
        return getHeight(blockAndTintGetter, fluid, blockPos, blockState);
    }

    private float getHeight(net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, net.minecraft.world.level.material.Fluid fluid, net.minecraft.core.BlockPos blockPos, net.minecraft.world.level.block.state.BlockState adjBlockState) {
        net.minecraft.world.level.material.FluidState adjFluidState = adjBlockState.getFluidState();
        if (!fluid.isSame(adjFluidState.getType())) {
            return !adjBlockState.isSolid() ? 0.0f : -1.0f;
        }
        net.minecraft.world.level.block.state.BlockState blockState2 = blockAndTintGetter.getBlockState(blockPos.offset(net.minecraft.core.Direction.UP.getUnitVec3i()));
        if (fluid.isSame(blockState2.getFluidState().getType())) {
            return 1.0f;
        }
        return adjFluidState.getOwnHeight();
    }

    private int calculateNormal(net.vulkanmod.render.model.quad.ModelQuad quad) {
        org.joml.Vector3f normal = new org.joml.Vector3f(quad.getX(1), quad.getY(1), quad.getZ(1)).cross(quad.getX(3), quad.getY(3), quad.getZ(3));
        normal.normalize();
        return net.vulkanmod.render.vertex.format.I32_SNorm.packNormal(normal.x(), normal.y(), normal.z());
    }

    private void putQuad(net.vulkanmod.render.model.quad.ModelQuad quad, net.vulkanmod.render.vertex.TerrainBufferBuilder bufferBuilder, float xOffset, float yOffset, float zOffset, boolean flip) {
        net.vulkanmod.render.chunk.build.light.data.QuadLightData quadLightData = this.resources.quadLightData;
        int k = net.vulkanmod.render.model.quad.QuadUtils.getIterationStartIdx(quadLightData.br);
        bufferBuilder.ensureCapacity();
        for (int j = 0; j < 4; j++) {
            int i = k;
            float x = xOffset + quad.getX(i);
            float y = yOffset + quad.getY(i);
            float z = zOffset + quad.getZ(i);
            bufferBuilder.vertex(x, y, z, this.quadColors[i], quad.getU(i), quad.getV(i), quadLightData.lm[i], 0);
            k = (k + (flip ? -1 : 1)) & 3;
        }
    }

    private void setVertex(net.vulkanmod.render.model.quad.ModelQuad quad, int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setU(i, u);
        quad.setV(i, v);
    }

    private void updateQuad(net.vulkanmod.render.model.quad.ModelQuad quad, net.minecraft.core.BlockPos blockPos, net.vulkanmod.render.chunk.build.light.LightPipeline lightPipeline, net.minecraft.core.Direction dir) {
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
