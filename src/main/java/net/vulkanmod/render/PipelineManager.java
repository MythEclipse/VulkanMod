package net.vulkanmod.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/PipelineManager.class */
public abstract class PipelineManager {
    public static com.mojang.blaze3d.vertex.VertexFormat terrainVertexFormat;
    static net.vulkanmod.vulkan.shader.GraphicsPipeline terrainShader;
    static net.vulkanmod.vulkan.shader.GraphicsPipeline terrainShaderEarlyZ;
    static net.vulkanmod.vulkan.shader.GraphicsPipeline fastBlitPipeline;
    static net.vulkanmod.vulkan.shader.GraphicsPipeline cloudsPipeline;
    private static java.util.function.Function<net.vulkanmod.render.vertex.TerrainRenderType, net.vulkanmod.vulkan.shader.GraphicsPipeline> shaderGetter;

    public static void setTerrainVertexFormat(com.mojang.blaze3d.vertex.VertexFormat format) {
        terrainVertexFormat = format;
    }

    public static void init() {
        setTerrainVertexFormat(net.vulkanmod.render.vertex.CustomVertexFormat.COMPRESSED_TERRAIN);
        createBasicPipelines();
        setDefaultShader();
        net.vulkanmod.render.chunk.build.thread.ThreadBuilderPack.defaultTerrainBuilderConstructor();
    }

    public static void setDefaultShader() {
        setShaderGetter(renderType -> {
            return terrainShader;
        });
    }

    private static void createBasicPipelines() {
        terrainShaderEarlyZ = createPipeline("terrain_earlyz", terrainVertexFormat);
        terrainShader = createPipeline("terrain", terrainVertexFormat);
        fastBlitPipeline = createPipeline("blit", net.vulkanmod.render.vertex.CustomVertexFormat.NONE);
        cloudsPipeline = createPipeline("clouds", com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
    }

    private static net.vulkanmod.vulkan.shader.GraphicsPipeline createPipeline(java.lang.String configName, com.mojang.blaze3d.vertex.VertexFormat vertexFormat) {
        net.vulkanmod.vulkan.shader.Pipeline.Builder pipelineBuilder = new net.vulkanmod.vulkan.shader.Pipeline.Builder(vertexFormat, configName);
        java.lang.String path = net.vulkanmod.render.shader.ShaderLoadUtil.resolveShaderPath("basic");
        com.google.gson.JsonObject config = net.vulkanmod.render.shader.ShaderLoadUtil.getJsonConfig(path, configName);
        pipelineBuilder.parseBindings(config);
        net.vulkanmod.render.shader.ShaderLoadUtil.loadShaders(pipelineBuilder, config, configName, path);
        net.vulkanmod.vulkan.shader.GraphicsPipeline pipeline = pipelineBuilder.createGraphicsPipeline();
        for (net.vulkanmod.vulkan.shader.descriptor.UBO buffer : pipeline.getBuffers()) {
            buffer.setUseGlobalBuffer(true);
        }
        return pipeline;
    }

    public static net.vulkanmod.vulkan.shader.GraphicsPipeline getTerrainShader(net.vulkanmod.render.vertex.TerrainRenderType renderType) {
        return shaderGetter.apply(renderType);
    }

    public static void setShaderGetter(java.util.function.Function<net.vulkanmod.render.vertex.TerrainRenderType, net.vulkanmod.vulkan.shader.GraphicsPipeline> consumer) {
        shaderGetter = consumer;
    }

    public static net.vulkanmod.vulkan.shader.GraphicsPipeline getTerrainDirectShader(net.minecraft.client.renderer.rendertype.RenderType renderType) {
        return terrainShader;
    }

    public static net.vulkanmod.vulkan.shader.GraphicsPipeline getTerrainIndirectShader(net.minecraft.client.renderer.rendertype.RenderType renderType) {
        return terrainShaderEarlyZ;
    }

    public static net.vulkanmod.vulkan.shader.GraphicsPipeline getFastBlitPipeline() {
        return fastBlitPipeline;
    }

    public static net.vulkanmod.vulkan.shader.GraphicsPipeline getCloudsPipeline() {
        return cloudsPipeline;
    }

    public static void destroyPipelines() {
        terrainShaderEarlyZ.cleanUp();
        terrainShader.cleanUp();
        fastBlitPipeline.cleanUp();
        cloudsPipeline.cleanUp();
    }
}
