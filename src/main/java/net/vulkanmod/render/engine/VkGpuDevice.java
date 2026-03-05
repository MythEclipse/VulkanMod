package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkGpuDevice.class */
public class VkGpuDevice implements com.mojang.blaze3d.systems.GpuDevice {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private final net.vulkanmod.render.engine.VkDebugLabel debugLabels;
    private final com.mojang.blaze3d.shaders.ShaderSource defaultShaderSource;
    private final java.util.Map<com.mojang.blaze3d.pipeline.RenderPipeline, com.mojang.blaze3d.opengl.GlRenderPipeline> pipelineCache = new java.util.IdentityHashMap();
    private final java.util.Map<net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey, com.mojang.blaze3d.opengl.GlShaderModule> shaderCache = new java.util.HashMap();
    private final java.util.Set<java.lang.String> enabledExtensions = new java.util.HashSet();
    private final java.util.Map<net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey, java.lang.String> shaderSrcCache = new java.util.HashMap();
    private final int maxSupportedTextureSize = net.vulkanmod.vulkan.VRenderSystem.maxSupportedTextureSize();
    private final int uniformOffsetAlignment = (int) net.vulkanmod.vulkan.device.DeviceManager.deviceProperties.limits().minUniformBufferOffsetAlignment();
    private final net.vulkanmod.render.engine.VkCommandEncoder encoder = new net.vulkanmod.render.engine.VkCommandEncoder(this);

    public VkGpuDevice(long l, int i, boolean bl, com.mojang.blaze3d.shaders.ShaderSource shaderSource, boolean bl2) {
        this.debugLabels = net.vulkanmod.render.engine.VkDebugLabel.create(bl2, this.enabledExtensions);
        this.defaultShaderSource = shaderSource;
    }

    public net.vulkanmod.render.engine.VkDebugLabel debugLabels() {
        return this.debugLabels;
    }

    public com.mojang.blaze3d.systems.CommandEncoder createCommandEncoder() {
        return this.encoder;
    }

    public com.mojang.blaze3d.textures.GpuSampler createSampler(com.mojang.blaze3d.textures.AddressMode addressMode, com.mojang.blaze3d.textures.AddressMode addressMode2, com.mojang.blaze3d.textures.FilterMode filterMode, com.mojang.blaze3d.textures.FilterMode filterMode2, int maxAnisotropy, java.util.OptionalDouble maxLod) {
        return new net.vulkanmod.render.engine.VkSampler(addressMode, addressMode2, filterMode, filterMode2, maxAnisotropy, maxLod);
    }

    public com.mojang.blaze3d.textures.GpuTexture createTexture(@org.jetbrains.annotations.Nullable java.util.function.Supplier<java.lang.String> supplier, int usage, com.mojang.blaze3d.textures.TextureFormat textureFormat, int width, int height, int layers, int mipLevels) {
        return createTexture((!this.debugLabels.exists() || supplier == null) ? null : supplier.get(), usage, textureFormat, width, height, layers, mipLevels);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public com.mojang.blaze3d.textures.GpuTexture createTexture(@org.jetbrains.annotations.Nullable java.lang.String string, int usage, com.mojang.blaze3d.textures.TextureFormat textureFormat, int width, int height, int layers, int mipLevels) throws java.lang.MatchException {
        if (mipLevels < 1) {
            throw new java.lang.IllegalArgumentException("mipLevels must be at least 1");
        }
        int id = net.vulkanmod.gl.VkGlTexture.genTextureId();
        if (string == null) {
            string = java.lang.String.valueOf(id);
        }
        int format = net.vulkanmod.render.engine.VkGpuTexture.vkFormat(textureFormat);
        int viewType = net.vulkanmod.render.engine.VkGpuTexture.vkImageViewType(usage);
        boolean depthFormat = net.vulkanmod.vulkan.texture.VulkanImage.isDepthFormat(format);
        int attachmentUsage = depthFormat ? 32 : 16;
        net.vulkanmod.vulkan.texture.VulkanImage texture = net.vulkanmod.vulkan.texture.VulkanImage.builder(width, height).setName(string).setFormat(format).setArrayLayers(layers).setMipLevels(mipLevels).addUsage(attachmentUsage).setViewType(viewType).createVulkanImage();
        net.vulkanmod.gl.VkGlTexture vGlTexture = net.vulkanmod.gl.VkGlTexture.getTexture(id);
        vGlTexture.setVulkanImage(texture);
        net.vulkanmod.gl.VkGlTexture.bindTexture(id);
        net.vulkanmod.render.engine.VkGpuTexture glTexture = new net.vulkanmod.render.engine.VkGpuTexture(usage, string, textureFormat, width, height, layers, mipLevels, id, vGlTexture);
        this.debugLabels.applyLabel(glTexture);
        return glTexture;
    }

    public net.vulkanmod.render.engine.VkGpuTexture gpuTextureFromVulkanImage(net.vulkanmod.vulkan.texture.VulkanImage image) {
        int id = net.vulkanmod.gl.VkGlTexture.genTextureId();
        net.vulkanmod.gl.VkGlTexture glTexture = net.vulkanmod.gl.VkGlTexture.getTexture(id);
        glTexture.setVulkanImage(image);
        com.mojang.blaze3d.textures.TextureFormat textureFormat = net.vulkanmod.render.engine.VkGpuTexture.textureFormat(image.format);
        net.vulkanmod.render.engine.VkGpuTexture gpuTexture = new net.vulkanmod.render.engine.VkGpuTexture(0, image.name, textureFormat, image.width, image.height, 1, image.mipLevels, id, glTexture);
        this.debugLabels.applyLabel(gpuTexture);
        return gpuTexture;
    }

    public com.mojang.blaze3d.textures.GpuTextureView createTextureView(com.mojang.blaze3d.textures.GpuTexture gpuTexture) {
        return createTextureView(gpuTexture, 0, gpuTexture.getMipLevels());
    }

    public com.mojang.blaze3d.textures.GpuTextureView createTextureView(com.mojang.blaze3d.textures.GpuTexture gpuTexture, int startLevel, int levels) {
        if (gpuTexture.isClosed()) {
            throw new java.lang.IllegalArgumentException("Can't create texture view with closed texture");
        }
        if (startLevel >= 0 && startLevel + levels <= gpuTexture.getMipLevels()) {
            if (gpuTexture.getClass() != net.vulkanmod.render.engine.VkGpuTexture.class) {
                gpuTexture = net.vulkanmod.render.engine.VkGpuTexture.fromGlTexture((com.mojang.blaze3d.opengl.GlTexture) gpuTexture);
            }
            return new net.vulkanmod.render.engine.VkTextureView((net.vulkanmod.render.engine.VkGpuTexture) gpuTexture, startLevel, levels);
        }
        throw new java.lang.IllegalArgumentException(levels + " mip levels starting from " + startLevel + " would be out of range for texture with only " + gpuTexture.getMipLevels() + " mip levels");
    }

    public com.mojang.blaze3d.buffers.GpuBuffer createBuffer(@org.jetbrains.annotations.Nullable java.util.function.Supplier<java.lang.String> supplier, @com.mojang.blaze3d.buffers.GpuBuffer.Usage int usage, long size) {
        if (size <= 0) {
            throw new java.lang.IllegalArgumentException("Buffer size must be greater than zero");
        }
        return new net.vulkanmod.render.engine.VkGpuBuffer(this.debugLabels, supplier, usage, size);
    }

    public com.mojang.blaze3d.buffers.GpuBuffer createBuffer(@org.jetbrains.annotations.Nullable java.util.function.Supplier<java.lang.String> supplier, int usage, java.nio.ByteBuffer byteBuffer) {
        if (!byteBuffer.hasRemaining()) {
            throw new java.lang.IllegalArgumentException("Buffer source must not be empty");
        }
        net.vulkanmod.render.engine.VkGpuBuffer glBuffer = new net.vulkanmod.render.engine.VkGpuBuffer(this.debugLabels, supplier, usage, byteBuffer.remaining());
        this.encoder.writeToBuffer(glBuffer.slice(), byteBuffer);
        return glBuffer;
    }

    public java.lang.String getImplementationInformation() {
        return "Vulkan " + net.vulkanmod.vulkan.Vulkan.getDevice().vkVersion + ", " + net.vulkanmod.vulkan.Vulkan.getDevice().vendorIdString;
    }

    public java.util.List<java.lang.String> getLastDebugMessages() {
        return java.util.Collections.emptyList();
    }

    public boolean isDebuggingEnabled() {
        return false;
    }

    public java.lang.String getRenderer() {
        return "VulkanMod %s".formatted(net.vulkanmod.Initializer.getVersion());
    }

    public java.lang.String getVendor() {
        return net.vulkanmod.vulkan.Vulkan.getDevice().vendorIdString;
    }

    public java.lang.String getBackendName() {
        return "Vulkan";
    }

    public java.lang.String getVersion() {
        return net.vulkanmod.vulkan.Vulkan.getDevice().vkVersion;
    }

    private static int getMaxSupportedTextureSize() {
        int i = com.mojang.blaze3d.opengl.GlStateManager._getInteger(3379);
        int iMax = java.lang.Math.max(32768, i);
        while (true) {
            int j = iMax;
            if (j >= 1024) {
                com.mojang.blaze3d.opengl.GlStateManager._texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, (java.nio.ByteBuffer) null);
                int k = com.mojang.blaze3d.opengl.GlStateManager._getTexLevelParameter(32868, 0, 4096);
                if (k == 0) {
                    iMax = j >> 1;
                } else {
                    return j;
                }
            } else {
                int jx = java.lang.Math.max(i, 1024);
                LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", java.lang.Integer.valueOf(jx));
                return jx;
            }
        }
    }

    public int getMaxTextureSize() {
        return this.maxSupportedTextureSize;
    }

    public int getUniformOffsetAlignment() {
        return this.uniformOffsetAlignment;
    }

    public void clearPipelineCache() {
        for (com.mojang.blaze3d.opengl.GlRenderPipeline glRenderPipeline : this.pipelineCache.values()) {
            if (glRenderPipeline.program() != com.mojang.blaze3d.opengl.GlProgram.INVALID_PROGRAM) {
                glRenderPipeline.program().close();
            }
        }
        this.pipelineCache.clear();
        for (com.mojang.blaze3d.opengl.GlShaderModule glShaderModule : this.shaderCache.values()) {
            if (glShaderModule != com.mojang.blaze3d.opengl.GlShaderModule.INVALID_SHADER) {
                glShaderModule.close();
            }
        }
        this.shaderCache.clear();
    }

    public java.util.List<java.lang.String> getEnabledExtensions() {
        return new java.util.ArrayList(this.enabledExtensions);
    }

    public int getMaxSupportedAnisotropy() {
        return 16;
    }

    public void close() {
        clearPipelineCache();
    }

    protected com.mojang.blaze3d.opengl.GlShaderModule getOrCompileShader(net.minecraft.resources.Identifier resourceLocation, com.mojang.blaze3d.shaders.ShaderType shaderType, net.minecraft.client.renderer.ShaderDefines shaderDefines, java.util.function.BiFunction<net.minecraft.resources.Identifier, com.mojang.blaze3d.shaders.ShaderType, java.lang.String> biFunction) {
        net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey shaderCompilationKey = new net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey(resourceLocation, shaderType, shaderDefines);
        return this.shaderCache.computeIfAbsent(shaderCompilationKey, shaderCompilationKey2 -> {
            return compileShader(shaderCompilationKey, biFunction);
        });
    }

    protected java.lang.String getCachedShaderSrc(net.minecraft.resources.Identifier resourceLocation, com.mojang.blaze3d.shaders.ShaderType shaderType, net.minecraft.client.renderer.ShaderDefines shaderDefines, com.mojang.blaze3d.shaders.ShaderSource shaderSourceGetter) {
        net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey shaderCompilationKey = new net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey(resourceLocation, shaderType, shaderDefines);
        return this.shaderSrcCache.computeIfAbsent(shaderCompilationKey, compilationKey -> {
            java.lang.String str;
            switch (net.vulkanmod.render.engine.VkGpuDevice.AnonymousClass1.$SwitchMap$com$mojang$blaze3d$shaders$ShaderType[shaderType.ordinal()]) {
                case 1:
                    str = ".vsh";
                    break;
                case 2:
                    str = ".fsh";
                    break;
                default:
                    throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
            }
            java.lang.String shaderExtension = str;
            java.lang.String shaderName = resourceLocation.getPath() + shaderExtension;
            if (net.vulkanmod.render.shader.ShaderLoadUtil.REMAPPED_SHADERS.contains(shaderName)) {
                java.lang.String src = net.vulkanmod.render.shader.ShaderLoadUtil.getShaderSource(resourceLocation, shaderType);
                if (src == null) {
                    throw new java.lang.RuntimeException("shader: (%s) not found.".formatted(src));
                }
                return src;
            }
            return shaderSourceGetter.get(compilationKey.id, compilationKey.type);
        });
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.engine.VkGpuDevice$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkGpuDevice$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$shaders$ShaderType = new int[com.mojang.blaze3d.shaders.ShaderType.values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$shaders$ShaderType[com.mojang.blaze3d.shaders.ShaderType.VERTEX.ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$shaders$ShaderType[com.mojang.blaze3d.shaders.ShaderType.FRAGMENT.ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
        }
    }

    public com.mojang.blaze3d.pipeline.CompiledRenderPipeline precompilePipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline, @org.jetbrains.annotations.Nullable com.mojang.blaze3d.shaders.ShaderSource shaderSourceGetter) {
        try {
            compilePipeline(renderPipeline, shaderSourceGetter == null ? this.defaultShaderSource : shaderSourceGetter);
            return new net.vulkanmod.render.engine.VkGpuDevice.VkRenderPipeline(renderPipeline);
        } catch (java.lang.Exception e) {
            throw new java.lang.RuntimeException("Caught exception compiling pipeline: %s".formatted(renderPipeline.toString()), e);
        }
    }

    public void compilePipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
        compilePipeline(renderPipeline, this.defaultShaderSource);
    }

    private com.mojang.blaze3d.opengl.GlShaderModule compileShader(net.vulkanmod.render.engine.VkGpuDevice.ShaderCompilationKey shaderCompilationKey, java.util.function.BiFunction<net.minecraft.resources.Identifier, com.mojang.blaze3d.shaders.ShaderType, java.lang.String> biFunction) {
        java.lang.String string = biFunction.apply(shaderCompilationKey.id, shaderCompilationKey.type);
        if (string == null) {
            LOGGER.error("Couldn't find source for {} shader ({})", shaderCompilationKey.type, shaderCompilationKey.id);
            return com.mojang.blaze3d.opengl.GlShaderModule.INVALID_SHADER;
        }
        java.lang.String string2 = com.mojang.blaze3d.preprocessor.GlslPreprocessor.injectDefines(string, shaderCompilationKey.defines);
        int i = com.mojang.blaze3d.opengl.GlStateManager.glCreateShader(com.mojang.blaze3d.opengl.GlConst.toGl(shaderCompilationKey.type));
        com.mojang.blaze3d.opengl.GlStateManager.glShaderSource(i, string2);
        com.mojang.blaze3d.opengl.GlStateManager.glCompileShader(i);
        if (com.mojang.blaze3d.opengl.GlStateManager.glGetShaderi(i, 35713) == 0) {
            java.lang.String string3 = org.apache.commons.lang3.StringUtils.trim(com.mojang.blaze3d.opengl.GlStateManager.glGetShaderInfoLog(i, 32768));
            LOGGER.error("Couldn't compile {} shader ({}): {}", new java.lang.Object[]{shaderCompilationKey.type.getName(), shaderCompilationKey.id, string3});
            return com.mojang.blaze3d.opengl.GlShaderModule.INVALID_SHADER;
        }
        com.mojang.blaze3d.opengl.GlShaderModule glShaderModule = new com.mojang.blaze3d.opengl.GlShaderModule(i, shaderCompilationKey.id, shaderCompilationKey.type);
        this.debugLabels.applyLabel(glShaderModule);
        return glShaderModule;
    }

    private void compilePipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline, com.mojang.blaze3d.shaders.ShaderSource shaderSrcGetter) {
        java.lang.String configName;
        java.lang.String locationPath = renderPipeline.getLocation().getPath();
        if (locationPath.contains("core")) {
            configName = locationPath.split("/")[1];
        } else {
            configName = locationPath;
        }
        net.vulkanmod.vulkan.shader.Pipeline.Builder builder = new net.vulkanmod.vulkan.shader.Pipeline.Builder(renderPipeline.getVertexFormat(), configName);
        net.vulkanmod.interfaces.shader.ExtendedRenderPipeline extPipeline = net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(renderPipeline);
        net.minecraft.resources.Identifier vertexShaderLocation = renderPipeline.getVertexShader();
        net.minecraft.resources.Identifier fragmentShaderLocation = renderPipeline.getFragmentShader();
        net.minecraft.client.renderer.ShaderDefines shaderDefines = renderPipeline.getShaderDefines();
        java.lang.String vshSrc = getCachedShaderSrc(vertexShaderLocation, com.mojang.blaze3d.shaders.ShaderType.VERTEX, shaderDefines, shaderSrcGetter);
        java.lang.String fshSrc = getCachedShaderSrc(fragmentShaderLocation, com.mojang.blaze3d.shaders.ShaderType.FRAGMENT, shaderDefines, shaderSrcGetter);
        java.lang.String vshSrc2 = com.mojang.blaze3d.preprocessor.GlslPreprocessor.injectDefines(vshSrc, shaderDefines);
        java.lang.String fshSrc2 = com.mojang.blaze3d.preprocessor.GlslPreprocessor.injectDefines(fshSrc, shaderDefines);
        net.vulkanmod.vulkan.shader.converter.Lexer lexer = new net.vulkanmod.vulkan.shader.converter.Lexer(vshSrc2);
        net.vulkanmod.vulkan.shader.converter.GLSLParser parser = new net.vulkanmod.vulkan.shader.converter.GLSLParser();
        parser.setVertexFormat(renderPipeline.getVertexFormat());
        try {
            parser.parse(lexer, net.vulkanmod.vulkan.shader.converter.GLSLParser.Stage.VERTEX);
            net.vulkanmod.vulkan.shader.converter.Lexer lexer2 = new net.vulkanmod.vulkan.shader.converter.Lexer(fshSrc2);
            parser.parse(lexer2, net.vulkanmod.vulkan.shader.converter.GLSLParser.Stage.FRAGMENT);
            net.vulkanmod.vulkan.shader.descriptor.UBO[] ubos = parser.createUBOs();
            java.lang.String vshProcessed = parser.getOutput(net.vulkanmod.vulkan.shader.converter.GLSLParser.Stage.VERTEX);
            java.lang.String fshProcessed = parser.getOutput(net.vulkanmod.vulkan.shader.converter.GLSLParser.Stage.FRAGMENT);
            builder.setUniforms(java.util.Arrays.asList(ubos), parser.getSamplerList());
            builder.compileShaders(configName, vshProcessed, fshProcessed);
            try {
                net.vulkanmod.vulkan.shader.GraphicsPipeline pipeline = builder.createGraphicsPipeline();
                net.vulkanmod.render.engine.EGlProgram eGlProgram = new net.vulkanmod.render.engine.EGlProgram(1, configName);
                eGlProgram.setupUniforms(pipeline, renderPipeline.getUniforms(), renderPipeline.getSamplers());
                extPipeline.setProgram(eGlProgram);
                extPipeline.setPipeline(pipeline);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                throw new java.lang.RuntimeException("Exception while compiling pipeline %s".formatted(renderPipeline));
            }
        } catch (java.lang.Exception e2) {
            throw new java.lang.RuntimeException("Caught exception while parsing: %s".formatted(renderPipeline.toString()), e2);
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    record ShaderCompilationKey(
            net.minecraft.resources.Identifier id,
            com.mojang.blaze3d.shaders.ShaderType type,
            net.minecraft.client.renderer.ShaderDefines defines) {

        @Override
        public String toString() {
            String string = String.valueOf(this.id) + " (" + String.valueOf(this.type) + ")";
            return !this.defines.isEmpty() ? string + " with " + String.valueOf(this.defines) : string;
        }
    }

    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkGpuDevice$VkRenderPipeline.class */
    private static class VkRenderPipeline implements com.mojang.blaze3d.pipeline.CompiledRenderPipeline {
        final com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline;

        public VkRenderPipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
            this.renderPipeline = renderPipeline;
        }

        public boolean isValid() {
            return true;
        }
    }
}
