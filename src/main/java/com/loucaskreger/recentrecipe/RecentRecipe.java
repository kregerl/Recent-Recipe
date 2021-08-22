package com.loucaskreger.recentrecipe;

import net.minecraft.block.StairsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;

@Mod(RecentRecipe.MOD_ID)
public class RecentRecipe {
    public static final String MOD_ID = "recentrecipe";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Minecraft mc = Minecraft.getInstance();
    private static Item lastItemCrafted;
    private static boolean wPressed = false;

    public RecentRecipe() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setupCommon);
        bus.addListener(this::setupClient);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
    }

    private void setupClient(final FMLClientSetupEvent event) {
    }

    @SubscribeEvent
    public void onItemCrafted(final PlayerEvent.ItemCraftedEvent event) {
        lastItemCrafted = event.getCrafting().getItem();
    }

    @SubscribeEvent
    public void onKeyPressed(final GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        Container openContainer = mc.player.containerMenu;
        if (event.getKeyCode() == GLFW.GLFW_KEY_SPACE && lastItemCrafted != null) {
            Collection<IRecipe<?>> recipes = mc.player.connection.getRecipeManager().getRecipes();
            for (IRecipe<?> recipe : recipes) {
                if (recipe.getResultItem().getItem() == lastItemCrafted) {
                    mc.gameMode.handlePlaceRecipe(openContainer.containerId, recipe, (event.getModifiers() & GLFW_MOD_SHIFT) == 1);
                }
            }
        }
        if (event.getKeyCode() == GLFW.GLFW_KEY_W) {
            wPressed = true;
        }
    }

    @SubscribeEvent
    public void onKeyPressed(final GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_W) {
            wPressed = false;
        }
    }

    @SubscribeEvent
    public void onMouseClicked(final GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (wPressed) {
            Screen screen = event.getGui();
            if (screen instanceof InventoryScreen) {
                event.setCanceled(true);
                InventoryScreen inv = ((InventoryScreen) screen);
                Slot slot = inv.getSlotUnderMouse();
                if (slot != null) {
                    ItemStack stack = inv.getSlotUnderMouse().getItem();
                    if (!stack.isEmpty()) {
                        int slotIndex = getFirstValidCraftingSlot(inv, stack);
                        if (slot.index > 4) {
                            mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, slot.index, 0, ClickType.PICKUP, mc.player);
                            mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, slotIndex, 0, ClickType.PICKUP, mc.player);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param inv   - The player's inventory container
     * @param stack - The stack the mouse is hovering over
     * @return - Returns the first valid crafting slot index
     */
    private int getFirstValidCraftingSlot(InventoryScreen inv, ItemStack stack) {
        for (int i = 1; i <= 4; i++) {
            ItemStack inventoryItem = inv.getMenu().slots.get(i).getItem();
            if (inventoryItem.isEmpty() || (inventoryItem.getItem().equals(stack.getItem()) && inventoryItem.getCount() < 64)) {
                return i;
            }
        }
        return -1;
    }


}
