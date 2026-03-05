package net.vulkanmod.render.chunk.buffer;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/buffer/DrawBuffers.class */
public class DrawBuffers {
    public static final int INDEX_SIZE = 2;
    private static final int CMD_STRIDE = 32;
    private final int index;
    private final org.joml.Vector3i origin;
    private final int minHeight;
    net.vulkanmod.render.chunk.buffer.AreaBuffer indexBuffer;
    public static final int VERTEX_SIZE = net.vulkanmod.render.PipelineManager.terrainVertexFormat.getVertexSize();
    public static final int UNDEFINED_FACING_IDX = net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal();
    public static final float POS_OFFSET = net.vulkanmod.render.vertex.CustomVertexFormat.getPositionOffset();
    private static final long cmdBufferPtr = org.lwjgl.system.MemoryUtil.nmemAlignedAlloc(32, (512 * ((long) net.vulkanmod.render.chunk.cull.QuadFacing.COUNT)) * 32);
    private boolean allocated = false;
    private final java.util.EnumMap<net.vulkanmod.render.vertex.TerrainRenderType, net.vulkanmod.render.chunk.buffer.AreaBuffer> vertexBuffers = new java.util.EnumMap<>(net.vulkanmod.render.vertex.TerrainRenderType.class);
    private final net.vulkanmod.vulkan.memory.buffer.UniformBuffer sectionDataBuffer = new net.vulkanmod.vulkan.memory.buffer.UniformBuffer(4096, net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM);
    final int[] sectionIndices = new int[512];
    final int[] masks = new int[512];
    final long[] buildTimes = new long[512];
    long latestBuildTime = 0;
    long lastFadeUpdate = -1;
    final long drawParamsPtr = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.allocateBuffer();

    public DrawBuffers(int index, org.joml.Vector3i origin, int minHeight) {
        this.index = index;
        this.origin = origin;
        this.minHeight = minHeight;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public void upload(net.vulkanmod.render.chunk.RenderSection section, net.vulkanmod.render.chunk.build.UploadBuffer buffer, net.vulkanmod.render.vertex.TerrainRenderType renderType) throws java.lang.MatchException {
        java.nio.ByteBuffer[] vertexBuffers = buffer.getVertexBuffers();
        if (buffer.indexOnly) {
            long paramsPtr = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getParamsPtr(this.drawParamsPtr, section.inAreaIndex, renderType.ordinal(), net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal());
            int firstIndex = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(paramsPtr);
            int indexCount = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(paramsPtr);
            int oldOffset = indexCount > 0 ? firstIndex : -1;
            int firstIndex2 = this.indexBuffer.upload(buffer.getIndexBuffer(), oldOffset, paramsPtr).offset / 2;
            net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.setFirstIndex(paramsPtr, firstIndex2);
            buffer.release();
            return;
        }
        int oldOffset2 = -1;
        int size = 0;
        for (int i = 0; i < net.vulkanmod.render.chunk.cull.QuadFacing.COUNT; i++) {
            int vertexOffset = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getVertexOffset(net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getParamsPtr(this.drawParamsPtr, section.inAreaIndex, renderType.ordinal(), i));
            if (oldOffset2 == -1) {
                oldOffset2 = vertexOffset;
            }
            java.nio.ByteBuffer vertexBuffer = vertexBuffers[i];
            if (vertexBuffer != null) {
                size += vertexBuffer.remaining();
            }
        }
        if (size == 0) {
            return;
        }
        net.vulkanmod.render.chunk.buffer.AreaBuffer areaBuffer = getAreaBufferOrAlloc(renderType);
        areaBuffer.freeSegment(oldOffset2);
        net.vulkanmod.render.chunk.buffer.AreaBuffer.Segment segment = areaBuffer.allocateSegment(size);
        int baseInstance = section.inAreaIndex;
        int offset = 0;
        for (int i2 = 0; i2 < net.vulkanmod.render.chunk.cull.QuadFacing.COUNT; i2++) {
            long paramPtr = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getParamsPtr(this.drawParamsPtr, section.inAreaIndex, renderType.ordinal(), i2);
            int vertexOffset2 = -1;
            int firstIndex3 = 0;
            int indexCount2 = 0;
            java.nio.ByteBuffer vertexBuffer2 = vertexBuffers[i2];
            int vertexCount = 0;
            if (vertexBuffer2 != null) {
                areaBuffer.upload(segment, vertexBuffer2, offset);
                vertexOffset2 = (segment.offset + offset) / VERTEX_SIZE;
                offset += vertexBuffer2.remaining();
                vertexCount = vertexBuffer2.limit() / VERTEX_SIZE;
                indexCount2 = (vertexCount * 6) / 4;
            }
            if (i2 == net.vulkanmod.render.chunk.cull.QuadFacing.UNDEFINED.ordinal() && !buffer.autoIndices) {
                if (this.indexBuffer == null) {
                    this.indexBuffer = new net.vulkanmod.render.chunk.buffer.AreaBuffer(net.vulkanmod.render.chunk.buffer.AreaBuffer.Usage.INDEX, 60000, 2);
                }
                int oldOffset3 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(paramPtr) > 0 ? net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(paramPtr) : -1;
                net.vulkanmod.render.chunk.buffer.AreaBuffer.Segment ibSegment = this.indexBuffer.upload(buffer.getIndexBuffer(), oldOffset3, paramPtr);
                firstIndex3 = ibSegment.offset / 2;
            } else {
                net.vulkanmod.vulkan.Renderer.getDrawer().getQuadsIndexBuffer().checkCapacity(vertexCount);
            }
            net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.setIndexCount(paramPtr, indexCount2);
            net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.setFirstIndex(paramPtr, firstIndex3);
            net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.setVertexOffset(paramPtr, vertexOffset2);
            net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.setBaseInstance(paramPtr, baseInstance);
        }
        updateUniformData(section);
        buffer.release();
    }

    private void updateUniformData(net.vulkanmod.render.chunk.RenderSection section) {
        int encodedOffset = encodeSectionOffset(section.xOffset(), section.yOffset(), section.zOffset());
        int ptrOffset = section.inAreaIndex * 4;
        org.lwjgl.system.MemoryUtil.memPutInt(this.sectionDataBuffer.getPointer() + ((long) ptrOffset), encodedOffset);
        if (section.getCompiledSection() == net.vulkanmod.render.chunk.build.task.CompiledSection.UNCOMPILED) {
            long buildTime = java.lang.System.currentTimeMillis();
            this.buildTimes[section.inAreaIndex] = buildTime;
            if (buildTime > this.latestBuildTime) {
                this.latestBuildTime = buildTime;
            }
        }
    }

    private void updateFadeUniform(long currentTime, int fadeTimeMs, float fadeTimeInv) {
        if (this.lastFadeUpdate < this.latestBuildTime + ((long) fadeTimeMs)) {
            int ptrOffset = 2048;
            for (int i = 0; i < 512; i++) {
                long delta = currentTime - this.buildTimes[i];
                float fade = fadeTimeMs > 0 ? net.minecraft.util.Mth.clamp(delta * fadeTimeInv, 0.0f, 1.0f) : 1.0f;
                org.lwjgl.system.MemoryUtil.memPutFloat(this.sectionDataBuffer.getPointer() + ((long) ptrOffset), fade);
                ptrOffset += 4;
            }
            this.lastFadeUpdate = currentTime;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    private net.vulkanmod.render.chunk.buffer.AreaBuffer getAreaBufferOrAlloc(net.vulkanmod.render.vertex.TerrainRenderType renderType) throws java.lang.MatchException {
        int i;
        this.allocated = true;
        switch (renderType) {
            case SOLID:
                i = 100000;
                break;
            case CUTOUT:
                i = 250000;
                break;
            case TRANSLUCENT:
            case TRIPWIRE:
                i = 60000;
                break;
            default:
                throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
        }
        int initialSize = i;
        return (net.vulkanmod.render.chunk.buffer.AreaBuffer) this.vertexBuffers.computeIfAbsent(renderType, renderType1 -> {
            return new net.vulkanmod.render.chunk.buffer.AreaBuffer(net.vulkanmod.render.chunk.buffer.AreaBuffer.Usage.VERTEX, initialSize, VERTEX_SIZE);
        });
    }

    public net.vulkanmod.render.chunk.buffer.AreaBuffer getAreaBuffer(net.vulkanmod.render.vertex.TerrainRenderType r) {
        return this.vertexBuffers.get(r);
    }

    private boolean hasRenderType(net.vulkanmod.render.vertex.TerrainRenderType r) {
        return this.vertexBuffers.containsKey(r);
    }

    private int encodeSectionOffset(int xOffset, int yOffset, int zOffset) {
        int xOffset1 = xOffset & org.lwjgl.vulkan.VK10.VK_FORMAT_S8_UINT;
        int zOffset1 = zOffset & org.lwjgl.vulkan.VK10.VK_FORMAT_S8_UINT;
        int yOffset1 = (yOffset - this.minHeight) & org.lwjgl.vulkan.VK10.VK_FORMAT_S8_UINT;
        return (yOffset1 << 16) | (zOffset1 << 8) | xOffset1;
    }

    private void updateChunkAreaOrigin(org.lwjgl.vulkan.VkCommandBuffer commandBuffer, net.vulkanmod.vulkan.shader.Pipeline pipeline, double camX, double camY, double camZ, org.lwjgl.system.MemoryStack stack) {
        float xOffset = (float) (((double) (this.origin.x + POS_OFFSET)) - camX);
        float yOffset = (float) (((double) (this.origin.y + POS_OFFSET)) - camY);
        float zOffset = (float) (((double) (this.origin.z + POS_OFFSET)) - camZ);
        java.nio.ByteBuffer byteBuffer = stack.malloc(12);
        byteBuffer.putFloat(0, xOffset);
        byteBuffer.putFloat(4, yOffset);
        byteBuffer.putFloat(8, zOffset);
        org.lwjgl.vulkan.VK10.vkCmdPushConstants(commandBuffer, pipeline.getLayout(), 1, 0, byteBuffer);
    }

    public void buildDrawBatchesIndirect(net.minecraft.world.phys.Vec3 cameraPos, net.vulkanmod.vulkan.memory.buffer.IndirectBuffer indirectBuffer, net.vulkanmod.render.chunk.util.StaticQueue<net.vulkanmod.render.chunk.RenderSection> queue, net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType) {
        long bufferPtr = cmdBufferPtr;
        boolean isTranslucent = terrainRenderType == net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
        boolean backFaceCulling = net.vulkanmod.Initializer.CONFIG.backFaceCulling && !isTranslucent;
        int drawCount = 0;
        long drawParamsBasePtr = this.drawParamsPtr + (((long) (terrainRenderType.ordinal() * 512 * 7)) * 16);
        int count = 0;
        if (backFaceCulling) {
            java.util.Iterator<net.vulkanmod.render.chunk.RenderSection> iterator = queue.iterator(isTranslucent);
            while (iterator.hasNext()) {
                net.vulkanmod.render.chunk.RenderSection section = iterator.next();
                this.sectionIndices[count] = section.inAreaIndex;
                this.masks[count] = getMask(cameraPos, section);
                count++;
            }
            long ptr = bufferPtr;
            for (int j = 0; j < count; j++) {
                int sectionIdx = this.sectionIndices[j];
                int mask = this.masks[j];
                long drawParamsBasePtr2 = drawParamsBasePtr + (((long) sectionIdx) * 112);
                int indexCount = 0;
                int firstIndex = 0;
                int vertexOffset = 0;
                int baseInstance = 0;
                for (int i = 0; i < net.vulkanmod.render.chunk.cull.QuadFacing.COUNT; i++) {
                    if ((mask & (1 << i)) == 0) {
                        drawParamsBasePtr2 += 16;
                        if (indexCount > 0) {
                            org.lwjgl.system.MemoryUtil.memPutInt(ptr, indexCount);
                            org.lwjgl.system.MemoryUtil.memPutInt(ptr + 4, 1);
                            org.lwjgl.system.MemoryUtil.memPutInt(ptr + 8, firstIndex);
                            org.lwjgl.system.MemoryUtil.memPutInt(ptr + 12, vertexOffset);
                            org.lwjgl.system.MemoryUtil.memPutInt(ptr + 16, baseInstance);
                            ptr += 32;
                            drawCount++;
                        }
                        indexCount = 0;
                        firstIndex = 0;
                        vertexOffset = 0;
                        baseInstance = 0;
                    } else {
                        long drawParamsPtr = drawParamsBasePtr2;
                        int indexCount_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(drawParamsPtr);
                        int firstIndex_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(drawParamsPtr);
                        int vertexOffset_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getVertexOffset(drawParamsPtr);
                        int baseInstance_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getBaseInstance(drawParamsPtr);
                        if (indexCount == 0) {
                            indexCount = indexCount_i;
                            firstIndex = firstIndex_i;
                            vertexOffset = vertexOffset_i;
                            baseInstance = baseInstance_i;
                        } else {
                            indexCount += indexCount_i;
                        }
                        drawParamsBasePtr2 += 16;
                    }
                }
                if (indexCount > 0) {
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr, indexCount);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr + 4, 1);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr + 8, firstIndex);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr + 12, vertexOffset);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr + 16, baseInstance);
                    ptr += 32;
                    drawCount++;
                }
            }
        } else {
            java.util.Iterator<net.vulkanmod.render.chunk.RenderSection> iterator2 = queue.iterator(isTranslucent);
            while (iterator2.hasNext()) {
                this.sectionIndices[count] = iterator2.next().inAreaIndex;
                count++;
            }
            long facingOffset = ((long) UNDEFINED_FACING_IDX) * 16;
            long drawParamsBasePtr3 = drawParamsBasePtr + facingOffset;
            long ptr2 = bufferPtr;
            for (int i2 = 0; i2 < count; i2++) {
                int sectionIdx2 = this.sectionIndices[i2];
                long drawParamsPtr2 = drawParamsBasePtr3 + (((long) sectionIdx2) * 112);
                int indexCount2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(drawParamsPtr2);
                int firstIndex2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(drawParamsPtr2);
                int vertexOffset2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getVertexOffset(drawParamsPtr2);
                int baseInstance2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getBaseInstance(drawParamsPtr2);
                if (indexCount2 > 0) {
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr2, indexCount2);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr2 + 4, 1);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr2 + 8, firstIndex2);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr2 + 12, vertexOffset2);
                    org.lwjgl.system.MemoryUtil.memPutInt(ptr2 + 16, baseInstance2);
                    ptr2 += 32;
                    drawCount++;
                }
            }
        }
        if (drawCount == 0) {
            return;
        }
        java.nio.ByteBuffer byteBuffer = org.lwjgl.system.MemoryUtil.memByteBuffer(cmdBufferPtr, queue.size() * net.vulkanmod.render.chunk.cull.QuadFacing.COUNT * 32);
        indirectBuffer.recordCopyCmd(byteBuffer.position(0));
        org.lwjgl.vulkan.VK10.vkCmdDrawIndexedIndirect(net.vulkanmod.vulkan.Renderer.getCommandBuffer(), indirectBuffer.getId(), indirectBuffer.getOffset(), drawCount, 32);
    }

    public void buildDrawBatchesDirect(net.minecraft.world.phys.Vec3 cameraPos, net.vulkanmod.render.chunk.util.StaticQueue<net.vulkanmod.render.chunk.RenderSection> queue, net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType) {
        boolean isTranslucent = terrainRenderType == net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
        boolean backFaceCulling = net.vulkanmod.Initializer.CONFIG.backFaceCulling && !isTranslucent;
        org.lwjgl.vulkan.VkCommandBuffer commandBuffer = net.vulkanmod.vulkan.Renderer.getCommandBuffer();
        long drawParamsBasePtr = this.drawParamsPtr + (((long) (terrainRenderType.ordinal() * 512 * 7)) * 16);
        int count = 0;
        if (backFaceCulling) {
            java.util.Iterator<net.vulkanmod.render.chunk.RenderSection> iterator = queue.iterator(isTranslucent);
            while (iterator.hasNext()) {
                net.vulkanmod.render.chunk.RenderSection section = iterator.next();
                this.sectionIndices[count] = section.inAreaIndex;
                this.masks[count] = getMask(cameraPos, section);
                count++;
            }
            for (int j = 0; j < count; j++) {
                int sectionIdx = this.sectionIndices[j];
                int mask = this.masks[j];
                long drawParamsBasePtr2 = drawParamsBasePtr + (((long) sectionIdx) * 112);
                int indexCount = 0;
                int firstIndex = 0;
                int vertexOffset = 0;
                int baseInstance = 0;
                for (int i = 0; i < net.vulkanmod.render.chunk.cull.QuadFacing.COUNT; i++) {
                    if ((mask & (1 << i)) == 0) {
                        drawParamsBasePtr2 += 16;
                        if (indexCount > 0) {
                            org.lwjgl.vulkan.VK10.vkCmdDrawIndexed(commandBuffer, indexCount, 1, firstIndex, vertexOffset, baseInstance);
                        }
                        indexCount = 0;
                        firstIndex = 0;
                        vertexOffset = 0;
                        baseInstance = 0;
                    } else {
                        long drawParamsPtr = drawParamsBasePtr2;
                        int indexCount_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(drawParamsPtr);
                        int firstIndex_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(drawParamsPtr);
                        int vertexOffset_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getVertexOffset(drawParamsPtr);
                        int baseInstance_i = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getBaseInstance(drawParamsPtr);
                        if (indexCount == 0) {
                            indexCount = indexCount_i;
                            firstIndex = firstIndex_i;
                            vertexOffset = vertexOffset_i;
                            baseInstance = baseInstance_i;
                        } else {
                            indexCount += indexCount_i;
                        }
                        drawParamsBasePtr2 += 16;
                    }
                }
                if (indexCount > 0) {
                    org.lwjgl.vulkan.VK10.vkCmdDrawIndexed(commandBuffer, indexCount, 1, firstIndex, vertexOffset, baseInstance);
                }
            }
            return;
        }
        long facingOffset = ((long) UNDEFINED_FACING_IDX) * 16;
        long drawParamsBasePtr3 = drawParamsBasePtr + facingOffset;
        java.util.Iterator<net.vulkanmod.render.chunk.RenderSection> iterator2 = queue.iterator(isTranslucent);
        while (iterator2.hasNext()) {
            this.sectionIndices[count] = iterator2.next().inAreaIndex;
            count++;
        }
        for (int i2 = 0; i2 < count; i2++) {
            int sectionIdx2 = this.sectionIndices[i2];
            long drawParamsPtr2 = drawParamsBasePtr3 + (((long) sectionIdx2) * 112);
            int indexCount2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getIndexCount(drawParamsPtr2);
            int firstIndex2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getFirstIndex(drawParamsPtr2);
            int vertexOffset2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getVertexOffset(drawParamsPtr2);
            int baseInstance2 = net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.getBaseInstance(drawParamsPtr2);
            if (indexCount2 > 0) {
                org.lwjgl.vulkan.VK10.vkCmdDrawIndexed(commandBuffer, indexCount2, 1, firstIndex2, vertexOffset2, baseInstance2);
            }
        }
    }

    private int getMask(net.minecraft.world.phys.Vec3 camera, net.vulkanmod.render.chunk.RenderSection section) {
        int secX = section.xOffset;
        int secY = section.yOffset;
        int secZ = section.zOffset;
        int mask = 1 << UNDEFINED_FACING_IDX;
        return mask | (camera.x - ((double) secX) >= 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.X_POS.ordinal() : 0) | (camera.y - ((double) secY) >= 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.Y_POS.ordinal() : 0) | (camera.z - ((double) secZ) >= 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.Z_POS.ordinal() : 0) | (camera.x - ((double) (secX + 16)) < 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.X_NEG.ordinal() : 0) | (camera.y - ((double) (secY + 16)) < 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.Y_NEG.ordinal() : 0) | (camera.z - ((double) (secZ + 16)) < 0.0d ? 1 << net.vulkanmod.render.chunk.cull.QuadFacing.Z_NEG.ordinal() : 0);
    }

    public void bindBuffers(org.lwjgl.vulkan.VkCommandBuffer commandBuffer, net.vulkanmod.vulkan.shader.Pipeline pipeline, net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType, double camX, double camY, double camZ, long currentTime, int fadeTimeMs, float fadeTimeInv) {
        org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush();
        try {
            net.vulkanmod.render.chunk.buffer.AreaBuffer vertexBuffer = getAreaBuffer(terrainRenderType);
            org.lwjgl.vulkan.VK10.nvkCmdBindVertexBuffers(commandBuffer, 0, 1, stack.npointer(vertexBuffer.getId()), stack.npointer(0L));
            updateChunkAreaOrigin(commandBuffer, pipeline, camX, camY, camZ, stack);
            if (stack != null) {
                stack.close();
            }
            updateFadeUniform(currentTime, fadeTimeMs, fadeTimeInv);
            net.vulkanmod.vulkan.shader.descriptor.UBO ubo = pipeline.getUBO(2);
            ubo.setUseGlobalBuffer(false);
            ubo.getBufferSlice().set(this.sectionDataBuffer, 0, (int) this.sectionDataBuffer.getBufferSize());
            if (terrainRenderType == net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT && this.indexBuffer != null) {
                org.lwjgl.vulkan.VK10.vkCmdBindIndexBuffer(commandBuffer, this.indexBuffer.getId(), 0L, 0);
            }
        } catch (java.lang.Throwable th) {
            if (stack != null) {
                try {
                    stack.close();
                } catch (java.lang.Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void releaseBuffers() {
        if (!this.allocated) {
            return;
        }
        this.vertexBuffers.values().forEach((v0) -> {
            v0.freeBuffer();
        });
        this.vertexBuffers.clear();
        if (this.indexBuffer != null) {
            this.indexBuffer.freeBuffer();
        }
        this.indexBuffer = null;
        this.allocated = false;
    }

    public void free() {
        releaseBuffers();
        net.vulkanmod.render.chunk.buffer.DrawParametersBuffer.freeBuffer(this.drawParamsPtr);
    }

    public boolean isAllocated() {
        return !this.vertexBuffers.isEmpty();
    }

    public java.util.EnumMap<net.vulkanmod.render.vertex.TerrainRenderType, net.vulkanmod.render.chunk.buffer.AreaBuffer> getVertexBuffers() {
        return this.vertexBuffers;
    }

    public net.vulkanmod.render.chunk.buffer.AreaBuffer getIndexBuffer() {
        return this.indexBuffer;
    }

    public long getDrawParamsPtr() {
        return this.drawParamsPtr;
    }
}
