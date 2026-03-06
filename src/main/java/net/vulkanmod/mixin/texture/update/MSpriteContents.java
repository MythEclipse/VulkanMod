package net.vulkanmod.mixin.texture.update;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/texture/update/MSpriteContents.class */
@org.spongepowered.asm.mixin.Mixin(
        targets =
                "net.minecraft.client.renderer.texture.SpriteContents$AnimationState")
public class MSpriteContents {

    @org.spongepowered.asm.mixin.Shadow private int subFrame;

    @org.spongepowered.asm.mixin.Shadow private int frame;

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private Object animationInfo;

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"tick"},
            at = {@org.spongepowered.asm.mixin.injection.At("HEAD")},
            cancellable = true)
    private void checkUpload(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {}
}
