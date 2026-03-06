package net.vulkanmod.render.chunk.build.frapi.mesh;

import org.jetbrains.annotations.Nullable;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/mesh/MutableQuadViewImpl.class */
public abstract class MutableQuadViewImpl
                extends net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl
                implements net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter {
        private static final net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform NO_TRANSFORM = q -> {
                return true;
        };
        private static final int[] DEFAULT_QUAD_DATA = new int[net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE];
        private net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform activeTransform = NO_TRANSFORM;
        private final it.unimi.dsi.fastutil.objects.ObjectArrayList<net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform> transformStack = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();
        private final net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform stackTransform = q -> {
                int i = this.transformStack.size() - 1;
                while (i >= 0) {
                        int i2 = i;
                        i--;
                        if (!((net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform) this.transformStack.get(i2))
                                        .transform(q)) {
                                return false;
                        }
                }
                return true;
        };

        protected abstract void emitDirectly();

        static {
                net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad = new net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl() {
                        @Override
                        protected void emitDirectly() {
                        }
                };
                quad.data = DEFAULT_QUAD_DATA;
                quad.color(-1, -1, -1, -1);
                quad.cullFace((net.minecraft.core.Direction) null);
                quad.renderLayer((net.minecraft.client.renderer.chunk.ChunkSectionLayer) null);
                quad.diffuseShade(true);
                quad.ambientOcclusion(net.fabricmc.fabric.api.util.TriState.DEFAULT);
                quad.glint((net.minecraft.client.renderer.item.ItemStackRenderState.FoilType) null);
                quad.tintIndex(-1);
        }

        public final void clear() {
                java.lang.System.arraycopy(
                                DEFAULT_QUAD_DATA,
                                0,
                                this.data,
                                this.baseIndex,
                                net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE);
                this.isGeometryInvalid = true;
                this.nominalFace = null;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl pos(
                        int vertexIndex, float x, float y, float z) {
                int index = this.baseIndex
                                + (vertexIndex
                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_X;
                this.data[index] = java.lang.Float.floatToRawIntBits(x);
                this.data[index + 1] = java.lang.Float.floatToRawIntBits(y);
                this.data[index + 2] = java.lang.Float.floatToRawIntBits(z);
                this.isGeometryInvalid = true;
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl color(
                        int vertexIndex, int color) {
                this.data[this.baseIndex
                                + (vertexIndex
                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_COLOR] = color;
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl uv(
                        int vertexIndex, float u, float v) {
                int i = this.baseIndex
                                + (vertexIndex
                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_U;
                this.data[i] = java.lang.Float.floatToRawIntBits(u);
                this.data[i + 1] = java.lang.Float.floatToRawIntBits(v);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl lightmap(
                        int vertexIndex, int lightmap) {
                this.data[this.baseIndex
                                + (vertexIndex
                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_LIGHTMAP] = lightmap;
                return this;
        }

        protected final void normalFlags(int flags) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.normalFlags(
                                this.data[this.baseIndex + 0], flags);
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl normal(
                        int vertexIndex, float x, float y, float z) {
                normalFlags(normalFlags() | (1 << vertexIndex));
                this.data[this.baseIndex
                                + (vertexIndex
                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_NORMAL] = net.vulkanmod.render.chunk.build.frapi.helper.NormalHelper
                                                .packNormal(x, y, z);
                return this;
        }

        public final void populateMissingNormals() {
                int normalFlags = normalFlags();
                if (normalFlags == 15) {
                        return;
                }
                int packedFaceNormal = packedFaceNormal();
                for (int v = 0; v < 4; v++) {
                        if ((normalFlags & (1 << v)) == 0) {
                                this.data[this.baseIndex
                                                + (v
                                                                * net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_STRIDE)
                                                + net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.VERTEX_NORMAL] = packedFaceNormal;
                        }
                }
                normalFlags(15);
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl nominalFace(
                        @Nullable net.minecraft.core.Direction face) {
                this.nominalFace = face;
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl cullFace(
                        @Nullable net.minecraft.core.Direction face) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.cullFace(
                                this.data[this.baseIndex + 0], face);
                nominalFace(face);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl renderLayer(
                        @Nullable net.minecraft.client.renderer.chunk.ChunkSectionLayer renderLayer) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.renderLayer(
                                this.data[this.baseIndex + 0], renderLayer);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl emissive(
                        boolean emissive) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.emissive(
                                this.data[this.baseIndex + 0], emissive);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl diffuseShade(
                        boolean shade) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.diffuseShade(
                                this.data[this.baseIndex + 0], shade);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl ambientOcclusion(
                        net.fabricmc.fabric.api.util.TriState ao) {
                java.util.Objects.requireNonNull(ao, "ambient occlusion TriState may not be null");
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat
                                .ambientOcclusion(
                                                this.data[this.baseIndex + 0], ao);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl glint(
                        @Nullable net.minecraft.client.renderer.item.ItemStackRenderState.FoilType glint) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.glint(
                                this.data[this.baseIndex + 0], glint);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl shadeMode(
                        net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode mode) {
                java.util.Objects.requireNonNull(mode, "ShadeMode may not be null");
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.shadeMode(
                                this.data[this.baseIndex + 0], mode);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl atlas(
                        net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas quadAtlas) {
                this.data[this.baseIndex + 0] = net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.quadAtlas(
                                this.data[this.baseIndex + 0], quadAtlas);
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl tintIndex(
                        int tintIndex) {
                this.data[this.baseIndex + 2] = tintIndex;
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl tag(int tag) {
                this.data[this.baseIndex + 3] = tag;
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl copyFrom(
                        net.fabricmc.fabric.api.renderer.v1.mesh.QuadView quad) {
                net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl q = (net.vulkanmod.render.chunk.build.frapi.mesh.QuadViewImpl) quad;
                java.lang.System.arraycopy(
                                q.data,
                                q.baseIndex,
                                this.data,
                                this.baseIndex,
                                net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE);
                this.nominalFace = q.nominalFace;
                this.isGeometryInvalid = q.isGeometryInvalid;
                if (!this.isGeometryInvalid) {
                        this.faceNormal.set(q.faceNormal);
                }
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter,
        // net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl fromBakedQuad(
                        net.minecraft.client.renderer.block.model.BakedQuad quad) {
                pos(0, quad.position0());
                pos(1, quad.position1());
                pos(2, quad.position2());
                pos(3, quad.position3());
                color(-1, -1, -1, -1);
                long packedUV0 = quad.packedUV0();
                long packedUV1 = quad.packedUV1();
                long packedUV2 = quad.packedUV2();
                long packedUV3 = quad.packedUV3();
                uv(
                                0,
                                net.minecraft.client.model.geom.builders.UVPair.unpackU(packedUV0),
                                net.minecraft.client.model.geom.builders.UVPair.unpackV(packedUV0));
                uv(
                                1,
                                net.minecraft.client.model.geom.builders.UVPair.unpackU(packedUV1),
                                net.minecraft.client.model.geom.builders.UVPair.unpackV(packedUV1));
                uv(
                                2,
                                net.minecraft.client.model.geom.builders.UVPair.unpackU(packedUV2),
                                net.minecraft.client.model.geom.builders.UVPair.unpackV(packedUV2));
                uv(
                                3,
                                net.minecraft.client.model.geom.builders.UVPair.unpackU(packedUV3),
                                net.minecraft.client.model.geom.builders.UVPair.unpackV(packedUV3));
                int lightEmission = quad.lightEmission();
                int lightmap = net.minecraft.client.renderer.LightTexture.pack(lightEmission, lightEmission);
                lightmap(lightmap, lightmap, lightmap, lightmap);
                normalFlags(0);
                nominalFace(quad.direction());
                emissive(lightEmission == 15);
                diffuseShade(quad.shade());
                net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas atlas = net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas
                                .of(
                                                quad.sprite().atlasLocation());
                if (atlas == null) {
                        atlas = net.fabricmc.fabric.api.renderer.v1.mesh.QuadAtlas.BLOCK;
                }
                atlas(atlas);
                tintIndex(quad.tintIndex());
                return this;
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
        public void pushTransform(net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform transform) {
                if (transform == null) {
                        throw new java.lang.NullPointerException("QuadTransform cannot be null!");
                }
                this.transformStack.push(transform);
                if (this.transformStack.size() == 1) {
                        this.activeTransform = transform;
                } else if (this.transformStack.size() == 2) {
                        this.activeTransform = this.stackTransform;
                }
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
        public void popTransform() {
                this.transformStack.pop();
                if (this.transformStack.isEmpty()) {
                        this.activeTransform = NO_TRANSFORM;
                } else if (this.transformStack.size() == 1) {
                        this.activeTransform = (net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform) this.transformStack
                                        .getFirst();
                }
        }

        public final void transformAndEmit() {
                if (this.activeTransform.transform(this)) {
                        emitDirectly();
                }
        }

        @Override // net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
        public final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl emit() {
                transformAndEmit();
                clear();
                return this;
        }
}
