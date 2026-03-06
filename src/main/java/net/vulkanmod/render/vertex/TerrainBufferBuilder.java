package net.vulkanmod.render.vertex;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/vertex/TerrainBufferBuilder.class */
public class TerrainBufferBuilder implements com.mojang.blaze3d.vertex.VertexConsumer {
    private static final org.apache.logging.log4j.Logger LOGGER = net.vulkanmod.Initializer.LOGGER;
    private static final org.lwjgl.system.MemoryUtil.MemoryAllocator ALLOCATOR =
            org.lwjgl.system.MemoryUtil.getAllocator(false);
    private int capacity;
    private int vertexSize;
    protected long bufferPtr;
    protected int nextElementByte;
    int vertices;
    private long elementPtr;
    private net.vulkanmod.render.vertex.VertexBuilder vertexBuilder;

    public TerrainBufferBuilder(
            int size, int vertexSize, net.vulkanmod.render.vertex.VertexBuilder vertexBuilder) {
        this.bufferPtr = ALLOCATOR.malloc(size);
        this.capacity = size;
        this.vertexSize = vertexSize;
        this.vertexBuilder = vertexBuilder;
    }

    public void ensureCapacity() {
        ensureCapacity(this.vertexSize * 4);
    }

    private void ensureCapacity(int size) {
        if (this.nextElementByte + size > this.capacity) {
            int capacity = this.capacity;
            int newSize = (capacity + size) * 2;
            resize(newSize);
        }
    }

    private void resize(int i) {
        this.bufferPtr = ALLOCATOR.realloc(this.bufferPtr, i);
        LOGGER.debug(
                "Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.",
                java.lang.Integer.valueOf(this.capacity),
                java.lang.Integer.valueOf(i));
        if (this.bufferPtr == 0) {
            throw new java.lang.OutOfMemoryError(
                    "Failed to resize buffer from " + this.capacity + " bytes to " + i + " bytes");
        }
        this.capacity = i;
    }

    public void endVertex() {
        this.nextElementByte += this.vertexSize;
        this.vertices++;
    }

    public void vertex(
            float x, float y, float z, int color, float u, float v, int light, int packedNormal) {
        long ptr = this.bufferPtr + ((long) this.nextElementByte);
        this.vertexBuilder.vertex(ptr, x, y, z, color, u, v, light, packedNormal);
        endVertex();
    }

    public void end() {}

    public void clear() {
        this.nextElementByte = 0;
        this.vertices = 0;
    }

    public void free() {
        ALLOCATOR.free(this.bufferPtr);
    }

    public java.nio.ByteBuffer getBuffer() {
        return org.lwjgl.system.MemoryUtil.memByteBuffer(
                this.bufferPtr, this.vertices * this.vertexSize);
    }

    public long getPtr() {
        return this.bufferPtr;
    }

    public int getVertices() {
        return this.vertices;
    }

    public int getNextElementByte() {
        return this.nextElementByte;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer addVertex(float x, float y, float z) {
        this.elementPtr = this.bufferPtr + ((long) this.nextElementByte);
        endVertex();
        this.vertexBuilder.position(this.elementPtr, x, y, z);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setColor(int r, int g, int b, int a) {
        int color = ((a & 255) << 24) | ((b & 255) << 16) | ((g & 255) << 8) | (r & 255);
        this.vertexBuilder.color(this.elementPtr, color);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setColor(int color) {
        this.vertexBuilder.color(this.elementPtr, color);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setUv(float u, float v) {
        this.vertexBuilder.uv(this.elementPtr, u, v);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setLight(int i) {
        this.vertexBuilder.light(this.elementPtr, i);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setNormal(float f, float g, float h) {
        int packedNormal = net.vulkanmod.render.vertex.format.I32_SNorm.packNormal(f, g, h);
        this.vertexBuilder.normal(this.elementPtr, packedNormal);
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setLineWidth(float f) {
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setUv1(int i, int j) {
        return this;
    }

    public com.mojang.blaze3d.vertex.VertexConsumer setUv2(int i, int j) {
        return this;
    }
}
