package luowei.fengxskillsandinter.spell.spells;

import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class XRay implements Spell{
    private static final double CASTING_DELAY = 0.20; // TODO: 平衡数值
    private static final double RECHARGE_DELAY = 0.45; // TODO: 平衡数值
    private static final double MANA_COST = 18.0; // TODO: 平衡数值
    private static final int DRAW_COST = 1; // TODO: 平衡数值
    private static final double START_SPEED = 1.0; // TODO: 平衡数值
    public static final double GRAVITY = 0.01;
    public static final double DAMAGE = 10.0;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        
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
        return false;
    }
}
