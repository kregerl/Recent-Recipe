package com.loucaskreger.recentrecipe.mixin;

import com.loucaskreger.recentrecipe.callback.MouseCallbacks;
import net.minecraft.client.Mouse;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(at = @At("HEAD"), method = "onMouseButton(JIII)V", cancellable = true)
    private void onMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
        ActionResult result = MouseCallbacks.MOUSE_BUTTON_PRESSED_EVENT.invoker().mouseButtonPressed(button, action, modifiers);
        if (result == ActionResult.SUCCESS) {
            ci.cancel();
        }
    }
}
