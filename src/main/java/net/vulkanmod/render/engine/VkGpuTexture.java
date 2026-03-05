package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkGpuTexture.class */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class VkGpuTexture extends com.mojang.blaze3d.opengl.GlTexture {
    private static final it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap<com.mojang.blaze3d.opengl.GlTexture, net.vulkanmod.render.engine.VkGpuTexture> glToVkMap = new it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap<>();
    protected net.vulkanmod.gl.VkGlTexture glTexture;
    protected final int id;
    private final it.unimi.dsi.fastutil.ints.Int2ReferenceMap<net.vulkanmod.render.engine.VkFbo> fboCache;
    protected boolean closed;
    net.vulkanmod.render.engine.VkTextureView fboView;
    boolean needsClear;
    int clearColor;
    float depthClearValue;

    protected VkGpuTexture(int usage, java.lang.String string, com.mojang.blaze3d.textures.TextureFormat textureFormat, int width, int height, int layers, int mipLevel, int id, net.vulkanmod.gl.VkGlTexture glTexture) {
        super(usage, string, textureFormat, width, height, layers, mipLevel, id);
        this.fboCache = new it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap();
        this.needsClear = false;
        this.clearColor = 0;
        this.depthClearValue = 1.0f;
        this.id = id;
        this.glTexture = glTexture;
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            com.mojang.blaze3d.opengl.GlStateManager._deleteTexture(this.id);
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public int glId() {
        return this.id;
    }

    public void setClearColor(int clearColor) {
        this.needsClear = true;
        this.clearColor = clearColor;
    }

    public void setDepthClearValue(float depthClearValue) {
        this.needsClear = true;
        this.depthClearValue = depthClearValue;
    }

    public boolean needsClear() {
        return this.needsClear;
    }

    public net.vulkanmod.render.engine.VkFbo getFbo(@org.jetbrains.annotations.Nullable com.mojang.blaze3d.textures.GpuTexture depthAttachment) {
        int depthAttachmentId = depthAttachment == null ? 0 : ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment).id;
        if (this.fboView == null) {
            net.vulkanmod.render.engine.VkGpuDevice gpuDevice = (net.vulkanmod.render.engine.VkGpuDevice) com.mojang.blaze3d.systems.RenderSystem.getDevice();
            this.fboView = (net.vulkanmod.render.engine.VkTextureView) gpuDevice.createTextureView(this, 0, getMipLevels());
        }
        return (net.vulkanmod.render.engine.VkFbo) this.fboCache.computeIfAbsent(depthAttachmentId, j -> {
            return new net.vulkanmod.render.engine.VkFbo(this.fboView, (net.vulkanmod.render.engine.VkGpuTexture) depthAttachment);
        });
    }

    public net.vulkanmod.vulkan.texture.VulkanImage getVulkanImage() {
        return this.glTexture.getVulkanImage();
    }

    public static net.vulkanmod.render.engine.VkGpuTexture fromGlTexture(com.mojang.blaze3d.opengl.GlTexture glTexture) {
        return (net.vulkanmod.render.engine.VkGpuTexture) glToVkMap.computeIfAbsent(glTexture, glTexture1 -> {
            java.lang.String name = glTexture.getLabel();
            int id = glTexture.glId();
            net.vulkanmod.gl.VkGlTexture vglTexture = net.vulkanmod.gl.VkGlTexture.getTexture(id);
            net.vulkanmod.render.engine.VkGpuTexture gpuTexture = new net.vulkanmod.render.engine.VkGpuTexture(0, name, glTexture.getFormat(), glTexture.getWidth(0), glTexture.getHeight(0), 1, glTexture.getMipLevels(), glTexture.glId(), vglTexture);
            return gpuTexture;
        });
    }

    public static com.mojang.blaze3d.textures.TextureFormat textureFormat(int format) {
        switch (format) {
            case 9:
                return com.mojang.blaze3d.textures.TextureFormat.RED8;
            case 37:
            case 44:
                return com.mojang.blaze3d.textures.TextureFormat.RGBA8;
            case org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT /* 126 */:
                return com.mojang.blaze3d.textures.TextureFormat.DEPTH32;
            default:
                return null;
        }
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.engine.VkGpuTexture$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkGpuTexture$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$textures$TextureFormat = new int[com.mojang.blaze3d.textures.TextureFormat.values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$textures$TextureFormat[com.mojang.blaze3d.textures.TextureFormat.RGBA8.ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$textures$TextureFormat[com.mojang.blaze3d.textures.TextureFormat.RED8.ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$textures$TextureFormat[com.mojang.blaze3d.textures.TextureFormat.RED8I.ordinal()] = 3;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$textures$TextureFormat[com.mojang.blaze3d.textures.TextureFormat.DEPTH32.ordinal()] = 4;
            } catch (java.lang.NoSuchFieldError e4) {
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static int vkFormat(com.mojang.blaze3d.textures.TextureFormat textureFormat) throws java.lang.MatchException {
        switch (net.vulkanmod.render.engine.VkGpuTexture.AnonymousClass1.$SwitchMap$com$mojang$blaze3d$textures$TextureFormat[textureFormat.ordinal()]) {
            case 1:
                return 37;
            case 2:
                return 9;
            case 3:
                return 14;
            case 4:
                return org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
            default:
                throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
        }
    }

    public static int vkImageViewType(int usage) {
        int viewType;
        if ((usage & 16) != 0) {
            viewType = 3;
        } else {
            viewType = 1;
        }
        return viewType;
    }
}
