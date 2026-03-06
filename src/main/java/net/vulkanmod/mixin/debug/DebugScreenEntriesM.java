package net.vulkanmod.mixin.debug;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/debug/DebugScreenEntriesM.class */
@org.spongepowered.asm.mixin.Mixin({
    net.minecraft.client.gui.components.debug.DebugScreenEntries.class
})
public abstract class DebugScreenEntriesM {
    @org.spongepowered.asm.mixin.Shadow
    public static net.minecraft.resources.Identifier register(
            net.minecraft.resources.Identifier resourceLocation,
            net.minecraft.client.gui.components.debug.DebugScreenEntry debugScreenEntry) {
        return null;
    }

    @org.spongepowered.asm.mixin.injection.Inject(
            method = {"<clinit>"},
            at = {@org.spongepowered.asm.mixin.injection.At("RETURN")})
    private static void addEntry(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        register(
                net.minecraft.resources.Identifier.fromNamespaceAndPath("vkmod", "stats"),
                new net.vulkanmod.render.profiling.DebugEntryMemoryStats());
    }
}
