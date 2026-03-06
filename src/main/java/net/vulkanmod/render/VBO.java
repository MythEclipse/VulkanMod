package net.vulkanmod.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/VBO.class */
public class VBO {
    private final net.vulkanmod.vulkan.memory.MemoryType memoryType;
    private net.vulkanmod.vulkan.memory.buffer.VertexBuffer vertexBuffer;
    private net.vulkanmod.vulkan.memory.buffer.IndexBuffer indexBuffer;
    private com.mojang.blaze3d.vertex.VertexFormat.Mode mode;
    private boolean autoIndexed = false;
    private int indexCount;
    private int vertexCount;

    public VBO(boolean useGpuMem) {
        this.memoryType =
                useGpuMem
                        ? net.vulkanmod.vulkan.memory.MemoryTypes.GPU_MEM
                        : net.vulkanmod.vulkan.memory.MemoryTypes.HOST_MEM;
    }

    public void upload(com.mojang.blaze3d.vertex.MeshData meshData) {
        com.mojang.blaze3d.vertex.MeshData.DrawState parameters = meshData.drawState();
        this.indexCount = parameters.indexCount();
        this.vertexCount = parameters.vertexCount();
        this.mode = parameters.mode();
        uploadVertexBuffer(parameters, meshData.vertexBuffer());
        uploadIndexBuffer(meshData.indexBuffer());
        meshData.close();
    }

    private void uploadVertexBuffer(
            com.mojang.blaze3d.vertex.MeshData.DrawState parameters, java.nio.ByteBuffer data) {
        if (data != null) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.scheduleFree();
            }
            int size = parameters.format().getVertexSize() * parameters.vertexCount();
            this.vertexBuffer =
                    new net.vulkanmod.vulkan.memory.buffer.VertexBuffer(size, this.memoryType);
            this.vertexBuffer.copyBuffer(data, size);
        }
    }

    public void uploadIndexBuffer(java.nio.ByteBuffer data) {
        net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer autoIndexBuffer;
        if (data == null) {
            switch (net.vulkanmod.render.VBO.AnonymousClass1
                    .$SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[this.mode.ordinal()]) {
                case 1:
                    autoIndexBuffer =
                            net.vulkanmod.vulkan.Renderer.getDrawer().getTriangleFanIndexBuffer();
                    this.indexCount =
                            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType
                                    .getTriangleStripIndexCount(this.vertexCount);
                    break;
                case 2:
                    autoIndexBuffer =
                            net.vulkanmod.vulkan.Renderer.getDrawer().getTriangleStripIndexBuffer();
                    this.indexCount =
                            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer.DrawType
                                    .getTriangleStripIndexCount(this.vertexCount);
                    break;
                case 3:
                    autoIndexBuffer =
                            net.vulkanmod.vulkan.Renderer.getDrawer().getQuadsIndexBuffer();
                    break;
                case 4:
                    autoIndexBuffer =
                            net.vulkanmod.vulkan.Renderer.getDrawer().getLinesIndexBuffer();
                    break;
                case 5:
                    autoIndexBuffer =
                            net.vulkanmod.vulkan.Renderer.getDrawer()
                                    .getDebugLineStripIndexBuffer();
                    break;
                case 6:
                case 7:
                    autoIndexBuffer = null;
                    break;
                default:
                    throw new java.lang.IllegalStateException(
                            "Unexpected draw mode: %s".formatted(this.mode));
            }
            if (this.indexBuffer != null && !this.autoIndexed) {
                this.indexBuffer.scheduleFree();
            }
            if (autoIndexBuffer != null) {
                autoIndexBuffer.checkCapacity(this.vertexCount);
                this.indexBuffer = autoIndexBuffer.getIndexBuffer();
            }
            this.autoIndexed = true;
            return;
        }
        if (this.indexBuffer != null && !this.autoIndexed) {
            this.indexBuffer.scheduleFree();
        }
        this.indexBuffer =
                new net.vulkanmod.vulkan.memory.buffer.IndexBuffer(
                        data.remaining(), net.vulkanmod.vulkan.memory.MemoryTypes.GPU_MEM);
        this.indexBuffer.copyBuffer(data, data.remaining());
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.VBO$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/VBO$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode =
                new int[com.mojang.blaze3d.vertex.VertexFormat.Mode.values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN
                                        .ordinal()] =
                        1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP
                                        .ordinal()] =
                        2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS.ordinal()] =
                        3;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.LINES.ordinal()] =
                        4;
            } catch (java.lang.NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.DEBUG_LINE_STRIP
                                        .ordinal()] =
                        5;
            } catch (java.lang.NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLES.ordinal()] =
                        6;
            } catch (java.lang.NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$Mode[
                                com.mojang.blaze3d.vertex.VertexFormat.Mode.DEBUG_LINES.ordinal()] =
                        7;
            } catch (java.lang.NoSuchFieldError e7) {
            }
        }
    }

    public void bind(net.vulkanmod.vulkan.shader.GraphicsPipeline pipeline) {
        net.vulkanmod.vulkan.Renderer renderer = net.vulkanmod.vulkan.Renderer.getInstance();
        renderer.bindGraphicsPipeline(pipeline);
        net.vulkanmod.vulkan.texture.VTextureSelector.bindShaderTextures(pipeline);
        renderer.uploadAndBindUBOs(pipeline);
    }

    public void draw() {
        if (this.indexCount != 0) {
            net.vulkanmod.vulkan.Renderer renderer = net.vulkanmod.vulkan.Renderer.getInstance();
            net.vulkanmod.vulkan.shader.Pipeline pipeline = renderer.getBoundPipeline();
            renderer.uploadAndBindUBOs(pipeline);
            if (this.indexBuffer != null) {
                net.vulkanmod.vulkan.Renderer.getDrawer()
                        .drawIndexed(this.vertexBuffer, this.indexBuffer, this.indexCount);
            } else {
                net.vulkanmod.vulkan.Renderer.getDrawer().draw(this.vertexBuffer, this.vertexCount);
            }
        }
    }

    public void close() {
        if (this.vertexCount <= 0) {
            return;
        }
        this.vertexBuffer.scheduleFree();
        this.vertexBuffer = null;
        if (!this.autoIndexed) {
            this.indexBuffer.scheduleFree();
            this.indexBuffer = null;
        }
        this.vertexCount = 0;
        this.indexCount = 0;
    }
}
