package net.vulkanmod.mixin.debug;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/mixin/debug/DebugEntryMemoryM.class */
@org.spongepowered.asm.mixin.Mixin({
    net.minecraft.client.gui.components.debug.DebugEntryMemory.class
})
public abstract class DebugEntryMemoryM {

    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
    private static net.minecraft.resources.Identifier GROUP;

    @org.spongepowered.asm.mixin.Shadow
    protected static long bytesToMegabytes(long l) {
        return 0L;
    }

    @org.spongepowered.asm.mixin.Overwrite
    public void display(
            net.minecraft.client.gui.components.debug.DebugScreenDisplayer debugScreenDisplayer,
            @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level level,
            @org.jetbrains.annotations.Nullable
                    net.minecraft.world.level.chunk.LevelChunk levelChunk,
            @org.jetbrains.annotations.Nullable
                    net.minecraft.world.level.chunk.LevelChunk levelChunk2) {
        long l = java.lang.Runtime.getRuntime().maxMemory();
        long m = java.lang.Runtime.getRuntime().totalMemory();
        long n = java.lang.Runtime.getRuntime().freeMemory();
        long o = m - n;
        debugScreenDisplayer.addToGroup(
                GROUP,
                java.util.List.of(
                        java.lang.String.format(
                                java.util.Locale.ROOT,
                                "Mem: %2d%% %03d/%03dMB",
                                java.lang.Long.valueOf((o * 100) / l),
                                java.lang.Long.valueOf(bytesToMegabytes(o)),
                                java.lang.Long.valueOf(bytesToMegabytes(l))),
                        java.lang.String.format(
                                java.util.Locale.ROOT,
                                "Allocated: %2d%% %03dMB",
                                java.lang.Long.valueOf((m * 100) / l),
                                java.lang.Long.valueOf(bytesToMegabytes(m))),
                        java.lang.String.format(
                                "Off-heap: " + getOffHeapMemory() + "MB", new java.lang.Object[0]),
                        "NativeMemory: %dMB"
                                .formatted(
                                        java.lang.Integer.valueOf(
                                                net.vulkanmod.vulkan.memory.MemoryManager
                                                        .getInstance()
                                                        .getNativeMemoryMB())),
                        "DeviceMemory: %dMB"
                                .formatted(
                                        java.lang.Integer.valueOf(
                                                net.vulkanmod.vulkan.memory.MemoryManager
                                                        .getInstance()
                                                        .getAllocatedDeviceMemoryMB()))));
    }

    private long getOffHeapMemory() {
        return bytesToMegabytes(
                java.lang.management.ManagementFactory.getMemoryMXBean()
                        .getNonHeapMemoryUsage()
                        .getUsed());
    }
}
