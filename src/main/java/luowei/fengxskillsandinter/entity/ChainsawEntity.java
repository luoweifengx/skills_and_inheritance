package luowei.fengxskillsandinter.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import luowei.fengxskillsandinter.spell.spells.Chainsaw;

public class ChainsawEntity extends SpellEntity {

    private static final double BOX_EXPAND = 0.15;
    private static final double BLOCK_POS_EPSILON = 1.0E-7;
    /** 每 tick 火花数量（短寿命静止实体仅存活数 tick，总量 = 本值 × tick 数） */
    private static final int SPARKS_PER_TICK = 28;
    private static final double POS_SPREAD = 0.45;
    private static final double SPARK_VEL = 0.12;

    public ChainsawEntity(EntityType<? extends SpellEntity> entityType, World world) {
        super(entityType, world);
        this.damage = (float) Chainsaw.DAMAGE;
    }

    @Override
    protected boolean isShortLivedStationary() {
        return true;
    }

    @Override
    protected void tickShortLivedStationaryExtra() {
        World world = this.getWorld();
        if (!(world instanceof ServerWorld sw)) {
            return;
        }
        Box box = this.getBoundingBox().expand(BOX_EXPAND);
        BlockPos min = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.ofFloored(box.maxX - BLOCK_POS_EPSILON, box.maxY - BLOCK_POS_EPSILON, box.maxZ - BLOCK_POS_EPSILON);
        for (BlockPos pos : BlockPos.iterate(min, max)) {
            BlockState state = sw.getBlockState(pos);
            if (!state.isIn(BlockTags.AXE_MINEABLE)) {
                continue;
            }
            // drop=true：破坏时按战利品表掉落（false 则只碎方块不掉落）
            sw.breakBlock(pos, true, this);
        }
    }

    @Override
    public void renderParticles() {
        if (!this.getWorld().isClient) {
            return;
        }
        for (int i = 0; i < SPARKS_PER_TICK; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 2.0 * POS_SPREAD;
            double oy = this.random.nextDouble() * 0.35;
            double oz = (this.random.nextDouble() - 0.5) * 2.0 * POS_SPREAD;
            double vx = (this.random.nextDouble() - 0.5) * 2.0 * SPARK_VEL;
            double vy = this.random.nextDouble() * SPARK_VEL;
            double vz = (this.random.nextDouble() - 0.5) * 2.0 * SPARK_VEL;
            this.getWorld().addParticleClient(
                    ParticleTypes.ELECTRIC_SPARK,
                    this.getX() + ox,
                    this.getY() + oy,
                    this.getZ() + oz,
                    vx,
                    vy,
                    vz);
        }
        // 少量暴击粒子增强「金属切削」感
        for (int i = 0; i < 8; i++) {
            double ox = (this.random.nextDouble() - 0.5) * 0.5;
            double oy = this.random.nextDouble() * 0.25;
            double oz = (this.random.nextDouble() - 0.5) * 0.5;
            this.getWorld().addParticleClient(
                    ParticleTypes.CRIT,
                    this.getX() + ox,
                    this.getY() + oy,
                    this.getZ() + oz,
                    (this.random.nextDouble() - 0.5) * 0.15,
                    this.random.nextDouble() * 0.08,
                    (this.random.nextDouble() - 0.5) * 0.15);
        }
    }
}
