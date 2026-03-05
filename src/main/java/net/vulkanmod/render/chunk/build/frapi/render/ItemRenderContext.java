package net.vulkanmod.render.chunk.build.frapi.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/render/ItemRenderContext.class */
public class ItemRenderContext extends net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext {
    private static final int GLINT_COUNT = net.minecraft.client.renderer.item.ItemStackRenderState.FoilType.values().length;
    private net.minecraft.world.item.ItemDisplayContext itemDisplayContext;
    private com.mojang.blaze3d.vertex.PoseStack matrixStack;
    private net.minecraft.client.renderer.MultiBufferSource vertexConsumerProvider;
    private int lightmap;
    private int[] tints;
    private net.minecraft.client.renderer.rendertype.RenderType defaultLayer;
    private net.minecraft.client.renderer.item.ItemStackRenderState.FoilType defaultGlint;
    private boolean ignoreQuadGlint;
    private com.mojang.blaze3d.vertex.PoseStack.Pose specialGlintEntry;
    private final com.mojang.blaze3d.vertex.VertexConsumer[] vertexConsumerCache = new com.mojang.blaze3d.vertex.VertexConsumer[3 * GLINT_COUNT];

    public void renderModel(net.minecraft.world.item.ItemDisplayContext itemDisplayContext, com.mojang.blaze3d.vertex.PoseStack matrixStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, int lightmap, int overlay, int[] tints, java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> modelQuads, net.fabricmc.fabric.api.renderer.v1.mesh.MeshView mesh, net.minecraft.client.renderer.rendertype.RenderType renderType, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType foilType, boolean ignoreQuadGlint) {
        this.itemDisplayContext = itemDisplayContext;
        this.matrixStack = matrixStack;
        this.vertexConsumerProvider = bufferSource;
        this.lightmap = lightmap;
        this.overlay = overlay;
        this.tints = tints;
        this.defaultLayer = renderType;
        this.defaultGlint = foilType;
        this.ignoreQuadGlint = ignoreQuadGlint;
        bufferQuads(modelQuads, mesh);
        this.matrixStack = null;
        this.vertexConsumerProvider = null;
        this.tints = null;
        this.specialGlintEntry = null;
        java.util.Arrays.fill(this.vertexConsumerCache, (java.lang.Object) null);
    }

    private void bufferQuads(java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> vanillaQuads, net.fabricmc.fabric.api.renderer.v1.mesh.MeshView mesh) {
        net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter emitter = getEmitter();
        int vanillaQuadCount = vanillaQuads.size();
        for (int i = 0; i < vanillaQuadCount; i++) {
            net.minecraft.client.renderer.block.model.BakedQuad q = vanillaQuads.get(i);
            emitter.fromBakedQuad(q);
            emitter.emit();
        }
        mesh.outputTo(emitter);
    }

    @Override // net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext
    protected void bufferQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = getVertexConsumer(quad.renderLayer(), quad.glint());
        tintQuad(quad);
        shadeQuad(quad, quad.emissive());
        bufferQuad(quad, vertexConsumer);
    }

    private void tintQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad) {
        int tintIndex = quad.tintIndex();
        if (tintIndex != -1 && tintIndex < this.tints.length) {
            int tint = this.tints[tintIndex];
            for (int i = 0; i < 4; i++) {
                quad.color(i, net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.multiplyColor(tint, quad.color(i)));
            }
        }
    }

    private void shadeQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad, boolean emissive) {
        if (emissive) {
            for (int i = 0; i < 4; i++) {
                quad.lightmap(i, 15728880);
            }
            return;
        }
        int lightmap = this.lightmap;
        for (int i2 = 0; i2 < 4; i2++) {
            quad.lightmap(i2, net.vulkanmod.render.chunk.build.frapi.helper.ColorHelper.maxBrightness(quad.lightmap(i2), lightmap));
        }
    }

    private com.mojang.blaze3d.vertex.VertexConsumer getVertexConsumer(@org.jetbrains.annotations.Nullable net.minecraft.client.renderer.chunk.ChunkSectionLayer quadRenderLayer, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.item.ItemStackRenderState.FoilType quadGlint) {
        net.minecraft.client.renderer.rendertype.RenderType layer;
        net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint;
        int cacheIndex;
        if (quadRenderLayer == null) {
            layer = this.defaultLayer;
        } else {
            layer = net.fabricmc.fabric.api.renderer.v1.render.RenderLayerHelper.getEntityBlockLayer(quadRenderLayer);
        }
        if (this.ignoreQuadGlint || quadGlint == null) {
            glint = this.defaultGlint;
        } else {
            glint = quadGlint;
        }
        if (layer == net.minecraft.client.renderer.Sheets.translucentItemSheet()) {
            cacheIndex = 0;
        } else if (layer == net.minecraft.client.renderer.Sheets.cutoutBlockSheet()) {
            cacheIndex = GLINT_COUNT;
        } else {
            cacheIndex = 2 * GLINT_COUNT;
        }
        int cacheIndex2 = cacheIndex + glint.ordinal();
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = this.vertexConsumerCache[cacheIndex2];
        if (vertexConsumer == null) {
            vertexConsumer = createVertexConsumer(layer, glint);
            this.vertexConsumerCache[cacheIndex2] = vertexConsumer;
        }
        return vertexConsumer;
    }

    private com.mojang.blaze3d.vertex.VertexConsumer createVertexConsumer(net.minecraft.client.renderer.rendertype.RenderType layer, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint) {
        if (glint != net.minecraft.client.renderer.item.ItemStackRenderState.FoilType.SPECIAL) {
            return net.minecraft.client.renderer.entity.ItemRenderer.getFoilBuffer(this.vertexConsumerProvider, layer, true, glint != net.minecraft.client.renderer.item.ItemStackRenderState.FoilType.NONE);
        }
        if (this.specialGlintEntry == null) {
            this.specialGlintEntry = this.matrixStack.last().copy();
            if (this.itemDisplayContext == net.minecraft.world.item.ItemDisplayContext.GUI) {
                com.mojang.math.MatrixUtil.mulComponentWise(this.specialGlintEntry.pose(), 0.5f);
            } else if (this.itemDisplayContext.firstPerson()) {
                com.mojang.math.MatrixUtil.mulComponentWise(this.specialGlintEntry.pose(), 0.75f);
            }
        }
        return net.vulkanmod.mixin.render.frapi.ItemRendererAccessor.getSpecialFoilBuffer(this.vertexConsumerProvider, layer, this.specialGlintEntry);
    }
}
