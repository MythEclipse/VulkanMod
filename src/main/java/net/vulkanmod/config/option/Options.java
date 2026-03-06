package net.vulkanmod.config.option;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/config/option/Options.class */
public abstract class Options {
    public static boolean fullscreenDirty = false;
    static net.vulkanmod.config.Config config = net.vulkanmod.Initializer.CONFIG;
    static net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
    static com.mojang.blaze3d.platform.Window window = minecraft.getWindow();
    static net.minecraft.client.Options minecraftOptions = minecraft.options;

    public static net.vulkanmod.config.gui.OptionBlock[] getVideoOpts() {
        net.vulkanmod.config.video.VideoModeSet.VideoMode videoMode = config.videoMode;
        net.vulkanmod.config.video.VideoModeSet videoModeSet =
                net.vulkanmod.config.video.VideoModeManager.getFromVideoMode(videoMode);
        if (videoModeSet == null) {
            videoModeSet = net.vulkanmod.config.video.VideoModeSet.getDummy();
            videoMode = videoModeSet.getVideoMode(-1);
        }
        net.vulkanmod.config.video.VideoModeManager.selectedVideoMode = videoMode;
        java.util.List<java.lang.Integer> refreshRates = videoModeSet.getRefreshRates();
        net.vulkanmod.config.option.CyclingOption<java.lang.Integer> RefreshRate =
                (net.vulkanmod.config.option.CyclingOption)
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.refreshRate"),
                                        (java.lang.Integer[])
                                                refreshRates.toArray(new java.lang.Integer[0]),
                                        value -> {
                                            net.vulkanmod.config.video.VideoModeManager
                                                            .selectedVideoMode
                                                            .refreshRate =
                                                    ((Integer) value).intValue();
                                            net.vulkanmod.config.video.VideoModeManager
                                                    .applySelectedVideoMode();
                                            if (((java.lang.Boolean)
                                                            minecraftOptions.fullscreen().get())
                                                    .booleanValue()) {
                                                fullscreenDirty = true;
                                            }
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(
                                                    net.vulkanmod.config.video.VideoModeManager
                                                            .selectedVideoMode
                                                            .refreshRate);
                                        })
                                .setTranslator(
                                        refreshRate -> {
                                            return net.minecraft.network.chat.Component.nullToEmpty(
                                                    refreshRate.toString());
                                        });
        net.vulkanmod.config.option.Option translator =
                new net.vulkanmod.config.option.CyclingOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.fullscreen.resolution"),
                                net.vulkanmod.config.video.VideoModeManager.getVideoResolutions(),
                                value2 -> {
                                    net.vulkanmod.config.video.VideoModeManager.selectedVideoMode =
                                            ((net.vulkanmod.config.video.VideoModeSet) value2)
                                                    .getVideoMode(
                                                            ((java.lang.Integer)
                                                                            RefreshRate
                                                                                    .getNewValue())
                                                                    .intValue());
                                    net.vulkanmod.config.video.VideoModeManager
                                            .applySelectedVideoMode();
                                    if (((java.lang.Boolean) minecraftOptions.fullscreen().get())
                                            .booleanValue()) {
                                        fullscreenDirty = true;
                                    }
                                },
                                () -> {
                                    net.vulkanmod.config.video.VideoModeSet.VideoMode
                                            selectedVideoMode =
                                                    net.vulkanmod.config.video.VideoModeManager
                                                            .selectedVideoMode;
                                    net.vulkanmod.config.video.VideoModeSet selectedVideoModeSet =
                                            net.vulkanmod.config.video.VideoModeManager
                                                    .getFromVideoMode(selectedVideoMode);
                                    return selectedVideoModeSet != null
                                            ? selectedVideoModeSet
                                            : net.vulkanmod.config.video.VideoModeSet.getDummy();
                                })
                        .setTranslator(
                                resolution -> {
                                    return net.minecraft.network.chat.Component.nullToEmpty(
                                            resolution.toString());
                                });
        translator.setOnChange(
                () -> {
                    net.vulkanmod.config.video.VideoModeSet newVideoMode =
                            (net.vulkanmod.config.video.VideoModeSet) translator.getNewValue();
                    java.lang.Integer[] newRefreshRates =
                            (java.lang.Integer[])
                                    newVideoMode
                                            .getRefreshRates()
                                            .toArray(new java.lang.Integer[0]);
                    RefreshRate.setValues(newRefreshRates);
                    RefreshRate.setNewValue(newRefreshRates[newRefreshRates.length - 1]);
                });
        return new net.vulkanmod.config.gui.OptionBlock[] {
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        translator,
                        RefreshRate,
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.windowMode"),
                                        net.vulkanmod.config.video.WindowMode.values(),
                                        value3 -> {
                                            boolean exclusiveFullscreen =
                                                    value3
                                                            == net.vulkanmod.config.video.WindowMode
                                                                    .EXCLUSIVE_FULLSCREEN;
                                            minecraftOptions
                                                    .fullscreen()
                                                    .set(
                                                            java.lang.Boolean.valueOf(
                                                                    exclusiveFullscreen));
                                            config.windowMode =
                                                    ((net.vulkanmod.config.video.WindowMode) value3)
                                                            .mode;
                                            fullscreenDirty = true;
                                        },
                                        () -> {
                                            return net.vulkanmod.config.video.WindowMode.fromValue(
                                                    config.windowMode);
                                        })
                                .setTranslator(
                                        value4 -> {
                                            return net.minecraft.network.chat.Component
                                                    .translatable(
                                                            net.vulkanmod.config.video.WindowMode
                                                                    .getComponentName(
                                                                            (net.vulkanmod.config
                                                                                            .video
                                                                                            .WindowMode)
                                                                                    value4));
                                        }),
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.framerateLimit"),
                                10,
                                260,
                                10,
                                value5 -> {
                                    java.lang.String strValueOf;
                                    if (value5.intValue() == 260) {
                                        strValueOf =
                                                net.minecraft.network.chat.Component.translatable(
                                                                "options.framerateLimit.max")
                                                        .getString();
                                    } else {
                                        strValueOf = java.lang.String.valueOf(value5);
                                    }
                                    return net.minecraft.network.chat.Component.nullToEmpty(
                                            strValueOf);
                                },
                                value6 -> {
                                    minecraftOptions.framerateLimit().set(value6);
                                    minecraft
                                            .getFramerateLimitTracker()
                                            .setFramerateLimit(value6.intValue());
                                },
                                () -> {
                                    return (java.lang.Integer)
                                            minecraftOptions.framerateLimit().get();
                                }),
                        new net.vulkanmod.config.option.SwitchOption(
                                net.minecraft.network.chat.Component.translatable("options.vsync"),
                                value7 -> {
                                    minecraftOptions.enableVsync().set(value7);
                                    window.updateVsync(value7.booleanValue());
                                },
                                () -> {
                                    return (java.lang.Boolean) minecraftOptions.enableVsync().get();
                                }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.inactivityFpsLimit"),
                                        net.minecraft.client.InactivityFpsLimit.values(),
                                        value8 -> {
                                            minecraftOptions
                                                    .inactivityFpsLimit()
                                                    .set(
                                                            (net.minecraft.client
                                                                            .InactivityFpsLimit)
                                                                    value8);
                                        },
                                        () -> {
                                            return (net.minecraft.client.InactivityFpsLimit)
                                                    minecraftOptions.inactivityFpsLimit().get();
                                        })
                                .setTranslator(
                                        (v0) -> {
                                            return ((net.minecraft.client.InactivityFpsLimit) v0)
                                                    .caption();
                                        })
                    }),
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.guiScale"),
                                0,
                                window.calculateScale(0, minecraft.isEnforceUnicode()),
                                1,
                                value9 -> {
                                    java.lang.String strValueOf;
                                    if (value9.intValue() == 0) {
                                        strValueOf = "options.guiScale.auto";
                                    } else {
                                        strValueOf = java.lang.String.valueOf(value9);
                                    }
                                    return net.minecraft.network.chat.Component.translatable(
                                            strValueOf);
                                },
                                value10 -> {
                                    minecraftOptions.guiScale().set(value10);
                                    minecraft.resizeDisplay();
                                },
                                () -> {
                                    return (java.lang.Integer) minecraftOptions.guiScale().get();
                                }),
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable("options.gamma"),
                                0,
                                100,
                                1,
                                value11 -> {
                                    java.lang.String strValueOf;
                                    switch (value11.intValue()) {
                                        case 0:
                                            strValueOf = "options.gamma.min";
                                            break;
                                        case 50:
                                            strValueOf = "options.gamma.default";
                                            break;
                                        case 100:
                                            strValueOf = "options.gamma.max";
                                            break;
                                        default:
                                            strValueOf = java.lang.String.valueOf(value11);
                                            break;
                                    }
                                    return net.minecraft.network.chat.Component.translatable(
                                            strValueOf);
                                },
                                value12 -> {
                                    minecraftOptions
                                            .gamma()
                                            .set(
                                                    java.lang.Double.valueOf(
                                                            ((double) value12.intValue()) * 0.01d));
                                },
                                () -> {
                                    return java.lang.Integer.valueOf(
                                            (int)
                                                    (((java.lang.Double)
                                                                            minecraftOptions
                                                                                    .gamma()
                                                                                    .get())
                                                                    .doubleValue()
                                                            * 100.0d));
                                })
                    }),
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.SwitchOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.viewBobbing"),
                                value13 -> {
                                    minecraftOptions.bobView().set(value13);
                                },
                                () -> {
                                    return (java.lang.Boolean) minecraftOptions.bobView().get();
                                }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.attackIndicator"),
                                        net.minecraft.client.AttackIndicatorStatus.values(),
                                        value14 -> {
                                            minecraftOptions
                                                    .attackIndicator()
                                                    .set(
                                                            (net.minecraft.client
                                                                            .AttackIndicatorStatus)
                                                                    value14);
                                        },
                                        () -> {
                                            return (net.minecraft.client.AttackIndicatorStatus)
                                                    minecraftOptions.attackIndicator().get();
                                        })
                                .setTranslator(
                                        (v0) -> {
                                            return ((net.minecraft.client.AttackIndicatorStatus) v0)
                                                    .caption();
                                        }),
                        new net.vulkanmod.config.option.SwitchOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.autosaveIndicator"),
                                value15 -> {
                                    minecraftOptions.showAutosaveIndicator().set(value15);
                                },
                                () -> {
                                    return (java.lang.Boolean)
                                            minecraftOptions.showAutosaveIndicator().get();
                                })
                    })
        };
    }

    public static net.vulkanmod.config.gui.OptionBlock[] getGraphicsOpts() {
        net.vulkanmod.config.option.Option translator =
                new net.vulkanmod.config.option.CyclingOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.textureFiltering"),
                                net.minecraft.client.TextureFilteringMethod.values(),
                                value -> {
                                    net.minecraft.client.TextureFilteringMethod oldValue =
                                            (net.minecraft.client.TextureFilteringMethod)
                                                    minecraftOptions.textureFiltering().get();
                                    if ((oldValue
                                                            == net.minecraft.client
                                                                    .TextureFilteringMethod
                                                                    .ANISOTROPIC
                                                    && value
                                                            != net.minecraft.client
                                                                    .TextureFilteringMethod
                                                                    .ANISOTROPIC)
                                            || (value
                                                            == net.minecraft.client
                                                                    .TextureFilteringMethod
                                                                    .ANISOTROPIC
                                                    && oldValue
                                                            != net.minecraft.client
                                                                    .TextureFilteringMethod
                                                                    .ANISOTROPIC)) {
                                        minecraft.delayTextureReload();
                                        net.vulkanmod.render.chunk.WorldRenderer.getInstance()
                                                .resetSampler();
                                    }
                                    minecraftOptions
                                            .textureFiltering()
                                            .set(
                                                    (net.minecraft.client.TextureFilteringMethod)
                                                            value);
                                },
                                () -> {
                                    return (net.minecraft.client.TextureFilteringMethod)
                                            minecraftOptions.textureFiltering().get();
                                })
                        .setTranslator(
                                (v0) -> {
                                    return ((net.minecraft.client.TextureFilteringMethod) v0)
                                            .caption();
                                });
        net.vulkanmod.config.option.Option<java.lang.Integer> maxAnisotropyOption =
                new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.maxAnisotropy"),
                                1,
                                3,
                                1,
                                value2 -> {
                                    java.lang.Integer oldValue =
                                            (java.lang.Integer)
                                                    minecraftOptions.maxAnisotropyBit().get();
                                    if (minecraftOptions.textureFiltering().get()
                                                    == net.minecraft.client.TextureFilteringMethod
                                                            .ANISOTROPIC
                                            && !oldValue.equals(value2)) {
                                        minecraft.delayTextureReload();
                                        net.vulkanmod.render.chunk.WorldRenderer.getInstance()
                                                .resetSampler();
                                    }
                                    minecraftOptions.maxAnisotropyBit().set(value2);
                                },
                                () -> {
                                    return (java.lang.Integer)
                                            minecraftOptions.maxAnisotropyBit().get();
                                })
                        .setTranslator(
                                value3 -> {
                                    return net.minecraft.network.chat.Component.translatable(
                                            "options.multiplier",
                                            new java.lang.Object[] {
                                                java.lang.Integer.toString(1 << value3.intValue())
                                            });
                                });
        maxAnisotropyOption.setActivationFn(
                () -> {
                    return java.lang.Boolean.valueOf(
                            translator.getNewValue()
                                    == net.minecraft.client.TextureFilteringMethod.ANISOTROPIC);
                });
        java.util.Objects.requireNonNull(maxAnisotropyOption);
        translator.setOnChange(maxAnisotropyOption::updateActiveState);
        return new net.vulkanmod.config.gui.OptionBlock[] {
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.renderDistance"),
                                2,
                                32,
                                1,
                                value4 -> {
                                    minecraftOptions.renderDistance().set(value4);
                                },
                                () -> {
                                    return (java.lang.Integer)
                                            minecraftOptions.renderDistance().get();
                                }),
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.simulationDistance"),
                                5,
                                32,
                                1,
                                value5 -> {
                                    minecraftOptions.simulationDistance().set(value5);
                                },
                                () -> {
                                    return (java.lang.Integer)
                                            minecraftOptions.simulationDistance().get();
                                }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.prioritizeChunkUpdates"),
                                        net.minecraft.client.PrioritizeChunkUpdates.values(),
                                        value6 -> {
                                            minecraftOptions
                                                    .prioritizeChunkUpdates()
                                                    .set(
                                                            (net.minecraft.client
                                                                            .PrioritizeChunkUpdates)
                                                                    value6);
                                        },
                                        () -> {
                                            return (net.minecraft.client.PrioritizeChunkUpdates)
                                                    minecraftOptions.prioritizeChunkUpdates().get();
                                        })
                                .setTranslator(
                                        (v0) -> {
                                            return ((net.minecraft.client.PrioritizeChunkUpdates)
                                                            v0)
                                                    .caption();
                                        })
                    }),
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.graphics.preset"),
                                        new net.minecraft.client.GraphicsPreset[] {
                                            net.minecraft.client.GraphicsPreset.FAST,
                                            net.minecraft.client.GraphicsPreset.FANCY,
                                            net.minecraft.client.GraphicsPreset.CUSTOM
                                        },
                                        value7 -> {
                                            minecraftOptions
                                                    .graphicsPreset()
                                                    .set(
                                                            (net.minecraft.client.GraphicsPreset)
                                                                    value7);
                                        },
                                        () -> {
                                            return (net.minecraft.client.GraphicsPreset)
                                                    minecraftOptions.graphicsPreset().get();
                                        })
                                .setTranslator(
                                        graphicsMode -> {
                                            return net.minecraft.network.chat.Component
                                                    .translatable(
                                                            ((net.minecraft.client.GraphicsPreset)
                                                                            graphicsMode)
                                                                    .getKey());
                                        }),
                        translator,
                        maxAnisotropyOption,
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.particles"),
                                        new net.minecraft.server.level.ParticleStatus[] {
                                            net.minecraft.server.level.ParticleStatus.MINIMAL,
                                            net.minecraft.server.level.ParticleStatus.DECREASED,
                                            net.minecraft.server.level.ParticleStatus.ALL
                                        },
                                        value8 -> {
                                            minecraftOptions
                                                    .particles()
                                                    .set(
                                                            (net.minecraft.server.level
                                                                            .ParticleStatus)
                                                                    value8);
                                        },
                                        () -> {
                                            return (net.minecraft.server.level.ParticleStatus)
                                                    minecraftOptions.particles().get();
                                        })
                                .setTranslator(
                                        (v0) -> {
                                            return ((net.minecraft.server.level.ParticleStatus) v0)
                                                    .caption();
                                        }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.renderClouds"),
                                        net.minecraft.client.CloudStatus.values(),
                                        value9 -> {
                                            minecraftOptions
                                                    .cloudStatus()
                                                    .set((net.minecraft.client.CloudStatus) value9);
                                        },
                                        () -> {
                                            return (net.minecraft.client.CloudStatus)
                                                    minecraftOptions.cloudStatus().get();
                                        })
                                .setTranslator(
                                        (v0) -> {
                                            return ((net.minecraft.client.CloudStatus) v0)
                                                    .caption();
                                        }),
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.renderCloudsDistance"),
                                2,
                                128,
                                1,
                                value10 -> {
                                    minecraftOptions.cloudRange().set(value10);
                                },
                                () -> {
                                    return (java.lang.Integer) minecraftOptions.cloudRange().get();
                                }),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.cutoutLeaves"),
                                        value11 -> {
                                            minecraftOptions.cutoutLeaves().set(value11);
                                        },
                                        () -> {
                                            return (java.lang.Boolean)
                                                    minecraftOptions.cutoutLeaves().get();
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.cutoutLeaves.tooltip")),
                        new net.vulkanmod.config.option.RangeOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.chunkFade"),
                                        0,
                                        40,
                                        1,
                                        value12 -> {
                                            minecraftOptions
                                                    .chunkSectionFadeInTime()
                                                    .set(
                                                            java.lang.Double.valueOf(
                                                                    ((double) value12.intValue())
                                                                            / 20.0d));
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(
                                                    (int)
                                                            (((java.lang.Double)
                                                                                    minecraftOptions
                                                                                            .chunkSectionFadeInTime()
                                                                                            .get())
                                                                            .doubleValue()
                                                                    * 20.0d));
                                        })
                                .setTranslator(
                                        value13 -> {
                                            return net.minecraft.network.chat.Component.literal(
                                                    java.lang.String.valueOf(
                                                            value13.intValue() / 20.0f));
                                        }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.ao"),
                                        new java.lang.Integer[] {0, 1, 2},
                                        value14 -> {
                                            if (((Integer) value14).intValue() > 0) {
                                                minecraftOptions.ambientOcclusion().set(true);
                                            } else {
                                                minecraftOptions.ambientOcclusion().set(false);
                                            }
                                            config.ambientOcclusion =
                                                    ((Integer) value14).intValue();
                                            minecraft.levelRenderer.allChanged();
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(
                                                    config.ambientOcclusion);
                                        })
                                .setTranslator(
                                        value15 -> {
                                            java.lang.String str;
                                            switch (((Integer) value15).intValue()) {
                                                case 0:
                                                    str = "options.off";
                                                    break;
                                                case 1:
                                                    str = "options.on";
                                                    break;
                                                case 2:
                                                    str = "vulkanmod.options.ao.subBlock";
                                                    break;
                                                default:
                                                    str = "vulkanmod.options.unknown";
                                                    break;
                                            }
                                            return net.minecraft.network.chat.Component
                                                    .translatable(str);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.ao.subBlock.tooltip")),
                        new net.vulkanmod.config.option.RangeOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.biomeBlendRadius"),
                                0,
                                7,
                                1,
                                value16 -> {
                                    int v = (value16.intValue() * 2) + 1;
                                    return net.minecraft.network.chat.Component.nullToEmpty(
                                            "%d x %d"
                                                    .formatted(
                                                            java.lang.Integer.valueOf(v),
                                                            java.lang.Integer.valueOf(v)));
                                },
                                value17 -> {
                                    minecraftOptions.biomeBlendRadius().set(value17);
                                    minecraft.levelRenderer.allChanged();
                                },
                                () -> {
                                    return (java.lang.Integer)
                                            minecraftOptions.biomeBlendRadius().get();
                                })
                    }),
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.SwitchOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "options.entityShadows"),
                                value18 -> {
                                    minecraftOptions.entityShadows().set(value18);
                                },
                                () -> {
                                    return (java.lang.Boolean)
                                            minecraftOptions.entityShadows().get();
                                }),
                        new net.vulkanmod.config.option.RangeOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.entityDistanceScaling"),
                                        2,
                                        20,
                                        1,
                                        value19 -> {
                                            minecraftOptions
                                                    .entityDistanceScaling()
                                                    .set(
                                                            java.lang.Double.valueOf(
                                                                    ((double) value19.intValue())
                                                                            / 4.0d));
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(
                                                    (int)
                                                            (((java.lang.Double)
                                                                                    minecraftOptions
                                                                                            .entityDistanceScaling()
                                                                                            .get())
                                                                            .doubleValue()
                                                                    * 4.0d));
                                        })
                                .setTranslator(
                                        value20 -> {
                                            return net.minecraft.network.chat.Component.literal(
                                                    java.lang.String.valueOf(
                                                            ((double) value20.intValue()) / 4.0d));
                                        }),
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.mipmapLevels"),
                                        new java.lang.Integer[] {0, 1, 2, 3, 4},
                                        value21 -> {
                                            minecraftOptions.mipmapLevels().set((Integer) value21);
                                            minecraft.updateMaxMipLevel(
                                                    ((Integer) value21).intValue());
                                            minecraft.delayTextureReload();
                                        },
                                        () -> {
                                            return (java.lang.Integer)
                                                    minecraftOptions.mipmapLevels().get();
                                        })
                                .setTranslator(
                                        value22 -> {
                                            return net.minecraft.network.chat.Component.nullToEmpty(
                                                    value22.toString());
                                        }),
                        new net.vulkanmod.config.option.RangeOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.weatherRadius"),
                                        3,
                                        10,
                                        1,
                                        value23 -> {
                                            minecraftOptions.weatherRadius().set(value23);
                                        },
                                        () -> {
                                            return (java.lang.Integer)
                                                    minecraftOptions.weatherRadius().get();
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.weatherRadius.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.vignette"),
                                        value24 -> {
                                            minecraftOptions.vignette().set(value24);
                                        },
                                        () -> {
                                            return (java.lang.Boolean)
                                                    minecraftOptions.vignette().get();
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "options.vignette.tooltip"))
                    })
        };
    }

    public static net.vulkanmod.config.gui.OptionBlock[] getOptimizationOpts() {
        return new net.vulkanmod.config.gui.OptionBlock[] {
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.advCulling"),
                                        new java.lang.Integer[] {1, 2, 3, 10},
                                        value -> {
                                            config.advCulling = ((Integer) value).intValue();
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(config.advCulling);
                                        })
                                .setTranslator(
                                        value2 -> {
                                            java.lang.String str;
                                            switch (((Integer) value2).intValue()) {
                                                case 1:
                                                    str = "vulkanmod.options.advCulling.aggressive";
                                                    break;
                                                case 2:
                                                    str = "vulkanmod.options.advCulling.normal";
                                                    break;
                                                case 3:
                                                    str =
                                                            "vulkanmod.options.advCulling.conservative";
                                                    break;
                                                case 4:
                                                case 5:
                                                case 6:
                                                case 7:
                                                case 8:
                                                case 9:
                                                default:
                                                    str = "vulkanmod.options.unknown";
                                                    break;
                                                case 10:
                                                    str = "options.off";
                                                    break;
                                            }
                                            return net.minecraft.network.chat.Component
                                                    .translatable(str);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.advCulling.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.entityCulling"),
                                        value3 -> {
                                            config.entityCulling = value3.booleanValue();
                                        },
                                        () -> {
                                            return java.lang.Boolean.valueOf(config.entityCulling);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.entityCulling.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.uniqueOpaqueLayer"),
                                        value4 -> {
                                            config.uniqueOpaqueLayer = value4.booleanValue();
                                            net.vulkanmod.render.vertex.TerrainRenderType
                                                    .updateMapping();
                                            minecraft.levelRenderer.allChanged();
                                        },
                                        () -> {
                                            return java.lang.Boolean.valueOf(
                                                    config.uniqueOpaqueLayer);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.uniqueOpaqueLayer.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.backfaceCulling"),
                                        value5 -> {
                                            config.backFaceCulling = value5.booleanValue();
                                            net.minecraft.client.Minecraft.getInstance()
                                                    .levelRenderer
                                                    .allChanged();
                                        },
                                        () -> {
                                            return java.lang.Boolean.valueOf(
                                                    config.backFaceCulling);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.backfaceCulling.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.indirectDraw"),
                                        value6 -> {
                                            config.indirectDraw = value6.booleanValue();
                                        },
                                        () -> {
                                            return java.lang.Boolean.valueOf(config.indirectDraw);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.indirectDraw.tooltip"))
                    })
        };
    }

    public static net.vulkanmod.config.gui.OptionBlock[] getOtherOpts() {
        return new net.vulkanmod.config.gui.OptionBlock[] {
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.RangeOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.builderThreads"),
                                        0,
                                        java.lang.Runtime.getRuntime().availableProcessors() - 1,
                                        1,
                                        value -> {
                                            config.builderThreads = value.intValue();
                                            net.vulkanmod.render.chunk.WorldRenderer.getInstance()
                                                    .getTaskDispatcher()
                                                    .createThreads(value.intValue());
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(config.builderThreads);
                                        })
                                .setTranslator(
                                        value2 -> {
                                            if (value2.intValue() == 0) {
                                                return net.minecraft.network.chat.Component
                                                        .translatable(
                                                                "vulkanmod.options.builderThreads.auto");
                                            }
                                            return net.minecraft.network.chat.Component.nullToEmpty(
                                                    java.lang.String.valueOf(value2));
                                        }),
                        new net.vulkanmod.config.option.RangeOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.frameQueue"),
                                        2,
                                        5,
                                        1,
                                        value3 -> {
                                            config.frameQueueSize = value3.intValue();
                                            net.vulkanmod.vulkan.Renderer.scheduleSwapChainUpdate();
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(config.frameQueueSize);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.frameQueue.tooltip")),
                        new net.vulkanmod.config.option.SwitchOption(
                                net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.textureAnimations"),
                                value4 -> {
                                    config.textureAnimations = value4.booleanValue();
                                },
                                () -> {
                                    return java.lang.Boolean.valueOf(config.textureAnimations);
                                })
                    }),
            new net.vulkanmod.config.gui.OptionBlock(
                    "",
                    new net.vulkanmod.config.option.Option[] {
                        new net.vulkanmod.config.option.CyclingOption(
                                        net.minecraft.network.chat.Component.translatable(
                                                "vulkanmod.options.deviceSelector"),
                                        (java.lang.Integer[])
                                                java.util.stream.IntStream.range(
                                                                -1,
                                                                net.vulkanmod.vulkan.device
                                                                        .DeviceManager
                                                                        .suitableDevices
                                                                        .size())
                                                        .boxed()
                                                        .toArray(
                                                                x$0 -> {
                                                                    return new java.lang.Integer
                                                                            [x$0];
                                                                }),
                                        value5 -> {
                                            config.device = ((Integer) value5).intValue();
                                        },
                                        () -> {
                                            return java.lang.Integer.valueOf(config.device);
                                        })
                                .setTranslator(
                                        value6 -> {
                                            java.lang.String str;
                                            if (((Integer) value6).intValue() == -1) {
                                                str = "vulkanmod.options.deviceSelector.auto";
                                            } else {
                                                str =
                                                        net.vulkanmod.vulkan.device.DeviceManager
                                                                .suitableDevices
                                                                .get(((Integer) value6).intValue())
                                                                .deviceName;
                                            }
                                            return net.minecraft.network.chat.Component
                                                    .translatable(str);
                                        })
                                .setTooltip(
                                        net.minecraft.network.chat.Component.nullToEmpty(
                                                "%s: %s"
                                                        .formatted(
                                                                net.minecraft.network.chat.Component
                                                                        .translatable(
                                                                                "vulkanmod.options.deviceSelector.tooltip")
                                                                        .getString(),
                                                                net.vulkanmod.vulkan.device
                                                                        .DeviceManager.device
                                                                        .deviceName)))
                    })
        };
    }
}
