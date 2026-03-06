package net.vulkanmod.vulkan.texture;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/vulkan/texture/VTextureSelector.class */
public abstract class VTextureSelector {
    public static final int SIZE = 12;
    private static final net.vulkanmod.vulkan.texture.VulkanImage[] boundTextures =
            new net.vulkanmod.vulkan.texture.VulkanImage[12];
    private static final int[] levels = new int[12];
    private static final net.vulkanmod.vulkan.texture.VulkanImage whiteTexture =
            net.vulkanmod.vulkan.texture.VulkanImage.createWhiteTexture();
    private static int activeTexture = 0;

    public static void bindTexture(net.vulkanmod.vulkan.texture.VulkanImage texture) {
        boundTextures[0] = texture;
    }

    public static void bindTexture(int i, net.vulkanmod.vulkan.texture.VulkanImage texture) {
        if (i < 0 || i >= 12) {
            net.vulkanmod.Initializer.LOGGER.error(
                    java.lang.String.format(
                            "On Texture binding: index %d out of range [0, %d]",
                            java.lang.Integer.valueOf(i), 11));
        } else {
            boundTextures[i] = texture;
            levels[i] = -1;
        }
    }

    public static void bindImage(
            int i, net.vulkanmod.vulkan.texture.VulkanImage texture, int level) {
        if (i < 0 || i > 7) {
            net.vulkanmod.Initializer.LOGGER.error(
                    java.lang.String.format(
                            "On Texture binding: index %d out of range [0, %d]",
                            java.lang.Integer.valueOf(i), 11));
        } else {
            boundTextures[i] = texture;
            levels[i] = level;
        }
    }

    public static void uploadSubTexture(
            int mipLevel,
            int width,
            int height,
            int xOffset,
            int yOffset,
            int unpackSkipRows,
            int unpackSkipPixels,
            int unpackRowLength,
            java.nio.ByteBuffer buffer) {
        uploadSubTexture(
                mipLevel,
                0,
                width,
                height,
                xOffset,
                yOffset,
                unpackSkipRows,
                unpackSkipPixels,
                unpackRowLength,
                org.lwjgl.system.MemoryUtil.memAddress(buffer));
    }

    public static void uploadSubTexture(
            int mipLevel,
            int arrayLayer,
            int width,
            int height,
            int xOffset,
            int yOffset,
            int unpackSkipRows,
            int unpackSkipPixels,
            int unpackRowLength,
            long bufferPtr) {
        net.vulkanmod.vulkan.texture.VulkanImage texture = boundTextures[activeTexture];
        if (texture == null) {
            throw new java.lang.NullPointerException("Texture is null at index: " + activeTexture);
        }
        net.vulkanmod.render.texture.SpriteUpdateUtil.addTransitionedLayout(texture);
        texture.uploadSubTextureAsync(
                mipLevel,
                arrayLayer,
                width,
                height,
                xOffset,
                yOffset,
                unpackSkipRows,
                unpackSkipPixels,
                unpackRowLength,
                bufferPtr);
    }

    public static int getTextureIdx(java.lang.String name) {
        switch (name) {
            case "Sampler0":
            case "DiffuseSampler":
            case "InSampler":
            case "CloudFaces":
            case "Sprite":
            case "CurrentSprite":
                return 0;
            case "Sampler1":
            case "BlurSampler":
            case "NextSprite":
                return 1;
            case "Sampler2":
                return 2;
            case "Sampler3":
                return 3;
            case "Sampler4":
                return 4;
            case "Sampler5":
                return 5;
            case "Sampler6":
                return 6;
            case "Sampler7":
                return 7;
            default:
                return -1;
        }
    }

    public static void bindShaderTextures(net.vulkanmod.vulkan.shader.Pipeline pipeline) {
        java.util.List<net.vulkanmod.vulkan.shader.descriptor.ImageDescriptor> imageDescriptors =
                pipeline.getImageDescriptors();
        for (net.vulkanmod.vulkan.shader.descriptor.ImageDescriptor state : imageDescriptors) {
            com.mojang.blaze3d.textures.GpuTextureView textureView =
                    net.vulkanmod.vulkan.VRenderSystem.getShaderTexture(state.imageIdx);
            if (textureView != null) {
                net.vulkanmod.render.engine.VkGpuTexture gpuTexture =
                        (net.vulkanmod.render.engine.VkGpuTexture) textureView.texture();
                int shaderTexture = gpuTexture.glId();
                net.vulkanmod.gl.VkGlTexture texture =
                        net.vulkanmod.gl.VkGlTexture.getTexture(shaderTexture);
                if (texture != null && texture.getVulkanImage() != null) {
                    bindTexture(state.imageIdx, texture.getVulkanImage());
                }
            }
        }
    }

    public static net.vulkanmod.vulkan.texture.VulkanImage getImage(int i) {
        return boundTextures[i];
    }

    public static void setLightTexture(net.vulkanmod.vulkan.texture.VulkanImage texture) {
        boundTextures[2] = texture;
    }

    public static void setOverlayTexture(net.vulkanmod.vulkan.texture.VulkanImage texture) {
        boundTextures[1] = texture;
    }

    public static void setActiveTexture(int activeTexture2) {
        if (activeTexture2 < 0 || activeTexture2 >= 12) {
            net.vulkanmod.Initializer.LOGGER.error(
                    java.lang.String.format(
                            "On Texture binding: index %d out of range [0, %d]",
                            java.lang.Integer.valueOf(activeTexture2), 11));
        }
        activeTexture = activeTexture2;
    }

    public static net.vulkanmod.vulkan.texture.VulkanImage getBoundTexture() {
        return boundTextures[activeTexture];
    }

    public static net.vulkanmod.vulkan.texture.VulkanImage getBoundTexture(int i) {
        return boundTextures[i];
    }

    public static net.vulkanmod.vulkan.texture.VulkanImage getWhiteTexture() {
        return whiteTexture;
    }
}
