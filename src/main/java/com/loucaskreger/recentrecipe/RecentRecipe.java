package com.loucaskreger.recentrecipe;

import net.fabricmc.api.ModInitializer;
import com.loucaskreger.recentrecipe.callback.CraftingResultCallback;
import com.loucaskreger.recentrecipe.callback.KeyCallbacks;
import com.loucaskreger.recentrecipe.callback.MouseCallbacks;
import com.loucaskreger.recentrecipe.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;

import java.util.Collection;

import static org.lwjgl.glfw.GLFW.*;

public class RecentRecipe implements ModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static Item lastItemCrafted;
    private static boolean wPressed;

    @Override
    public void onInitialize() {
        CraftingResultCallback.EVENT.register((player, stack, amount) -> {
            lastItemCrafted = stack.getItem();
            return ActionResult.SUCCESS;
        });
        KeyCallbacks.KEY_PRESSED_EVENT.register(this::onKeyPressed);
        KeyCallbacks.KEY_RELEASED_EVENT.register(this::onKeyReleased);
        MouseCallbacks.MOUSE_BUTTON_PRESSED_EVENT.register(this::onMouseClicked);
    }

    private ActionResult onMouseClicked(int button, int action, int modifiers) {
        ActionResult result = ActionResult.PASS;
        if (button == 0 && wPressed) {
            var player = mc.player;
            if (player != null) {
                var currentScreen = mc.currentScreen;
                if (currentScreen instanceof InventoryScreen inventoryScreen) {
                    result = ActionResult.SUCCESS;
                    var inv = (HandledScreenAccessor) inventoryScreen;
                    var slot = inv.getFocusedSlot();
                    if (slot != null) {
                        var stack = slot.getStack();
                        if (!stack.isEmpty()) {
                            int slotIndex = getFirstValidCraftingSlot(inventoryScreen, stack);
                            if (slot.getIndex() > 4) {
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot.getIndex(), 0, SlotActionType.PICKUP, mc.player);
                                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotIndex, 0, SlotActionType.PICKUP, mc.player);
                            }
                        }
                    }

                }
            }
        }
        return result;
    }


    private void onKeyPressed(int key, int modifiers) {
        if (key == GLFW_KEY_SPACE) {
            var player = mc.player;
            if (player != null && lastItemCrafted != null) {
                var openScreenHandler = player.currentScreenHandler;
                if (openScreenHandler instanceof PlayerScreenHandler || openScreenHandler instanceof CraftingScreenHandler) {
                    Collection<Recipe<?>> recipes = mc.player.networkHandler.getRecipeManager().values();
                    for (Recipe<?> recipe : recipes) {
                        if (recipe.getOutput().getItem() == lastItemCrafted) {
                            mc.interactionManager.clickRecipe(openScreenHandler.syncId, recipe, (modifiers & GLFW_MOD_SHIFT) == 1);

                        }
                    }
                }
            }
        } else if (key == GLFW_KEY_W) {
            wPressed = true;
        }
    }

    private void onKeyReleased(int key, int modifiers) {
        if (key == GLFW_KEY_W) {
            wPressed = false;
        }
    }


    /**
     * @param inv   - The player's inventory container
     * @param stack - The stack the mouse is hovering over
     * @return - Returns the first valid crafting slot index
     */
    private int getFirstValidCraftingSlot(InventoryScreen inv, ItemStack stack) {
        for (int i = 1; i <= 4; i++) {
            ItemStack inventoryItem = inv.getScreenHandler().slots.get(i).getStack();
            if (inventoryItem.isEmpty() || (inventoryItem.getItem().equals(stack.getItem()) && inventoryItem.getCount() < 64)) {
                return i;
            }
        }
        return -1;
    }


}
