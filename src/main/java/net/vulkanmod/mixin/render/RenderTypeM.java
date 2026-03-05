package net.vulkanmod.mixin.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/RenderTypeM.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.rendertype.RenderType.class})
public class RenderTypeM implements net.vulkanmod.interfaces.ExtendedRenderType {

    @org.spongepowered.asm.mixin.Unique
    net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    private net.minecraft.client.renderer.rendertype.RenderSetup state;

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    protected java.lang.String name;

    @org.spongepowered.asm.mixin.injection.Inject(method = {"<init>"}, at = {@org.spongepowered.asm.mixin.injection.At("RETURN")})
    private void inj(java.lang.String string, net.minecraft.client.renderer.rendertype.RenderSetup renderSetup, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.vulkanmod.render.vertex.TerrainRenderType terrainRenderType;
        switch (string) {
            case "solid":
                terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.SOLID;
                break;
            case "cutout":
                terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.CUTOUT;
                break;
            case "translucent":
                terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.TRANSLUCENT;
                break;
            case "tripwire":
                terrainRenderType = net.vulkanmod.render.vertex.TerrainRenderType.TRIPWIRE;
                break;
            default:
                terrainRenderType = null;
                break;
        }
        this.terrainRenderType = terrainRenderType;
    }

    @Override // net.vulkanmod.interfaces.ExtendedRenderType
    public net.vulkanmod.render.vertex.TerrainRenderType getTerrainRenderType() {
        return this.terrainRenderType;
    }

    @org.spongepowered.asm.mixin.Overwrite
    public void draw(com.mojang.blaze3d.vertex.MeshData meshData) {
        com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer2;
        com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType;
        org.joml.Matrix4fStack matrix4fStack = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        net.vulkanmod.mixin.render.RenderSetupAccessor renderSetupAccessor = (net.vulkanmod.mixin.render.RenderSetupAccessor)(Object) this.state;
        java.util.function.Consumer<org.joml.Matrix4fStack> consumer = renderSetupAccessor.layeringTransform().getModifier();
        if (consumer != null) {
            matrix4fStack.pushMatrix();
            consumer.accept(matrix4fStack);
        }
        com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice = com.mojang.blaze3d.systems.RenderSystem.getDynamicUniforms().writeTransform(com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix(), new org.joml.Vector4f(1.0f, 1.0f, 1.0f, 1.0f), new org.joml.Vector3f(), renderSetupAccessor.textureTransform().getMatrix());
        java.util.Map<java.lang.String, net.minecraft.client.renderer.rendertype.RenderSetup.TextureAndSampler> map = this.state.getTextures();
        com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer = renderSetupAccessor.pipeline().getVertexFormat().uploadImmediateVertexBuffer(meshData.vertexBuffer());
        if (meshData.indexBuffer() == null) {
            com.mojang.blaze3d.systems.RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = com.mojang.blaze3d.systems.RenderSystem.getSequentialBuffer(meshData.drawState().mode());
            gpuBuffer2 = autoStorageIndexBuffer.getBuffer(meshData.drawState().indexCount());
            indexType = autoStorageIndexBuffer.type();
        } else {
            gpuBuffer2 = renderSetupAccessor.pipeline().getVertexFormat().uploadImmediateIndexBuffer(meshData.indexBuffer());
            indexType = meshData.drawState().indexType();
        }
        com.mojang.blaze3d.pipeline.RenderTarget renderTarget = renderSetupAccessor.outputTarget().getRenderTarget();
        com.mojang.blaze3d.textures.GpuTextureView gpuTextureView = com.mojang.blaze3d.systems.RenderSystem.outputColorTextureOverride != null ? com.mojang.blaze3d.systems.RenderSystem.outputColorTextureOverride : renderTarget.getColorTextureView();
        com.mojang.blaze3d.textures.GpuTextureView gpuTextureView2 = renderTarget.useDepth ? com.mojang.blaze3d.systems.RenderSystem.outputDepthTextureOverride != null ? com.mojang.blaze3d.systems.RenderSystem.outputDepthTextureOverride : renderTarget.getDepthTextureView() : null;
        com.mojang.blaze3d.systems.RenderPass renderPass = com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
            return "Immediate draw for " + this.name;
        }, gpuTextureView, java.util.OptionalInt.empty(), gpuTextureView2, java.util.OptionalDouble.empty());
        try {
            renderPass.setPipeline(renderSetupAccessor.pipeline());
            com.mojang.blaze3d.systems.ScissorState scissorState = com.mojang.blaze3d.systems.RenderSystem.getScissorStateForRenderTypeDraws();
            if (scissorState.enabled()) {
                renderPass.enableScissor(scissorState.x(), scissorState.y(), scissorState.width(), scissorState.height());
            }
            com.mojang.blaze3d.systems.RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, gpuBuffer);
            for (java.util.Map.Entry<java.lang.String, net.minecraft.client.renderer.rendertype.RenderSetup.TextureAndSampler> entry : map.entrySet()) {
                renderPass.bindTexture(entry.getKey(), entry.getValue().textureView(), entry.getValue().sampler());
            }
            renderPass.setIndexBuffer(gpuBuffer2, indexType);
            net.vulkanmod.render.engine.VkCommandEncoder commandEncoder = (net.vulkanmod.render.engine.VkCommandEncoder) com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder();
            commandEncoder.trySetup((net.vulkanmod.render.engine.VkRenderPass) renderPass);
            net.vulkanmod.vulkan.Renderer.getDrawer().draw(meshData.vertexBuffer(), meshData.indexBuffer(), meshData.drawState().mode(), meshData.drawState().format(), meshData.drawState().vertexCount());
            if (renderPass != null) {
                renderPass.close();
            }
            if (meshData != null) {
                meshData.close();
            }
            if (consumer != null) {
                matrix4fStack.popMatrix();
            }
        } catch (java.lang.Throwable th) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                } catch (java.lang.Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
