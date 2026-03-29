package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.NukeEntity;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Nuke implements Spell {

    /** 爆炸强度与投射物重力；{@link NukeEntity} 直接读取，勿在实体中重复写死。 */
    public static final float EXPLOSION_POWER = 25.0f;
    public static final double PROJECTILE_GRAVITY = 0.01;

    private static final double CASTING_DELAY = 0.35;
    private static final double RECHARGE_DELAY = 10.0;
    private static final double MANA_COST = 1.0;
    private static final int DRAW_COST = 1;
    private static final double START_SPEED = 3;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (!world.isClient) {
            Vec3d lookVec = SpellEntity.computeSpawnDirection(caster, world, context);
            Vec3d spawnPos = SpellEntity.computeSpawnPosition(caster, lookVec);
            NukeEntity projectile = new NukeEntity(ModEntities.NUKE, world);
            projectile.getAndSolveEffect(effectSpellList);
            lookVec = projectile.applyHeavySpreadToDirection(lookVec);
            projectile.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setVelocity(lookVec.multiply(START_SPEED));
            projectile.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            world.spawnEntity(projectile);
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
