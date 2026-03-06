package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkFbo.class */
public class VkFbo {
    final int glId = com.mojang.blaze3d.opengl.GlStateManager.glGenFramebuffers();
    final net.vulkanmod.render.engine.VkTextureView colorAttachmentView;
    final net.vulkanmod.render.engine.VkGpuTexture depthAttachment;

    protected VkFbo(net.vulkanmod.render.engine.VkTextureView colorAttachmentView, net.vulkanmod.render.engine.VkGpuTexture depthAttachment) {
        this.colorAttachmentView = colorAttachmentView;
        this.depthAttachment = depthAttachment;
        net.vulkanmod.gl.VkGlFramebuffer fbo = net.vulkanmod.gl.VkGlFramebuffer.getFramebuffer(this.glId);
        net.vulkanmod.render.engine.VkGpuTexture colorAttachmentTexture = this.colorAttachmentView.texture();
        int mipLevel = colorAttachmentView.baseMipLevel();
        fbo.setAttachmentTextureLevel(36064, colorAttachmentTexture.id, mipLevel);
        if (depthAttachment != null) {
            fbo.setAttachmentTexture(36096, depthAttachment.id);
        }
    }

    public void bind() {
        net.vulkanmod.gl.VkGlFramebuffer.bindFramebuffer(36160, this.glId);
        clearAttachments();
    }

    protected void clearAttachments() {
        int clear = 0;
        net.vulkanmod.render.engine.VkGpuTexture colorAttachmentTexture = this.colorAttachmentView.texture();
        if (colorAttachmentTexture.needsClear()) {
            clear = 0 | 16384;
            int clearColor = colorAttachmentTexture.clearColor;
            net.vulkanmod.vulkan.VRenderSystem.setClearColor(net.minecraft.util.ARGB.redFloat(clearColor), net.minecraft.util.ARGB.greenFloat(clearColor), net.minecraft.util.ARGB.blueFloat(clearColor), net.minecraft.util.ARGB.alphaFloat(clearColor));
            colorAttachmentTexture.needsClear = false;
        }
        if (this.depthAttachment != null && this.depthAttachment.needsClear()) {
            clear |= 256;
            float clearDepth = this.depthAttachment.depthClearValue;
            net.vulkanmod.vulkan.VRenderSystem.clearDepth(clearDepth);
            this.depthAttachment.needsClear = false;
        }
        if (clear != 0) {
            net.vulkanmod.vulkan.Renderer.clearAttachments(clear);
        }
    }

    protected void close() {
        net.vulkanmod.gl.VkGlFramebuffer.deleteFramebuffer(this.glId);
    }

    public boolean needsClear() {
        return this.colorAttachmentView.texture().needsClear() || (this.depthAttachment != null && this.depthAttachment.needsClear());
    }
}
