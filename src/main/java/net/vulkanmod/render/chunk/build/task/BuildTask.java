package net.vulkanmod.render.chunk.build.task;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/task/BuildTask.class */
public class BuildTask extends net.vulkanmod.render.chunk.build.task.ChunkTask {

    @org.jetbrains.annotations.Nullable
    protected net.vulkanmod.render.chunk.build.RenderRegion region;

    public BuildTask(net.vulkanmod.render.chunk.RenderSection renderSection, net.vulkanmod.render.chunk.build.RenderRegion renderRegion, boolean highPriority) {
        super(renderSection);
        this.region = renderRegion;
        this.highPriority = highPriority;
    }

    @Override // net.vulkanmod.render.chunk.build.task.ChunkTask
    public java.lang.String name() {
        return "rend_chk_rebuild";
    }

    @Override // net.vulkanmod.render.chunk.build.task.ChunkTask
    public net.vulkanmod.render.chunk.build.task.ChunkTask.Result runTask(net.vulkanmod.render.chunk.build.thread.BuilderResources builderResources) {
        long startTime = java.lang.System.nanoTime();
        if (this.cancelled.get()) {
            return net.vulkanmod.render.chunk.build.task.ChunkTask.Result.CANCELLED;
        }
        net.minecraft.world.phys.Vec3 vec3 = net.vulkanmod.render.chunk.WorldRenderer.getCameraPos();
        float x = (float) vec3.x;
        float y = (float) vec3.y;
        float z = (float) vec3.z;
        net.vulkanmod.render.chunk.build.task.CompileResult compileResult = compile(x, y, z, builderResources);
        net.vulkanmod.render.chunk.build.task.CompiledSection compiledSection = new net.vulkanmod.render.chunk.build.task.CompiledSection();
        compiledSection.blockEntities.addAll(compileResult.blockEntities);
        compiledSection.transparencyState = compileResult.transparencyState;
        compiledSection.isCompletelyEmpty = compileResult.renderedLayers.isEmpty();
        compileResult.compiledSection = compiledSection;
        if (this.cancelled.get()) {
            compileResult.renderedLayers.values().forEach((v0) -> {
                v0.release();
            });
            return net.vulkanmod.render.chunk.build.task.ChunkTask.Result.CANCELLED;
        }
        taskDispatcher.scheduleSectionUpdate(compileResult);
        float buildTime = (java.lang.System.nanoTime() - startTime) * 1.0E-6f;
        builderResources.updateBuildStats((int) buildTime);
        return net.vulkanmod.render.chunk.build.task.ChunkTask.Result.SUCCESSFUL;
    }

    private net.vulkanmod.render.chunk.build.task.CompileResult compile(float camX, float camY, float camZ, net.vulkanmod.render.chunk.build.thread.BuilderResources builderResources) {
        net.minecraft.world.level.block.entity.BlockEntity blockEntity;
        net.vulkanmod.render.chunk.build.task.CompileResult compileResult = new net.vulkanmod.render.chunk.build.task.CompileResult(this.section, true);
        net.minecraft.core.BlockPos startBlockPos = new net.minecraft.core.BlockPos(this.section.xOffset(), this.section.yOffset(), this.section.zOffset()).immutable();
        net.minecraft.client.renderer.chunk.VisGraph visGraph = new net.minecraft.client.renderer.chunk.VisGraph();
        if (this.region == null) {
            compileResult.visibilitySet = visGraph.resolve();
            return compileResult;
        }
        org.joml.Vector3f pos = new org.joml.Vector3f();
        net.vulkanmod.render.chunk.build.thread.ThreadBuilderPack bufferBuilders = builderResources.builderPack;
        setupBufferBuilders(bufferBuilders);
        this.region.loadBlockStates();
        this.region.initTintCache(builderResources.tintCache);
        builderResources.update(this.region, this.section);
        net.vulkanmod.render.chunk.build.renderer.BlockRenderer blockRenderer = builderResources.blockRenderer;
        net.vulkanmod.render.chunk.build.renderer.FluidRenderer fluidRenderer = builderResources.fluidRenderer;
        net.minecraft.core.BlockPos.MutableBlockPos class_2339Var = new net.minecraft.core.BlockPos.MutableBlockPos();
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    class_2339Var.set(this.section.xOffset() + x, this.section.yOffset() + y, this.section.zOffset() + z);
                    net.minecraft.world.level.block.state.BlockState blockState = this.region.getBlockState(class_2339Var);
                    if (blockState.isSolidRender()) {
                        visGraph.setOpaque(class_2339Var);
                    }
                    if (blockState.hasBlockEntity() && (blockEntity = this.region.getBlockEntity(class_2339Var)) != null) {
                        handleBlockEntity(compileResult, blockEntity);
                    }
                    net.minecraft.world.level.material.FluidState fluidState = blockState.getFluidState();
                    if (!fluidState.isEmpty()) {
                        fluidRenderer.renderLiquid(blockState, fluidState, class_2339Var);
                    }
                    if (blockState.getRenderShape() == net.minecraft.world.level.block.RenderShape.MODEL) {
                        pos.set(class_2339Var.getX() & 15, class_2339Var.getY() & 15, class_2339Var.getZ() & 15);
                        blockRenderer.renderBlock(blockState, class_2339Var, pos);
                    }
                }
            }
        }
        net.vulkanmod.render.vertex.TerrainBuilder trasnlucentTerrainBuilder = bufferBuilders.builder(net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT);
        if (trasnlucentTerrainBuilder.getBufferBuilder(net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal()).getVertices() > 0) {
            trasnlucentTerrainBuilder.setupQuadSortingPoints();
            trasnlucentTerrainBuilder.setupQuadSorting(camX - startBlockPos.getX(), camY - startBlockPos.getY(), camZ - startBlockPos.getZ());
            compileResult.transparencyState = trasnlucentTerrainBuilder.getSortState();
        }
        for (net.vulkanmod.render.vertex.TerrainRenderType renderType : net.vulkanmod.render.vertex.TerrainRenderType.VALUES) {
            net.vulkanmod.render.vertex.TerrainBuilder builder = bufferBuilders.builder(renderType);
            net.vulkanmod.render.vertex.TerrainBuilder.DrawState drawState = builder.endDrawing();
            net.vulkanmod.render.chunk.build.UploadBuffer uploadBuffer = new net.vulkanmod.render.chunk.build.UploadBuffer(builder, drawState);
            compileResult.renderedLayers.put(renderType, uploadBuffer);
            builder.clear();
        }
        compileResult.visibilitySet = visGraph.resolve();
        this.region = null;
        return compileResult;
    }

    private void setupBufferBuilders(net.vulkanmod.render.chunk.build.thread.ThreadBuilderPack builderPack) {
        for (net.vulkanmod.render.vertex.TerrainRenderType renderType : net.vulkanmod.render.vertex.TerrainRenderType.VALUES) {
            net.vulkanmod.render.vertex.TerrainBuilder bufferBuilder = builderPack.builder(renderType);
            bufferBuilder.begin();
        }
    }

    private net.vulkanmod.render.vertex.TerrainBuilder getTerrainBuilder(net.vulkanmod.render.chunk.build.thread.ThreadBuilderPack bufferBuilders, net.vulkanmod.render.vertex.TerrainRenderType renderType) {
        return bufferBuilders.builder(compactRenderTypes(renderType));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private net.vulkanmod.render.vertex.TerrainRenderType compactRenderTypes(net.vulkanmod.render.vertex.TerrainRenderType renderType) throws java.lang.MatchException {
        net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType;
        net.vulkanmod.render.vertex.TerrainRenderType renderType2;
        net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType2;
        if (net.vulkanmod.Initializer.CONFIG.uniqueOpaqueLayer) {
            switch (renderType) {
                case SOLID:
                    terrainRenderType2 = net.vulkanmod.render.vertex.TerrainRenderType.SOLID;
                    break;
                case CUTOUT:
                    terrainRenderType2 = net.vulkanmod.render.vertex.TerrainRenderType.CUTOUT;
                    break;
                case TRANSLUCENT:
                    terrainRenderType2 = net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
                    break;
                case TRIPWIRE:
                    terrainRenderType2 = net.vulkanmod.render.vertex.TerrainRenderType.TRIPWIRE;
                    break;
                default:
                    throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
            }
            renderType2 = terrainRenderType2;
        } else {
            switch (renderType) {
                case SOLID:
                    terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.SOLID;
                    break;
                case CUTOUT:
                    terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.CUTOUT;
                    break;
                case TRANSLUCENT:
                case TRIPWIRE:
                    terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
                    break;
                default:
                    throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
            }
            renderType2 = terrainRenderType;
        }
        return renderType2;
    }

    private <E extends net.minecraft.world.level.block.entity.BlockEntity> void handleBlockEntity(net.vulkanmod.render.chunk.build.task.CompileResult compileResult, E blockEntity) {
        net.minecraft.client.renderer.blockentity.BlockEntityRenderer<E, net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState> blockEntityRenderer = net.minecraft.client.Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (blockEntityRenderer != null) {
            compileResult.blockEntities.add(blockEntity);
            if (blockEntityRenderer.shouldRenderOffScreen()) {
                compileResult.globalBlockEntities.add(blockEntity);
            }
        }
    }
}
