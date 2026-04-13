package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.BlackHoleEntity;
import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 黑洞：在施法者位置生成短时引力场，将附近生物拉向中心。
 */
public class BlackHole implements Spell {

    private static final double CASTING_DELAY = 0.3;
    private static final double RECHARGE_DELAY = 2.5;
    private static final double MANA_COST = 45.0;
    private static final int DRAW_COST = 1;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (world.isClient || caster == null) {
            return;
        }
        BlackHoleEntity hole = new BlackHoleEntity(ModEntities.BLACK_HOLE_ENTITY, world);
        hole.setPosition(caster.getX(), caster.getY() + 0.1, caster.getZ());
        world.spawnEntity(hole);
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
