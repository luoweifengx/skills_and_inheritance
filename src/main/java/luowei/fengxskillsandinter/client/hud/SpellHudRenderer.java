package luowei.fengxskillsandinter.client.hud;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.item.WandItem;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class SpellHudRenderer {
    private SpellHudRenderer() {
    }

    public static void register() {
        Identifier layerId = Identifier.of(FengxSkillsAndInheritance.MOD_ID, "wand_spell_hud");
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(
            IdentifiedLayer.MISC_OVERLAYS,
            layerId,
            (drawContext, tickCounter) -> render(drawContext)));
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.options.hudHidden) {
            return;
        }

        ItemStack wand = findWand(client);
        if (wand.isEmpty()) {
            return;
        }

        long now = client.world.getTime();
        double mana = WandItem.getCurrentMana(wand);
        double maxMana = Math.max(1.0, WandItem.getMaxinumMana(wand));
        double manaRatio = clamp01(mana / maxMana);

        double rechargeRemaining = Math.max(0.0, WandItem.getRechargeDelayEndsAt(wand) - now);
        double rechargeBaseTicks = Math.max(1.0, WandItem.getCurrentRechargeDelay(wand) * 20.0);
        double rechargeRatio = progressFromRemaining(rechargeRemaining, rechargeBaseTicks);

        double castingRemaining = Math.max(0.0, WandItem.getCastingDelayEndsAt(wand) - now);
        double castingBaseTicks = Math.max(1.0, WandItem.getCurrentCastingDelay(wand) * 20.0);
        double castingRatio = progressFromRemaining(castingRemaining, castingBaseTicks);

        int x = 12;
        int y = context.getScaledWindowHeight() - 52;
        int width = 110;
        int barHeight = 5;
        int gap = 8;

        drawBar(context, x, y, width, barHeight, manaRatio, 0xAA202020, 0xAA28A8FF);
        drawBar(context, x, y + gap, width, barHeight, rechargeRatio, 0xAA202020, 0xAA45E35F);
        drawBar(context, x, y + gap * 2, width, barHeight, castingRatio, 0xAA202020, 0xAAFFB347);

        int capacity = WandItem.getCapacity(wand);
        int drawCount = WandItem.getDrawCount(wand);
        int rechargePercent = (int) Math.round(rechargeRatio * 100.0);
        int castingPercent = (int) Math.round(castingRatio * 100.0);
        String text = "Mana " + (int) mana + "/" + (int) maxMana
                + "  R " + rechargePercent + "%  C " + castingPercent + "%"
                + "  Cap " + capacity + "  Draw " + drawCount;
        context.drawText(client.textRenderer, Text.literal(text), x, y - 10, 0xFFFFFF, true);
    }

    private static ItemStack findWand(MinecraftClient client) {
        ItemStack main = client.player.getMainHandStack();
        if (main.getItem() instanceof WandItem) {
            return main;
        }
        ItemStack off = client.player.getOffHandStack();
        if (off.getItem() instanceof WandItem) {
            return off;
        }
        return ItemStack.EMPTY;
    }

    private static void drawBar(DrawContext context, int x, int y, int width, int height,
            double ratio, int bgColor, int fillColor) {
        context.fill(x, y, x + width, y + height, bgColor);
        int fill = (int) Math.round(width * clamp01(ratio));
        if (fill > 0) {
            context.fill(x, y, x + fill, y + height, fillColor);
        }
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    /** remaining -> progress(0..1)，按本次真实基准时长线性增长。 */
    private static double progressFromRemaining(double remainingTicks, double baseTicks) {
        if (remainingTicks <= 0.0) {
            return 1.0;
        }
        return clamp01(1.0 - remainingTicks / baseTicks);
    }
}
