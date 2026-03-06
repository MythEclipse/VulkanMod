package net.vulkanmod.render.engine;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkConst.class */
public class VkConst {
    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static int of(com.mojang.blaze3d.textures.AddressMode addressMode)
            throws java.lang.MatchException {
        switch (net.vulkanmod.render.engine.VkConst.AnonymousClass1
                .$SwitchMap$com$mojang$blaze3d$textures$AddressMode[addressMode.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 2;
            default:
                throw new java.lang.MatchException(
                        (java.lang.String) null, (java.lang.Throwable) null);
        }
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.engine.VkConst$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/engine/VkConst$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$textures$AddressMode;
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$textures$FilterMode =
                new int[com.mojang.blaze3d.textures.FilterMode.values().length];

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$textures$FilterMode[
                                com.mojang.blaze3d.textures.FilterMode.NEAREST.ordinal()] =
                        1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$textures$FilterMode[
                                com.mojang.blaze3d.textures.FilterMode.LINEAR.ordinal()] =
                        2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            $SwitchMap$com$mojang$blaze3d$textures$AddressMode =
                    new int[com.mojang.blaze3d.textures.AddressMode.values().length];
            try {
                $SwitchMap$com$mojang$blaze3d$textures$AddressMode[
                                com.mojang.blaze3d.textures.AddressMode.REPEAT.ordinal()] =
                        1;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$textures$AddressMode[
                                com.mojang.blaze3d.textures.AddressMode.CLAMP_TO_EDGE.ordinal()] =
                        2;
            } catch (java.lang.NoSuchFieldError e4) {
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static int of(com.mojang.blaze3d.textures.FilterMode filterMode)
            throws java.lang.MatchException {
        switch (net.vulkanmod.render.engine.VkConst.AnonymousClass1
                .$SwitchMap$com$mojang$blaze3d$textures$FilterMode[filterMode.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 1;
            default:
                throw new java.lang.MatchException(
                        (java.lang.String) null, (java.lang.Throwable) null);
        }
    }
}
