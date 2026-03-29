package luowei.fengxskillsandinter.spell.spells;

import luowei.fengxskillsandinter.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 魔力相关效果占位：可后续接回魔、增上限等逻辑（物品 id 为 {@code mana}）。
 */
public class ManaSpell implements Spell {

    private static final double CASTING_DELAY = 0.15;
    private static final double RECHARGE_DELAY = 0.0;
    private static final double MANA_COST = -30;
    private static final int DRAW_COST = 1;

    @Override
    public void cast(Entity caster, World world) {
    }

    @Override
    public double getCastingDelay(ItemStack stack) {
        return CASTING_DELAY;
    }

    @Override
    public double getRechargeDelay(ItemStack stack) {
        return RECHARGE_DELAY;
    }

    @Override
    public int getDrawCost(ItemStack stack) {
        return DRAW_COST;
    }

    @Override
    public double getManaCost(ItemStack stack) {
        return MANA_COST;
    }

    @Override
    public boolean isTrigger() {
        return false;
    }

    @Override
    public boolean isEffect() {
        return true;
    }
}
