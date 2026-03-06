package net.vulkanmod.vulkan;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/vulkan/Drawer.class */
public class Drawer {
    private static final int INITIAL_VB_SIZE = 4000000;
    private static final int INITIAL_IB_SIZE = 1000000;
    private static final int INITIAL_UB_SIZE = 200000;
    private static final java.nio.LongBuffer buffers = org.lwjgl.system.MemoryUtil.memAllocLong(1);
    private static final java.nio.LongBuffer offsets = org.lwjgl.system.MemoryUtil.memAllocLong(1);
    private static final long pBuffers = org.lwjgl.system.MemoryUtil.memAddress0(buffers);
    private static final long pOffsets = org.lwjgl.system.MemoryUtil.memAddress0(offsets);
    private int framesNum;
    private net.vulkanmod.vulkan.memory.buffer.VertexBuffer[] vertexBuffers;
    private net.vulkanmod.vulkan.memory.buffer.IndexBuffer[] indexBuffers;
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer quadsIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            65536, net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.QUADS);
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer quadsIntIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            100000,
            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.QUADS);
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer linesIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            10000, net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.LINES);
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer debugLineStripIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            10000,
            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.DEBUG_LINE_STRIP);
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer triangleFanIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            1000,
            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.TRIANGLE_FAN);
    private final net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer triangleStripIndexBuffer = new net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer(
            10000,
            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType.TRIANGLE_STRIP);
    private net.vulkanmod.vulkan.memory.buffer.UniformBuffer[] uniformBuffers;
    private int currentFrame;

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void createResources(int framesNum) {
        this.framesNum = framesNum;
        if (this.vertexBuffers != null) {
            java.util.Arrays.stream(this.vertexBuffers)
                    .iterator()
                    .forEachRemaining(
                            (v0) -> {
                                v0.scheduleFree();
                            });
        }
        this.vertexBuffers = new net.vulkanmod.vulkan.memory.buffer.VertexBuffer[framesNum];
        java.util.Arrays.setAll(
                this.vertexBuffers,
                i -> {
                    return new net.vulkanmod.vulkan.memory.buffer.VertexBuffer(
                            INITIAL_VB_SIZE, net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM);
                });
        if (this.indexBuffers != null) {
            java.util.Arrays.stream(this.indexBuffers)
                    .iterator()
                    .forEachRemaining(
                            (v0) -> {
                                v0.scheduleFree();
                            });
        }
        this.indexBuffers = new net.vulkanmod.vulkan.memory.buffer.IndexBuffer[framesNum];
        java.util.Arrays.setAll(
                this.indexBuffers,
                i2 -> {
                    return new net.vulkanmod.vulkan.memory.buffer.IndexBuffer(
                            INITIAL_IB_SIZE, net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM);
                });
        if (this.uniformBuffers != null) {
            java.util.Arrays.stream(this.uniformBuffers)
                    .iterator()
                    .forEachRemaining(
                            (v0) -> {
                                v0.scheduleFree();
                            });
        }
        this.uniformBuffers = new net.vulkanmod.vulkan.memory.buffer.UniformBuffer[framesNum];
        java.util.Arrays.setAll(
                this.uniformBuffers,
                i3 -> {
                    return new net.vulkanmod.vulkan.memory.buffer.UniformBuffer(
                            INITIAL_UB_SIZE, net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM);
                });
    }

    public void resetBuffers(int currentFrame) {
        this.vertexBuffers[currentFrame].reset();
        this.indexBuffers[currentFrame].reset();
        this.uniformBuffers[currentFrame].reset();
    }

    /*
     * JADX INFO: Thrown type has an unknown type hierarchy:
     * java.lang.MatchException
     */
    public void draw(
            java.nio.ByteBuffer vertexData,
            com.mojang.blaze3d.vertex.VertexFormat.Mode mode,
            com.mojang.blaze3d.vertex.VertexFormat vertexFormat,
            int vertexCount)
            throws java.lang.MatchException {
        draw(vertexData, null, mode, vertexFormat, vertexCount);
    }

    /*
     * JADX INFO: Thrown type has an unknown type hierarchy:
     * java.lang.MatchException
     */
    public void draw(
            java.nio.ByteBuffer vertexData,
            java.nio.ByteBuffer indexData,
            com.mojang.blaze3d.vertex.VertexFormat.Mode mode,
            com.mojang.blaze3d.vertex.VertexFormat vertexFormat,
            int vertexCount)
            throws java.lang.MatchException {
        net.vulkanmod.vulkan.memory.buffer.VertexBuffer vertexBuffer = this.vertexBuffers[this.currentFrame];
        int size = vertexFormat.getVertexSize() * vertexCount;
        vertexBuffer.copyBuffer(vertexData, size);
        if (indexData != null) {
            net.vulkanmod.vulkan.memory.buffer.IndexBuffer indexBuffer = this.indexBuffers[this.currentFrame];
            indexBuffer.copyBuffer(indexData, indexData.remaining());
            drawIndexed(vertexBuffer, indexBuffer, (vertexCount * 3) / 2);
            return;
        }
        net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer autoIndexBuffer = getAutoIndexBuffer(mode,
                vertexCount);
        if (autoIndexBuffer != null) {
            int indexCount = autoIndexBuffer.getIndexCount(vertexCount);
            autoIndexBuffer.checkCapacity(indexCount);
            drawIndexed(vertexBuffer, autoIndexBuffer.getIndexBuffer(), indexCount);
            return;
        }
        draw(vertexBuffer, vertexCount);
    }

    public void drawIndexed(
            net.vulkanmod.vulkan.memory.buffer.Buffer vertexBuffer,
            net.vulkanmod.vulkan.memory.buffer.IndexBuffer indexBuffer,
            int indexCount) {
        drawIndexed(vertexBuffer, indexBuffer, indexCount, indexBuffer.indexType.value);
    }

    public void drawIndexed(
            net.vulkanmod.vulkan.memory.buffer.Buffer vertexBuffer,
            net.vulkanmod.vulkan.memory.buffer.Buffer indexBuffer,
            int indexCount,
            int indexType) {
        org.lwjgl.vulkan.VkCommandBuffer commandBuffer = net.vulkanmod.vulkan.Renderer.getCommandBuffer();
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putLong(pBuffers, vertexBuffer.getId());
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putLong(pOffsets, vertexBuffer.getOffset());
        org.lwjgl.vulkan.VK10.nvkCmdBindVertexBuffers(commandBuffer, 0, 1, pBuffers, pOffsets);
        bindIndexBuffer(commandBuffer, indexBuffer, indexType);
        org.lwjgl.vulkan.VK10.vkCmdDrawIndexed(commandBuffer, indexCount, 1, 0, 0, 0);
    }

    public void draw(
            net.vulkanmod.vulkan.memory.buffer.VertexBuffer vertexBuffer, int vertexCount) {
        org.lwjgl.vulkan.VkCommandBuffer commandBuffer = net.vulkanmod.vulkan.Renderer.getCommandBuffer();
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putLong(pBuffers, vertexBuffer.getId());
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putLong(pOffsets, vertexBuffer.getOffset());
        org.lwjgl.vulkan.VK10.nvkCmdBindVertexBuffers(commandBuffer, 0, 1, pBuffers, pOffsets);
        org.lwjgl.vulkan.VK10.vkCmdDraw(commandBuffer, vertexCount, 1, 0, 0);
    }

    public void bindIndexBuffer(
            org.lwjgl.vulkan.VkCommandBuffer commandBuffer,
            net.vulkanmod.vulkan.memory.buffer.Buffer indexBuffer,
            int indexType) {
        org.lwjgl.vulkan.VK10.vkCmdBindIndexBuffer(
                commandBuffer, indexBuffer.getId(), indexBuffer.getOffset(), indexType);
    }

    public void cleanUpResources() {
        for (int i = 0; i < this.framesNum; i++) {
            net.vulkanmod.vulkan.memory.buffer.Buffer buffer = this.vertexBuffers[i];
            net.vulkanmod.vulkan.memory.MemoryManager.freeBuffer(
                    buffer.getId(), buffer.getAllocation());
            net.vulkanmod.vulkan.memory.buffer.Buffer buffer2 = this.indexBuffers[i];
            net.vulkanmod.vulkan.memory.MemoryManager.freeBuffer(
                    buffer2.getId(), buffer2.getAllocation());
            net.vulkanmod.vulkan.memory.buffer.Buffer buffer3 = this.uniformBuffers[i];
            net.vulkanmod.vulkan.memory.MemoryManager.freeBuffer(
                    buffer3.getId(), buffer3.getAllocation());
        }
        this.quadsIndexBuffer.freeBuffer();
        this.quadsIntIndexBuffer.freeBuffer();
        this.linesIndexBuffer.freeBuffer();
        this.triangleFanIndexBuffer.freeBuffer();
        this.triangleStripIndexBuffer.freeBuffer();
        this.debugLineStripIndexBuffer.freeBuffer();
    }

    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getQuadsIndexBuffer() {
        return this.quadsIndexBuffer;
    }

    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getLinesIndexBuffer() {
        return this.linesIndexBuffer;
    }

    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getTriangleFanIndexBuffer() {
        return this.triangleFanIndexBuffer;
    }

    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getTriangleStripIndexBuffer() {
        return this.triangleStripIndexBuffer;
    }

    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getDebugLineStripIndexBuffer() {
        return this.debugLineStripIndexBuffer;
    }

    public net.vulkanmod.vulkan.memory.buffer.UniformBuffer getUniformBuffer() {
        return this.uniformBuffers[this.currentFrame];
    }

    /*
     * JADX INFO: renamed from: net.vulkanmod.vulkan.Drawer$1, reason: invalid class
     * name
     */
    /*
     * JADX INFO: loaded from:
     * VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/vulkan/Drawer$1.class
     */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode = new int[com.mojang.blaze3d.vertex.VertexFormat.Mode
                .values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS
                        .ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.LINES
                        .ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN
                        .ordinal()] = 3;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP
                        .ordinal()] = 4;
            } catch (java.lang.NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.DEBUG_LINE_STRIP
                        .ordinal()] = 5;
            } catch (java.lang.NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.POINTS
                        .ordinal()] = 6;
            } catch (java.lang.NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLES
                        .ordinal()] = 7;
            } catch (java.lang.NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[com.mojang.blaze3d.vertex.VertexFormat.Mode.DEBUG_LINES
                        .ordinal()] = 8;
            } catch (java.lang.NoSuchFieldError e8) {
            }
        }
    }

    /*
     * JADX INFO: Thrown type has an unknown type hierarchy:
     * java.lang.MatchException
     */
    public net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer getAutoIndexBuffer(
            com.mojang.blaze3d.vertex.VertexFormat.Mode mode, int vertexCount)
            throws java.lang.MatchException {
        switch (net.vulkanmod.vulkan.Drawer.AnonymousClass1.$SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[mode
                .ordinal()]) {
            case 1:
                int indexCount = (vertexCount * 3) / 2;
                return indexCount > 65536 ? this.quadsIntIndexBuffer : this.quadsIndexBuffer;
            case 2:
                return this.linesIndexBuffer;
            case 3:
                return this.triangleFanIndexBuffer;
            case 4:
                return this.triangleStripIndexBuffer;
            case 5:
                return this.debugLineStripIndexBuffer;
            case 6:
                return null;
            case 7:
            case 8:
                return null;
            default:
                throw new java.lang.MatchException(
                        "Unknown VertexFormat.Mode", (java.lang.Throwable) null);
        }
    }
}
