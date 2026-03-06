package net.vulkanmod.render.engine;

import org.jetbrains.annotations.Nullable;

public class VkRenderPass implements com.mojang.blaze3d.systems.RenderPass {
    protected static final int MAX_VERTEX_BUFFERS = 1;
    public static final boolean VALIDATION = net.minecraft.SharedConstants.IS_RUNNING_IN_IDE;
    private final net.vulkanmod.render.engine.VkCommandEncoder encoder;
    private final boolean hasDepthTexture;
    private boolean closed;

    @Nullable
    protected com.mojang.blaze3d.pipeline.RenderPipeline pipeline;

    @Nullable
    protected com.mojang.blaze3d.buffers.GpuBuffer indexBuffer;
    protected int pushedDebugGroups;
    private final boolean autoManaged;
    protected final com.mojang.blaze3d.buffers.GpuBuffer[] vertexBuffers = new com.mojang.blaze3d.buffers.GpuBuffer[1];
    protected com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType = com.mojang.blaze3d.vertex.VertexFormat.IndexType.INT;
    private final com.mojang.blaze3d.systems.ScissorState scissorState = new com.mojang.blaze3d.systems.ScissorState();
    protected final java.util.HashMap<java.lang.String, com.mojang.blaze3d.buffers.GpuBufferSlice> uniforms = new java.util.HashMap<>();
    protected final java.util.HashMap<java.lang.String, net.vulkanmod.render.engine.VkRenderPass.TextureViewAndSampler> samplers = new java.util.HashMap<>();
    protected final java.util.Set<java.lang.String> dirtyUniforms = new java.util.HashSet<>();

    public VkRenderPass(
            net.vulkanmod.render.engine.VkCommandEncoder commandEncoder,
            boolean hasDepthTexture,
            boolean autoManaged) {
        this.encoder = commandEncoder;
        this.hasDepthTexture = hasDepthTexture;
        this.autoManaged = autoManaged;
    }

    public boolean hasDepthTexture() {
        return this.hasDepthTexture;
    }

    @Override
    public void pushDebugGroup(java.util.function.Supplier<java.lang.String> supplier) {
        if (this.closed) {
            throw new java.lang.IllegalStateException("Can't use a closed render pass");
        }
        this.pushedDebugGroups++;
    }

    @Override
    public void popDebugGroup() {
        if (this.closed) {
            throw new java.lang.IllegalStateException("Can't use a closed render pass");
        }
        if (this.pushedDebugGroups == 0) {
            throw new java.lang.IllegalStateException(
                    "Can't pop more debug groups than was pushed!");
        }
        this.pushedDebugGroups--;
    }

    @Override
    public void setPipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
        if (this.pipeline == null || this.pipeline != renderPipeline) {
            this.dirtyUniforms.addAll(this.uniforms.keySet());
        }
        this.pipeline = renderPipeline;
        if (net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(renderPipeline).getPipeline() == null) {
            this.encoder.getDevice().compilePipeline(renderPipeline);
        }
    }

    @Override
    public void bindTexture(
            java.lang.String string,
            @Nullable com.mojang.blaze3d.textures.GpuTextureView gpuTextureView,
            @Nullable com.mojang.blaze3d.textures.GpuSampler gpuSampler) {
        if (gpuSampler == null) {
            this.samplers.remove(string);
        } else {
            net.vulkanmod.render.engine.VkGpuTexture texture = (net.vulkanmod.render.engine.VkGpuTexture) gpuTextureView
                    .texture();
            if (texture.needsClear()) {
            }
            this.samplers.put(
                    string,
                    new net.vulkanmod.render.engine.VkRenderPass.TextureViewAndSampler(
                            (net.vulkanmod.render.engine.VkTextureView) gpuTextureView,
                            (net.vulkanmod.render.engine.VkSampler) gpuSampler));
        }
        this.dirtyUniforms.add(string);
    }

    @Override
    public void setUniform(
            java.lang.String string, com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer) {
        this.uniforms.put(string, gpuBuffer.slice());
        this.dirtyUniforms.add(string);
    }

    @Override
    public void setUniform(
            java.lang.String string, com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice) {
        int i = this.encoder.getDevice().getUniformOffsetAlignment();
        if (gpuBufferSlice.offset() % ((long) i) > 0) {
            throw new java.lang.IllegalArgumentException(
                    "Uniform buffer offset must be aligned to " + i);
        }
        this.uniforms.put(string, gpuBufferSlice);
        this.dirtyUniforms.add(string);
    }

    @Override
    public void enableScissor(int i, int j, int k, int l) {
        this.scissorState.enable(i, j, k, l);
    }

    @Override
    public void disableScissor() {
        this.scissorState.disable();
    }

    public boolean isScissorEnabled() {
        return this.scissorState.enabled();
    }

    public int getScissorX() {
        return this.scissorState.x();
    }

    public int getScissorY() {
        return this.scissorState.y();
    }

    public int getScissorWidth() {
        return this.scissorState.width();
    }

    public int getScissorHeight() {
        return this.scissorState.height();
    }

    public com.mojang.blaze3d.systems.ScissorState getScissorState() {
        return this.scissorState;
    }

    @Override
    public void setVertexBuffer(int i, com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer) {
        if (i >= 0 && i < 1) {
            this.vertexBuffers[i] = gpuBuffer;
            return;
        }
        throw new java.lang.IllegalArgumentException("Vertex buffer slot is out of range: " + i);
    }

    @Override
    public void setIndexBuffer(
            @Nullable com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer,
            com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType) {
        this.indexBuffer = gpuBuffer;
        this.indexType = indexType;
    }

    @Override
    public void drawIndexed(int vertexOffset, int firstIndex, int vertexCount, int instanceCount) {
        if (this.closed) {
            throw new java.lang.IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDraw(
                this, vertexOffset, firstIndex, vertexCount, this.indexType, instanceCount);
    }

    @Override
    public <T> void drawMultipleIndexed(
            java.util.Collection<com.mojang.blaze3d.systems.RenderPass.Draw<T>> collection,
            @Nullable com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer,
            @Nullable com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType,
            java.util.Collection<java.lang.String> collection2,
            T object) {
        if (this.closed) {
            throw new java.lang.IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDrawMultiple(
                this, collection, gpuBuffer, indexType, collection2, object);
    }

    @Override
    public void draw(int vertexOffset, int vertexCount) {
        if (this.closed) {
            throw new java.lang.IllegalStateException("Can't use a closed render pass");
        }
        this.encoder.executeDraw(this, vertexOffset, 0, vertexCount, null, 1);
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (this.pushedDebugGroups > 0) {
                throw new java.lang.IllegalStateException(
                        "Render pass had debug groups left open!");
            }
            this.closed = true;
            this.encoder.finishRenderPass(!this.autoManaged);
        }
    }

    @Nullable
    public com.mojang.blaze3d.pipeline.RenderPipeline getPipeline() {
        return this.pipeline;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    protected record TextureViewAndSampler(VkTextureView view, VkSampler sampler) {
    }
}
