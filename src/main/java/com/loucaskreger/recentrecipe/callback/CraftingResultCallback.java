package com.loucaskreger.recentrecipe.callback;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface CraftingResultCallback {

    Event<CraftingResultCallback> EVENT = EventFactory.createArrayBacked(CraftingResultCallback.class, (listeners) -> (player, itemStack, amount) -> {

        for (CraftingResultCallback listener : listeners) {
            ActionResult result = listener.interact(player, itemStack, amount);

            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult interact(PlayerEntity player, ItemStack itemStack, int amount);
}
