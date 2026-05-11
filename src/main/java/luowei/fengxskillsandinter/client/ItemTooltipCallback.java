package luowei.fengxskillsandinter.client;

import java.util.List;
import java.util.Locale;

import luowei.fengxskillsandinter.item.SpellItem;
import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 法术物品与魔杖的数值 Tooltip。
 */
public final class ItemTooltipCallback {

    private ItemTooltipCallback() {
    }

    public static void register() {
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(ItemTooltipCallback::appendTooltips);
    }

    private static void appendTooltips(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        Item item = stack.getItem();
        if (item instanceof SpellItem spellItem) {
            appendSpellTooltips(spellItem, stack, lines);
        } else if (item instanceof WandItem) {
            appendWandTooltips(stack, lines);
        }
    }

    private static void appendSpellTooltips(SpellItem spellItem, ItemStack stack, List<Text> lines) {
        Spell spell = SpellRegistry.getSpell(spellItem.getSpellId());
        if (spell == null) {
            lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.spell_unknown")
                    .formatted(Formatting.DARK_RED));
            return;
        }
        String recharge = formatSeconds(spell.getRechargeDelay(stack));
        String casting = formatSeconds(spell.getCastingDelay(stack));
        String mana = formatMana(spell.getManaCost(stack));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.spell_recharge", recharge)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.spell_casting", casting)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.spell_mana", mana)
                .formatted(Formatting.GRAY));
    }

    private static void appendWandTooltips(ItemStack stack, List<Text> lines) {
        String recharge = formatSeconds(WandItem.getRechargeDelay(stack));
        String casting = formatSeconds(WandItem.getCastingDelay(stack));
        String maxMana = formatMana(WandItem.getMaxinumMana(stack));
        String currentMana = formatMana(WandItem.getCurrentMana(stack));
        int capacity = WandItem.getCapacity(stack);
        int draw = WandItem.getDrawCount(stack);
        String chargeSpeed = formatMana(WandItem.getManaChargeSpeed(stack));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_recharge", recharge)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_casting", casting)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_mana_current", maxMana)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_capacity", capacity)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_draw", draw)
                .formatted(Formatting.GRAY));
        lines.add(Text.translatable("tooltip.fengx-skills-and-inheritance.wand_mana_charge_speed", chargeSpeed)
                .formatted(Formatting.GRAY));
    }

    private static String formatSeconds(double seconds) {
        return String.format(Locale.ROOT, "%.2f", seconds);
    }

    private static String formatMana(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "?";
        }
        if (Math.abs(value - Math.rint(value)) < 1.0E-6) {
            return String.format(Locale.ROOT, "%d", Math.round(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
