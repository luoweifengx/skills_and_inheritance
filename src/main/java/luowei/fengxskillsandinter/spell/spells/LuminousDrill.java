package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.LuminousDrillEntity;
import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LuminousDrill implements Spell{

    private static final double CASTING_DELAY = -0.55; 
    private static final double RECHARGE_DELAY = -0.15; 
    private static final double MANA_COST = 10; 
    private static final int DRAW_COST = 1; 
    public static final double DAMAGE = 7.0;

    private static Vec3d computeDrillSpawn(Entity caster, Vec3d lookVec) {
        Vec3d casterPos = caster.getPos();
        double eyeHeight = caster.getStandingEyeHeight();
        return casterPos.add(lookVec.multiply(SpellEntity.PLAYER_SPAWN_FORWARD_OFFSET).add(0, eyeHeight, 0));
    }

    @Override
    public void cast(Entity caster, World world) {
        if (!world.isClient) {
            Vec3d lookVec = SpellCastUtil.getCastDirection(caster);
            Vec3d spawnPos = computeDrillSpawn(caster, lookVec);
            LuminousDrillEntity luminousDrill = new LuminousDrillEntity(ModEntities.LUMINOUS_DRILL, world);
            luminousDrill.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            luminousDrill.setVelocity(Vec3d.ZERO);
            SpellCastUtil.applyFacingFromDirection(luminousDrill, lookVec);
            luminousDrill.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            world.spawnEntity(luminousDrill);
        }
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (!world.isClient) {
            Vec3d lookVec = SpellCastUtil.getCastDirection(caster);
            Vec3d spawnPos = computeDrillSpawn(caster, lookVec);
            LuminousDrillEntity luminousDrill = new LuminousDrillEntity(ModEntities.LUMINOUS_DRILL, world);
            luminousDrill.getAndSolveEffect(effectSpellList);
            luminousDrill.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            luminousDrill.setVelocity(Vec3d.ZERO);
            SpellCastUtil.applyFacingFromDirection(luminousDrill, lookVec);
            luminousDrill.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            world.spawnEntity(luminousDrill);
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
