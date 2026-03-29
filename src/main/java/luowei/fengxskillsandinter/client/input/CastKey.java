package luowei.fengxskillsandinter.client.input;

import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.network.CastRequestC2SPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;

public class CastKey {//TODO：改变施法动画
    // public static final KeyBinding castKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
    //     "key.fengx_skills_and_inheritance.cast",
    //     InputUtil.Type.MOUSE,
    //     GLFW.GLFW_MOUSE_BUTTON_LEFT,
    //     "key.category.fengx_skills_and_inheritance"
    // ));
    private static boolean canSendThisTick = true;
    public static void register() {
        // 持法杖时取消原版左键逻辑（攻击、破坏方块、挥手）
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (player == null) return false;
            return player.getMainHandStack().getItem() instanceof WandItem;
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            canSendThisTick = true;
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // if (client.player == null || client.world == null) return;
            // var stack = client.player.getMainHandStack();
            // if (!(stack.getItem() instanceof WandItem)) return;
            // FengxSkillsAndInheritance.LOGGER.info("stack: " + stack.getItem().getName().getString());
            // if (!castKey.isPressed()) return;
            if (!canSendThisTick) return;
            canSendThisTick = false;
            if (client.player == null || client.world == null) return;//不用键位，因为只拦截了左键
            if (!client.options.attackKey.isPressed()) return;  // 用原版攻击键
    
            var stack = client.player.getMainHandStack();
            if (!(stack.getItem() instanceof WandItem)) return;
            if(client.world.getTime() < WandItem.getCastingDelayEndsAt(stack)){
                return;
            }
            if(client.world.getTime() < WandItem.getRechargeDelayEndsAt(stack)){
                return;
            }
            // if(false){
            //     return;
            // }
            // 发送到服务端，由服务端执行 SpellCaster.castSpells
            ClientPlayNetworking.send(new CastRequestC2SPayload());
            
        });
    }
}
