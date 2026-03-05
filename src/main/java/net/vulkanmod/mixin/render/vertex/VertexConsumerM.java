package net.vulkanmod.mixin.render.vertex;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/vertex/VertexConsumerM.class */
@org.spongepowered.asm.mixin.Mixin({com.mojang.blaze3d.vertex.VertexConsumer.class})
public interface VertexConsumerM {
    @org.spongepowered.asm.mixin.Shadow
    void addVertex(float f, float f2, float f3, int i, float f4, float f5, int i2, int i3, float f6, float f7, float f8);

    @org.spongepowered.asm.mixin.Overwrite
    default void putBulkData(com.mojang.blaze3d.vertex.PoseStack.Pose pose, net.minecraft.client.renderer.block.model.BakedQuad bakedQuad, float[] brightness, float r, float g, float b, float a, int[] lights, int overlay) {
        org.joml.Vector3fc vector3fc = bakedQuad.direction().getUnitVec3f();
        org.joml.Matrix4f matrix4f = pose.pose();
        boolean trustedNormals = ((net.vulkanmod.mixin.matrix.PoseAccessor)(Object) pose).trustedNormals();
        int packedNormal = net.vulkanmod.render.util.MathUtil.packTransformedNorm(pose.normal(), trustedNormals, vector3fc.x(), vector3fc.y(), vector3fc.z());
        int lightEmission = bakedQuad.lightEmission();
        for (int l = 0; l < 4; l++) {
            org.joml.Vector3fc quadPos = bakedQuad.position(l);
            long packedUV = bakedQuad.packedUV(l);
            float br = brightness[l];
            int color = net.vulkanmod.vulkan.util.ColorUtil.RGBA.pack(r * br, g * br, b * br, a);
            int light = net.minecraft.client.renderer.LightTexture.lightCoordsWithEmission(lights[l], lightEmission);
            float x = quadPos.x();
            float y = quadPos.y();
            float z = quadPos.z();
            float tx = net.vulkanmod.render.util.MathUtil.transformX(matrix4f, x, y, z);
            float ty = net.vulkanmod.render.util.MathUtil.transformY(matrix4f, x, y, z);
            float tz = net.vulkanmod.render.util.MathUtil.transformZ(matrix4f, x, y, z);
            float u = net.minecraft.client.model.geom.builders.UVPair.unpackU(packedUV);
            float v = net.minecraft.client.model.geom.builders.UVPair.unpackV(packedUV);
            addVertex(tx, ty, tz, color, u, v, overlay, light, net.vulkanmod.render.vertex.format.I32_SNorm.unpackX(packedNormal), net.vulkanmod.render.vertex.format.I32_SNorm.unpackY(packedNormal), net.vulkanmod.render.vertex.format.I32_SNorm.unpackZ(packedNormal));
        }
    }
}
