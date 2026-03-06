package net.vulkanmod.render.engine;

import com.mojang.blaze3d.opengl.*;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VkDebugLabel {

    public void applyLabel(VkGpuBuffer glBuffer) {
    }

    public void applyLabel(VkGpuTexture glTexture) {
    }

    public void applyLabel(GlShaderModule glShaderModule) {
    }

    public void applyLabel(GlProgram glProgram) {
    }

    public void applyLabel(VertexArrayCache.VertexArray vertexArray) {
    }

    public static VkDebugLabel create(boolean bl, Set<String> set) {
        return new VkDebugLabel();
    }

    public boolean exists() {
        return true;
    }
}
