package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkTextureView.class */
public class VkTextureView extends com.mojang.blaze3d.textures.GpuTextureView {
    private boolean closed;
    private final it.unimi.dsi.fastutil.ints.Int2ReferenceMap<net.vulkanmod.render.engine.VkFbo> fboCache;

    protected VkTextureView(
            net.vulkanmod.render.engine.VkGpuTexture gpuTexture, int baseMipLevel, int mipLevels) {
        super(gpuTexture, baseMipLevel, mipLevels);
        this.fboCache = new it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap<>();
        gpuTexture.addViews();
    }

    public net.vulkanmod.render.engine.VkFbo getFbo(
            @org.jetbrains.annotations.Nullable com.mojang.blaze3d.textures.GpuTexture depthAttachment) {
        int depthAttachmentId = depthAttachment == null
                ? 0
                : ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment).id;
        return (net.vulkanmod.render.engine.VkFbo) this.fboCache.computeIfAbsent(
                depthAttachmentId,
                j -> {
                    return new net.vulkanmod.render.engine.VkFbo(
                            this,
                            (net.vulkanmod.render.engine.VkGpuTexture) depthAttachment);
                });
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            texture().removeViews();
        }
        it.unimi.dsi.fastutil.objects.ObjectIterator<net.vulkanmod.render.engine.VkFbo> it = this.fboCache.values()
                .iterator();
        while (it.hasNext()) {
            net.vulkanmod.render.engine.VkFbo fbo = (net.vulkanmod.render.engine.VkFbo) it.next();
            fbo.close();
        }
    }

    /*
     * JADX INFO: renamed from: texture, reason: merged with bridge method
     * [inline-methods]
     */
    public net.vulkanmod.render.engine.VkGpuTexture texture() {
        return (net.vulkanmod.render.engine.VkGpuTexture) super.texture();
    }
}
