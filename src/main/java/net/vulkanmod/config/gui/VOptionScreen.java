package net.vulkanmod.config.gui;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/config/gui/VOptionScreen.class */
public class VOptionScreen extends net.minecraft.client.gui.screens.Screen {
    public static final int MARGIN = 20;
    public static final int RED =
            net.vulkanmod.vulkan.util.ColorUtil.ARGB.pack(0.3f, 0.0f, 0.0f, 0.8f);
    final net.minecraft.resources.Identifier ICON;
    private final net.minecraft.client.gui.screens.Screen parent;
    private final java.util.List<net.vulkanmod.config.option.OptionPage> optionPages;
    private int currentListIdx;
    private int tooltipX;
    private int tooltipY;
    private int tooltipWidth;
    private net.vulkanmod.config.gui.widget.VButtonWidget supportButton;
    private net.vulkanmod.config.gui.widget.VButtonWidget doneButton;
    private net.vulkanmod.config.gui.widget.VButtonWidget applyButton;
    private final java.util.List<net.vulkanmod.config.gui.widget.VButtonWidget> pageButtons;
    private final java.util.List<net.vulkanmod.config.gui.widget.VButtonWidget> buttons;

    public VOptionScreen(
            net.minecraft.network.chat.Component title,
            net.minecraft.client.gui.screens.Screen parent) {
        super(title);
        this.ICON =
                net.minecraft.resources.Identifier.fromNamespaceAndPath(
                        "vulkanmod", "vlogo_transparent.png");
        this.currentListIdx = 0;
        this.pageButtons = com.google.common.collect.Lists.newArrayList();
        this.buttons = com.google.common.collect.Lists.newArrayList();
        this.parent = parent;
        this.optionPages = new java.util.ArrayList();
    }

    private void addPages() {
        this.optionPages.clear();
        net.vulkanmod.config.option.OptionPage page =
                new net.vulkanmod.config.option.OptionPage(
                        net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.pages.video")
                                .getString(),
                        net.vulkanmod.config.option.Options.getVideoOpts());
        this.optionPages.add(page);
        net.vulkanmod.config.option.OptionPage page2 =
                new net.vulkanmod.config.option.OptionPage(
                        net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.pages.graphics")
                                .getString(),
                        net.vulkanmod.config.option.Options.getGraphicsOpts());
        this.optionPages.add(page2);
        net.vulkanmod.config.option.OptionPage page3 =
                new net.vulkanmod.config.option.OptionPage(
                        net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.pages.optimizations")
                                .getString(),
                        net.vulkanmod.config.option.Options.getOptimizationOpts());
        this.optionPages.add(page3);
        net.vulkanmod.config.option.OptionPage page4 =
                new net.vulkanmod.config.option.OptionPage(
                        net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.pages.other")
                                .getString(),
                        net.vulkanmod.config.option.Options.getOtherOpts());
        this.optionPages.add(page4);
    }

    protected void init() {
        addPages();
        int listWidth =
                java.lang.Math.min(
                        (this.width - org.lwjgl.vulkan.VK10.VK_FORMAT_R64_UINT) - 20, 420);
        int listHeight = (this.height - 40) - 60;
        buildLists(org.lwjgl.vulkan.VK10.VK_FORMAT_R64_UINT, 40, listWidth, listHeight, 20);
        int x = org.lwjgl.vulkan.VK10.VK_FORMAT_R64_UINT + listWidth + 10;
        int width = (this.width - x) - 10;
        int y = 50;
        if (width < 200) {
            x = 100;
            width = listWidth;
            y = (this.height - 60) + 10;
        }
        this.tooltipX = x;
        this.tooltipY = y;
        this.tooltipWidth = width;
        buildPage();
        this.applyButton.active = false;
    }

    private void buildLists(int left, int top, int listWidth, int listHeight, int itemHeight) {
        for (net.vulkanmod.config.option.OptionPage page : this.optionPages) {
            page.createList(left, top, listWidth, listHeight, itemHeight);
            page.updateOptionStates();
        }
    }

    private void addPageButtons(int x0, int y0, int width, int height, boolean verticalLayout) {
        int x = x0;
        int y = y0;
        for (int i = 0; i < this.optionPages.size(); i++) {
            net.vulkanmod.config.option.OptionPage page = this.optionPages.get(i);
            int finalIdx = i;
            net.vulkanmod.config.gui.widget.VButtonWidget widget =
                    new net.vulkanmod.config.gui.widget.VButtonWidget(
                            x,
                            y,
                            width,
                            height,
                            net.minecraft.network.chat.Component.nullToEmpty(page.name),
                            button -> {
                                setOptionList(finalIdx);
                            });
            this.buttons.add(widget);
            this.pageButtons.add(widget);
            addWidget(widget);
            if (verticalLayout) {
                y += height + 1;
            } else {
                x += width + 1;
            }
        }
        this.pageButtons.get(this.currentListIdx).setSelected(true);
    }

    private void buildPage() {
        this.buttons.clear();
        this.pageButtons.clear();
        clearWidgets();
        addPageButtons(20, 40, 80, 22, true);
        net.vulkanmod.config.gui.VOptionList currentList =
                this.optionPages.get(this.currentListIdx).getOptionList();
        addWidget(currentList);
        addButtons();
    }

    private void addButtons() {
        int buttonWidth =
                this.minecraft.font.width(net.minecraft.network.chat.CommonComponents.GUI_DONE)
                        + (2 * 10);
        int x0 = (this.width - buttonWidth) - 20;
        int y0 = (this.height - 20) - 7;
        this.doneButton =
                new net.vulkanmod.config.gui.widget.VButtonWidget(
                        x0,
                        y0,
                        buttonWidth,
                        20,
                        net.minecraft.network.chat.CommonComponents.GUI_DONE,
                        button -> {
                            this.minecraft.setScreen(this.parent);
                        });
        int buttonWidth2 =
                this.minecraft.font.width(
                                net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.buttons.apply"))
                        + (2 * 10);
        this.applyButton =
                new net.vulkanmod.config.gui.widget.VButtonWidget(
                        x0 - (buttonWidth2 + 5),
                        y0,
                        buttonWidth2,
                        20,
                        net.minecraft.network.chat.Component.translatable(
                                "vulkanmod.options.buttons.apply"),
                        button2 -> {
                            applyOptions();
                        });
        int buttonWidth3 =
                this.minecraft.font.width(
                                net.minecraft.network.chat.Component.translatable(
                                        "vulkanmod.options.buttons.kofi"))
                        + 10;
        int x02 = (this.width - buttonWidth3) - 20;
        this.supportButton =
                new net.vulkanmod.config.gui.widget.VButtonWidget(
                        x02,
                        6,
                        buttonWidth3,
                        20,
                        net.minecraft.network.chat.Component.translatable(
                                "vulkanmod.options.buttons.kofi"),
                        button3 -> {
                            net.minecraft.util.Util.getPlatform()
                                    .openUri("https://ko-fi.com/xcollateral");
                        });
        this.buttons.add(this.applyButton);
        this.buttons.add(this.doneButton);
        this.buttons.add(this.supportButton);
        addWidget(this.applyButton);
        addWidget(this.doneButton);
        addWidget(this.supportButton);
        if (net.vulkanmod.config.UpdateChecker.isUpdateAvailable()) {
            int buttonWidth4 =
                    this.minecraft.font.width(
                                    net.minecraft.network.chat.Component.translatable(
                                            "vulkanmod.options.buttons.update_available"))
                            + 10;
            net.vulkanmod.config.gui.widget.VButtonWidget updateButton =
                    new net.vulkanmod.config.gui.widget.VButtonWidget(
                            (x02 - buttonWidth4) - 5,
                            6,
                            buttonWidth4,
                            20,
                            net.minecraft.network.chat.Component.translatable(
                                            "vulkanmod.options.buttons.update_available")
                                    .withStyle(net.minecraft.ChatFormatting.UNDERLINE),
                            button4 -> {
                                net.minecraft.util.Util.getPlatform()
                                        .openUri("https://modrinth.com/mod/vulkanmod");
                            });
            this.buttons.add(updateButton);
            addWidget(updateButton);
        }
    }

    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        for (net.minecraft.client.gui.components.events.GuiEventListener element : children()) {
            if (element.mouseClicked(event, bl)) {
                setFocused(element);
                if (event.button() == 0) {
                    setDragging(true);
                }
                updateState();
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        setDragging(false);
        updateState();
        return getChildAt(event.x(), event.y())
                .filter(
                        guiEventListener -> {
                            return guiEventListener.mouseReleased(event);
                        })
                .isPresent();
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public void render(
            net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        net.vulkanmod.config.gui.render.GuiRenderer.guiGraphics = guiGraphics;
        net.vulkanmod.vulkan.VRenderSystem.enableBlend();
        guiGraphics.blit(
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                this.ICON,
                42,
                4,
                0.0f,
                0.0f,
                36,
                36,
                36,
                36);
        net.vulkanmod.config.gui.VOptionList currentList =
                this.optionPages.get(this.currentListIdx).getOptionList();
        currentList.updateState(mouseX, mouseY);
        currentList.renderWidget(mouseX, mouseY);
        renderButtons(mouseX, mouseY);
        java.util.List<net.minecraft.util.FormattedCharSequence> list =
                getHoveredButtonTooltip(currentList, mouseX, mouseY);
        if (list != null) {
            renderTooltip(list, this.tooltipX, this.tooltipY);
        }
    }

    public void renderButtons(int mouseX, int mouseY) {
        for (net.vulkanmod.config.gui.widget.VButtonWidget button : this.buttons) {
            button.render(mouseX, mouseY);
        }
    }

    private void renderTooltip(
            java.util.List<net.minecraft.util.FormattedCharSequence> list, int x, int y) {
        int width = net.vulkanmod.config.gui.render.GuiRenderer.getMaxTextWidth(this.font, list);
        int height = list.size() * 10;
        int color = net.vulkanmod.vulkan.util.ColorUtil.ARGB.pack(0.05f, 0.05f, 0.05f, 0.6f);
        net.vulkanmod.config.gui.render.GuiRenderer.fill(
                x - 3, y - 3, x + width + 3, y + height + 3, color);
        int color2 = RED;
        net.vulkanmod.config.gui.render.GuiRenderer.renderBorder(
                x - 3, y - 3, x + width + 3, y + height + 3, 1, color2);
        int yOffset = 0;
        for (net.minecraft.util.FormattedCharSequence text : list) {
            net.vulkanmod.config.gui.render.GuiRenderer.drawString(
                    this.font, text, x, y + yOffset, -1);
            yOffset += 10;
        }
    }

    private java.util.List<net.minecraft.util.FormattedCharSequence> getHoveredButtonTooltip(
            net.vulkanmod.config.gui.VOptionList buttonList, int mouseX, int mouseY) {
        net.minecraft.network.chat.Component tooltip;
        net.vulkanmod.config.gui.widget.VAbstractWidget widget =
                buttonList.getHoveredWidget(mouseX, mouseY);
        if (widget == null || (tooltip = widget.getTooltip()) == null) {
            return null;
        }
        return this.font.split(tooltip, this.tooltipWidth);
    }

    private void updateState() {
        boolean modified = false;
        for (net.vulkanmod.config.option.OptionPage page : this.optionPages) {
            modified |= page.optionChanged();
        }
        if (modified) {
            for (net.vulkanmod.config.option.OptionPage page2 : this.optionPages) {
                page2.optionChanged();
            }
        }
        this.applyButton.active = modified;
    }

    private void setOptionList(int i) {
        this.currentListIdx = i;
        buildPage();
        this.pageButtons.get(i).setSelected(true);
    }

    private void applyOptions() {
        java.util.List<net.vulkanmod.config.option.OptionPage> pages =
                java.util.List.copyOf(this.optionPages);
        for (net.vulkanmod.config.option.OptionPage page : pages) {
            page.applyOptionChanges();
            page.updateOptionStates();
        }
        net.vulkanmod.Initializer.CONFIG.write();
    }
}
