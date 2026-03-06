package net.vulkanmod.render.chunk.build.frapi.mesh;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/mesh/EncodingFormat.class */
public final class EncodingFormat {

    static final int VERTEX_X;
    static final int VERTEX_Y;
    static final int VERTEX_Z;
    static final int VERTEX_COLOR;
    static final int VERTEX_U;
    static final int VERTEX_V;
    static final int VERTEX_LIGHTMAP;
    static final int VERTEX_NORMAL;
    public static final int VERTEX_STRIDE;
    public static final int QUAD_STRIDE;
    public static final int QUAD_STRIDE_BYTES;
    public static final int TOTAL_STRIDE;
    private static final int DIRECTION_COUNT;
    private static final int NULLABLE_DIRECTION_COUNT;
    private static final net.minecraft.client.renderer.chunk.ChunkSectionLayer[] NULLABLE_BLOCK_RENDER_LAYERS;
    private static final int NULLABLE_BLOCK_RENDER_LAYER_COUNT;
    private static final net.fabricmc.fabric.api.util.TriState[] TRI_STATES;
    private static final int TRI_STATE_COUNT;
    private static final net.minecraft.client.renderer.item.ItemStackRenderState.FoilType[] NULLABLE_GLINTS;
    private static final int NULLABLE_GLINT_COUNT;
    private static final net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode[] SHADE_MODES;
    private static final int SHADE_MODE_COUNT;
    private static final net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas[] QUAD_ATLASES;
    private static final int QUAD_ATLAS_COUNT;
    private static final int NULL_RENDER_LAYER_INDEX;
    private static final int NULL_GLINT_INDEX;

    private static final int LIGHT_BIT_OFFSET;
    private static final int NORMALS_BIT_OFFSET;
    private static final int GEOMETRY_BIT_OFFSET;
    private static final int RENDER_LAYER_BIT_OFFSET;
    private static final int EMISSIVE_BIT_OFFSET;
    private static final int DIFFUSE_BIT_OFFSET;
    private static final int AO_BIT_OFFSET;
    private static final int GLINT_BIT_OFFSET;
    private static final int SHADE_MODE_BIT_OFFSET;
    private static final int QUAD_ATLAS_BIT_OFFSET;
    private static final int TOTAL_BIT_LENGTH;
    private static final int CULL_MASK;
    private static final int LIGHT_MASK;
    private static final int NORMALS_MASK;
    private static final int GEOMETRY_MASK;
    private static final int RENDER_LAYER_MASK;
    private static final int EMISSIVE_MASK;
    private static final int DIFFUSE_MASK;
    private static final int AO_MASK;
    private static final int GLINT_MASK;
    private static final int SHADE_MODE_MASK;
    private static final int QUAD_ATLAS_MASK;

    private EncodingFormat() {
    }

    static {
        com.mojang.blaze3d.vertex.VertexFormat format = com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK;

        VERTEX_X = 4;
        VERTEX_Y = 5;
        VERTEX_Z = 6;
        VERTEX_COLOR = 7;
        VERTEX_U = 8;
        VERTEX_V = VERTEX_U + 1;
        VERTEX_LIGHTMAP = 10;
        VERTEX_NORMAL = 11;
        VERTEX_STRIDE = format.getVertexSize() / 4;
        QUAD_STRIDE = VERTEX_STRIDE * 4;
        QUAD_STRIDE_BYTES = QUAD_STRIDE * 4;
        TOTAL_STRIDE = 4 + QUAD_STRIDE;
        DIRECTION_COUNT = net.minecraft.core.Direction.values().length;
        NULLABLE_DIRECTION_COUNT = DIRECTION_COUNT + 1;
        net.minecraft.client.renderer.chunk.ChunkSectionLayer[] layers = net.minecraft.client.renderer.chunk.ChunkSectionLayer
                .values();
        NULLABLE_BLOCK_RENDER_LAYERS = java.util.Arrays.copyOf(layers, layers.length + 1);
        NULLABLE_BLOCK_RENDER_LAYER_COUNT = NULLABLE_BLOCK_RENDER_LAYERS.length;
        TRI_STATES = net.fabricmc.fabric.api.util.TriState.values();
        TRI_STATE_COUNT = TRI_STATES.length;
        net.minecraft.client.renderer.item.ItemStackRenderState.FoilType[] glints = net.minecraft.client.renderer.item.ItemStackRenderState.FoilType
                .values();
        NULLABLE_GLINTS = java.util.Arrays.copyOf(glints, glints.length + 1);
        NULLABLE_GLINT_COUNT = NULLABLE_GLINTS.length;
        SHADE_MODES = net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode.values();
        SHADE_MODE_COUNT = SHADE_MODES.length;
        QUAD_ATLASES = net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas.values();
        QUAD_ATLAS_COUNT = QUAD_ATLASES.length;
        NULL_RENDER_LAYER_INDEX = NULLABLE_BLOCK_RENDER_LAYER_COUNT - 1;
        NULL_GLINT_INDEX = NULLABLE_GLINT_COUNT - 1;
        int CULL_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(NULLABLE_DIRECTION_COUNT);
        int LIGHT_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(DIRECTION_COUNT);
        int RENDER_LAYER_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(NULLABLE_BLOCK_RENDER_LAYER_COUNT);
        int AO_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(TRI_STATE_COUNT);
        int GLINT_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(NULLABLE_GLINT_COUNT);
        int SHADE_MODE_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(SHADE_MODE_COUNT);
        int QUAD_ATLAS_BIT_LENGTH = net.minecraft.util.Mth.ceillog2(QUAD_ATLAS_COUNT);
        LIGHT_BIT_OFFSET = CULL_BIT_LENGTH;
        NORMALS_BIT_OFFSET = LIGHT_BIT_OFFSET + LIGHT_BIT_LENGTH;
        GEOMETRY_BIT_OFFSET = NORMALS_BIT_OFFSET + 4;
        RENDER_LAYER_BIT_OFFSET = GEOMETRY_BIT_OFFSET + 3;
        EMISSIVE_BIT_OFFSET = RENDER_LAYER_BIT_OFFSET + RENDER_LAYER_BIT_LENGTH;
        DIFFUSE_BIT_OFFSET = EMISSIVE_BIT_OFFSET + 1;
        AO_BIT_OFFSET = DIFFUSE_BIT_OFFSET + 1;
        GLINT_BIT_OFFSET = AO_BIT_OFFSET + AO_BIT_LENGTH;
        SHADE_MODE_BIT_OFFSET = GLINT_BIT_OFFSET + GLINT_BIT_LENGTH;
        QUAD_ATLAS_BIT_OFFSET = SHADE_MODE_BIT_OFFSET + SHADE_MODE_BIT_LENGTH;
        TOTAL_BIT_LENGTH = QUAD_ATLAS_BIT_OFFSET + QUAD_ATLAS_BIT_LENGTH;
        CULL_MASK = bitMask(CULL_BIT_LENGTH, 0);
        LIGHT_MASK = bitMask(LIGHT_BIT_LENGTH, LIGHT_BIT_OFFSET);
        NORMALS_MASK = bitMask(4, NORMALS_BIT_OFFSET);
        GEOMETRY_MASK = bitMask(3, GEOMETRY_BIT_OFFSET);
        RENDER_LAYER_MASK = bitMask(RENDER_LAYER_BIT_LENGTH, RENDER_LAYER_BIT_OFFSET);
        EMISSIVE_MASK = bitMask(1, EMISSIVE_BIT_OFFSET);
        DIFFUSE_MASK = bitMask(1, DIFFUSE_BIT_OFFSET);
        AO_MASK = bitMask(AO_BIT_LENGTH, AO_BIT_OFFSET);
        GLINT_MASK = bitMask(GLINT_BIT_LENGTH, GLINT_BIT_OFFSET);
        SHADE_MODE_MASK = bitMask(SHADE_MODE_BIT_LENGTH, SHADE_MODE_BIT_OFFSET);
        QUAD_ATLAS_MASK = bitMask(QUAD_ATLAS_BIT_LENGTH, QUAD_ATLAS_BIT_OFFSET);
        com.google.common.base.Preconditions.checkArgument(
                TOTAL_BIT_LENGTH <= 32,
                "Indigo header encoding bit count (%s) exceeds integer bit length)",
                TOTAL_STRIDE);
    }

    private static int bitMask(int bitLength, int bitOffset) {
        return ((1 << bitLength) - 1) << bitOffset;
    }

    static net.minecraft.core.Direction cullFace(int bits) {
        return net.fabricmc.fabric.api.renderer.v1.model.ModelHelper.faceFromIndex(
                (bits & CULL_MASK) >>> 0);
    }

    static int cullFace(int bits, net.minecraft.core.Direction face) {
        return (bits & (CULL_MASK ^ (-1)))
                | (net.fabricmc.fabric.api.renderer.v1.model.ModelHelper.toFaceIndex(face) << 0);
    }

    static net.minecraft.core.Direction lightFace(int bits) {
        return net.fabricmc.fabric.api.renderer.v1.model.ModelHelper.faceFromIndex(
                (bits & LIGHT_MASK) >>> LIGHT_BIT_OFFSET);
    }

    static int lightFace(int bits, net.minecraft.core.Direction face) {
        return (bits & (LIGHT_MASK ^ (-1)))
                | (net.fabricmc.fabric.api.renderer.v1.model.ModelHelper.toFaceIndex(face) << LIGHT_BIT_OFFSET);
    }

    static int normalFlags(int bits) {
        return (bits & NORMALS_MASK) >>> NORMALS_BIT_OFFSET;
    }

    static int normalFlags(int bits, int normalFlags) {
        return (bits & (NORMALS_MASK ^ (-1)))
                | ((normalFlags << NORMALS_BIT_OFFSET) & NORMALS_MASK);
    }

    static int geometryFlags(int bits) {
        return (bits & GEOMETRY_MASK) >>> GEOMETRY_BIT_OFFSET;
    }

    static int geometryFlags(int bits, int geometryFlags) {
        return (bits & (GEOMETRY_MASK ^ (-1)))
                | ((geometryFlags << GEOMETRY_BIT_OFFSET) & GEOMETRY_MASK);
    }

    static net.minecraft.client.renderer.chunk.ChunkSectionLayer renderLayer(int bits) {
        return NULLABLE_BLOCK_RENDER_LAYERS[(bits & RENDER_LAYER_MASK) >>> RENDER_LAYER_BIT_OFFSET];
    }

    static int renderLayer(
            int bits, net.minecraft.client.renderer.chunk.ChunkSectionLayer renderLayer) {
        int index = renderLayer == null ? NULL_RENDER_LAYER_INDEX : renderLayer.ordinal();
        return (bits & (RENDER_LAYER_MASK ^ (-1))) | (index << RENDER_LAYER_BIT_OFFSET);
    }

    static boolean emissive(int bits) {
        return (bits & EMISSIVE_MASK) != 0;
    }

    static int emissive(int bits, boolean emissive) {
        return emissive ? bits | EMISSIVE_MASK : bits & (EMISSIVE_MASK ^ (-1));
    }

    static boolean diffuseShade(int bits) {
        return (bits & DIFFUSE_MASK) != 0;
    }

    static int diffuseShade(int bits, boolean shade) {
        return shade ? bits | DIFFUSE_MASK : bits & (DIFFUSE_MASK ^ (-1));
    }

    static net.fabricmc.fabric.api.util.TriState ambientOcclusion(int bits) {
        return TRI_STATES[(bits & AO_MASK) >>> AO_BIT_OFFSET];
    }

    static int ambientOcclusion(int bits, net.fabricmc.fabric.api.util.TriState ao) {
        return (bits & (AO_MASK ^ (-1))) | (ao.ordinal() << AO_BIT_OFFSET);
    }

    static net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint(int bits) {
        return NULLABLE_GLINTS[(bits & GLINT_MASK) >>> GLINT_BIT_OFFSET];
    }

    static int glint(
            int bits, net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint) {
        int index = glint == null ? NULL_GLINT_INDEX : glint.ordinal();
        return (bits & (GLINT_MASK ^ (-1))) | (index << GLINT_BIT_OFFSET);
    }

    static net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode shadeMode(int bits) {
        return SHADE_MODES[(bits & SHADE_MODE_MASK) >>> SHADE_MODE_BIT_OFFSET];
    }

    static int shadeMode(int bits, net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode mode) {
        return (bits & (SHADE_MODE_MASK ^ (-1))) | (mode.ordinal() << SHADE_MODE_BIT_OFFSET);
    }

    static net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas quadAtlas(int bits) {
        return QUAD_ATLASES[(bits & QUAD_ATLAS_MASK) >>> QUAD_ATLAS_BIT_OFFSET];
    }

    static int quadAtlas(int bits, net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas quadAtlas) {
        return (bits & (QUAD_ATLAS_MASK ^ (-1))) | (quadAtlas.ordinal() << QUAD_ATLAS_BIT_OFFSET);
    }
}
