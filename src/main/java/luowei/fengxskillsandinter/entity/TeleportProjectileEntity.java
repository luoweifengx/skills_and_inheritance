package luowei.fengxskillsandinter.entity;

import java.util.List;

import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

/**
 * 传送魔弹：无模型，灰色粒子；命中实体/方块或存活满 2s 时将施法者传送到当前位置。
 */
public class TeleportProjectileEntity extends SparkProjectileEntity {

    private static final int LIFETIME_TICKS = 2 * 20;
    private static final int ASH_COUNT = 20;
    private static final int SMOKE_COUNT = 14;
    private static final int WHITE_SMOKE_COUNT = 12;
    /** 细小锐利感（暴击粒子） */
    private static final int CRIT_COUNT = 8;
    private static final double POS_SPREAD = 0.45;
    private static final double DRIFT = 0.035;

    public TeleportProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = 0.0f;
    }

    public TeleportProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world,
            List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
        this.damage = 0.0f;
    }

    @Override
    public void killEntity() {
        if (this.getWorld().isClient) {
            return;
        }
        if (this.age >= LIFETIME_TICKS) {
            teleportOwnerAndDiscard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if (!this.getWorld().isClient) {
            teleportOwnerAndDiscard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        if (!this.getWorld().isClient) {
            teleportOwnerAndDiscard();
        }
    }

    private void teleportOwnerAndDiscard() {
        Entity owner = this.getOwner();
        if (owner instanceof ServerPlayerEntity sp) {
            sp.requestTeleport(this.getX(), this.getY(), this.getZ());
        }
        this.discard();
    }

    @Override
    public void renderParticles() {
        if (!this.getWorld().isClient) {
            return;
        }
        spawnCloud(ASH_COUNT, ParticleTypes.ASH);
        spawnCloud(SMOKE_COUNT, ParticleTypes.SMOKE);
        spawnCloud(WHITE_SMOKE_COUNT, ParticleTypes.WHITE_SMOKE);
        for (int i = 0; i < CRIT_COUNT; i++) {
            double ox = (this.random.nextDouble() - 0.5) * POS_SPREAD;
            double oy = (this.random.nextDouble() - 0.5) * POS_SPREAD;
            double oz = (this.random.nextDouble() - 0.5) * POS_SPREAD;
            this.getWorld().addParticleClient(
                    ParticleTypes.CRIT,
                    this.getX() + ox,
                    this.getY() + oy,
                    this.getZ() + oz,
                    (this.random.nextDouble() - 0.5) * DRIFT * 2.0,
                    (this.random.nextDouble() - 0.5) * DRIFT * 2.0,
                    (this.random.nextDouble() - 0.5) * DRIFT * 2.0);
        }
    }

    private void spawnCloud(int count, ParticleEffect type) {
        for (int i = 0; i < count; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 2.0 * POS_SPREAD;
            double oy = (this.random.nextDouble() - 0.5) * 2.0 * POS_SPREAD;
            double oz = (this.random.nextDouble() - 0.5) * 2.0 * POS_SPREAD;
            double vx = (this.random.nextDouble() - 0.5) * DRIFT * 2.0;
            double vy = (this.random.nextDouble() - 0.5) * DRIFT * 2.0;
            double vz = (this.random.nextDouble() - 0.5) * DRIFT * 2.0;
            this.getWorld().addParticleClient(
                    type,
                    this.getX() + ox,
                    this.getY() + oy,
                    this.getZ() + oz,
                    vx,
                    vy,
                    vz);
        }
    }
}
