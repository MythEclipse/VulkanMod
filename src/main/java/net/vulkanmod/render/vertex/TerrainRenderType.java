package net.vulkanmod.render.vertex;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/vertex/TerrainRenderType.class */
public enum TerrainRenderType {
    SOLID(0.0f),
    CUTOUT(0.5f),
    TRANSLUCENT(0.01f),
    TRIPWIRE(0.1f);

    private static java.util.function.Function<net.vulkanmod.render.vertex.TerrainRenderType, net.vulkanmod.render.vertex.TerrainRenderType> remapper;
    public final float alphaCutout;
    public static final net.vulkanmod.render.vertex.TerrainRenderType[] VALUES = values();
    public static final java.util.EnumSet<net.vulkanmod.render.vertex.TerrainRenderType> COMPACT_RENDER_TYPES = java.util.EnumSet.of(SOLID, CUTOUT, TRANSLUCENT);
    public static final java.util.EnumSet<net.vulkanmod.render.vertex.TerrainRenderType> SEMI_COMPACT_RENDER_TYPES = java.util.EnumSet.of(SOLID, CUTOUT, TRANSLUCENT);

    static {
        COMPACT_RENDER_TYPES.add(TRIPWIRE);
        SEMI_COMPACT_RENDER_TYPES.add(TRIPWIRE);
    }

    TerrainRenderType(float alphaCutout) {
        this.alphaCutout = alphaCutout;
    }

    public void setCutoutUniform() {
        net.vulkanmod.vulkan.VRenderSystem.alphaCutout = this.alphaCutout;
    }

    public static net.vulkanmod.render.vertex.TerrainRenderType get(net.minecraft.client.renderer.rendertype.RenderType renderType) {
        return ((net.vulkanmod.interfaces.ExtendedRenderType) renderType).getTerrainRenderType();
    }

    /* JADX INFO: renamed from: net.vulkanmod.render.vertex.TerrainRenderType$1, reason: invalid class name */
    /* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/vertex/TerrainRenderType$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer = new int[net.minecraft.client.renderer.chunk.ChunkSectionLayer.values().length];

        static {
            try {
                $SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer[net.minecraft.client.renderer.chunk.ChunkSectionLayer.SOLID.ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer[net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT.ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer[net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT.ordinal()] = 3;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer[net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRIPWIRE.ordinal()] = 4;
            } catch (java.lang.NoSuchFieldError e4) {
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static net.vulkanmod.render.vertex.TerrainRenderType get(net.minecraft.client.renderer.chunk.ChunkSectionLayer layer) throws java.lang.MatchException {
        switch (net.vulkanmod.render.vertex.TerrainRenderType.AnonymousClass1.$SwitchMap$net$minecraft$client$renderer$chunk$ChunkSectionLayer[layer.ordinal()]) {
            case 1:
                return SOLID;
            case 2:
                return CUTOUT;
            case 3:
                return TRANSLUCENT;
            case 4:
                return TRIPWIRE;
            default:
                throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
        }
    }

    public static net.vulkanmod.render.vertex.TerrainRenderType get(java.lang.String name) {
        switch (name) {
            case "solid":
                return SOLID;
            case "cutout":
                return CUTOUT;
            case "translucent":
                return TRANSLUCENT;
            case "tripwire":
                return TRIPWIRE;
            default:
                return null;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.lang.MatchException */
    public static net.minecraft.client.renderer.chunk.ChunkSectionLayer getLayer(net.vulkanmod.render.vertex.TerrainRenderType renderType) throws java.lang.MatchException {
        switch (renderType) {
            case SOLID:
                return net.minecraft.client.renderer.chunk.ChunkSectionLayer.SOLID;
            case CUTOUT:
                return net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT;
            case TRANSLUCENT:
                return net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT;
            case TRIPWIRE:
                return net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRIPWIRE;
            default:
                throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
        }
    }

    public static void updateMapping() {
        if (net.vulkanmod.Initializer.CONFIG.uniqueOpaqueLayer) {
            net.vulkanmod.Initializer.LOGGER.warn("uniqueOpaqueLayer is temporarily using safe fallback mapping to avoid broken block textures.");
            remapper = renderType -> {
                switch (renderType) {
                    case SOLID:
                        return SOLID;
                    case CUTOUT:
                        return CUTOUT;
                    case TRANSLUCENT:
                        return TRANSLUCENT;
                    case TRIPWIRE:
                        return TRIPWIRE;
                    default:
                        throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
                }
            };
        } else {
            remapper = renderType2 -> {
                switch (renderType2) {
                    case SOLID:
                        return SOLID;
                    case CUTOUT:
                        return CUTOUT;
                    case TRANSLUCENT:
                    case TRIPWIRE:
                        return TRANSLUCENT;
                    default:
                        throw new java.lang.MatchException((java.lang.String) null, (java.lang.Throwable) null);
                }
            };
        }
    }

    public static net.vulkanmod.render.vertex.TerrainRenderType getRemapped(net.vulkanmod.render.vertex.TerrainRenderType renderType) {
        return remapper.apply(renderType);
    }
}
