package net.vulkanmod.config.option;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.vulkanmod.config.gui.widget.OptionWidget;
import net.vulkanmod.config.gui.widget.SwitchOptionWidget;

public class SwitchOption extends Option<Boolean> {
    public SwitchOption(Component name, Consumer<Boolean> setter, Supplier<Boolean> getter) {
        super(name, setter, getter, i -> Component.nullToEmpty(String.valueOf(i)));
    }

    @Override
    public OptionWidget createWidget() {
        return new SwitchOptionWidget(this, this.name);
    }
}
