package net.vulkanmod.render.chunk.build.frapi.mesh;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/mesh/QuadViewImpl.class */
public class QuadViewImpl implements net.fabricmc.fabric.api.renderer.v1.mesh.QuadView, net.vulkanmod.render.model.quad.ModelQuadView {

    @org.jetbrains.annotations.Nullable
    protected net.minecraft.core.Direction nominalFace;
    protected int[] data;
    protected net.vulkanmod.render.chunk.cull.QuadFacing facing;
    protected boolean isGeometryInvalid = true;
    protected final org.joml.Vector3f faceNormal = new org.joml.Vector3f();
    protected int baseIndex = 0;

    public final void load() {
        this.isGeometryInvalid = false;
        this.nominalFace = lightFace();
        net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.unpackNormal(packedFaceNormal(), this.faceNormal);
        this.facing = net.vulkanmod.render.chunk.cull.QuadFacing.fromNormal(this.faceNormal);
    }

    protected final void computeGeometry() {
        if (this.isGeometryInvalid) {
            this.isGeometryInvalid = false;
            net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.computeFaceNormal(this.faceNormal, this);
            this.data[this.baseIndex + 1] = net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.packNormal(this.faceNormal);
            net.minecraft.core.Direction lightFace = net.vulkanmod.render.chunk.build.frapi.helper.GeometryHelper.lightFace(this);
            this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.lightFace(this.data[this.baseIndex + 0], lightFace);
            this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.geometryFlags(this.data[this.baseIndex + 0], net.vulkanmod.render.model.quad.ModelQuadFlags.getQuadFlags(this, lightFace));
            this.facing = net.vulkanmod.render.chunk.cull.QuadFacing.fromNormal(this.faceNormal);
        }
    }

    public final int geometryFlags() {
        computeGeometry();
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.geometryFlags(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float x(int vertexIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_X]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float y(int vertexIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_Y]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float z(int vertexIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_Z]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float posByIndex(int vertexIndex, int coordinateIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_X + coordinateIndex]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final org.joml.Vector3f copyPos(int vertexIndex, @org.jetbrains.annotations.Nullable org.joml.Vector3f target) {
        if (target == null) {
            target = new org.joml.Vector3f();
        }
        int index = this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_X;
        target.set(java.lang.Float.intBitsToFloat(this.data[index]), java.lang.Float.intBitsToFloat(this.data[index + 1]), java.lang.Float.intBitsToFloat(this.data[index + 2]));
        return target;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final int color(int vertexIndex) {
        return this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_COLOR];
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float u(int vertexIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_U]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float v(int vertexIndex) {
        return java.lang.Float.intBitsToFloat(this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_V]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final org.joml.Vector2f copyUv(int vertexIndex, @org.jetbrains.annotations.Nullable org.joml.Vector2f target) {
        if (target == null) {
            target = new org.joml.Vector2f();
        }
        int index = this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_U;
        target.set(java.lang.Float.intBitsToFloat(this.data[index]), java.lang.Float.intBitsToFloat(this.data[index + 1]));
        return target;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final int lightmap(int vertexIndex) {
        return this.data[this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_LIGHTMAP];
    }

    public final int normalFlags() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.normalFlags(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final boolean hasNormal(int vertexIndex) {
        return (normalFlags() & (1 << vertexIndex)) != 0;
    }

    public final boolean hasVertexNormals() {
        return normalFlags() != 0;
    }

    public final boolean hasAllVertexNormals() {
        return (normalFlags() & 15) == 15;
    }

    protected final int normalIndex(int vertexIndex) {
        return this.baseIndex + (vertexIndex * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE) + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_NORMAL;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float normalX(int vertexIndex) {
        if (hasNormal(vertexIndex)) {
            return net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.unpackNormalX(this.data[normalIndex(vertexIndex)]);
        }
        return Float.NaN;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float normalY(int vertexIndex) {
        if (hasNormal(vertexIndex)) {
            return net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.unpackNormalY(this.data[normalIndex(vertexIndex)]);
        }
        return Float.NaN;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final float normalZ(int vertexIndex) {
        if (hasNormal(vertexIndex)) {
            return net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.unpackNormalZ(this.data[normalIndex(vertexIndex)]);
        }
        return Float.NaN;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    @org.jetbrains.annotations.Nullable
    public final org.joml.Vector3f copyNormal(int vertexIndex, @org.jetbrains.annotations.Nullable org.joml.Vector3f target) {
        if (hasNormal(vertexIndex)) {
            if (target == null) {
                target = new org.joml.Vector3f();
            }
            int normal = this.data[normalIndex(vertexIndex)];
            net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper.unpackNormal(normal, target);
            return target;
        }
        return null;
    }

    public final int packedFaceNormal() {
        computeGeometry();
        return this.data[this.baseIndex + 1];
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final org.joml.Vector3fc faceNormal() {
        computeGeometry();
        return this.faceNormal;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView, net.vulkanmod.render.model.quad.ModelQuadView
    public final net.minecraft.core.Direction lightFace() {
        computeGeometry();
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.lightFace(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    @org.jetbrains.annotations.Nullable
    public final net.minecraft.core.Direction nominalFace() {
        return this.nominalFace;
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    @org.jetbrains.annotations.Nullable
    public final net.minecraft.core.Direction cullFace() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.cullFace(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    @org.jetbrains.annotations.Nullable
    public net.minecraft.client.renderer.chunk.ChunkSectionLayer renderLayer() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.renderLayer(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public boolean emissive() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.emissive(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public boolean diffuseShade() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.diffuseShade(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public net.fabricmc.fabric.api.util.TriState ambientOcclusion() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.ambientOcclusion(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.glint(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode shadeMode() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.shadeMode(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas atlas() {
        return net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.quadAtlas(this.data[this.baseIndex + 0]);
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final int tintIndex() {
        return this.data[this.baseIndex + 2];
    }

    @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadView
    public final int tag() {
        return this.data[this.baseIndex + 3];
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public int getFlags() {
        return geometryFlags();
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public float getX(int idx) {
        return x(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public float getY(int idx) {
        return y(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public float getZ(int idx) {
        return z(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public int getColor(int idx) {
        return color(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public float getU(int idx) {
        return u(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public float getV(int idx) {
        return v(idx);
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public int getColorIndex() {
        return tintIndex();
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public net.minecraft.core.Direction getFacingDirection() {
        return lightFace();
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public int getNormal() {
        return packedFaceNormal();
    }

    @Override // net.vulkanmod.render.model.quad.ModelQuadView
    public net.vulkanmod.render.chunk.cull.QuadFacing getQuadFacing() {
        return this.facing;
    }
}
