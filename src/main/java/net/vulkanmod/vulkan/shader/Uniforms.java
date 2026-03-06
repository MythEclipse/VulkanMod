package net.vulkanmod.vulkan.shader;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/vulkan/shader/Uniforms.class */
public class Uniforms {
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String, java.util.function.Supplier<java.lang.Integer>>
            vec1i_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String, java.util.function.Supplier<java.lang.Float>>
            vec1f_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String,
                    java.util.function.Supplier<net.vulkanmod.vulkan.util.MappedBuffer>>
            vec2f_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String,
                    java.util.function.Supplier<net.vulkanmod.vulkan.util.MappedBuffer>>
            vec3f_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String,
                    java.util.function.Supplier<net.vulkanmod.vulkan.util.MappedBuffer>>
            vec4f_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();
    public static it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<
                    java.lang.String,
                    java.util.function.Supplier<net.vulkanmod.vulkan.util.MappedBuffer>>
            mat4f_uniformMap = new it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap<>();

    public static void setupDefaultUniforms() {
        mat4f_uniformMap.put(
                "ModelViewMat", net.vulkanmod.vulkan.VRenderSystem::getModelViewMatrix);
        mat4f_uniformMap.put("ProjMat", net.vulkanmod.vulkan.VRenderSystem::getProjectionMatrix);
        mat4f_uniformMap.put("MVP", net.vulkanmod.vulkan.VRenderSystem::getMVP);
        mat4f_uniformMap.put("TextureMat", net.vulkanmod.vulkan.VRenderSystem::getTextureMatrix);
        vec1i_uniformMap.put(
                "EndPortalLayers",
                () -> {
                    return 15;
                });
        vec1i_uniformMap.put(
                "UseRgss",
                () -> {
                    return java.lang.Integer.valueOf(
                            net.minecraft.client.Minecraft.getInstance()
                                                    .options
                                                    .textureFiltering()
                                                    .get()
                                            == net.minecraft.client.TextureFilteringMethod.RGSS
                                    ? 1
                                    : 0);
                });
        vec1i_uniformMap.put("CurrentTime", net.vulkanmod.vulkan.VRenderSystem::getCurrentTime);
        vec1f_uniformMap.put(
                "FogStart",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().renderDistanceStart);
                });
        vec1f_uniformMap.put(
                "FogEnd",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().renderDistanceEnd);
                });
        vec1f_uniformMap.put(
                "FogEnvironmentalStart",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().environmentalStart);
                });
        vec1f_uniformMap.put(
                "FogEnvironmentalEnd",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().environmentalEnd);
                });
        vec1f_uniformMap.put(
                "FogRenderDistanceStart",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().renderDistanceStart);
                });
        vec1f_uniformMap.put(
                "FogRenderDistanceEnd",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().renderDistanceEnd);
                });
        vec1f_uniformMap.put(
                "FogSkyEnd",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().skyEnd);
                });
        vec1f_uniformMap.put(
                "FogCloudsEnd",
                () -> {
                    return java.lang.Float.valueOf(
                            net.vulkanmod.vulkan.VRenderSystem.getFogData().cloudEnd);
                });
        vec1f_uniformMap.put(
                "AlphaCutout",
                () -> {
                    return java.lang.Float.valueOf(net.vulkanmod.vulkan.VRenderSystem.alphaCutout);
                });
        vec2f_uniformMap.put("ScreenSize", net.vulkanmod.vulkan.VRenderSystem::getScreenSize);
        vec2f_uniformMap.put("TextureSize", net.vulkanmod.vulkan.VRenderSystem::getTextureSize);
        vec2f_uniformMap.put("TexelSize", net.vulkanmod.vulkan.VRenderSystem::getTexelSize);
        vec3f_uniformMap.put(
                "Light0_Direction",
                () -> {
                    return net.vulkanmod.vulkan.VRenderSystem.lightDirection0;
                });
        vec3f_uniformMap.put(
                "Light1_Direction",
                () -> {
                    return net.vulkanmod.vulkan.VRenderSystem.lightDirection1;
                });
        vec3f_uniformMap.put(
                "ModelOffset",
                () -> {
                    return net.vulkanmod.vulkan.VRenderSystem.modelOffset;
                });
        vec3f_uniformMap.put(
                "ChunkOffset",
                () -> {
                    return net.vulkanmod.vulkan.VRenderSystem.modelOffset;
                });
        vec4f_uniformMap.put("ColorModulator", net.vulkanmod.vulkan.VRenderSystem::getShaderColor);
        vec4f_uniformMap.put("FogColor", net.vulkanmod.vulkan.VRenderSystem::getShaderFogColor);
    }

    public static java.util.function.Supplier<net.vulkanmod.vulkan.util.MappedBuffer>
            getUniformSupplier(java.lang.String type, java.lang.String name) {
        switch (type) {
            case "mat4":
                return (java.util.function.Supplier) mat4f_uniformMap.get(name);
            case "vec4":
                return (java.util.function.Supplier) vec4f_uniformMap.get(name);
            case "vec3":
                return (java.util.function.Supplier) vec3f_uniformMap.get(name);
            case "vec2":
            case "ivec2":
                return (java.util.function.Supplier) vec2f_uniformMap.get(name);
            default:
                return null;
        }
    }
}
