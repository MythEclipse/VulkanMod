package net.vulkanmod.render.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/WorldRenderer.class */
public class WorldRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldRenderer.class);
    private static int debugLogCounter = 0;
    private static net.vulkanmod.render.chunk.WorldRenderer INSTANCE;
    private net.minecraft.client.multiplayer.ClientLevel level;
    private int renderDistance;
    private final net.minecraft.client.renderer.RenderBuffers renderBuffers;
    private final net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher;
    private final net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final net.minecraft.client.renderer.state.LevelRenderState levelRenderState;
    private final net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderDispatcher;
    private float partialTick;
    private net.minecraft.world.phys.Vec3 cameraPos;
    private int lastCameraSectionX;
    private int lastCameraSectionY;
    private int lastCameraSectionZ;
    private float lastCameraX;
    private float lastCameraY;
    private float lastCameraZ;
    private float lastCamRotX;
    private float lastCamRotY;
    private net.vulkanmod.render.chunk.SectionGrid sectionGrid;
    private net.vulkanmod.render.chunk.graph.SectionGraph sectionGraph;
    private boolean graphNeedsUpdate;
    private double xTransparentOld;
    private double yTransparentOld;
    private double zTransparentOld;
    net.vulkanmod.vulkan.memory.buffer.IndirectBuffer[] indirectBuffers;
    private long terrainSampler;
    private final java.util.Set<net.minecraft.world.level.block.entity.BlockEntity> globalBlockEntities = com.google.common.collect.Sets.newHashSet();
    private final java.util.List<java.lang.Runnable> onAllChangedCallbacks = new it.unimi.dsi.fastutil.objects.ObjectArrayList();
    private final net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
    public net.vulkanmod.render.chunk.build.RenderRegionBuilder renderRegionCache = new net.vulkanmod.render.chunk.build.RenderRegionBuilder();
    private final net.vulkanmod.render.chunk.build.task.TaskDispatcher taskDispatcher = new net.vulkanmod.render.chunk.build.task.TaskDispatcher();

    public static net.vulkanmod.render.chunk.WorldRenderer init(net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher, net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher blockEntityRenderDispatcher, net.minecraft.client.renderer.RenderBuffers renderBuffers, net.minecraft.client.renderer.state.LevelRenderState levelRenderState, net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderDispatcher) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        net.vulkanmod.render.chunk.WorldRenderer worldRenderer = new net.vulkanmod.render.chunk.WorldRenderer(entityRenderDispatcher, blockEntityRenderDispatcher, renderBuffers, levelRenderState, featureRenderDispatcher);
        INSTANCE = worldRenderer;
        return worldRenderer;
    }

    private WorldRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher, net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher blockEntityRenderDispatcher, net.minecraft.client.renderer.RenderBuffers renderBuffers, net.minecraft.client.renderer.state.LevelRenderState levelRenderState, net.minecraft.client.renderer.feature.FeatureRenderDispatcher featureRenderDispatcher) {
        this.renderBuffers = renderBuffers;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.levelRenderState = levelRenderState;
        this.featureRenderDispatcher = featureRenderDispatcher;
        net.vulkanmod.render.chunk.build.task.ChunkTask.setTaskDispatcher(this.taskDispatcher);
        allocateIndirectBuffers();
        net.vulkanmod.render.vertex.TerrainRenderType.updateMapping();
        net.vulkanmod.vulkan.Renderer.getInstance().addOnResizeCallback(() -> {
            if (this.indirectBuffers.length != net.vulkanmod.vulkan.Renderer.getFramesNum()) {
                allocateIndirectBuffers();
            }
        });
    }

    private void allocateIndirectBuffers() {
        if (this.indirectBuffers != null) {
            java.util.Arrays.stream(this.indirectBuffers).forEach((v0) -> {
                v0.scheduleFree();
            });
        }
        this.indirectBuffers = new net.vulkanmod.vulkan.memory.buffer.IndirectBuffer[net.vulkanmod.vulkan.Renderer.getFramesNum()];
        for (int i = 0; i < this.indirectBuffers.length; i++) {
            this.indirectBuffers[i] = new net.vulkanmod.vulkan.memory.buffer.IndirectBuffer(1000000, net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM);
        }
    }

    private void benchCallback() {
        net.vulkanmod.render.profiling.BuildTimeProfiler.runBench(this.graphNeedsUpdate || !this.taskDispatcher.isIdle());
    }

    public void setupRenderer(net.minecraft.client.Camera camera, net.minecraft.client.renderer.culling.Frustum frustum, boolean isCapturedFrustum, boolean spectator) {
        net.vulkanmod.render.profiling.Profiler profiler = net.vulkanmod.render.profiling.Profiler.getMainProfiler();
        profiler.push("Setup_Renderer");
        net.minecraft.util.profiling.ProfilerFiller mcProfiler = net.minecraft.util.profiling.Profiler.get();
        benchCallback();
        this.cameraPos = camera.position();
        if (this.minecraft.options.getEffectiveRenderDistance() != this.renderDistance) {
            allChanged();
        }
        mcProfiler.push("camera");
        float cameraX = (float) this.cameraPos.x();
        float cameraY = (float) this.cameraPos.y();
        float cameraZ = (float) this.cameraPos.z();
        int sectionX = net.minecraft.core.SectionPos.posToSectionCoord(cameraX);
        int sectionY = net.minecraft.core.SectionPos.posToSectionCoord(cameraY);
        int sectionZ = net.minecraft.core.SectionPos.posToSectionCoord(cameraZ);
        profiler.push("reposition");
        if (this.lastCameraSectionX != sectionX || this.lastCameraSectionY != sectionY || this.lastCameraSectionZ != sectionZ) {
            this.lastCameraSectionX = sectionX;
            this.lastCameraSectionY = sectionY;
            this.lastCameraSectionZ = sectionZ;
            this.sectionGrid.repositionCamera(cameraX, cameraZ);
        }
        profiler.pop();
        double entityDistanceScaling = ((java.lang.Double) this.minecraft.options.entityDistanceScaling().get()).doubleValue();
        net.minecraft.world.entity.Entity.setViewScale(net.minecraft.util.Mth.clamp(((double) this.renderDistance) / 8.0d, 1.0d, 2.5d) * entityDistanceScaling);
        mcProfiler.popPush("cull");
        mcProfiler.popPush("update");
        float d_xRot = java.lang.Math.abs(camera.xRot() - this.lastCamRotX);
        float d_yRot = java.lang.Math.abs(camera.yRot() - this.lastCamRotY);
        boolean cameraMoved = false | (d_xRot > 2.0f || d_yRot > 2.0f);
        this.graphNeedsUpdate |= cameraMoved | ((cameraX == this.lastCameraX && cameraY == this.lastCameraY && cameraZ == this.lastCameraZ) ? false : true);
        if (!isCapturedFrustum && graphNeedsUpdate()) {
            this.graphNeedsUpdate = false;
            this.lastCameraX = cameraX;
            this.lastCameraY = cameraY;
            this.lastCameraZ = cameraZ;
            this.lastCamRotX = camera.xRot();
            this.lastCamRotY = camera.yRot();
            this.sectionGraph.update(camera, frustum, spectator);
        }
        this.indirectBuffers[net.vulkanmod.vulkan.Renderer.getCurrentFrame()].reset();
        mcProfiler.pop();
        profiler.pop();

        // Debug logging every 200 frames
        if ((debugLogCounter++ % 200) == 0) {
            String stats = this.sectionGraph != null ? this.sectionGraph.getStatistics() : "null";
            LOGGER.info("[VulkanMod] setupRenderer stats: {}", stats);
        }
    }

    public void uploadSections() {
        net.minecraft.util.profiling.ProfilerFiller mcProfiler = net.minecraft.util.profiling.Profiler.get();
        mcProfiler.push("upload");
        net.vulkanmod.render.profiling.Profiler profiler = net.vulkanmod.render.profiling.Profiler.getMainProfiler();
        profiler.push("Uploads");
        try {
            if (this.taskDispatcher.updateSections()) {
                this.graphNeedsUpdate = true;
            }
        } catch (java.lang.Exception e) {
            net.vulkanmod.Initializer.LOGGER.error("Section upload error: {}", e.getMessage(), e);
            allChanged();
        }
        profiler.pop();
        mcProfiler.pop();
    }

    public boolean isSectionCompiled(net.minecraft.core.BlockPos blockPos) {
        net.vulkanmod.render.chunk.RenderSection renderSection = this.sectionGrid.getSectionAtBlockPos(blockPos);
        return renderSection != null && renderSection.isCompiled();
    }

    public void allChanged() {
        if (this.level != null) {
            this.level.clearTintCaches();
            this.renderRegionCache.clear();
            this.taskDispatcher.createThreads(net.vulkanmod.Initializer.CONFIG.builderThreads);
            this.graphNeedsUpdate = true;
            this.renderDistance = this.minecraft.options.getEffectiveRenderDistance();
            if (this.sectionGrid != null) {
                this.sectionGrid.freeAllBuffers();
            }
            this.taskDispatcher.clearBatchQueue();
            synchronized (this.globalBlockEntities) {
                this.globalBlockEntities.clear();
            }
            this.sectionGrid = new net.vulkanmod.render.chunk.SectionGrid(this.level, this.renderDistance);
            this.sectionGraph = new net.vulkanmod.render.chunk.graph.SectionGraph(this.level, this.sectionGrid, this.taskDispatcher);
            this.onAllChangedCallbacks.forEach((v0) -> {
                v0.run();
            });
            net.minecraft.world.entity.Entity entity = this.minecraft.getCameraEntity();
            if (entity != null) {
                this.sectionGrid.repositionCamera(entity.getX(), entity.getZ());
            }
        }
    }

    public void setLevel(@org.jetbrains.annotations.Nullable net.minecraft.client.multiplayer.ClientLevel level) {
        this.lastCameraX = Float.MIN_VALUE;
        this.lastCameraY = Float.MIN_VALUE;
        this.lastCameraZ = Float.MIN_VALUE;
        this.lastCameraSectionX = Integer.MIN_VALUE;
        this.lastCameraSectionY = Integer.MIN_VALUE;
        this.lastCameraSectionZ = Integer.MIN_VALUE;
        this.level = level;
        net.vulkanmod.render.chunk.ChunkStatusMap.createInstance(this.renderDistance);
        if (level != null) {
            allChanged();
            return;
        }
        if (this.sectionGrid != null) {
            this.sectionGrid.freeAllBuffers();
            this.sectionGrid = null;
        }
        this.taskDispatcher.stopThreads();
        this.graphNeedsUpdate = true;
    }

    public void addOnAllChangedCallback(java.lang.Runnable runnable) {
        this.onAllChangedCallbacks.add(runnable);
    }

    public void clearOnAllChangedCallbacks() {
        this.onAllChangedCallbacks.clear();
    }

    public void renderSectionLayer(net.vulkanmod.render.vertex.TerrainRenderType renderType, double camX, double camY, double camZ, org.joml.Matrix4f modelView, org.joml.Matrix4f projection) {
        if ((debugLogCounter % 200) < 4) {
            int sectionCount = this.sectionGraph != null ? this.sectionGraph.getSectionQueue().size() : -1;
            LOGGER.info("[VulkanMod] renderSectionLayer called: type={}, visibleSections={}, cam=({},{},{})",
                    renderType, sectionCount, (int)camX, (int)camY, (int)camZ);
        }
        net.vulkanmod.vulkan.Renderer.getInstance().getMainPass().rebindMainTarget();
        sortTranslucentSections(camX, camY, camZ);
        net.minecraft.util.profiling.ProfilerFiller mcProfiler = net.minecraft.util.profiling.Profiler.get();
        net.minecraft.util.profiling.Zone zone = mcProfiler.zone(() -> {
            return "render_" + java.lang.String.valueOf(renderType);
        });
        boolean isTranslucent = renderType == net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
        boolean indirectDraw = net.vulkanmod.Initializer.CONFIG.indirectDraw;
        if (!isTranslucent) {
            com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
        } else {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
            net.vulkanmod.vulkan.VRenderSystem.blendFuncSeparate(770, 771, 1, 771);
        }
        net.vulkanmod.vulkan.VRenderSystem.enableCull();
        net.vulkanmod.vulkan.VRenderSystem.depthFunc(515);
        com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
        com.mojang.blaze3d.opengl.GlStateManager._depthMask(true);
        com.mojang.blaze3d.opengl.GlStateManager._colorMask(true, true, true, true);
        com.mojang.blaze3d.opengl.GlStateManager._disablePolygonOffset();
        net.vulkanmod.vulkan.VRenderSystem.setPolygonModeGL(6914);
        net.vulkanmod.vulkan.VRenderSystem.applyMVP(modelView, projection);
        net.vulkanmod.vulkan.VRenderSystem.setPrimitiveTopologyGL(4);
        net.vulkanmod.vulkan.Renderer renderer = net.vulkanmod.vulkan.Renderer.getInstance();
        net.vulkanmod.vulkan.shader.GraphicsPipeline pipeline = net.vulkanmod.render.PipelineManager.getTerrainShader(renderType);
        renderer.bindGraphicsPipeline(pipeline);
        net.minecraft.client.renderer.texture.TextureManager textureManager = net.minecraft.client.Minecraft.getInstance().getTextureManager();
        net.minecraft.client.renderer.texture.AbstractTexture atlasTexture = textureManager.getTexture(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
        com.mojang.blaze3d.textures.GpuTextureView texView = atlasTexture.getTextureView();
        boolean useAnisotropy = this.minecraft.options.textureFiltering().get() == net.minecraft.client.TextureFilteringMethod.ANISOTROPIC;
        int maxAnisotropy = this.minecraft.options.maxAnisotropyValue();
        net.vulkanmod.render.engine.VkGpuTexture texture = (net.vulkanmod.render.engine.VkGpuTexture) texView.texture();
        if (this.terrainSampler == 0) {
            this.terrainSampler = net.vulkanmod.vulkan.texture.SamplerManager.getSampler(true, true, texture.getVulkanImage().mipLevels - 1, useAnisotropy, maxAnisotropy);
        }
        texture.getVulkanImage().setSampler(this.terrainSampler);
        net.vulkanmod.vulkan.VRenderSystem.setShaderTexture(0, texView);
        net.vulkanmod.vulkan.VRenderSystem.setShaderTexture(2, net.minecraft.client.Minecraft.getInstance().gameRenderer.lightTexture().getTextureView());
        net.vulkanmod.vulkan.texture.VTextureSelector.bindShaderTextures(pipeline);
        int atlasTexWidth = texView.getWidth(0);
        int atlasTexHeight = texView.getHeight(0);
        net.vulkanmod.vulkan.VRenderSystem.setTextureSize(atlasTexWidth, atlasTexHeight);
        net.vulkanmod.vulkan.VRenderSystem.setCurrentTime((int) java.lang.System.currentTimeMillis());
        long currentTimeMs = java.lang.System.currentTimeMillis();
        float fadeTime = ((java.lang.Double) net.minecraft.client.Minecraft.getInstance().options.chunkSectionFadeInTime().get()).floatValue();
        int fadeTimeMs = (int) (fadeTime * 1000.0f);
        float fadeTimeInv = fadeTime > 0.0f ? 1.0f / (fadeTime * 1000.0f) : 1.0f;
        net.vulkanmod.vulkan.memory.buffer.IndexBuffer indexBuffer = net.vulkanmod.vulkan.Renderer.getDrawer().getQuadsIndexBuffer().getIndexBuffer();
        net.vulkanmod.vulkan.Renderer.getDrawer().bindIndexBuffer(net.vulkanmod.vulkan.Renderer.getCommandBuffer(), indexBuffer, indexBuffer.indexType.value);
        int currentFrame = net.vulkanmod.vulkan.Renderer.getCurrentFrame();
        java.util.Set<net.vulkanmod.render.vertex.TerrainRenderType> allowedRenderTypes = net.vulkanmod.Initializer.CONFIG.uniqueOpaqueLayer ? net.vulkanmod.render.vertex.TerrainRenderType.COMPACT_RENDER_TYPES : net.vulkanmod.render.vertex.TerrainRenderType.SEMI_COMPACT_RENDER_TYPES;
        if (allowedRenderTypes.contains(renderType)) {
            renderType.setCutoutUniform();
            java.util.Iterator<net.vulkanmod.render.chunk.ChunkArea> iterator = this.sectionGraph.getChunkAreaQueue().iterator(isTranslucent);
            while (iterator.hasNext()) {
                net.vulkanmod.render.chunk.ChunkArea chunkArea = iterator.next();
                net.vulkanmod.render.chunk.util.StaticQueue<net.vulkanmod.render.chunk.RenderSection> queue = chunkArea.sectionQueue;
                net.vulkanmod.render.chunk.buffer.DrawBuffers drawBuffers = chunkArea.drawBuffers;
                if (drawBuffers.getAreaBuffer(renderType) != null && queue.size() > 0) {
                    drawBuffers.bindBuffers(net.vulkanmod.vulkan.Renderer.getCommandBuffer(), pipeline, renderType, camX, camY, camZ, currentTimeMs, fadeTimeMs, fadeTimeInv);
                    renderer.uploadAndBindUBOs(pipeline);
                    if (indirectDraw) {
                        drawBuffers.buildDrawBatchesIndirect(this.cameraPos, this.indirectBuffers[currentFrame], queue, renderType);
                    } else {
                        drawBuffers.buildDrawBatchesDirect(this.cameraPos, queue, renderType);
                    }
                }
            }
        }
        if (renderType == net.vulkanmod.render.vertex.TerrainRenderType.CUTOUT || renderType == net.vulkanmod.render.vertex.TerrainRenderType.TRIPWIRE) {
            this.indirectBuffers[currentFrame].submitUploads();
        }
        if (!indirectDraw) {
            net.vulkanmod.vulkan.VRenderSystem.setModelOffset(0.0f, 0.0f, 0.0f);
            renderer.pushConstants(pipeline);
        }
        zone.close();
    }

    private void sortTranslucentSections(double camX, double camY, double camZ) {
        net.minecraft.util.profiling.ProfilerFiller mcProfiler = net.minecraft.util.profiling.Profiler.get();
        mcProfiler.push("translucent_sort");
        double d0 = camX - this.xTransparentOld;
        double d1 = camY - this.yTransparentOld;
        double d2 = camZ - this.zTransparentOld;
        if ((d0 * d0) + (d1 * d1) + (d2 * d2) > 2.0d) {
            this.xTransparentOld = camX;
            this.yTransparentOld = camY;
            this.zTransparentOld = camZ;
            java.util.Iterator<net.vulkanmod.render.chunk.RenderSection> iterator = this.sectionGraph.getSectionQueue().iterator(false);
            for (int j = 0; iterator.hasNext() && j < 200; j++) {
                net.vulkanmod.render.chunk.RenderSection section = iterator.next();
                section.resortTransparency(this.taskDispatcher);
            }
        }
        mcProfiler.pop();
    }

    public void renderBlockEntities(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.state.LevelRenderState levelRenderState, net.minecraft.client.renderer.SubmitNodeStorage submitNodeStorage, it.unimi.dsi.fastutil.longs.Long2ObjectMap<java.util.SortedSet<net.minecraft.server.level.BlockDestructionProgress>> destructionProgress) {
        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay;
        net.vulkanmod.render.profiling.Profiler profiler = net.vulkanmod.render.profiling.Profiler.getMainProfiler();
        profiler.pop();
        profiler.push("Block-entities");
        net.minecraft.world.phys.Vec3 vec3 = levelRenderState.cameraRenderState.pos;
        double camX = vec3.x();
        double camY = vec3.y();
        double camZ = vec3.z();
        for (net.vulkanmod.render.chunk.RenderSection renderSection : this.sectionGraph.getBlockEntitiesSections()) {
            java.util.List<net.minecraft.world.level.block.entity.BlockEntity> list = renderSection.getCompiledSection().getBlockEntities();
            if (!list.isEmpty()) {
                for (net.minecraft.world.level.block.entity.BlockEntity blockEntity : list) {
                    net.minecraft.core.BlockPos blockPos = blockEntity.getBlockPos();
                    java.util.SortedSet<net.minecraft.server.level.BlockDestructionProgress> sortedSet = (java.util.SortedSet) destructionProgress.get(blockPos.asLong());
                    if (sortedSet != null && !sortedSet.isEmpty()) {
                        poseStack.pushPose();
                        poseStack.translate(((double) blockPos.getX()) - camX, ((double) blockPos.getY()) - camY, ((double) blockPos.getZ()) - camZ);
                        crumblingOverlay = new net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay(sortedSet.last().getProgress(), poseStack.last());
                        poseStack.popPose();
                    } else {
                        crumblingOverlay = null;
                    }
                    net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState blockEntityRenderState = this.blockEntityRenderDispatcher.tryExtractRenderState(blockEntity, this.partialTick, crumblingOverlay);
                    if (blockEntityRenderState != null) {
                        levelRenderState.blockEntityRenderStates.add(blockEntityRenderState);
                    }
                }
            }
        }
        java.util.Iterator<net.minecraft.world.level.block.entity.BlockEntity> iterator = this.level.getGloballyRenderedBlockEntities().iterator();
        while (iterator.hasNext()) {
            net.minecraft.world.level.block.entity.BlockEntity blockEntity2 = iterator.next();
            if (blockEntity2.isRemoved()) {
                iterator.remove();
            } else {
                net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState blockEntityRenderState2 = this.blockEntityRenderDispatcher.tryExtractRenderState(blockEntity2, this.partialTick, (net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay) null);
                if (blockEntityRenderState2 != null) {
                    levelRenderState.blockEntityRenderStates.add(blockEntityRenderState2);
                }
            }
        }
        for (net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState blockEntityRenderState3 : levelRenderState.blockEntityRenderStates) {
            net.minecraft.core.BlockPos blockPos2 = blockEntityRenderState3.blockPos;
            poseStack.pushPose();
            poseStack.translate(((double) blockPos2.getX()) - camX, ((double) blockPos2.getY()) - camY, ((double) blockPos2.getZ()) - camZ);
            net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher blockEntityRenderDispatcher = this.minecraft.getBlockEntityRenderDispatcher();
            blockEntityRenderDispatcher.submit(blockEntityRenderState3, poseStack, submitNodeStorage, levelRenderState.cameraRenderState);
            poseStack.popPose();
        }
    }

    public void resetSampler() {
        this.terrainSampler = 0L;
    }

    public void setPartialTick(float partialTick) {
        this.partialTick = partialTick;
    }

    public void scheduleGraphUpdate() {
        this.graphNeedsUpdate = true;
    }

    public boolean graphNeedsUpdate() {
        return this.graphNeedsUpdate;
    }

    public int getVisibleSectionsCount() {
        return this.sectionGraph.getSectionQueue().size();
    }

    public void setSectionDirty(int x, int y, int z, boolean flag) {
        this.sectionGrid.setDirty(x, y, z, flag);
        this.renderRegionCache.remove(x, z);
    }

    public net.vulkanmod.render.chunk.SectionGrid getSectionGrid() {
        return this.sectionGrid;
    }

    public net.vulkanmod.render.chunk.ChunkAreaManager getChunkAreaManager() {
        if (this.sectionGrid == null) {
            return null;
        }
        return this.sectionGrid.chunkAreaManager;
    }

    public net.vulkanmod.render.chunk.build.task.TaskDispatcher getTaskDispatcher() {
        return this.taskDispatcher;
    }

    public short getLastFrame() {
        return this.sectionGraph.getLastFrame();
    }

    public int getRenderDistance() {
        return this.renderDistance;
    }

    public java.lang.String getChunkStatistics() {
        if (this.sectionGraph == null) {
            return null;
        }
        return this.sectionGraph.getStatistics();
    }

    public void cleanUp() {
        if (this.indirectBuffers != null) {
            java.util.Arrays.stream(this.indirectBuffers).forEach((v0) -> {
                v0.scheduleFree();
            });
        }
    }

    public static net.vulkanmod.render.chunk.WorldRenderer getInstance() {
        return INSTANCE;
    }

    public static net.minecraft.client.multiplayer.ClientLevel getLevel() {
        return INSTANCE.level;
    }

    public static net.minecraft.world.phys.Vec3 getCameraPos() {
        return INSTANCE.cameraPos;
    }
}
