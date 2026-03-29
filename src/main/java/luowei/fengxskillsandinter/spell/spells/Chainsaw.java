package luowei.fengxskillsandinter.spell.spells;

import luowei.fengxskillsandinter.entity.ChainsawEntity;
import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Chainsaw implements Spell {

    private static final double CASTING_DELAY = -10000.0;
    private static final double RECHARGE_DELAY = -0.15;
    private static final double MANA_COST = 1.0;
    private static final int DRAW_COST = 1;
    public static final double DAMAGE = 4.0;

    @Override
    public void cast(Entity caster, World world) {
        if (!world.isClient) {
            Vec3d casterPos = caster.getPos();
            Vec3d lookVec = SpellCastUtil.getCastDirection(caster);
            double eyeHeight = caster.getStandingEyeHeight();
            Vec3d spawnPos = casterPos.add(lookVec.multiply(SpellEntity.PLAYER_SPAWN_FORWARD_OFFSET).add(0, eyeHeight, 0));
            ChainsawEntity chainsaw = new ChainsawEntity(ModEntities.CHAINSAW, world);
            chainsaw.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            chainsaw.setVelocity(Vec3d.ZERO);
            chainsaw.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            world.spawnEntity(chainsaw);
        }
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
