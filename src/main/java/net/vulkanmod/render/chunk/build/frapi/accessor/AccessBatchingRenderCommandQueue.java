package net.vulkanmod.render.chunk.build.frapi.accessor;

import java.util.List;
import net.vulkanmod.render.chunk.build.frapi.render.MeshItemCommand;

public interface AccessBatchingRenderCommandQueue {
    List<MeshItemCommand> getMeshItemCommands();
}
