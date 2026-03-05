package net.vulkanmod.mixin.render.frapi;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/render/frapi/ItemStackRenderStateMixin.class */
@org.spongepowered.asm.mixin.Mixin({net.minecraft.client.renderer.item.ItemStackRenderState.class})
abstract class ItemStackRenderStateMixin {
    ItemStackRenderStateMixin() {
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = {"visitExtents"}, at = {@org.spongepowered.asm.mixin.injection.At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;")})
    private void afterInitVecLoad(java.util.function.Consumer<org.joml.Vector3fc> posConsumer, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local org.joml.Vector3f vec, @com.llamalad7.mixinextras.sugar.Share("pipe") com.llamalad7.mixinextras.sugar.ref.LocalRef<net.vulkanmod.render.chunk.build.frapi.render.QuadToPosPipe> pipeRef) {
        pipeRef.set(new net.vulkanmod.render.chunk.build.frapi.render.QuadToPosPipe(posConsumer, vec));
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = {"visitExtents"}, at = {@org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;setIdentity()V", shift = org.spongepowered.asm.mixin.injection.At.Shift.BEFORE)})
    private void afterLayerLoad(java.util.function.Consumer<org.joml.Vector3fc> posConsumer, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local(ordinal = 0) org.joml.Vector3f vec, @com.llamalad7.mixinextras.sugar.Local net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState layer, @com.llamalad7.mixinextras.sugar.Local org.joml.Matrix4f matrix, @com.llamalad7.mixinextras.sugar.Share("pipe") com.llamalad7.mixinextras.sugar.ref.LocalRef<net.vulkanmod.render.chunk.build.frapi.render.QuadToPosPipe> pipeRef) {
        net.vulkanmod.render.chunk.build.frapi.mesh.MutableMeshImpl mutableMesh = ((net.vulkanmod.render.chunk.build.frapi.accessor.AccessLayerRenderState) layer).getMutableMesh();
        if (mutableMesh.size() > 0) {
            net.vulkanmod.render.chunk.build.frapi.render.QuadToPosPipe pipe = (net.vulkanmod.render.chunk.build.frapi.render.QuadToPosPipe) pipeRef.get();
            pipe.matrix = matrix;
            mutableMesh.forEachMutable(pipe);
        }
    }
}
