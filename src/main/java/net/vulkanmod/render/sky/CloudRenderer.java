package net.vulkanmod.render.sky;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/sky/CloudRenderer.class */
public class CloudRenderer {
    private static final net.minecraft.resources.Identifier TEXTURE_LOCATION =
            net.minecraft.resources.Identifier.withDefaultNamespace(
                    "textures/environment/clouds.png");
    private static final int DIR_NEG_Y_BIT = 1;
    private static final int DIR_POS_Y_BIT = 2;
    private static final int DIR_NEG_X_BIT = 4;
    private static final int DIR_POS_X_BIT = 8;
    private static final int DIR_NEG_Z_BIT = 16;
    private static final int DIR_POS_Z_BIT = 32;
    private static final byte Y_BELOW_CLOUDS = 0;
    private static final byte Y_ABOVE_CLOUDS = 1;
    private static final byte Y_INSIDE_CLOUDS = 2;
    private static final int CELL_WIDTH = 12;
    private static final int CELL_HEIGHT = 4;
    private net.vulkanmod.render.sky.CloudRenderer.CloudGrid cloudGrid;
    private int prevCloudX;
    private int prevCloudZ;
    private byte prevCloudY;
    private net.minecraft.client.CloudStatus prevCloudsType;
    private boolean generateClouds;
    private net.vulkanmod.render.VBO cloudBuffer;

    public CloudRenderer() {
        loadTexture();
    }

    public void loadTexture() {
        this.cloudGrid = createCloudGrid(TEXTURE_LOCATION);
    }

    public void renderClouds(
            float cloudHeight,
            int cloudColor,
            double camX,
            double camY,
            double camZ,
            long gameTime,
            float partialTicks) {
        byte yState;
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        float timeOffset = (gameTime % (((long) this.cloudGrid.width) * 400)) + partialTicks;
        double centerX = camX + ((double) (timeOffset * 0.03f));
        double centerZ = camZ + 3.9600000381469727d;
        double centerY = (cloudHeight - ((float) camY)) + 0.33f;
        int centerCellX = (int) java.lang.Math.floor(centerX / 12.0d);
        int centerCellZ = (int) java.lang.Math.floor(centerZ / 12.0d);
        if (centerY < -4.0d) {
            yState = 0;
        } else if (centerY > 0.0d) {
            yState = 1;
        } else {
            yState = 2;
        }
        if (centerCellX != this.prevCloudX
                || centerCellZ != this.prevCloudZ
                || minecraft.options.getCloudsType() != this.prevCloudsType
                || this.prevCloudY != yState
                || this.cloudBuffer == null) {
            this.prevCloudX = centerCellX;
            this.prevCloudZ = centerCellZ;
            this.prevCloudsType = minecraft.options.getCloudsType();
            this.prevCloudY = yState;
            this.generateClouds = true;
        }
        if (this.generateClouds) {
            this.generateClouds = false;
            if (this.cloudBuffer != null) {
                this.cloudBuffer.close();
            }
            resetBuffer();
            com.mojang.blaze3d.vertex.MeshData cloudsMesh =
                    buildClouds(
                            com.mojang.blaze3d.vertex.Tesselator.getInstance(),
                            centerCellX,
                            centerCellZ,
                            centerY);
            if (cloudsMesh == null) {
                return;
            }
            this.cloudBuffer = new net.vulkanmod.render.VBO(true);
            this.cloudBuffer.upload(cloudsMesh);
        }
        if (this.cloudBuffer == null) {
            return;
        }
        float xTranslation = (float) (centerX - ((double) (centerCellX * 12)));
        float yTranslation = (float) centerY;
        float zTranslation = (float) (centerZ - ((double) (centerCellZ * 12)));
        net.vulkanmod.vulkan.Renderer.getInstance().getMainPass().rebindMainTarget();
        org.joml.Matrix4fStack poseStack =
                com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        poseStack.pushMatrix();
        poseStack.translate(-xTranslation, yTranslation, -zTranslation);
        net.vulkanmod.vulkan.VRenderSystem.applyModelViewMatrix(poseStack);
        net.vulkanmod.vulkan.VRenderSystem.calculateMVP();
        net.vulkanmod.vulkan.VRenderSystem.setModelOffset(-xTranslation, 0.0f, -zTranslation);
        float r = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackR(cloudColor);
        float g = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackG(cloudColor);
        float b = net.vulkanmod.vulkan.util.ColorUtil.ARGB.unpackB(cloudColor);
        net.vulkanmod.vulkan.VRenderSystem.setShaderColor(r, g, b, 0.8f);
        net.vulkanmod.vulkan.shader.GraphicsPipeline pipeline =
                net.vulkanmod.render.PipelineManager.getCloudsPipeline();
        net.vulkanmod.vulkan.VRenderSystem.enableBlend();
        net.vulkanmod.vulkan.VRenderSystem.blendFuncSeparate(770, 771, 1, 0);
        net.vulkanmod.vulkan.VRenderSystem.enableDepthTest();
        net.vulkanmod.vulkan.VRenderSystem.depthFunc(515);
        com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
        com.mojang.blaze3d.opengl.GlStateManager._depthMask(true);
        com.mojang.blaze3d.opengl.GlStateManager._colorMask(true, true, true, true);
        com.mojang.blaze3d.opengl.GlStateManager._disablePolygonOffset();
        net.vulkanmod.vulkan.VRenderSystem.setPolygonModeGL(6914);
        net.vulkanmod.vulkan.VRenderSystem.setPrimitiveTopologyGL(4);
        boolean fastClouds = this.prevCloudsType == net.minecraft.client.CloudStatus.FAST;
        boolean insideClouds = yState == 2;
        boolean disableCull = insideClouds || (fastClouds && centerY <= 0.0d);
        if (disableCull) {
            net.vulkanmod.vulkan.VRenderSystem.disableCull();
        } else {
            net.vulkanmod.vulkan.VRenderSystem.enableCull();
        }
        if (!fastClouds) {
            net.vulkanmod.vulkan.VRenderSystem.colorMask(false, false, false, false);
            this.cloudBuffer.bind(pipeline);
            this.cloudBuffer.draw();
            net.vulkanmod.vulkan.VRenderSystem.colorMask(true, true, true, true);
        }
        this.cloudBuffer.bind(pipeline);
        this.cloudBuffer.draw();
        poseStack.popMatrix();
        net.vulkanmod.vulkan.VRenderSystem.enableCull();
        net.vulkanmod.vulkan.VRenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        net.vulkanmod.vulkan.VRenderSystem.setModelOffset(0.0f, 0.0f, 0.0f);
    }

    public void resetBuffer() {
        if (this.cloudBuffer != null) {
            this.cloudBuffer.close();
            this.cloudBuffer = null;
        }
    }

    private com.mojang.blaze3d.vertex.MeshData buildClouds(
            com.mojang.blaze3d.vertex.Tesselator tesselator,
            int centerCellX,
            int centerCellZ,
            double cloudY) {
        com.mojang.blaze3d.vertex.BufferBuilder bufferBuilder =
                tesselator.begin(
                        com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                        com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
        int cloudRange =
                java.lang.Math.min(
                                ((java.lang.Integer)
                                                net.minecraft.client.Minecraft.getInstance()
                                                        .options
                                                        .cloudRange()
                                                        .get())
                                        .intValue(),
                                128)
                        * 16;
        int renderDistance = net.minecraft.util.Mth.ceil(cloudRange / 12.0f);
        boolean insideClouds = this.prevCloudY == 2;
        if (this.prevCloudsType == net.minecraft.client.CloudStatus.FANCY) {
            for (int cellX = -renderDistance; cellX < renderDistance; cellX++) {
                for (int cellZ = -renderDistance; cellZ < renderDistance; cellZ++) {
                    int cellIdx =
                            this.cloudGrid.getWrappedIdx(centerCellX + cellX, centerCellZ + cellZ);
                    byte renderFaces = this.cloudGrid.renderFaces[cellIdx];
                    int baseColor = this.cloudGrid.pixels[cellIdx];
                    float x = cellX * 12;
                    float z = cellZ * 12;
                    if ((renderFaces & 2) != 0 && cloudY <= 0.0d) {
                        int color =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 1.0f);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 12.0f, color);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 0.0f, color);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 0.0f, color);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 12.0f, color);
                    }
                    if ((renderFaces & 1) != 0 && cloudY >= -4.0d) {
                        int color2 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 0.7f);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 12.0f, color2);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 0.0f, color2);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 0.0f, color2);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 12.0f, color2);
                    }
                    if ((renderFaces & 8) != 0 && (x < 1.0f || insideClouds)) {
                        int color3 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 0.9f);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 12.0f, color3);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 12.0f, color3);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 0.0f, color3);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 0.0f, color3);
                    }
                    if ((renderFaces & 4) != 0 && (x > -1.0f || insideClouds)) {
                        int color4 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 0.9f);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 0.0f, color4);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 0.0f, color4);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 12.0f, color4);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 12.0f, color4);
                    }
                    if ((renderFaces & 32) != 0 && (z < 1.0f || insideClouds)) {
                        int color5 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 0.8f);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 12.0f, color5);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 12.0f, color5);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 12.0f, color5);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 12.0f, color5);
                    }
                    if ((renderFaces & 16) != 0 && (z > -1.0f || insideClouds)) {
                        int color6 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor, 0.8f);
                        putVertex(bufferBuilder, x + 12.0f, 4.0f, z + 0.0f, color6);
                        putVertex(bufferBuilder, x + 12.0f, 0.0f, z + 0.0f, color6);
                        putVertex(bufferBuilder, x + 0.0f, 0.0f, z + 0.0f, color6);
                        putVertex(bufferBuilder, x + 0.0f, 4.0f, z + 0.0f, color6);
                    }
                }
            }
        } else {
            for (int cellX2 = -renderDistance; cellX2 < renderDistance; cellX2++) {
                for (int cellZ2 = -renderDistance; cellZ2 < renderDistance; cellZ2++) {
                    int cellIdx2 =
                            this.cloudGrid.getWrappedIdx(
                                    centerCellX + cellX2, centerCellZ + cellZ2);
                    byte renderFaces2 = this.cloudGrid.renderFaces[cellIdx2];
                    int baseColor2 = this.cloudGrid.pixels[cellIdx2];
                    float x2 = cellX2 * 12;
                    float z2 = cellZ2 * 12;
                    if ((renderFaces2 & 1) != 0) {
                        int color7 =
                                net.vulkanmod.vulkan.util.ColorUtil.ARGB.multiplyRGB(
                                        baseColor2, 1.0f);
                        putVertex(bufferBuilder, x2 + 0.0f, 0.0f, z2 + 12.0f, color7);
                        putVertex(bufferBuilder, x2 + 0.0f, 0.0f, z2 + 0.0f, color7);
                        putVertex(bufferBuilder, x2 + 12.0f, 0.0f, z2 + 0.0f, color7);
                        putVertex(bufferBuilder, x2 + 12.0f, 0.0f, z2 + 12.0f, color7);
                    }
                }
            }
        }
        return bufferBuilder.build();
    }

    private static void putVertex(
            com.mojang.blaze3d.vertex.BufferBuilder bufferBuilder,
            float x,
            float y,
            float z,
            int color) {
        bufferBuilder.addVertex(x, y, z).setColor(color);
    }

    private static net.vulkanmod.render.sky.CloudRenderer.CloudGrid createCloudGrid(
            net.minecraft.resources.Identifier textureLocation) {
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        try {
            net.minecraft.server.packs.resources.Resource resource =
                    resourceManager.getResourceOrThrow(textureLocation);
            java.io.InputStream inputStream = resource.open();
            try {
                com.mojang.blaze3d.platform.NativeImage image =
                        com.mojang.blaze3d.platform.NativeImage.read(inputStream);
                int width = image.getWidth();
                int height = image.getHeight();
                org.apache.commons.lang3.Validate.isTrue(
                        width == height,
                        "Image width and height must be the same",
                        new java.lang.Object[0]);
                int[] pixels = image.getPixelsABGR();
                net.vulkanmod.render.sky.CloudRenderer.CloudGrid cloudGrid =
                        new net.vulkanmod.render.sky.CloudRenderer.CloudGrid(pixels, width);
                if (inputStream != null) {
                    inputStream.close();
                }
                return cloudGrid;
            } finally {
            }
        } catch (java.io.IOException e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/sky/CloudRenderer$CloudGrid.class */
    static class CloudGrid {
        final int width;
        final int[] pixels;
        final byte[] renderFaces;

        CloudGrid(int[] pixels, int width) {
            this.pixels = pixels;
            this.width = width;
            this.renderFaces = computeRenderFaces();
        }

        byte[] computeRenderFaces() {
            byte[] renderFaces = new byte[this.pixels.length];
            for (int z = 0; z < this.width; z++) {
                for (int x = 0; x < this.width; x++) {
                    int idx = getIdx(x, z);
                    int pixel = this.pixels[idx];
                    if (hasColor(pixel)) {
                        byte faces = 3;
                        int adjPixel = getTexelWrapped(x - 1, z);
                        if (pixel != adjPixel) {
                            faces = (byte) (3 | 4);
                        }
                        int adjPixel2 = getTexelWrapped(x + 1, z);
                        if (pixel != adjPixel2) {
                            faces = (byte) (faces | 8);
                        }
                        int adjPixel3 = getTexelWrapped(x, z - 1);
                        if (pixel != adjPixel3) {
                            faces = (byte) (faces | 16);
                        }
                        int adjPixel4 = getTexelWrapped(x, z + 1);
                        if (pixel != adjPixel4) {
                            faces = (byte) (faces | 32);
                        }
                        renderFaces[idx] = faces;
                    }
                }
            }
            return renderFaces;
        }

        int getTexelWrapped(int x, int z) {
            if (x < 0) {
                x = this.width - 1;
            }
            if (x > this.width - 1) {
                x = 0;
            }
            if (z < 0) {
                z = this.width - 1;
            }
            if (z > this.width - 1) {
                z = 0;
            }
            return this.pixels[getIdx(x, z)];
        }

        int getWrappedIdx(int x, int z) {
            return getIdx(
                    java.lang.Math.floorMod(x, this.width), java.lang.Math.floorMod(z, this.width));
        }

        int getIdx(int x, int z) {
            return (z * this.width) + x;
        }

        private static boolean hasColor(int pixel) {
            return ((pixel >> 24) & 255) > 1;
        }
    }
}
