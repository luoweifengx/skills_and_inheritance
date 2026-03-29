package luowei.fengxskillsandinter.event;

import luowei.fengxskillsandinter.item.WandItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class ManaCharge {
    public static void manaCharge() {
        ServerTickEvents.END_WORLD_TICK.register(ServerWorld -> {
            for(var player : ServerWorld.getPlayers()) {
                PlayerInventory inv = player.getInventory();
                for(int i = 0; i < inv.size(); i++) {
                    ItemStack stack = inv.getStack(i);
                    if(stack.getItem() instanceof WandItem) {
                        double manaChargeSpeed = WandItem.getManaChargeSpeed(stack) / 20;
                        double currentMana = WandItem.getCurrentMana(stack);
                        double maxinumMana = WandItem.getMaxinumMana(stack);
                        if(currentMana < maxinumMana) {
                            currentMana += manaChargeSpeed;
                        }
                        if(currentMana > maxinumMana){
                            currentMana = maxinumMana;
                        }
                        WandItem.setCurrentMana(stack, currentMana);
                        
                    }
                }
            }
        });
        
    }
}
