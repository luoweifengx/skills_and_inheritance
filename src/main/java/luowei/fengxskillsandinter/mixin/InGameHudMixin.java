package luowei.fengxskillsandinter.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import luowei.fengxskillsandinter.experience.BlacksmithExperience;
import luowei.fengxskillsandinter.item.HammerItem;
import luowei.fengxskillsandinter.item.ModItems;

/**
 * InGameHud Mixin
 * 在饥饿值上方渲染铁匠等级（手持锤子时）
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @Shadow @Final private net.minecraft.client.MinecraftClient client;
    
    // 存储上一帧的等级，用于检测升级
    private static int lastBlacksmithLevel = -1;
    
    /**
     * 在渲染HUD时注入，在饥饿值上方渲染铁匠等级
     */
    @Inject(
        method = "renderStatusBars",
        at = @At("RETURN")
    )
    private void renderBlacksmithLevel(DrawContext context, CallbackInfo ci) {
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        
        PlayerEntity player = client.player;
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        
        // 检查是否手持锤子
        boolean holdingHammer = (mainHand.getItem() instanceof HammerItem) || 
                               (offHand.getItem() instanceof HammerItem);
        
        if (!holdingHammer) {
            lastBlacksmithLevel = -1; // 重置等级记录
            return;
        }
        
        // 获取铁匠等级和经验
        int level = BlacksmithExperience.getLevel(player);
        int experience = BlacksmithExperience.getExperience(player);
        
        // 调试：如果等级为0，也显示（至少显示一个图标表示0级）
        // 或者我们可以选择不显示0级，但至少应该显示等级信息用于调试
        
        // 检测升级并播放音效
        if (lastBlacksmithLevel >= 0 && level > lastBlacksmithLevel) {
            // 播放原版等级升级音效
            client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
        lastBlacksmithLevel = level;
        
        // 如果等级为0且经验也为0，可能数据还没同步，先不显示
        // 但如果有经验只是等级为0，也应该显示（0级也是有效等级）
        // 为了调试，我们总是显示，即使等级为0
        
        // 计算渲染位置（饥饿值上方）
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        
        // 饥饿值的位置：屏幕宽度/2 + 91, 屏幕高度 - 39
        int hungerX = screenWidth / 2 + 91;
        int hungerY = screenHeight - 39;
        
        // 铁匠等级显示在饥饿值上方，偏移20像素
        int blacksmithY = hungerY - 20;
        
        // 图标大小和间距
        int iconSize = 9;
        int iconSpacing = 1;
        
        // 检查IRON_HAMMER是否已注册
        if (ModItems.IRON_HAMMER == null) {
            // 如果物品还没注册，使用文本显示等级（调试用）
            String levelText = "等级: " + level + " (经验: " + experience + ")";
            context.drawText(client.textRenderer, levelText, hungerX - 100, blacksmithY, 0xFFFFFF, true);
            return;
        }
        
        // 现在最低等级是1级，所以level >= 1时都应该显示
        // 如果等级小于1（理论上不应该发生），不显示
        if (level < 1) {
            return;
        }
        
        // 计算总宽度（从右向左显示）
        int totalWidth = level * (iconSize + iconSpacing) - iconSpacing;
        int startX = hungerX - totalWidth; // 从右向左
        
        // 渲染锤子图标（使用铁锤物品图标）
        ItemStack hammerStack = ModItems.IRON_HAMMER.getDefaultStack();
        if (hammerStack.isEmpty()) {
            // 如果物品堆栈为空，使用文本显示（调试用）
            String levelText = "Lv:" + level;
            context.drawText(client.textRenderer, levelText, hungerX - 30, blacksmithY, 0xFFFFFF, true);
            return;
        }
        
        for (int i = 0; i < level; i++) {
            int x = startX + i * (iconSize + iconSpacing);
            // 使用drawItem绘制锤子图标
            context.drawItem(hammerStack, x, blacksmithY);
        }
    }
}

