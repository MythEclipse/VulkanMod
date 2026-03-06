package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkSampler.class */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class VkSampler extends com.mojang.blaze3d.textures.GpuSampler {
    private final com.mojang.blaze3d.textures.AddressMode addressModeU;
    private final com.mojang.blaze3d.textures.AddressMode addressModeV;
    private final com.mojang.blaze3d.textures.FilterMode minFilter;
    private final com.mojang.blaze3d.textures.FilterMode magFilter;
    private final int maxAnisotropy;
    private final float maxLod;
    private boolean closed;
    private final long id;

    public VkSampler(
            com.mojang.blaze3d.textures.AddressMode addressModeU,
            com.mojang.blaze3d.textures.AddressMode addressModeV,
            com.mojang.blaze3d.textures.FilterMode minFilter,
            com.mojang.blaze3d.textures.FilterMode magFilter,
            int maxAnisotropy,
            java.util.OptionalDouble maxLod) {
        this.addressModeU = addressModeU;
        this.addressModeV = addressModeV;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.maxAnisotropy = maxAnisotropy;
        this.maxLod = maxLod.isPresent() ? (byte) maxLod.getAsDouble() : 1000.0f;
        this.id =
                net.vulkanmod.vulkan.texture.SamplerManager.getSampler(
                        net.vulkanmod.render.engine.VkConst.of(addressModeU),
                        net.vulkanmod.render.engine.VkConst.of(addressModeV),
                        net.vulkanmod.render.engine.VkConst.of(minFilter),
                        net.vulkanmod.render.engine.VkConst.of(magFilter),
                        1,
                        this.maxLod,
                        maxAnisotropy > 1,
                        maxAnisotropy,
                        -1);
    }

    public long getId() {
        return this.id;
    }

    public com.mojang.blaze3d.textures.AddressMode getAddressModeU() {
        return this.addressModeU;
    }

    public com.mojang.blaze3d.textures.AddressMode getAddressModeV() {
        return this.addressModeV;
    }

    public com.mojang.blaze3d.textures.FilterMode getMinFilter() {
        return this.minFilter;
    }

    public com.mojang.blaze3d.textures.FilterMode getMagFilter() {
        return this.magFilter;
    }

    public int getMaxAnisotropy() {
        return this.maxAnisotropy;
    }

    public java.util.OptionalDouble getMaxLod() {
        return null;
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }
}
