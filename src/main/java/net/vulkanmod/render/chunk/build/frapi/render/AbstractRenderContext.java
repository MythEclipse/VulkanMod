package net.vulkanmod.render.chunk.build.frapi.render;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/chunk/build/frapi/render/AbstractRenderContext.class */
public abstract class AbstractRenderContext {
    private final net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl editorQuad = new net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl() { // from class: net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext.1
        {
            this.data = new int[net.vulkanmod.render.chunk.build.frapi.mesh.EncodingFormat.TOTAL_STRIDE];
            clear();
        }

        @Override // net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl
        protected void emitDirectly() {
            net.vulkanmod.render.chunk.build.frapi.render.AbstractRenderContext.this.bufferQuad(this);
        }
    };
    private final org.joml.Vector4f posVec = new org.joml.Vector4f();
    private final org.joml.Vector3f normalVec = new org.joml.Vector3f();
    protected com.mojang.blaze3d.vertex.PoseStack.Pose matrices;
    protected int overlay;

    protected abstract void bufferQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl mutableQuadViewImpl);

    protected net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter getEmitter() {
        this.editorQuad.clear();
        return this.editorQuad;
    }

    protected void bufferQuad(net.vulkanmod.render.chunk.build.frapi.mesh.MutableQuadViewImpl quad, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer) {
        org.joml.Vector4f posVec = this.posVec;
        org.joml.Vector3f normalVec = this.normalVec;
        com.mojang.blaze3d.vertex.PoseStack.Pose matrices = this.matrices;
        org.joml.Matrix4f posMatrix = matrices.pose();
        boolean useNormals = quad.hasVertexNormals();
        if (useNormals) {
            quad.populateMissingNormals();
        } else {
            matrices.transformNormal(quad.faceNormal(), normalVec);
        }
        for (int i = 0; i < 4; i++) {
            posVec.set(quad.x(i), quad.y(i), quad.z(i), 1.0f);
            posVec.mul(posMatrix);
            if (useNormals) {
                quad.copyNormal(i, normalVec);
                matrices.transformNormal(normalVec, normalVec);
            }
            vertexConsumer.addVertex(posVec.x(), posVec.y(), posVec.z(), quad.color(i), quad.u(i), quad.v(i), this.overlay, quad.lightmap(i), normalVec.x(), normalVec.y(), normalVec.z());
        }
    }
}
