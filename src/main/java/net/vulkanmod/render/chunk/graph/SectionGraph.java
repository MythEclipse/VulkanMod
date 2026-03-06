package net.vulkanmod.render.chunk.graph;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/graph/SectionGraph.class */
public class SectionGraph {
    private final net.minecraft.world.level.Level level;
    private final net.vulkanmod.render.chunk.SectionGrid sectionGrid;

    private final net.vulkanmod.render.chunk.build.task.TaskDispatcher taskDispatcher;
    private net.vulkanmod.render.chunk.util.AreaSetQueue chunkAreaQueue;
    private net.vulkanmod.render.chunk.frustum.VFrustum frustum;
    int nonEmptyChunks;
    private final net.vulkanmod.render.chunk.util.ResettableQueue<net.vulkanmod.render.chunk.RenderSection> sectionQueue = new net.vulkanmod.render.chunk.util.ResettableQueue<>();
    private short lastFrame = 0;
    private final net.vulkanmod.render.chunk.util.ResettableQueue<net.vulkanmod.render.chunk.RenderSection> blockEntitiesSections = new net.vulkanmod.render.chunk.util.ResettableQueue<>();
    private final net.vulkanmod.render.chunk.util.ResettableQueue<net.vulkanmod.render.chunk.RenderSection> rebuildQueue = new net.vulkanmod.render.chunk.util.ResettableQueue<>();
    net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
    public net.vulkanmod.render.chunk.build.RenderRegionBuilder renderRegionCache = net.vulkanmod.render.chunk.WorldRenderer
            .getInstance().renderRegionCache;

    public SectionGraph(
            net.minecraft.world.level.Level level,
            net.vulkanmod.render.chunk.SectionGrid sectionGrid,
            net.vulkanmod.render.chunk.build.task.TaskDispatcher taskDispatcher) {
        this.level = level;
        this.sectionGrid = sectionGrid;

        this.taskDispatcher = taskDispatcher;
        this.chunkAreaQueue = new net.vulkanmod.render.chunk.util.AreaSetQueue(
                sectionGrid.getChunkAreaManager().size);
    }

    public void update(
            net.minecraft.client.Camera camera,
            net.minecraft.client.renderer.culling.Frustum frustum,
            boolean spectator) {
        net.vulkanmod.render.profiling.Profiler profiler = net.vulkanmod.render.profiling.Profiler.getMainProfiler();
        net.minecraft.util.profiling.ProfilerFiller mcProfiler = net.minecraft.util.profiling.Profiler.get();
        net.minecraft.core.BlockPos blockpos = camera.blockPosition();
        mcProfiler.popPush("update");
        boolean flag = this.minecraft.smartCull;
        if (spectator && this.level.getBlockState(blockpos).isSolidRender()) {
            flag = false;
        }
        profiler.push("frustum");
        this.frustum = ((net.vulkanmod.interfaces.FrustumMixed) frustum)
                .customFrustum()
                .offsetToFullyIncludeCameraCube(8);
        this.sectionGrid.updateFrustumVisibility(this.frustum);
        profiler.pop();
        mcProfiler.push("partial_update");
        initUpdate();
        initializeQueueForFullUpdate(camera);
        if (flag) {
            updateRenderChunks();
        } else {
            updateRenderChunksSpectator();
        }
        scheduleRebuilds();
        mcProfiler.pop();
    }

    private void initializeQueueForFullUpdate(net.minecraft.client.Camera camera) {
        net.minecraft.world.phys.Vec3 vec3 = camera.position();
        net.minecraft.core.BlockPos blockpos = camera.blockPosition();
        net.vulkanmod.render.chunk.RenderSection renderSection = this.sectionGrid.getSectionAtBlockPos(blockpos);
        if (renderSection == null) {
            boolean flag = blockpos.getY() > this.level.getMinY();
            int y = flag ? this.level.getMaxY() - 8 : this.level.getMinY() + 8;
            int x = net.minecraft.util.Mth.floor(vec3.x / 16.0d) * 16;
            int z = net.minecraft.util.Mth.floor(vec3.z / 16.0d) * 16;
            java.util.List<net.vulkanmod.render.chunk.RenderSection> list = com.google.common.collect.Lists
                    .newArrayList();
            int renderDistance = net.vulkanmod.render.chunk.WorldRenderer.getInstance().getRenderDistance();
            for (int x1 = -renderDistance; x1 <= renderDistance; x1++) {
                for (int z1 = -renderDistance; z1 <= renderDistance; z1++) {
                    net.vulkanmod.render.chunk.RenderSection renderSection1 = this.sectionGrid.getSectionAtBlockPos(
                            new net.minecraft.core.BlockPos(
                                    x
                                            + net.minecraft.core.SectionPos
                                                    .sectionToBlockCoord(x1, 8),
                                    y,
                                    z
                                            + net.minecraft.core.SectionPos
                                                    .sectionToBlockCoord(z1, 8)));
                    if (renderSection1 != null) {
                        initFirstNode(renderSection1, this.lastFrame);
                        list.add(renderSection1);
                    }
                }
            }
            this.sectionQueue.ensureCapacity(list.size());
            for (net.vulkanmod.render.chunk.RenderSection chunkInfo : list) {
                this.sectionQueue.add(chunkInfo);
            }
            return;
        }
        initFirstNode(renderSection, this.lastFrame);
        this.sectionQueue.add(renderSection);
    }

    private static void initFirstNode(
            net.vulkanmod.render.chunk.RenderSection renderSection, short frame) {
        renderSection.mainDir = (byte) 7;
        renderSection.sourceDirs = (byte) -128;
        renderSection.directions = (byte) -1;
        renderSection.setLastFrame(frame);
        renderSection.visibility |= initVisibility();
        renderSection.directionChanges = (byte) 0;
        renderSection.steps = (byte) 0;
    }

    private static long initVisibility() {
        long vis = 0;
        for (int dir = 0; dir < 6; dir++) {
            vis = vis | (1L << (48 + dir)) | (1L << (56 + dir));
        }
        return vis;
    }

    private void initUpdate() {
        resetUpdateQueues();
        this.lastFrame = (short) (this.lastFrame + 1);
        this.nonEmptyChunks = 0;
    }

    private void resetUpdateQueues() {
        this.chunkAreaQueue.clear();
        this.sectionGrid.getChunkAreaManager().resetQueues();
        this.sectionQueue.clear();
        this.blockEntitiesSections.clear();
        this.rebuildQueue.clear();
    }

    private void updateRenderChunks() {
        int maxDirectionsChanges = net.vulkanmod.Initializer.CONFIG.advCulling - 1;
        while (this.sectionQueue.hasNext()) {
            net.vulkanmod.render.chunk.RenderSection renderSection = this.sectionQueue.poll();
            if (!notInFrustum(renderSection)
                    && renderSection.directionChanges <= maxDirectionsChanges) {
                if (!renderSection.isCompletelyEmpty()) {
                    renderSection.getChunkArea().sectionQueue.add(renderSection);
                    this.chunkAreaQueue.add(renderSection.getChunkArea());
                    this.nonEmptyChunks++;
                }
                if (renderSection.containsBlockEntities()) {
                    this.blockEntitiesSections.ensureCapacity(1);
                    this.blockEntitiesSections.add(renderSection);
                }
                if (renderSection.isDirty()) {
                    this.rebuildQueue.ensureCapacity(1);
                    this.rebuildQueue.add(renderSection);
                }
                byte dirs = (byte) (renderSection.getVisibilityDirs() & renderSection.getDirections());
                visitAdjacentNodes(renderSection, dirs);
            }
        }
    }

    private void scheduleRebuilds() {
        for (int i = 0; i < this.rebuildQueue.size(); i++) {
            net.vulkanmod.render.chunk.RenderSection section = this.rebuildQueue.get(i);
            boolean scheduled = section.rebuildChunkAsync(this.taskDispatcher, this.renderRegionCache);
            if (scheduled) {
                section.setNotDirty();
            }
            // If not scheduled (chunk not ready), keep dirty so it retries when chunk
            // becomes ready
        }
        this.rebuildQueue.clear();
    }

    private boolean notInFrustum(net.vulkanmod.render.chunk.RenderSection renderSection) {
        byte frustumRes = renderSection.getChunkArea().inFrustum(renderSection.frustumIndex);
        if (frustumRes > -1) {
            return true;
        }
        return frustumRes == -1
                && !this.frustum.testFrustum(
                        (float) renderSection.xOffset,
                        (float) renderSection.yOffset,
                        (float) renderSection.zOffset,
                        (float) (renderSection.xOffset + 16),
                        (float) (renderSection.yOffset + 16),
                        (float) (renderSection.zOffset + 16));
    }

    private void visitAdjacentNodes(
            net.vulkanmod.render.chunk.RenderSection renderSection, byte dirs) {
        byte dirs2 = (byte) (dirs & renderSection.adjDirs);
        this.sectionQueue.ensureCapacity(6);
        net.vulkanmod.render.chunk.RenderSection relativeSection = renderSection.adjDown;
        checkToAdd(renderSection, relativeSection, (byte) 0, (byte) 1, dirs2);
        net.vulkanmod.render.chunk.RenderSection relativeSection2 = renderSection.adjUp;
        checkToAdd(renderSection, relativeSection2, (byte) 1, (byte) 0, dirs2);
        net.vulkanmod.render.chunk.RenderSection relativeSection3 = renderSection.adjNorth;
        checkToAdd(renderSection, relativeSection3, (byte) 2, (byte) 3, dirs2);
        net.vulkanmod.render.chunk.RenderSection relativeSection4 = renderSection.adjSouth;
        checkToAdd(renderSection, relativeSection4, (byte) 3, (byte) 2, dirs2);
        net.vulkanmod.render.chunk.RenderSection relativeSection5 = renderSection.adjWest;
        checkToAdd(renderSection, relativeSection5, (byte) 4, (byte) 5, dirs2);
        net.vulkanmod.render.chunk.RenderSection relativeSection6 = renderSection.adjEast;
        checkToAdd(renderSection, relativeSection6, (byte) 5, (byte) 4, dirs2);
    }

    private void checkToAdd(
            net.vulkanmod.render.chunk.RenderSection renderSection,
            net.vulkanmod.render.chunk.RenderSection relativeSection,
            byte dir,
            byte opposite,
            byte dirs) {
        if ((dirs & (1 << dir)) != 0) {
            addNode(renderSection, relativeSection, dir, opposite);
        }
    }

    private void updateRenderChunksSpectator() {
        while (this.sectionQueue.hasNext()) {
            net.vulkanmod.render.chunk.RenderSection renderSection = this.sectionQueue.poll();
            if (!notInFrustum(renderSection)) {
                if (!renderSection.isCompletelyEmpty()) {
                    renderSection.getChunkArea().sectionQueue.add(renderSection);
                    this.chunkAreaQueue.add(renderSection.getChunkArea());
                    this.nonEmptyChunks++;
                }
                if (renderSection.isDirty()) {
                    this.rebuildQueue.ensureCapacity(1);
                    this.rebuildQueue.add(renderSection);
                }
                byte dirs = (byte) (renderSection.adjDirs & renderSection.getDirections());
                visitAdjacentNodes(renderSection, dirs);
            }
        }
    }

    private void addNode(
            net.vulkanmod.render.chunk.RenderSection renderSection,
            net.vulkanmod.render.chunk.RenderSection relativeSection,
            byte direction,
            byte opposite) {
        if (relativeSection.getLastFrame() != this.lastFrame) {
            relativeSection.setLastFrame(this.lastFrame);
            relativeSection.mainDir = direction;
            relativeSection.sourceDirs = (byte) (1 << direction);
            byte steps = (byte) (renderSection.steps + 1);
            relativeSection.directionChanges = (byte) (steps < 10 ? 0 : org.lwjgl.vulkan.VK10.VK_FORMAT_S8_UINT);
            relativeSection.steps = steps;
            relativeSection.directions = (byte) (renderSection.directions & ((1 << opposite) ^ (-1)));
            this.sectionQueue.add(relativeSection);
        }
        relativeSection.addDir(direction);
        boolean increase = (renderSection.sourceDirs & (1 << direction)) == 0
                && !renderSection.isCompletelyEmpty();
        byte dc = increase
                ? (byte) (renderSection.directionChanges + 1)
                : renderSection.directionChanges;
        relativeSection.directionChanges = dc < relativeSection.directionChanges ? dc
                : relativeSection.directionChanges;
    }

    public net.vulkanmod.render.chunk.util.AreaSetQueue getChunkAreaQueue() {
        return this.chunkAreaQueue;
    }

    public net.vulkanmod.render.chunk.util.ResettableQueue<net.vulkanmod.render.chunk.RenderSection> getSectionQueue() {
        return this.sectionQueue;
    }

    public net.vulkanmod.render.chunk.util.ResettableQueue<net.vulkanmod.render.chunk.RenderSection> getBlockEntitiesSections() {
        return this.blockEntitiesSections;
    }

    public short getLastFrame() {
        return this.lastFrame;
    }

    public java.lang.String getStatistics() {
        int totalSections = this.sectionGrid.getSectionCount();
        int sections = this.sectionQueue.size();
        int renderDistance = net.vulkanmod.render.chunk.WorldRenderer.getInstance().getRenderDistance();
        java.lang.String tasksInfo = this.taskDispatcher == null ? "null" : this.taskDispatcher.getStats();
        return java.lang.String.format(
                "Chunks: %d(%d)/%d D: %d, %s",
                java.lang.Integer.valueOf(this.nonEmptyChunks),
                java.lang.Integer.valueOf(sections),
                java.lang.Integer.valueOf(totalSections),
                java.lang.Integer.valueOf(renderDistance),
                tasksInfo);
    }
}
