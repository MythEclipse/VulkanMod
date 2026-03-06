package net.vulkanmod.mixin.texture.update;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.*;

@Mixin(LightTexture.class)
public class MLightTexture {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private GameRenderer renderer;

    @Shadow
    private boolean updateLightTexture;
    @Shadow
    private float blockLightRedFlicker;

}
