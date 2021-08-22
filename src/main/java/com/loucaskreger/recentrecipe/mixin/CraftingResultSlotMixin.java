package com.loucaskreger.recentrecipe.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.loucaskreger.recentrecipe.callback.CraftingResultCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow
    @Final
    private PlayerEntity player;

    @Shadow
    private int amount;

    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V"), cancellable = true)
    private void onItemCrafted(ItemStack stack, CallbackInfo ci) {
        ActionResult result = CraftingResultCallback.EVENT.invoker().interact(player, stack, amount);

        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
