package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkCommandEncoder.class */
public class VkCommandEncoder implements com.mojang.blaze3d.systems.CommandEncoder {
    private static final org.slf4j.Logger LOGGER;
    private final net.vulkanmod.render.engine.VkGpuDevice device;

    @org.jetbrains.annotations.Nullable
    private com.mojang.blaze3d.pipeline.RenderPipeline lastPipeline;

    private boolean inRenderPass;

    @org.jetbrains.annotations.Nullable private net.vulkanmod.render.engine.EGlProgram lastProgram;
    private int framebufferId = net.vulkanmod.gl.VkGlFramebuffer.genFramebufferId();
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled =
                !net.vulkanmod.render.engine.VkCommandEncoder.class.desiredAssertionStatus();
        LOGGER = com.mojang.logging.LogUtils.getLogger();
    }

    protected VkCommandEncoder(net.vulkanmod.render.engine.VkGpuDevice glDevice) {
        this.device = glDevice;
    }

    public com.mojang.blaze3d.systems.RenderPass createRenderPass(
            java.util.function.Supplier<java.lang.String> supplier,
            com.mojang.blaze3d.textures.GpuTextureView colorAttachmentView,
            java.util.OptionalInt optionalInt) {
        return createRenderPass(
                supplier, colorAttachmentView, optionalInt, null, java.util.OptionalDouble.empty());
    }

    public com.mojang.blaze3d.systems.RenderPass createRenderPass(
            java.util.function.Supplier<java.lang.String> supplier,
            com.mojang.blaze3d.textures.GpuTextureView colorAttachmentView,
            java.util.OptionalInt optionalInt,
            @org.jetbrains.annotations.Nullable
                    com.mojang.blaze3d.textures.GpuTextureView depthTexture,
            java.util.OptionalDouble optionalDouble) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before creating a new one!");
        }
        if (optionalDouble.isPresent() && depthTexture == null) {
            LOGGER.warn("Depth clear value was provided but no depth texture is being used");
        }
        if (net.minecraft.client.Minecraft.getInstance().getMainRenderTarget().getColorTexture()
                == colorAttachmentView.texture()) {
            net.vulkanmod.vulkan.Renderer.getInstance().getMainPass().rebindMainTarget();
            int j = 0;
            if (optionalInt.isPresent()) {
                int k = optionalInt.getAsInt();
                org.lwjgl.opengl.GL11.glClearColor(
                        net.minecraft.util.ARGB.redFloat(k),
                        net.minecraft.util.ARGB.greenFloat(k),
                        net.minecraft.util.ARGB.blueFloat(k),
                        net.minecraft.util.ARGB.alphaFloat(k));
                j = 0 | 16384;
            }
            if (depthTexture != null && optionalDouble.isPresent()) {
                org.lwjgl.opengl.GL11.glClearDepth(optionalDouble.getAsDouble());
                j |= 256;
            }
            if (j != 0) {
                com.mojang.blaze3d.opengl.GlStateManager._disableScissorTest();
                com.mojang.blaze3d.opengl.GlStateManager._depthMask(true);
                com.mojang.blaze3d.opengl.GlStateManager._colorMask(true, true, true, true);
                com.mojang.blaze3d.opengl.GlStateManager._clear(j);
            }
            return new net.vulkanmod.render.engine.VkRenderPass(this, depthTexture != null, true);
        }
        if (colorAttachmentView.isClosed()) {
            throw new java.lang.IllegalStateException("Color texture is closed");
        }
        if (depthTexture != null && depthTexture.isClosed()) {
            throw new java.lang.IllegalStateException("Depth texture is closed");
        }
        this.inRenderPass = true;
        com.mojang.blaze3d.textures.GpuTexture depthTexture1 =
                depthTexture != null ? depthTexture.texture() : null;
        net.vulkanmod.render.engine.VkFbo fbo =
                ((net.vulkanmod.render.engine.VkTextureView) colorAttachmentView)
                        .getFbo(depthTexture1);
        fbo.bind();
        int j2 = 0;
        if (optionalInt.isPresent()) {
            int k2 = optionalInt.getAsInt();
            org.lwjgl.opengl.GL11.glClearColor(
                    net.minecraft.util.ARGB.redFloat(k2),
                    net.minecraft.util.ARGB.greenFloat(k2),
                    net.minecraft.util.ARGB.blueFloat(k2),
                    net.minecraft.util.ARGB.alphaFloat(k2));
            j2 = 0 | 16384;
        }
        if (depthTexture != null && optionalDouble.isPresent()) {
            org.lwjgl.opengl.GL11.glClearDepth(optionalDouble.getAsDouble());
            j2 |= 256;
        }
        if (j2 != 0) {
            com.mojang.blaze3d.opengl.GlStateManager._disableScissorTest();
            com.mojang.blaze3d.opengl.GlStateManager._depthMask(true);
            com.mojang.blaze3d.opengl.GlStateManager._colorMask(true, true, true, true);
            com.mojang.blaze3d.opengl.GlStateManager._clear(j2);
        }
        com.mojang.blaze3d.opengl.GlStateManager._viewport(
                0, 0, colorAttachmentView.getWidth(0), colorAttachmentView.getHeight(0));
        this.lastPipeline = null;
        return new net.vulkanmod.render.engine.VkRenderPass(this, depthTexture != null, true);
    }

    public void clearColorTexture(
            com.mojang.blaze3d.textures.GpuTexture colorAttachment, int clearColor) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before creating a new one!");
        }
        if (net.vulkanmod.vulkan.Renderer.isRecording()) {
            if (net.minecraft.client.Minecraft.getInstance().getMainRenderTarget().getColorTexture()
                    == colorAttachment) {
                net.vulkanmod.vulkan.Renderer.getInstance().getMainPass().rebindMainTarget();
                net.vulkanmod.vulkan.VRenderSystem.setClearColor(
                        net.minecraft.util.ARGB.redFloat(clearColor),
                        net.minecraft.util.ARGB.greenFloat(clearColor),
                        net.minecraft.util.ARGB.blueFloat(clearColor),
                        net.minecraft.util.ARGB.alphaFloat(clearColor));
                net.vulkanmod.vulkan.Renderer.clearAttachments(16384);
                return;
            }
            net.vulkanmod.render.engine.VkGpuTexture vkGpuTexture =
                    (net.vulkanmod.render.engine.VkGpuTexture) colorAttachment;
            net.vulkanmod.gl.VkGlFramebuffer.bindFramebuffer(36160, this.framebufferId);
            net.vulkanmod.gl.VkGlFramebuffer.framebufferTexture2D(
                    36160, 36064, 3553, vkGpuTexture.glId(), 0);
            net.vulkanmod.gl.VkGlFramebuffer.beginRendering(
                    net.vulkanmod.gl.VkGlFramebuffer.getFramebuffer(this.framebufferId));
            net.vulkanmod.vulkan.VRenderSystem.setClearColor(
                    net.minecraft.util.ARGB.redFloat(clearColor),
                    net.minecraft.util.ARGB.greenFloat(clearColor),
                    net.minecraft.util.ARGB.blueFloat(clearColor),
                    net.minecraft.util.ARGB.alphaFloat(clearColor));
            net.vulkanmod.vulkan.Renderer.clearAttachments(16384);
            net.vulkanmod.vulkan.Renderer.getInstance().endRenderPass();
            net.vulkanmod.render.engine.VkFbo fbo =
                    ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment).getFbo(null);
            ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment).setClearColor(clearColor);
            net.vulkanmod.vulkan.framebuffer.Framebuffer boundFramebuffer =
                    net.vulkanmod.vulkan.Renderer.getInstance().getBoundFramebuffer();
            if (boundFramebuffer != null
                    && boundFramebuffer.getColorAttachment()
                            == ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment)
                                    .getVulkanImage()) {
                fbo.clearAttachments();
                return;
            }
            return;
        }
        net.vulkanmod.vulkan.queue.GraphicsQueue graphicsQueue =
                net.vulkanmod.vulkan.device.DeviceManager.getGraphicsQueue();
        net.vulkanmod.vulkan.queue.CommandPool.CommandBuffer commandBuffer =
                graphicsQueue.getCommandBuffer();
        net.vulkanmod.render.engine.VkGpuTexture vkGpuTexture2 =
                (net.vulkanmod.render.engine.VkGpuTexture) colorAttachment;
        net.vulkanmod.gl.VkGlFramebuffer glFramebuffer =
                net.vulkanmod.gl.VkGlFramebuffer.getFramebuffer(this.framebufferId);
        glFramebuffer.setAttachmentTexture(36064, vkGpuTexture2.glId());
        glFramebuffer.create();
        net.vulkanmod.vulkan.framebuffer.Framebuffer framebuffer = glFramebuffer.getFramebuffer();
        net.vulkanmod.vulkan.framebuffer.RenderPass renderPass = glFramebuffer.getRenderPass();
        org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush();
        try {
            framebuffer.beginRenderPass(commandBuffer.handle, renderPass, stack);
            if (stack != null) {
                stack.close();
            }
            net.vulkanmod.vulkan.VRenderSystem.setClearColor(
                    net.minecraft.util.ARGB.redFloat(clearColor),
                    net.minecraft.util.ARGB.greenFloat(clearColor),
                    net.minecraft.util.ARGB.blueFloat(clearColor),
                    net.minecraft.util.ARGB.alphaFloat(clearColor));
            net.vulkanmod.vulkan.Renderer.clearAttachments(
                    commandBuffer.handle,
                    16384,
                    0,
                    0,
                    framebuffer.getWidth(),
                    framebuffer.getHeight());
            renderPass.endRenderPass(commandBuffer.handle);
            long fence = graphicsQueue.submitCommands(commandBuffer);
            net.vulkanmod.vulkan.Synchronization.waitFence(fence);
        } catch (java.lang.Throwable th) {
            if (stack != null) {
                try {
                    stack.close();
                } catch (java.lang.Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void clearColorAndDepthTextures(
            com.mojang.blaze3d.textures.GpuTexture colorAttachment,
            int clearColor,
            com.mojang.blaze3d.textures.GpuTexture depthAttachment,
            double clearDepth) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before creating a new one!");
        }
        if (net.minecraft.client.Minecraft.getInstance().getMainRenderTarget().getColorTexture()
                == colorAttachment) {
            net.vulkanmod.vulkan.Renderer.getInstance().getMainPass().rebindMainTarget();
            net.vulkanmod.vulkan.VRenderSystem.clearDepth(clearDepth);
            net.vulkanmod.vulkan.VRenderSystem.setClearColor(
                    net.minecraft.util.ARGB.redFloat(clearColor),
                    net.minecraft.util.ARGB.greenFloat(clearColor),
                    net.minecraft.util.ARGB.blueFloat(clearColor),
                    net.minecraft.util.ARGB.alphaFloat(clearColor));
            net.vulkanmod.vulkan.Renderer.clearAttachments(16640);
            return;
        }
        net.vulkanmod.render.engine.VkFbo fbo =
                ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment)
                        .getFbo(depthAttachment);
        ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment).setClearColor(clearColor);
        ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment)
                .setDepthClearValue((float) clearDepth);
        net.vulkanmod.vulkan.framebuffer.Framebuffer boundFramebuffer =
                net.vulkanmod.vulkan.Renderer.getInstance().getBoundFramebuffer();
        if (boundFramebuffer != null
                && boundFramebuffer.getColorAttachment()
                        == ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment)
                                .getVulkanImage()
                && boundFramebuffer.getDepthAttachment()
                        == ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment)
                                .getVulkanImage()) {
            fbo.clearAttachments();
        }
    }

    public void clearColorAndDepthTextures(
            com.mojang.blaze3d.textures.GpuTexture colorAttachment,
            int clearColor,
            com.mojang.blaze3d.textures.GpuTexture depthAttachment,
            double clearDepth,
            int x0,
            int y0,
            int width,
            int height) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before creating a new one!");
        }
        net.vulkanmod.vulkan.VRenderSystem.clearDepth(clearDepth);
        net.vulkanmod.vulkan.VRenderSystem.setClearColor(
                net.minecraft.util.ARGB.redFloat(clearColor),
                net.minecraft.util.ARGB.greenFloat(clearColor),
                net.minecraft.util.ARGB.blueFloat(clearColor),
                net.minecraft.util.ARGB.alphaFloat(clearColor));
        int framebufferHeight = colorAttachment.getHeight(0);
        int y02 = (framebufferHeight - height) - y0;
        net.vulkanmod.vulkan.framebuffer.Framebuffer boundFramebuffer =
                net.vulkanmod.vulkan.Renderer.getInstance().getBoundFramebuffer();
        if (boundFramebuffer != null
                && boundFramebuffer.getColorAttachment()
                        == ((net.vulkanmod.render.engine.VkGpuTexture) colorAttachment)
                                .getVulkanImage()
                && boundFramebuffer.getDepthAttachment()
                        == ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment)
                                .getVulkanImage()) {
            net.vulkanmod.vulkan.Renderer.clearAttachments(16640, x0, y02, width, height);
        }
    }

    public void clearDepthTexture(
            com.mojang.blaze3d.textures.GpuTexture depthAttachment, double clearDepth) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before creating a new one!");
        }
        net.vulkanmod.vulkan.framebuffer.Framebuffer boundFramebuffer =
                net.vulkanmod.vulkan.Renderer.getInstance().getBoundFramebuffer();
        if (boundFramebuffer != null
                && boundFramebuffer.getDepthAttachment()
                        == ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment)
                                .getVulkanImage()) {
            net.vulkanmod.vulkan.VRenderSystem.clearDepth(clearDepth);
            net.vulkanmod.vulkan.Renderer.clearAttachments(256);
        } else {
            ((net.vulkanmod.render.engine.VkGpuTexture) depthAttachment)
                    .setDepthClearValue((float) clearDepth);
        }
    }

    public void writeToBuffer(
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice,
            java.nio.ByteBuffer byteBuffer) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        net.vulkanmod.render.engine.VkGpuBuffer vkGpuBuffer =
                (net.vulkanmod.render.engine.VkGpuBuffer) gpuBufferSlice.buffer();
        if (vkGpuBuffer.closed) {
            throw new java.lang.IllegalStateException("Buffer already closed");
        }
        int size = byteBuffer.remaining();
        if (((long) size) + gpuBufferSlice.offset() > vkGpuBuffer.size()) {
            long jOffset = gpuBufferSlice.offset();
            long sliceSize = gpuBufferSlice.length();
            throw new java.lang.IllegalArgumentException(
                    "Cannot write more data than this buffer can hold (attempting to write "
                            + size
                            + " bytes at offset "
                            + jOffset
                            + " to "
                            + sliceSize
                            + " slice size)");
        }
        long dstOffset = gpuBufferSlice.offset();
        net.vulkanmod.vulkan.queue.CommandPool.CommandBuffer commandBuffer =
                net.vulkanmod.vulkan.Renderer.getInstance().getTransferCb();
        net.vulkanmod.vulkan.memory.buffer.StagingBuffer stagingBuffer =
                net.vulkanmod.vulkan.Vulkan.getStagingBuffer();
        stagingBuffer.copyBuffer(size, byteBuffer);
        long srcOffset = stagingBuffer.getOffset();
        org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush();
        try {
            if (!commandBuffer.isRecording()) {
                commandBuffer.begin(stack);
            }
            org.lwjgl.vulkan.VkBufferCopy.Buffer copyRegion =
                    org.lwjgl.vulkan.VkBufferCopy.calloc(1, stack);
            copyRegion.size(size);
            copyRegion.srcOffset(srcOffset);
            copyRegion.dstOffset(dstOffset);
            org.lwjgl.vulkan.VK10.vkCmdCopyBuffer(
                    commandBuffer.handle,
                    stagingBuffer.getId(),
                    vkGpuBuffer.buffer.getId(),
                    copyRegion);
            if (stack != null) {
                stack.close();
            }
        } catch (java.lang.Throwable th) {
            if (stack != null) {
                try {
                    stack.close();
                } catch (java.lang.Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public com.mojang.blaze3d.buffers.GpuBuffer.MappedView mapBuffer(
            com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer, boolean readable, boolean writable) {
        return mapBuffer(gpuBuffer.slice(), readable, writable);
    }

    public com.mojang.blaze3d.buffers.GpuBuffer.MappedView mapBuffer(
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice,
            boolean readable,
            boolean writable) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        net.vulkanmod.render.engine.VkGpuBuffer gpuBuffer =
                (net.vulkanmod.render.engine.VkGpuBuffer) gpuBufferSlice.buffer();
        if (gpuBuffer.closed) {
            throw new java.lang.IllegalStateException("Buffer already closed");
        }
        if (!readable && !writable) {
            throw new java.lang.IllegalArgumentException("At least read or write must be true");
        }
        if (readable && (gpuBuffer.usage() & 1) == 0) {
            throw new java.lang.IllegalStateException("Buffer is not readable");
        }
        if (writable && (gpuBuffer.usage() & 2) == 0) {
            throw new java.lang.IllegalStateException("Buffer is not writable");
        }
        if (gpuBufferSlice.offset() + gpuBufferSlice.length() > gpuBuffer.size()) {
            long length = gpuBufferSlice.length();
            long jOffset = gpuBufferSlice.offset();
            long bufSize = gpuBuffer.size();
            throw new java.lang.IllegalArgumentException(
                    "Cannot map more data than this buffer can hold (attempting to map "
                            + length
                            + " bytes at offset "
                            + jOffset
                            + " from "
                            + bufSize
                            + " size buffer)");
        }
        int i = 0;
        if (readable) {
            i = 0 | 1;
        }
        if (writable) {
            int i2 = i | 34;
        }
        java.nio.ByteBuffer byteBuffer =
                org.lwjgl.system.MemoryUtil.memByteBuffer(
                        gpuBuffer.getBuffer().getDataPtr() + gpuBufferSlice.offset(),
                        (int) gpuBufferSlice.length());
        return new net.vulkanmod.render.engine.VkGpuBuffer.MappedView(0, byteBuffer);
    }

    public void copyToBuffer(
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice,
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice2) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        net.vulkanmod.render.engine.VkGpuBuffer vkGpuBuffer =
                (net.vulkanmod.render.engine.VkGpuBuffer) gpuBufferSlice.buffer();
        if (vkGpuBuffer.closed) {
            throw new java.lang.IllegalStateException("Source buffer already closed");
        }
        if ((vkGpuBuffer.usage() & 8) == 0) {
            throw new java.lang.IllegalStateException(
                    "Source buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        net.vulkanmod.render.engine.VkGpuBuffer vkGpuBuffer2 =
                (net.vulkanmod.render.engine.VkGpuBuffer) gpuBufferSlice2.buffer();
        if (vkGpuBuffer2.closed) {
            throw new java.lang.IllegalStateException("Target buffer already closed");
        }
        if ((vkGpuBuffer2.usage() & 8) == 0) {
            throw new java.lang.IllegalStateException(
                    "Target buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        if (gpuBufferSlice.length() != gpuBufferSlice2.length()) {
            long var6 = gpuBufferSlice.length();
            long len2 = gpuBufferSlice2.length();
            throw new java.lang.IllegalArgumentException(
                    "Cannot copy from slice of size "
                            + var6
                            + " to slice of size "
                            + len2
                            + ", they must be equal");
        }
        if (gpuBufferSlice.offset() + gpuBufferSlice.length() > vkGpuBuffer.size()) {
            long var5 = gpuBufferSlice.length();
            long jOffset = gpuBufferSlice.offset();
            long srcBufSize = vkGpuBuffer.size();
            throw new java.lang.IllegalArgumentException(
                    "Cannot copy more data than the source buffer holds (attempting to copy "
                            + var5
                            + " bytes at offset "
                            + jOffset
                            + " from "
                            + srcBufSize
                            + " size buffer)");
        }
        if (gpuBufferSlice2.offset() + gpuBufferSlice2.length() > vkGpuBuffer2.size()) {
            long var10002 = gpuBufferSlice2.length();
            long jOffset2 = gpuBufferSlice2.offset();
            long dstBufSize = vkGpuBuffer2.size();
            throw new java.lang.IllegalArgumentException(
                    "Cannot copy more data than the target buffer can hold (attempting to copy "
                            + var10002
                            + " bytes at offset "
                            + jOffset2
                            + " to "
                            + dstBufSize
                            + " size buffer)");
        }
        throw new java.lang.UnsupportedOperationException();
    }

    public void writeToTexture(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            com.mojang.blaze3d.platform.NativeImage nativeImage) {
        int i = gpuTexture.getWidth(0);
        int j = gpuTexture.getHeight(0);
        if (nativeImage.getWidth() != i || nativeImage.getHeight() != j) {
            throw new java.lang.IllegalArgumentException(
                    "Cannot replace texture of size "
                            + i
                            + "x"
                            + j
                            + " with image of size "
                            + nativeImage.getWidth()
                            + "x"
                            + nativeImage.getHeight());
        }
        if (gpuTexture.isClosed()) {
            throw new java.lang.IllegalStateException("Destination texture is closed");
        }
        writeToTexture(gpuTexture, nativeImage, 0, 0, 0, 0, i, j, 0, 0);
    }

    public void writeToTexture(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            com.mojang.blaze3d.platform.NativeImage nativeImage,
            int level,
            int arrayLayer,
            int xOffset,
            int yOffset,
            int width,
            int height,
            int unpackSkipPixels,
            int unpackSkipRows) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        if (level >= 0 && level < gpuTexture.getMipLevels()) {
            if (unpackSkipPixels + width > nativeImage.getWidth()
                    || unpackSkipRows + height > nativeImage.getHeight()) {
                throw new java.lang.IllegalArgumentException(
                        "Copy source ("
                                + nativeImage.getWidth()
                                + "x"
                                + nativeImage.getHeight()
                                + ") is not large enough to read a rectangle of "
                                + width
                                + "x"
                                + height
                                + " from "
                                + unpackSkipPixels
                                + "x"
                                + unpackSkipRows);
            }
            if (xOffset + width > gpuTexture.getWidth(level)
                    || yOffset + height > gpuTexture.getHeight(level)) {
                throw new java.lang.IllegalArgumentException(
                        "Dest texture ("
                                + width
                                + "x"
                                + height
                                + ") is not large enough to write a rectangle of "
                                + width
                                + "x"
                                + height
                                + " at "
                                + xOffset
                                + "x"
                                + yOffset
                                + " (at mip level "
                                + level
                                + ")");
            }
            if (gpuTexture.isClosed()) {
                throw new java.lang.IllegalStateException("Destination texture is closed");
            }
            net.vulkanmod.vulkan.texture.VTextureSelector.setActiveTexture(0);
            net.vulkanmod.gl.VkGlTexture glTexture =
                    net.vulkanmod.gl.VkGlTexture.getTexture(
                            ((com.mojang.blaze3d.opengl.GlTexture) gpuTexture).glId());
            net.vulkanmod.vulkan.texture.VTextureSelector.bindTexture(glTexture.getVulkanImage());
            net.vulkanmod.vulkan.texture.VTextureSelector.uploadSubTexture(
                    level,
                    arrayLayer,
                    width,
                    height,
                    xOffset,
                    yOffset,
                    unpackSkipRows,
                    unpackSkipPixels,
                    nativeImage.getWidth(),
                    nativeImage.getPointer());
            return;
        }
        throw new java.lang.IllegalArgumentException(
                "Invalid mipLevel " + level + ", must be >= 0 and < " + gpuTexture.getMipLevels());
    }

    public void writeToTexture(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            java.nio.ByteBuffer byteBuffer,
            com.mojang.blaze3d.platform.NativeImage.Format format,
            int level,
            int j,
            int xOffset,
            int yOffset,
            int width,
            int height) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        if (level >= 0 && level < gpuTexture.getMipLevels()) {
            if (width * height * format.components() > byteBuffer.remaining()) {
                throw new java.lang.IllegalArgumentException(
                        "Copy would overrun the source buffer (remaining length of "
                                + byteBuffer.remaining()
                                + ", but copy is "
                                + width
                                + "x"
                                + height
                                + " of format "
                                + java.lang.String.valueOf(format)
                                + ")");
            }
            if (xOffset + width > gpuTexture.getWidth(level)
                    || yOffset + height > gpuTexture.getHeight(level)) {
                throw new java.lang.IllegalArgumentException(
                        "Dest texture ("
                                + gpuTexture.getWidth(level)
                                + "x"
                                + gpuTexture.getHeight(level)
                                + ") is not large enough to write a rectangle of "
                                + width
                                + "x"
                                + height
                                + " at "
                                + xOffset
                                + "x"
                                + yOffset);
            }
            if (gpuTexture.isClosed()) {
                throw new java.lang.IllegalStateException("Destination texture is closed");
            }
            if ((gpuTexture.usage() & 1) == 0) {
                throw new java.lang.IllegalStateException(
                        "Color texture must have USAGE_COPY_DST to be a destination for a write");
            }
            if (j >= gpuTexture.getDepthOrLayers()) {
                throw new java.lang.UnsupportedOperationException(
                        "Depth or layer is out of range, must be >= 0 and < "
                                + gpuTexture.getDepthOrLayers());
            }
            com.mojang.blaze3d.opengl.GlStateManager._bindTexture(
                    ((net.vulkanmod.render.engine.VkGpuTexture) gpuTexture).id);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3314, width);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3316, 0);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3315, 0);
            com.mojang.blaze3d.opengl.GlStateManager._pixelStore(3317, format.components());
            com.mojang.blaze3d.opengl.GlStateManager._texSubImage2D(
                    3553,
                    level,
                    xOffset,
                    yOffset,
                    width,
                    height,
                    com.mojang.blaze3d.opengl.GlConst.toGl(format),
                    5121,
                    byteBuffer);
            return;
        }
        throw new java.lang.IllegalArgumentException(
                "Invalid mipLevel, must be >= 0 and < " + gpuTexture.getMipLevels());
    }

    public void copyTextureToBuffer(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer,
            long i,
            java.lang.Runnable runnable,
            int j) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        copyTextureToBuffer(
                gpuTexture,
                gpuBuffer,
                i,
                runnable,
                j,
                0,
                0,
                gpuTexture.getWidth(j),
                gpuTexture.getHeight(j));
    }

    public void copyTextureToBuffer(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer,
            long dstOffset,
            java.lang.Runnable runnable,
            int mipLevel,
            int xOffset,
            int yOffset,
            int width,
            int height) {
        net.vulkanmod.render.engine.VkGpuBuffer vkGpuBuffer =
                (net.vulkanmod.render.engine.VkGpuBuffer) gpuBuffer;
        net.vulkanmod.render.engine.VkGpuTexture vkGpuTexture =
                (net.vulkanmod.render.engine.VkGpuTexture) gpuTexture;
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        if (mipLevel < 0 || mipLevel >= gpuTexture.getMipLevels()) {
            throw new java.lang.IllegalArgumentException(
                    "Invalid mipLevel "
                            + mipLevel
                            + ", must be >= 0 and < "
                            + gpuTexture.getMipLevels());
        }
        if (((long)
                                (gpuTexture.getWidth(mipLevel)
                                        * gpuTexture.getHeight(mipLevel)
                                        * vkGpuTexture.getVulkanImage().formatSize))
                        + dstOffset
                > gpuBuffer.size()) {
            throw new java.lang.IllegalArgumentException(
                    "Buffer of size "
                            + gpuBuffer.size()
                            + " is not large enough to hold "
                            + gpuTexture.getWidth(mipLevel)
                            + "x"
                            + width
                            + " pixels ("
                            + height
                            + " bytes each) starting from offset "
                            + vkGpuTexture.getVulkanImage().formatSize);
        }
        if (xOffset + width > gpuTexture.getWidth(mipLevel)
                || yOffset + height > gpuTexture.getHeight(mipLevel)) {
            throw new java.lang.IllegalArgumentException(
                    "Copy source texture ("
                            + gpuTexture.getWidth(mipLevel)
                            + "x"
                            + gpuTexture.getHeight(mipLevel)
                            + ") is not large enough to read a rectangle of "
                            + width
                            + "x"
                            + height
                            + " from "
                            + xOffset
                            + ","
                            + yOffset);
        }
        if (gpuTexture.isClosed()) {
            throw new java.lang.IllegalStateException("Source texture is closed");
        }
        if (gpuBuffer.isClosed()) {
            throw new java.lang.IllegalStateException("Destination buffer is closed");
        }
        net.vulkanmod.vulkan.texture.ImageUtil.copyImageToBuffer(
                vkGpuTexture.getVulkanImage(),
                vkGpuBuffer.getBuffer(),
                mipLevel,
                width,
                height,
                xOffset,
                yOffset,
                (int) dstOffset,
                width,
                height);
        runnable.run();
    }

    public void copyTextureToTexture(
            com.mojang.blaze3d.textures.GpuTexture gpuTexture,
            com.mojang.blaze3d.textures.GpuTexture gpuTexture2,
            int mipLevel,
            int j,
            int k,
            int l,
            int m,
            int n,
            int o) {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        if (mipLevel >= 0
                && mipLevel < gpuTexture.getMipLevels()
                && mipLevel < gpuTexture2.getMipLevels()) {
            if (j + n > gpuTexture2.getWidth(mipLevel) || k + o > gpuTexture2.getHeight(mipLevel)) {
                throw new java.lang.IllegalArgumentException(
                        "Dest texture ("
                                + gpuTexture2.getWidth(mipLevel)
                                + "x"
                                + gpuTexture2.getHeight(mipLevel)
                                + ") is not large enough to write a rectangle of "
                                + n
                                + "x"
                                + o
                                + " at "
                                + j
                                + "x"
                                + k);
            }
            if (l + n > gpuTexture.getWidth(mipLevel) || m + o > gpuTexture.getHeight(mipLevel)) {
                throw new java.lang.IllegalArgumentException(
                        "Source texture ("
                                + gpuTexture.getWidth(mipLevel)
                                + "x"
                                + gpuTexture.getHeight(mipLevel)
                                + ") is not large enough to read a rectangle of "
                                + n
                                + "x"
                                + o
                                + " at "
                                + l
                                + "x"
                                + m);
            }
            if (gpuTexture.isClosed()) {
                throw new java.lang.IllegalStateException("Source texture is closed");
            }
            if (gpuTexture2.isClosed()) {
                throw new java.lang.IllegalStateException("Destination texture is closed");
            }
            return;
        }
        throw new java.lang.IllegalArgumentException(
                "Invalid mipLevel "
                        + mipLevel
                        + ", must be >= 0 and < "
                        + gpuTexture.getMipLevels()
                        + " and < "
                        + gpuTexture2.getMipLevels());
    }

    public com.mojang.blaze3d.buffers.GpuFence createFence() {
        if (this.inRenderPass) {
            throw new java.lang.IllegalStateException(
                    "Close the existing render pass before performing additional commands");
        }
        return new com.mojang.blaze3d.buffers
                .GpuFence() { // from class: net.vulkanmod.render.engine.VkCommandEncoder.1
            public void close() {}

            public boolean awaitCompletion(long l) {
                return true;
            }
        };
    }

    public com.mojang.blaze3d.systems.GpuQuery timerQueryBegin() {
        return null;
    }

    public void timerQueryEnd(com.mojang.blaze3d.systems.GpuQuery gpuQuery) {}

    public void presentTexture(com.mojang.blaze3d.textures.GpuTextureView gpuTexture) {
        throw new java.lang.UnsupportedOperationException();
    }

    protected <T> void executeDrawMultiple(
            net.vulkanmod.render.engine.VkRenderPass renderPass,
            java.util.Collection<com.mojang.blaze3d.systems.RenderPass.Draw<T>> collection,
            @org.jetbrains.annotations.Nullable com.mojang.blaze3d.buffers.GpuBuffer gpuBuffer,
            @org.jetbrains.annotations.Nullable
                    com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType,
            java.util.Collection<java.lang.String> collection2,
            T object) {
        if (trySetup(renderPass)) {
            if (indexType == null) {
                indexType = com.mojang.blaze3d.vertex.VertexFormat.IndexType.SHORT;
            }
            net.vulkanmod.vulkan.shader.Pipeline pipeline =
                    net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(
                                    renderPass.getPipeline())
                            .getPipeline();
            for (com.mojang.blaze3d.systems.RenderPass.Draw<T> class_10884Var : collection) {
                com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType2 =
                        class_10884Var.indexType() == null ? indexType : class_10884Var.indexType();
                renderPass.setIndexBuffer(
                        class_10884Var.indexBuffer() == null
                                ? gpuBuffer
                                : class_10884Var.indexBuffer(),
                        indexType2);
                renderPass.setVertexBuffer(class_10884Var.slot(), class_10884Var.vertexBuffer());
                if (com.mojang.blaze3d.opengl.GlRenderPass.VALIDATION) {
                    if (renderPass.indexBuffer == null) {
                        throw new java.lang.IllegalStateException("Missing index buffer");
                    }
                    if (renderPass.indexBuffer.isClosed()) {
                        throw new java.lang.IllegalStateException("Index buffer has been closed!");
                    }
                    if (renderPass.vertexBuffers[0] == null) {
                        throw new java.lang.IllegalStateException(
                                "Missing vertex buffer at slot 0");
                    }
                    if (renderPass.vertexBuffers[0].isClosed()) {
                        throw new java.lang.IllegalStateException(
                                "Vertex buffer at slot 0 has been closed!");
                    }
                }
                java.util.function.BiConsumer<
                                T, com.mojang.blaze3d.systems.RenderPass.UniformUploader>
                        biConsumer = class_10884Var.uniformUploaderConsumer();
                if (biConsumer != null) {
                    biConsumer.accept(
                            object,
                            (string, gpuBufferSlice) -> {
                                net.vulkanmod.render.engine.EGlProgram glProgram =
                                        net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(
                                                        renderPass.pipeline)
                                                .getProgram();
                                com.mojang.blaze3d.opengl.Uniform uniform =
                                        glProgram.getUniform(string);
                                if (uniform instanceof com.mojang.blaze3d.opengl.Uniform.Ubo) {
                                    com.mojang.blaze3d.opengl.Uniform.Ubo ubo =
                                            (com.mojang.blaze3d.opengl.Uniform.Ubo) uniform;
                                    try {
                                        ubo.blockBinding();
                                    } catch (java.lang.Throwable var7) {
                                        throw new java.lang.MatchException(var7.toString(), var7);
                                    }
                                }
                            });
                    net.vulkanmod.vulkan.Renderer.getInstance().uploadAndBindUBOs(pipeline);
                }
                drawFromBuffers(
                        renderPass,
                        0,
                        class_10884Var.firstIndex(),
                        class_10884Var.indexCount(),
                        indexType2,
                        renderPass.pipeline,
                        1);
            }
        }
    }

    protected void executeDraw(
            net.vulkanmod.render.engine.VkRenderPass renderPass,
            int vertexOffset,
            int firstIndex,
            int vertexCount,
            @org.jetbrains.annotations.Nullable
                    com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType,
            int instanceCount) {
        if (trySetup(renderPass)) {
            if (com.mojang.blaze3d.opengl.GlRenderPass.VALIDATION) {
                if (indexType != null) {
                    if (renderPass.indexBuffer == null) {
                        throw new java.lang.IllegalStateException("Missing index buffer");
                    }
                    if (renderPass.indexBuffer.isClosed()) {
                        throw new java.lang.IllegalStateException("Index buffer has been closed!");
                    }
                }
                if (renderPass.vertexBuffers[0] == null) {
                    throw new java.lang.IllegalStateException("Missing vertex buffer at slot 0");
                }
                if (renderPass.vertexBuffers[0].isClosed()) {
                    throw new java.lang.IllegalStateException(
                            "Vertex buffer at slot 0 has been closed!");
                }
            }
            drawFromBuffers(
                    renderPass,
                    vertexOffset,
                    firstIndex,
                    vertexCount,
                    indexType,
                    renderPass.pipeline,
                    instanceCount);
        }
    }

    public void drawFromBuffers(
            net.vulkanmod.render.engine.VkRenderPass renderPass,
            int vertexOffset,
            int firstIndex,
            int vertexCount,
            @org.jetbrains.annotations.Nullable
                    com.mojang.blaze3d.vertex.VertexFormat.IndexType indexType,
            com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline,
            int instanceCount) {
        int i;
        if (instanceCount < 1) {
            instanceCount = 1;
        }
        if (vertexOffset < 0) {
            vertexOffset = 0;
        }
        org.lwjgl.vulkan.VkCommandBuffer vkCommandBuffer =
                net.vulkanmod.vulkan.Renderer.getCommandBuffer();
        net.vulkanmod.render.engine.VkGpuBuffer vertexBuffer =
                (net.vulkanmod.render.engine.VkGpuBuffer) renderPass.vertexBuffers[0];
        org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush();
        if (vertexBuffer != null) {
            try {
                org.lwjgl.vulkan.VK11.vkCmdBindVertexBuffers(
                        vkCommandBuffer,
                        0,
                        stack.longs(vertexBuffer.buffer.getId()),
                        stack.longs(0L));
            } catch (java.lang.Throwable th) {
                if (stack != null) {
                    try {
                        stack.close();
                    } catch (java.lang.Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (renderPass.indexBuffer != null) {
            net.vulkanmod.render.engine.VkGpuBuffer indexBuffer =
                    (net.vulkanmod.render.engine.VkGpuBuffer) renderPass.indexBuffer;
            switch (net.vulkanmod.render.engine.VkCommandEncoder.AnonymousClass2
                    .$SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$IndexType[
                    indexType.ordinal()]) {
                case 1:
                    i = 0;
                    break;
                case 2:
                    i = 1;
                    break;
                default:
                    throw new java.lang.MatchException(
                            (java.lang.String) null, (java.lang.Throwable) null);
            }
            int vkIndexType = i;
            org.lwjgl.vulkan.VK11.vkCmdBindIndexBuffer(
                    vkCommandBuffer, indexBuffer.buffer.getId(), 0L, vkIndexType);
            org.lwjgl.vulkan.VK11.vkCmdDrawIndexed(
                    vkCommandBuffer, vertexCount, instanceCount, firstIndex, vertexOffset, 0);
        } else {
            net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer autoIndexBuffer =
                    net.vulkanmod.vulkan.Renderer.getDrawer()
                            .getAutoIndexBuffer(renderPipeline.getVertexFormatMode(), vertexCount);
            if (autoIndexBuffer != null) {
                int indexCount = autoIndexBuffer.getIndexCount(vertexCount);
                org.lwjgl.vulkan.VK11.vkCmdBindIndexBuffer(
                        vkCommandBuffer,
                        autoIndexBuffer.getIndexBuffer().getId(),
                        0L,
                        autoIndexBuffer.getIndexBuffer().indexType.value);
                org.lwjgl.vulkan.VK11.vkCmdDrawIndexed(
                        vkCommandBuffer, indexCount, instanceCount, firstIndex, vertexOffset, 0);
            } else {
                org.lwjgl.vulkan.VK11.vkCmdDraw(
                        vkCommandBuffer, vertexCount, instanceCount, vertexOffset, 0);
            }
        }
        if (stack != null) {
            stack.close();
        }
    }

    public boolean trySetup(net.vulkanmod.render.engine.VkRenderPass renderPass) {
        if (net.vulkanmod.render.engine.VkRenderPass.VALIDATION) {
            if (renderPass.pipeline == null) {
                throw new java.lang.IllegalStateException("Can't draw without a render pipeline");
            }
            for (com.mojang.blaze3d.pipeline.RenderPipeline.UniformDescription uniformDescription :
                    renderPass.pipeline.getUniforms()) {
                java.lang.Object object = renderPass.uniforms.get(uniformDescription.name());
                if (object == null
                        && !com.mojang.blaze3d.opengl.GlProgram.BUILT_IN_UNIFORMS.contains(
                                uniformDescription.name())) {
                    throw new java.lang.IllegalStateException(
                            "Missing uniform "
                                    + uniformDescription.name()
                                    + " (should be "
                                    + java.lang.String.valueOf(uniformDescription.type())
                                    + ")");
                }
            }
        }
        applyPipelineState(renderPass.pipeline);
        setupUniforms(renderPass);
        if (renderPass.isScissorEnabled()) {
            com.mojang.blaze3d.opengl.GlStateManager._enableScissorTest();
            com.mojang.blaze3d.opengl.GlStateManager._scissorBox(
                    renderPass.getScissorX(),
                    renderPass.getScissorY(),
                    renderPass.getScissorWidth(),
                    renderPass.getScissorHeight());
        } else {
            com.mojang.blaze3d.opengl.GlStateManager._disableScissorTest();
        }
        return bindPipeline(renderPass.pipeline);
    }

    public void setupUniforms(net.vulkanmod.render.engine.VkRenderPass renderPass) {
        com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline = renderPass.pipeline;
        net.vulkanmod.render.engine.EGlProgram glProgram =
                net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(renderPass.pipeline)
                        .getProgram();
        net.vulkanmod.vulkan.shader.Pipeline pipeline =
                net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(renderPass.pipeline)
                        .getPipeline();
        for (net.vulkanmod.vulkan.shader.descriptor.UBO ubo : pipeline.getBuffers()) {
            java.lang.String uniformName = ubo.name;
            glProgram.getUniform(uniformName);
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice =
                    renderPass.uniforms.get(uniformName);
            if (gpuBufferSlice == null) {
                ubo.setUseGlobalBuffer(true);
                ubo.setUpdate(true);
            } else {
                net.vulkanmod.render.engine.VkGpuBuffer gpuBuffer =
                        (net.vulkanmod.render.engine.VkGpuBuffer) gpuBufferSlice.buffer();
                if (!$assertionsDisabled && ubo == null) {
                    throw new java.lang.AssertionError();
                }
                ubo.setUseGlobalBuffer(false);
                ubo.getBufferSlice()
                        .set(
                                gpuBuffer.buffer,
                                (int) gpuBufferSlice.offset(),
                                (int) gpuBufferSlice.length());
            }
        }
        for (net.vulkanmod.vulkan.shader.descriptor.ImageDescriptor imageDescriptor :
                pipeline.getImageDescriptors()) {
            java.lang.String uniformName2 = imageDescriptor.name;
            int samplerIndex = imageDescriptor.imageIdx;
            net.vulkanmod.render.engine.VkRenderPass.TextureViewAndSampler textureSampler =
                    renderPass.samplers.get(uniformName2);
            if (textureSampler != null) {
                net.vulkanmod.render.engine.VkTextureView textureView = textureSampler.view();
                net.vulkanmod.render.engine.VkGpuTexture gpuTexture = textureView.texture();
                if (!gpuTexture.isClosed()) {
                    com.mojang.blaze3d.opengl.GlStateManager._activeTexture(33984 + samplerIndex);
                    com.mojang.blaze3d.opengl.GlStateManager._bindTexture(gpuTexture.id);
                    gpuTexture.getVulkanImage().setSampler(textureSampler.sampler().getId());
                }
            }
        }
    }

    public boolean bindPipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
        net.vulkanmod.vulkan.shader.Pipeline pipeline =
                net.vulkanmod.interfaces.shader.ExtendedRenderPipeline.of(renderPipeline)
                        .getPipeline();
        if (pipeline == null) {
            return false;
        }
        net.vulkanmod.vulkan.Renderer renderer = net.vulkanmod.vulkan.Renderer.getInstance();
        renderer.bindGraphicsPipeline((net.vulkanmod.vulkan.shader.GraphicsPipeline) pipeline);
        renderer.uploadAndBindUBOs(pipeline);
        return true;
    }

    public void applyPipelineState(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
        if (this.lastPipeline != renderPipeline) {
            this.lastPipeline = renderPipeline;
            if (renderPipeline.getDepthTestFunction()
                    != com.mojang.blaze3d.platform.DepthTestFunction.NO_DEPTH_TEST) {
                com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
                com.mojang.blaze3d.opengl.GlStateManager._depthFunc(
                        com.mojang.blaze3d.opengl.GlConst.toGl(
                                renderPipeline.getDepthTestFunction()));
            } else {
                com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
            }
            if (renderPipeline.isCull()) {
                com.mojang.blaze3d.opengl.GlStateManager._enableCull();
            } else {
                com.mojang.blaze3d.opengl.GlStateManager._disableCull();
            }
            if (renderPipeline.getBlendFunction().isPresent()) {
                com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
                com.mojang.blaze3d.pipeline.BlendFunction blendFunction =
                        (com.mojang.blaze3d.pipeline.BlendFunction)
                                renderPipeline.getBlendFunction().get();
                com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(
                        com.mojang.blaze3d.opengl.GlConst.toGl(blendFunction.sourceColor()),
                        com.mojang.blaze3d.opengl.GlConst.toGl(blendFunction.destColor()),
                        com.mojang.blaze3d.opengl.GlConst.toGl(blendFunction.sourceAlpha()),
                        com.mojang.blaze3d.opengl.GlConst.toGl(blendFunction.destAlpha()));
            } else {
                com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
            }
            com.mojang.blaze3d.opengl.GlStateManager._polygonMode(
                    1032, com.mojang.blaze3d.opengl.GlConst.toGl(renderPipeline.getPolygonMode()));
            com.mojang.blaze3d.opengl.GlStateManager._depthMask(renderPipeline.isWriteDepth());
            com.mojang.blaze3d.opengl.GlStateManager._colorMask(
                    renderPipeline.isWriteColor(),
                    renderPipeline.isWriteColor(),
                    renderPipeline.isWriteColor(),
                    renderPipeline.isWriteAlpha());
            if (renderPipeline.getDepthBiasConstant() == 0.0f
                    && renderPipeline.getDepthBiasScaleFactor() == 0.0f) {
                com.mojang.blaze3d.opengl.GlStateManager._disablePolygonOffset();
            } else {
                com.mojang.blaze3d.opengl.GlStateManager._polygonOffset(
                        renderPipeline.getDepthBiasScaleFactor(),
                        renderPipeline.getDepthBiasConstant());
                com.mojang.blaze3d.opengl.GlStateManager._enablePolygonOffset();
            }
            switch (net.vulkanmod.render.engine.VkCommandEncoder.AnonymousClass2
                    .$SwitchMap$com$mojang$blaze3d$platform$LogicOp[
                    renderPipeline.getColorLogic().ordinal()]) {
                case 1:
                    com.mojang.blaze3d.opengl.GlStateManager._disableColorLogicOp();
                    break;
                case 2:
                    com.mojang.blaze3d.opengl.GlStateManager._enableColorLogicOp();
                    com.mojang.blaze3d.opengl.GlStateManager._logicOp(5387);
                    break;
            }
            net.vulkanmod.vulkan.VRenderSystem.setPrimitiveTopologyGL(
                    com.mojang.blaze3d.opengl.GlConst.toGl(renderPipeline.getVertexFormatMode()));
        }
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.engine.VkCommandEncoder$2, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkCommandEncoder$2.class */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[]
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$IndexType;
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$platform$LogicOp =
                new int[com.mojang.blaze3d.platform.LogicOp.values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$platform$LogicOp[
                                com.mojang.blaze3d.platform.LogicOp.NONE.ordinal()] =
                        1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$platform$LogicOp[
                                com.mojang.blaze3d.platform.LogicOp.OR_REVERSE.ordinal()] =
                        2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$IndexType =
                    new int[com.mojang.blaze3d.vertex.VertexFormat.IndexType.values().length];
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$IndexType[
                                com.mojang.blaze3d.vertex.VertexFormat.IndexType.SHORT.ordinal()] =
                        1;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$vertex$VertexFormat$IndexType[
                                com.mojang.blaze3d.vertex.VertexFormat.IndexType.INT.ordinal()] =
                        2;
            } catch (java.lang.NoSuchFieldError e4) {
            }
        }
    }

    public void finishRenderPass(boolean forceEnd) {
        if (forceEnd) {
            net.vulkanmod.vulkan.Renderer.getInstance().endRenderPass();
        }
        this.inRenderPass = false;
    }

    protected net.vulkanmod.render.engine.VkGpuDevice getDevice() {
        return this.device;
    }
}
