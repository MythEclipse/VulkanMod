package net.vulkanmod.render.shader;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/shader/ShaderLoadUtil.class */
public abstract class ShaderLoadUtil {
    public static final java.lang.String RESOURCES_PATH = net.vulkanmod.vulkan.shader.SPIRVUtils.class
            .getResource("/assets/vulkanmod")
            .toExternalForm();
    public static final java.lang.String SHADERS_PATH = "%s/shaders/".formatted(RESOURCES_PATH);
    public static final java.util.Set<java.lang.String> REMAPPED_SHADERS = java.util.Set.of(
            new java.lang.String[] {
                    "core/screenquad.vsh",
                    "core/rendertype_item_entity_translucent_cull.vsh",
                    "core/animate_sprite.vsh",
                    "core/animate_sprite_blit.fsh"
            });

    public static java.lang.String resolveShaderPath(java.lang.String path) {
        return resolveShaderPath(SHADERS_PATH, path);
    }

    public static java.lang.String resolveShaderPath(
            java.lang.String shaderPath, java.lang.String path) {
        return "%s%s".formatted(shaderPath, path);
    }

    public static void loadShaders(
            net.vulkanmod.vulkan.shader.Pipeline.Builder pipelineBuilder,
            com.google.gson.JsonObject config,
            java.lang.String configName,
            java.lang.String path) {
        java.lang.String vertexShader = config.has("vertex") ? config.get("vertex").getAsString() : configName;
        java.lang.String fragmentShader = config.has("fragment") ? config.get("fragment").getAsString() : configName;
        if (vertexShader == null) {
            vertexShader = configName;
        }
        if (fragmentShader == null) {
            fragmentShader = configName;
        }
        java.lang.String vertexShader2 = removeNameSpace(vertexShader);
        java.lang.String fragmentShader2 = removeNameSpace(fragmentShader);
        java.lang.String vertexShader3 = getFileName(vertexShader2);
        java.lang.String fragmentShader3 = getFileName(fragmentShader2);
        loadShader(
                pipelineBuilder,
                configName,
                path,
                vertexShader3,
                net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind.VERTEX_SHADER);
        loadShader(
                pipelineBuilder,
                configName,
                path,
                fragmentShader3,
                net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind.FRAGMENT_SHADER);
    }

    public static void loadShader(
            net.vulkanmod.vulkan.shader.Pipeline.Builder pipelineBuilder,
            java.lang.String configName,
            java.lang.String path,
            net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind type) {
        java.lang.String[] splitPath = splitPath(path);
        if (splitPath == null) {
            throw new IllegalArgumentException("Invalid shader path: " + path);
        }
        java.lang.String shaderName = splitPath[1];
        java.lang.String subPath = splitPath[0];
        loadShader(pipelineBuilder, configName, subPath, shaderName, type);
    }

    public static void loadShader(
            net.vulkanmod.vulkan.shader.Pipeline.Builder pipelineBuilder,
            java.lang.String configName,
            java.lang.String path,
            java.lang.String shaderName,
            net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind type) {
        java.lang.String source = getShaderSource(path, configName, shaderName, type);
        net.vulkanmod.vulkan.shader.SPIRVUtils.SPIRV spirv = net.vulkanmod.vulkan.shader.SPIRVUtils
                .compileShader(shaderName, source, type);
        switch (type) {
            case VERTEX_SHADER:
                pipelineBuilder.setVertShaderSPIRV(spirv);
                break;
            case FRAGMENT_SHADER:
                pipelineBuilder.setFragShaderSPIRV(spirv);
                break;
            case COMPUTE_SHADER:
            case GEOMETRY_SHADER:
                break;
        }
    }

    public static java.lang.String getConfigFilePath(
            java.lang.String path, java.lang.String rendertype) {
        java.lang.String basePath = "%s/shaders/%s".formatted(RESOURCES_PATH, path);
        java.lang.String configPath = "%s/%s/%s.json".formatted(basePath, rendertype, rendertype);
        try {
            java.nio.file.Path filePath = java.nio.file.FileSystems.getDefault()
                    .getPath(configPath);
            if (!java.nio.file.Files.exists(filePath, new java.nio.file.LinkOption[0])) {
                java.lang.String configPath2 = "%s/%s.json".formatted(basePath, rendertype);
                filePath = java.nio.file.FileSystems.getDefault()
                        .getPath(configPath2);
            }
            if (!java.nio.file.Files.exists(filePath, new java.nio.file.LinkOption[0])) {
                return null;
            }
            return filePath.toString();
        } catch (java.lang.Throwable e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    public static com.google.gson.JsonObject getJsonConfig(
            java.lang.String path, java.lang.String rendertype) {
        if (rendertype.contains(java.lang.String.valueOf(':'))) {
            return null;
        }
        java.lang.String configPath = "%s/%s/%s.json".formatted(path, rendertype, rendertype);
        try {
            java.io.InputStream stream = getInputStream(configPath);
            if (stream == null) {
                java.lang.String configPath2 = "%s/%s.json".formatted(path, rendertype);
                stream = getInputStream(configPath2);
            }
            if (stream == null) {
                return null;
            }
            com.google.gson.JsonObject reader = (com.google.gson.JsonObject) com.google.gson.JsonParser.parseReader(
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(stream)));
            stream.close();
            return reader;
        } catch (java.lang.Throwable e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    /*
     * JADX INFO: renamed from: net.vulkanmod.render.shader.ShaderLoadUtil$1,
     * reason: invalid class name
     */
    /*
     * JADX INFO: loaded from:
     * VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/render/shader/ShaderLoadUtil$1.
     * class
     */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mojang$blaze3d$shaders$ShaderType = new int[com.mojang.blaze3d.shaders.ShaderType
                .values().length];
        static /* synthetic */ int[] $SwitchMap$net$vulkanmod$vulkan$shader$SPIRVUtils$ShaderKind;

        static {
            try {
                $SwitchMap$com$mojang$blaze3d$shaders$ShaderType[com.mojang.blaze3d.shaders.ShaderType.VERTEX
                        .ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mojang$blaze3d$shaders$ShaderType[com.mojang.blaze3d.shaders.ShaderType.FRAGMENT
                        .ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e2) {
            }
            $SwitchMap$net$vulkanmod$vulkan$shader$SPIRVUtils$ShaderKind = new int[net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind
                    .values().length];
            try {
                $SwitchMap$net$vulkanmod$vulkan$shader$SPIRVUtils$ShaderKind[net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind.VERTEX_SHADER
                        .ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$net$vulkanmod$vulkan$shader$SPIRVUtils$ShaderKind[net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind.FRAGMENT_SHADER
                        .ordinal()] = 2;
            } catch (java.lang.NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$net$vulkanmod$vulkan$shader$SPIRVUtils$ShaderKind[net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind.COMPUTE_SHADER
                        .ordinal()] = 3;
            } catch (java.lang.NoSuchFieldError e5) {
            }
        }
    }

    public static java.lang.String getShaderSource(
            net.minecraft.resources.Identifier resourceLocation,
            com.mojang.blaze3d.shaders.ShaderType type) {
        java.lang.String str;
        switch (net.vulkanmod.render.shader.ShaderLoadUtil.AnonymousClass1.$SwitchMap$com$mojang$blaze3d$shaders$ShaderType[type
                .ordinal()]) {
            case 1:
                str = ".vsh";
                break;
            case 2:
                str = ".fsh";
                break;
            default:
                throw new java.lang.MatchException(
                        "Unknown ShaderType", (java.lang.Throwable) null);
        }
        java.lang.String shaderExtension = str;
        java.lang.String path = resourceLocation.getPath();
        java.lang.String[] splitPath = splitPath(path);
        java.lang.String shaderName = "%s%s".formatted(splitPath[1], shaderExtension);
        java.lang.String shaderFile = "%s/shaders/%s/%s".formatted(RESOURCES_PATH, path, shaderName);
        try {
            java.io.InputStream stream = getInputStream(shaderFile);
            if (stream == null) {
                java.lang.String shaderFile2 = "%s/shaders/%s%s".formatted(RESOURCES_PATH, path, shaderExtension);
                stream = getInputStream(shaderFile2);
            }
            if (stream == null) {
                return null;
            }
            java.lang.String source = org.apache.commons.io.IOUtils.toString(
                    new java.io.BufferedReader(new java.io.InputStreamReader(stream)));
            stream.close();
            return source;
        } catch (java.lang.Throwable e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    /*
     * JADX INFO: Thrown type has an unknown type hierarchy:
     * java.lang.MatchException
     */
    public static java.lang.String getShaderSource(
            java.lang.String path, com.mojang.blaze3d.shaders.ShaderType type)
            throws java.lang.MatchException {
        java.lang.String str;
        switch (net.vulkanmod.render.shader.ShaderLoadUtil.AnonymousClass1.$SwitchMap$com$mojang$blaze3d$shaders$ShaderType[type
                .ordinal()]) {
            case 1:
                str = ".vsh";
                break;
            case 2:
                str = ".fsh";
                break;
            default:
                throw new java.lang.MatchException(
                        "Unknown ShaderType", (java.lang.Throwable) null);
        }
        java.lang.String shaderExtension = str;
        java.lang.String[] splitPath = splitPath(path);
        java.lang.String shaderName = "%s%s".formatted(splitPath[1], shaderExtension);
        java.lang.String shaderFile = "%s/shaders/%s/%s".formatted(RESOURCES_PATH, path, shaderName);
        try {
            java.io.InputStream stream = getInputStream(shaderFile);
            java.lang.String source = org.apache.commons.io.IOUtils.toString(
                    new java.io.BufferedReader(new java.io.InputStreamReader(stream)));
            stream.close();
            return source;
        } catch (java.lang.Throwable e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    public static java.lang.String getShaderSource(
            java.lang.String path,
            java.lang.String configName,
            java.lang.String shaderName,
            net.vulkanmod.vulkan.shader.SPIRVUtils.ShaderKind type) {
        java.lang.String str;
        switch (type) {
            case VERTEX_SHADER:
                str = ".vsh";
                break;
            case FRAGMENT_SHADER:
                str = ".fsh";
                break;
            case GEOMETRY_SHADER:
                str = ".geom";
                break;
            case COMPUTE_SHADER:
                str = ".comp";
                break;
            default:
                throw new java.lang.UnsupportedOperationException("shader type %s unsupported".formatted(type));
        }
        java.lang.String shaderExtension = str;
        java.lang.String shaderPath = "/%s/%s".formatted(configName, configName);
        java.lang.String shaderFile = "%s%s%s".formatted(path, shaderPath, shaderExtension);
        try {
            java.io.InputStream stream = getInputStream(shaderFile);
            if (stream == null) {
                java.lang.String shaderPath2 = "/%s".formatted(shaderName);
                java.lang.String shaderFile2 = "%s%s%s".formatted(path, shaderPath2, shaderExtension);
                stream = getInputStream(shaderFile2);
            }
            if (stream == null) {
                java.lang.String shaderPath3 = "/%s/%s".formatted(configName, shaderName);
                java.lang.String shaderFile3 = "%s%s%s".formatted(path, shaderPath3, shaderExtension);
                stream = getInputStream(shaderFile3);
            }
            if (stream == null) {
                java.lang.String shaderPath4 = "/%s/%s".formatted(shaderName, shaderName);
                java.lang.String shaderFile4 = "%s%s%s".formatted(path, shaderPath4, shaderExtension);
                stream = getInputStream(shaderFile4);
            }
            if (stream == null) {
                return null;
            }
            java.lang.String source = org.apache.commons.io.IOUtils.toString(
                    new java.io.BufferedReader(new java.io.InputStreamReader(stream)));
            stream.close();
            return source;
        } catch (java.lang.Throwable e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    public static java.lang.String getFileName(java.lang.String path) {
        int idx = path.lastIndexOf(47);
        return idx > -1 ? path.substring(idx + 1) : path;
    }

    public static java.lang.String removeNameSpace(java.lang.String path) {
        int idx = path.indexOf(58);
        return idx > -1 ? path.substring(idx + 1) : path;
    }

    public static java.lang.String[] splitPath(java.lang.String path) {
        int idx = path.lastIndexOf(47);
        if (idx == -1)
            return null;
        return new java.lang.String[] { path.substring(0, idx), path.substring(idx + 1) };
    }

    public static java.io.InputStream getInputStream(java.lang.String path) {
        try {
            java.nio.file.Path path1 = java.nio.file.Paths.get(new java.net.URI(path));
            if (!java.nio.file.Files.exists(path1, new java.nio.file.LinkOption[0])) {
                return null;
            }
            return java.nio.file.Files.newInputStream(path1, new java.nio.file.OpenOption[0]);
        } catch (java.io.IOException | java.net.URISyntaxException e) {
            throw new java.lang.RuntimeException(e);
        }
    }
}
