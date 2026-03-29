package luowei.fengxskillsandinter.spell.spells;

import java.util.List;

import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.entity.SpellEntity;
import luowei.fengxskillsandinter.entity.TeleportProjectileEntity;
import luowei.fengxskillsandinter.spell.Spell;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.util.SpellCastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** 传送魔弹：沿视线发射，命中或 2s 后将玩家传送到弹丸位置。 */
public class TeleportProjectile implements Spell {

    private static final double CASTING_DELAY = 0.05;
    private static final double RECHARGE_DELAY = 0;
    private static final double MANA_COST = 40.0;
    private static final int DRAW_COST = 1;
    private static final double START_SPEED = 1.5;
    public static final double GRAVITY = 0.1;

    @Override
    public void cast(Entity caster, World world) {
        cast(caster, world, null, List.of());
    }

    @Override
    public void cast(Entity caster, World world, SpellCastContext context, List<Spell> effectSpellList) {
        if (!world.isClient && world instanceof ServerWorld sw) {
            Vec3d lookVec = SpellEntity.computeSpawnDirection(caster, world, context);
            Vec3d spawnPos = SpellEntity.computeSpawnPosition(caster, lookVec);
            TeleportProjectileEntity projectile = new TeleportProjectileEntity(ModEntities.TELEPORT_PROJECTILE, sw);
            projectile.getAndSolveEffect(effectSpellList);
            lookVec = projectile.applyHeavySpreadToDirection(lookVec);
            projectile.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setVelocity(lookVec.multiply(START_SPEED));
            projectile.setOwner(SpellCastUtil.resolveOwnerForProjectile(caster));
            projectile.configureFromSpell(0.0f, GRAVITY);
            sw.spawnEntity(projectile);
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
