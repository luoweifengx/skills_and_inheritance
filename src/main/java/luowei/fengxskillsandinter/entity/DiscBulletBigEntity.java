package luowei.fengxskillsandinter.entity;

import java.util.List;

import luowei.fengxskillsandinter.util.SpellDamageUtil;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;


public class DiscBulletBigEntity extends SpellEntity {

    /** 略稀疏：锋利火花（暴击粒子） */
    private static final int CRIT_INTERVAL_TICKS = 4;
    private static final double CRIT_SPREAD = 0.28;
    private static final double CRIT_VEL = 0.015;
    /** 偶尔一层扫击闪光 */
    private static final int ENCHANT_Glint_INTERVAL_TICKS = 14;

    public DiscBulletBigEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public DiscBulletBigEntity(EntityType<? extends ProjectileEntity> entityType, World world,
            List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity == this.getOwner()) {
            return false;
        }
        if (entity == this) {
            return false;
        }
        if (entity instanceof SpellEntity) {
            return false;
        }
        if (entity instanceof ItemEntity) {
            return false;
        }
        return true;
    }
    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if(!this.getWorld().isClient) {
            if (hitResult.getEntity() instanceof LivingEntity target) {
                if(this.getWorld() instanceof ServerWorld world) {
                    SpellDamageUtil.applySpellProjectileDamage(world, target, this, damage);
                }
            }
        }
    }

    @Override
    public void renderParticles() {
        if (!this.getWorld().isClient) {
            return;
        }
        var world = this.getWorld();
        var random = world.random;
        double x = this.getX() + (random.nextDouble() - 0.5) * CRIT_SPREAD;
        double y = this.getBodyY(0.35) + (random.nextDouble() - 0.5) * 0.12;
        double z = this.getZ() + (random.nextDouble() - 0.5) * CRIT_SPREAD;
        if (this.age % CRIT_INTERVAL_TICKS == 0) {
            world.addParticleClient(
                    ParticleTypes.CRIT,
                    x,
                    y,
                    z,
                    CRIT_VEL,
                    CRIT_VEL,
                    CRIT_VEL);
        }
        if (this.age % ENCHANT_Glint_INTERVAL_TICKS == 0) {
            world.addParticleClient(
                    ParticleTypes.ENCHANTED_HIT,
                    this.getX(),
                    this.getBodyY(0.5),
                    this.getZ(),
                    0.0,
                    0.0,
                    0.0);
        }
    }

    @Override
    public void killEntity() {
        if(this.age >= 2000){
            this.discard();
        }
    }
    
}
