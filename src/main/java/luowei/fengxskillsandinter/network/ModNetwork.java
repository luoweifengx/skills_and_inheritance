package luowei.fengxskillsandinter.network;

import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.spell.SpellCaster;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Hand;

public class ModNetwork {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(CastRequestC2SPayload.ID, CastRequestC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
            CastRequestC2SPayload.ID,    // 哪种包（按 ID 识别）
            (payload, context) -> {      // Handler：收到后执行这段
                context.server().execute(() -> {
                    var player = context.player();
                    var stack = player.getStackInHand(Hand.MAIN_HAND);
                    if(stack.getItem() instanceof WandItem){
                        if(player.getWorld().getTime() < WandItem.getCastingDelayEndsAt(stack)){
                            return;
                        }
                        if(player.getWorld().getTime() < WandItem.getRechargeDelayEndsAt(stack)){
                            return;
                        }
                        SpellCaster.castSpells(stack, player, player.getWorld());
                    }
                });
            }
        );
    }
}
