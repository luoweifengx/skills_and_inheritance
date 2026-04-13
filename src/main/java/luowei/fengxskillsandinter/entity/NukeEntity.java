package luowei.fengxskillsandinter.entity;

import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.spell.spells.Nuke;

public class NukeEntity extends SpellEntity {

    private static final int PARTICLE_LOOP = 2;
    private static final double PARTICLE_DELTA = 0.01;

    /** 爆炸中心向外淡蓝射线（视觉近似，与原版爆炸采样不必一致）。 */
    private static final int BLAST_RAY_COUNT = 500;
    private static final double BLAST_RAY_STEP = 0.85;
    private static final double BLAST_VISUAL_RADIUS_FACTOR = 1.35;
    private static final double BLAST_VISUAL_RADIUS_CAP = 42.0;
    /** ARGB，淡青蓝。 */
    private static final DustParticleEffect BLAST_DUST = new DustParticleEffect(0xFF7DD3FF, 1.05f);

    public NukeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public NukeEntity(EntityType<? extends ProjectileEntity> entityType, World world, List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
    }

    private void explode(ServerWorld world) {
        Vec3d p = this.getPos();
        
        world.createExplosion(
                this,
                null,
                null,
                p.x,
                p.y,
                p.z,
                Nuke.EXPLOSION_POWER,
                false,
                World.ExplosionSourceType.TNT,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.ENTITY_GENERIC_EXPLODE);
        //spawnBlastRayParticles(world, p);
    }

    /**
     * 沿多方向步进撒淡蓝 dust，半径与 {@link Nuke#EXPLOSION_POWER} 大致挂钩，粒子量适中。
     */
    private static void spawnBlastRayParticles(ServerWorld world, Vec3d center) {
        double radius = Math.min(Nuke.EXPLOSION_POWER * BLAST_VISUAL_RADIUS_FACTOR, BLAST_VISUAL_RADIUS_CAP);
        for (int i = 0; i < BLAST_RAY_COUNT; i++) {
            double y = 1.0 - (2.0 * (i + 0.5)) / BLAST_RAY_COUNT;
            y = Math.max(-1.0, Math.min(1.0, y));
            double ring = Math.sqrt(Math.max(0.0, 1.0 - y * y));
            double theta = Math.PI * (3.0 - Math.sqrt(5.0)) * i;
            double x = ring * Math.cos(theta);
            double z = ring * Math.sin(theta);
            Vec3d dir = new Vec3d(x, y, z);
            for (double t = 0.0; t <= radius; t += BLAST_RAY_STEP) {
                Vec3d pos = center.add(dir.multiply(t));
                world.spawnParticles(BLAST_DUST, pos.x, pos.y, pos.z, 1, 0.06, 0.06, 0.06, 0.0);
            }
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder dataTracker) {
        super.initDataTracker(dataTracker);
    }

    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw) {
            this.explode(sw);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if (hitResult.getEntity() == this.getOwner()) {
            return;
        }
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw) {
            this.explode(sw);
            this.discard();
        }
    }

    @Override
    protected double getGravity() {
        return Nuke.PROJECTILE_GRAVITY;
    }

    @Override
    public void renderParticles() {
        if (this.getWorld().isClient) {
            for (int i = 0; i < PARTICLE_LOOP; i++) {
                this.getWorld().addParticleClient(
                        ParticleTypes.SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        PARTICLE_DELTA,
                        PARTICLE_DELTA,
                        PARTICLE_DELTA);
                this.getWorld().addParticleClient(
                        ParticleTypes.WHITE_SMOKE,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        PARTICLE_DELTA,
                        PARTICLE_DELTA,
                        PARTICLE_DELTA);
            }
        }
    }
}
