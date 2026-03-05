package net.vulkanmod.config.gui;

/* JADX INFO: loaded from: VulkanMod_1.21.11-0.6.0.jar:net/vulkanmod/config/gui/GuiElement.class */
public abstract class GuiElement implements net.minecraft.client.gui.components.events.GuiEventListener, net.minecraft.client.gui.narration.NarratableEntry {
    protected int width;
    protected int height;
    public int x;
    public int y;
    protected boolean hovered;
    protected long hoverStartTime;
    protected int hoverTime;
    protected long hoverStopTime;

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void updateState(double mX, double mY) {
        if (isMouseOver(mX, mY)) {
            if (!this.hovered) {
                this.hoverStartTime = net.minecraft.util.Util.getMillis();
            }
            this.hovered = true;
            this.hoverTime = (int) (net.minecraft.util.Util.getMillis() - this.hoverStartTime);
            return;
        }
        if (this.hovered) {
            this.hoverStopTime = net.minecraft.util.Util.getMillis();
        }
        this.hovered = false;
        this.hoverTime = 0;
    }

    public float getHoverMultiplier(float time) {
        if (this.hovered) {
            return java.lang.Math.min(this.hoverTime / time, 1.0f);
        }
        int delta = (int) (net.minecraft.util.Util.getMillis() - this.hoverStopTime);
        return java.lang.Math.max(1.0f - (delta / time), 0.0f);
    }

    @org.jetbrains.annotations.Nullable
    public net.minecraft.client.gui.ComponentPath nextFocusPath(net.minecraft.client.gui.navigation.FocusNavigationEvent focusNavigationEvent) {
        return null;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= ((double) this.x) && mouseY >= ((double) this.y) && mouseX <= ((double) (this.x + this.width)) && mouseY <= ((double) (this.y + this.height));
    }

    @org.jetbrains.annotations.Nullable
    public net.minecraft.client.gui.ComponentPath getCurrentFocusPath() {
        return null;
    }

    public net.minecraft.client.gui.navigation.ScreenRectangle getRectangle() {
        return new net.minecraft.client.gui.navigation.ScreenRectangle(this.x, this.y, this.width, this.height);
    }

    public void setFocused(boolean bl) {
    }

    public boolean isFocused() {
        return false;
    }

    public net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority narrationPriority() {
        return net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority.NONE;
    }

    public void updateNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
    }
}
