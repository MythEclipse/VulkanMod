package net.vulkanmod.vulkan;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/vulkan/VRenderSystem.class */
public abstract class VRenderSystem {
    private static long window;
    public static net.minecraft.client.renderer.fog.FogData fogData =
            new net.minecraft.client.renderer.fog.FogData();
    private static int currentTime;
    public static boolean depthTest = true;
    public static boolean depthMask = true;
    public static int depthFun = 515;
    public static int topology = 3;
    public static int polygonMode = 0;
    public static boolean canSetLineWidth = false;
    public static int colorMask =
            net.vulkanmod.vulkan.shader.PipelineState.ColorMask.getColorMask(
                    true, true, true, true);
    public static boolean cull = true;
    public static boolean logicOp = false;
    public static int logicOpFun = 0;
    private static final com.mojang.blaze3d.textures.GpuTextureView[] shaderTextures =
            new com.mojang.blaze3d.textures.GpuTextureView[12];
    private static final float DEFAULT_DEPTH_VALUE = 1.0f;
    public static float clearDepthValue = DEFAULT_DEPTH_VALUE;
    public static java.nio.FloatBuffer clearColor = org.lwjgl.system.MemoryUtil.memCallocFloat(4);
    public static net.vulkanmod.vulkan.util.MappedBuffer modelViewMatrix =
            new net.vulkanmod.vulkan.util.MappedBuffer(64);
    public static net.vulkanmod.vulkan.util.MappedBuffer projectionMatrix =
            new net.vulkanmod.vulkan.util.MappedBuffer(64);
    public static net.vulkanmod.vulkan.util.MappedBuffer TextureMatrix =
            new net.vulkanmod.vulkan.util.MappedBuffer(64);
    public static net.vulkanmod.vulkan.util.MappedBuffer MVP =
            new net.vulkanmod.vulkan.util.MappedBuffer(64);
    public static net.vulkanmod.vulkan.util.MappedBuffer modelOffset =
            new net.vulkanmod.vulkan.util.MappedBuffer(12);
    public static net.vulkanmod.vulkan.util.MappedBuffer lightDirection0 =
            new net.vulkanmod.vulkan.util.MappedBuffer(12);
    public static net.vulkanmod.vulkan.util.MappedBuffer lightDirection1 =
            new net.vulkanmod.vulkan.util.MappedBuffer(12);
    public static net.vulkanmod.vulkan.util.MappedBuffer shaderColor =
            new net.vulkanmod.vulkan.util.MappedBuffer(16);
    public static net.vulkanmod.vulkan.util.MappedBuffer shaderFogColor =
            new net.vulkanmod.vulkan.util.MappedBuffer(16);
    public static net.vulkanmod.vulkan.util.MappedBuffer screenSize =
            new net.vulkanmod.vulkan.util.MappedBuffer(8);
    public static net.vulkanmod.vulkan.util.MappedBuffer textureSize =
            new net.vulkanmod.vulkan.util.MappedBuffer(8);
    public static net.vulkanmod.vulkan.util.MappedBuffer texelSize =
            new net.vulkanmod.vulkan.util.MappedBuffer(8);
    public static float alphaCutout = 0.0f;
    private static boolean depthBiasEnabled = false;
    private static float depthBiasConstant = 0.0f;
    private static float depthBiasSlope = 0.0f;

    public static void initRenderer() {
        net.vulkanmod.vulkan.Vulkan.initVulkan(window);
        setShaderColor(
                DEFAULT_DEPTH_VALUE, DEFAULT_DEPTH_VALUE, DEFAULT_DEPTH_VALUE, DEFAULT_DEPTH_VALUE);
    }

    public static void setShaderTexture(
            int i,
            @org.jetbrains.annotations.Nullable
                    com.mojang.blaze3d.textures.GpuTextureView gpuTextureView) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        if (i >= 0 && i < shaderTextures.length) {
            shaderTextures[i] = gpuTextureView;
        }
    }

    @org.jetbrains.annotations.Nullable
    public static com.mojang.blaze3d.textures.GpuTextureView getShaderTexture(int i) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        if (i < 0 || i >= shaderTextures.length) {
            return null;
        }
        return shaderTextures[i];
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getScreenSize() {
        updateScreenSize();
        return screenSize;
    }

    public static void updateScreenSize() {
        com.mojang.blaze3d.platform.Window window2 =
                net.minecraft.client.Minecraft.getInstance().getWindow();
        screenSize.putFloat(0, window2.getWidth());
        screenSize.putFloat(4, window2.getHeight());
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getTextureSize() {
        return textureSize;
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getTexelSize() {
        return texelSize;
    }

    public static void setTextureSize(int width, int height) {
        textureSize.putInt(0, width);
        textureSize.putInt(4, height);
        texelSize.putFloat(0, DEFAULT_DEPTH_VALUE / width);
        texelSize.putFloat(4, DEFAULT_DEPTH_VALUE / height);
    }

    public static void setWindow(long window2) {
        window = window2;
    }

    public static java.nio.ByteBuffer getModelOffset() {
        return modelOffset.buffer;
    }

    public static int maxSupportedTextureSize() {
        return net.vulkanmod.vulkan.device.DeviceManager.deviceProperties
                .limits()
                .maxImageDimension2D();
    }

    public static void applyMVP(org.joml.Matrix4f MV, org.joml.Matrix4f P) {
        applyModelViewMatrix(MV);
        applyProjectionMatrix(P);
        calculateMVP();
    }

    public static void applyModelViewMatrix(org.joml.Matrix4f mat) {
        mat.get(modelViewMatrix.buffer.asFloatBuffer());
    }

    public static void applyProjectionMatrix(org.joml.Matrix4f mat) {
        mat.get(projectionMatrix.buffer.asFloatBuffer());
    }

    public static void applyProjectionMatrix(
            com.mojang.blaze3d.buffers.GpuBufferSlice bufferSlice) {
        long ptr =
                ((net.vulkanmod.render.engine.VkGpuBuffer) bufferSlice.buffer())
                        .getBuffer()
                        .getDataPtr();
        java.nio.ByteBuffer byteBuffer =
                org.lwjgl.system.MemoryUtil.memByteBuffer(
                        ptr + bufferSlice.offset(), (int) bufferSlice.length());
        org.joml.Matrix4f matrix4f = new org.joml.Matrix4f().set(byteBuffer);
        matrix4f.get(projectionMatrix.buffer.asFloatBuffer());
    }

    public static void calculateMVP() {
        org.joml.Matrix4f MV = new org.joml.Matrix4f(modelViewMatrix.buffer.asFloatBuffer());
        org.joml.Matrix4f P = new org.joml.Matrix4f(projectionMatrix.buffer.asFloatBuffer());
        P.mul(MV).get(MVP.buffer);
    }

    public static void setTextureMatrix(org.joml.Matrix4f mat) {
        mat.get(TextureMatrix.buffer.asFloatBuffer());
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getTextureMatrix() {
        return TextureMatrix;
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getModelViewMatrix() {
        return modelViewMatrix;
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getProjectionMatrix() {
        return projectionMatrix;
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getMVP() {
        return MVP;
    }

    public static void setModelOffset(float x, float y, float z) {
        long ptr = modelOffset.ptr;
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putFloat(ptr, x);
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putFloat(ptr + 4, y);
        net.vulkanmod.vulkan.util.VUtil.UNSAFE.putFloat(ptr + 8, z);
    }

    public static void setShaderColor(float f1, float f2, float f3, float f4) {
        net.vulkanmod.vulkan.util.ColorUtil.setRGBA_Buffer(shaderColor, f1, f2, f3, f4);
    }

    public static void setShaderFogColor(float f1, float f2, float f3, float f4) {
        net.vulkanmod.vulkan.util.ColorUtil.setRGBA_Buffer(shaderFogColor, f1, f2, f3, f4);
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getShaderColor() {
        return shaderColor;
    }

    public static net.vulkanmod.vulkan.util.MappedBuffer getShaderFogColor() {
        return shaderFogColor;
    }

    public static net.minecraft.client.renderer.fog.FogData getFogData() {
        return fogData;
    }

    public static void setCurrentTime(int currentTime2) {
        currentTime = currentTime2;
    }

    public static int getCurrentTime() {
        return currentTime;
    }

    public static void setClearColor(float f1, float f2, float f3, float f4) {
        net.vulkanmod.vulkan.util.ColorUtil.setRGBA_Buffer(clearColor, f1, f2, f3, f4);
    }

    public static void clear(int mask) {
        net.vulkanmod.vulkan.Renderer.clearAttachments(mask);
    }

    public static void clearDepth(double depth) {
        clearDepthValue = (float) depth;
    }

    public static void disableDepthTest() {
        depthTest = false;
    }

    public static void depthMask(boolean b) {
        depthMask = b;
    }

    public static void setPrimitiveTopologyGL(int mode) {
        int i;
        switch (mode) {
            case 0:
                i = 0;
                break;
            case 1:
            case 3:
                i = 1;
                break;
            case 2:
            default:
                throw new java.lang.RuntimeException(
                        java.lang.String.format(
                                "Unknown GL primitive topology: %s",
                                java.lang.Integer.valueOf(mode)));
            case 4:
            case 5:
            case 6:
                i = 3;
                break;
        }
        topology = i;
    }

    public static void setPolygonModeGL(int mode) {
        int i;
        switch (mode) {
            case 6912:
                i = 2;
                break;
            case 6913:
                i = 1;
                break;
            case 6914:
                i = 0;
                break;
            default:
                throw new java.lang.RuntimeException(
                        java.lang.String.format(
                                "Unknown GL polygon mode: %s", java.lang.Integer.valueOf(mode)));
        }
        polygonMode = i;
    }

    public static void setLineWidth(float width) {
        if (canSetLineWidth) {
            net.vulkanmod.vulkan.Renderer.setLineWidth(width);
        }
    }

    public static void colorMask(boolean b, boolean b1, boolean b2, boolean b3) {
        colorMask = net.vulkanmod.vulkan.shader.PipelineState.ColorMask.getColorMask(b, b1, b2, b3);
    }

    public static int getColorMask() {
        return colorMask;
    }

    public static void enableDepthTest() {
        depthTest = true;
    }

    public static void enableCull() {
        cull = true;
    }

    public static void disableCull() {
        cull = false;
    }

    public static void depthFunc(int depthFun2) {
        depthFun = depthFun2;
    }

    public static void enableBlend() {
        net.vulkanmod.vulkan.shader.PipelineState.blendInfo.enabled = true;
    }

    public static void disableBlend() {
        net.vulkanmod.vulkan.shader.PipelineState.blendInfo.enabled = false;
    }

    public static void blendFunc(int srcFactor, int dstFactor) {
        net.vulkanmod.vulkan.shader.PipelineState.blendInfo.setBlendFunction(srcFactor, dstFactor);
    }

    public static void blendFuncSeparate(
            int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        net.vulkanmod.vulkan.shader.PipelineState.blendInfo.setBlendFuncSeparate(
                srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
    }

    public static void blendOp(int op) {
        net.vulkanmod.vulkan.shader.PipelineState.blendInfo.setBlendOp(op);
    }

    public static void enableColorLogicOp() {
        logicOp = true;
    }

    public static void disableColorLogicOp() {
        logicOp = false;
    }

    public static void logicOp(int glLogicOp) {
        logicOpFun = glLogicOp;
    }

    public static void polygonOffset(float slope, float biasConstant) {
        if (depthBiasConstant != biasConstant || depthBiasSlope != slope) {
            depthBiasConstant = biasConstant;
            depthBiasSlope = slope;
            net.vulkanmod.vulkan.Renderer.setDepthBias(depthBiasConstant, depthBiasSlope);
        }
    }

    public static void enablePolygonOffset() {
        if (!depthBiasEnabled) {
            net.vulkanmod.vulkan.Renderer.setDepthBias(depthBiasConstant, depthBiasSlope);
            depthBiasEnabled = true;
        }
    }

    public static void disablePolygonOffset() {
        if (depthBiasEnabled) {
            net.vulkanmod.vulkan.Renderer.setDepthBias(0.0f, 0.0f);
            depthBiasEnabled = false;
        }
    }
}
