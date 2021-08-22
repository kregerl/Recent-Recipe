package com.loucaskreger.recentrecipe.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public class MouseCallbacks {

    public static final Event<MouseButtonPressed> MOUSE_BUTTON_PRESSED_EVENT = EventFactory.createArrayBacked(MouseButtonPressed.class, listeners -> (button, action, modifiers) -> {
        for (MouseButtonPressed listener : listeners) {
            return listener.mouseButtonPressed(button, action, modifiers);
        }
        return ActionResult.PASS;
    });

    @FunctionalInterface
    public interface MouseButtonPressed {
        ActionResult mouseButtonPressed(int button, int action, int modifiers);
    }

}
