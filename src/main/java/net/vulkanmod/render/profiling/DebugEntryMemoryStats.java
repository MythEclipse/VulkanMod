package net.vulkanmod.render.profiling;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/profiling/DebugEntryMemoryStats.class */
public class DebugEntryMemoryStats
        implements net.minecraft.client.gui.components.debug.DebugScreenEntry {
    private static final net.minecraft.resources.Identifier GROUP =
            net.minecraft.resources.Identifier.withDefaultNamespace("vk_memory");

    public void display(
            net.minecraft.client.gui.components.debug.DebugScreenDisplayer debugScreenDisplayer,
            @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level level,
            @org.jetbrains.annotations.Nullable
                    net.minecraft.world.level.chunk.LevelChunk levelChunk,
            @org.jetbrains.annotations.Nullable
                    net.minecraft.world.level.chunk.LevelChunk levelChunk2) {
        net.vulkanmod.render.chunk.ChunkAreaManager chunkAreaManager =
                net.vulkanmod.render.chunk.WorldRenderer.getInstance().getChunkAreaManager();
        if (chunkAreaManager != null) {
            debugScreenDisplayer.addToGroup(
                    GROUP, java.util.Arrays.asList(chunkAreaManager.getStats()));
        }
    }
}
