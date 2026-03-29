package luowei.fengxskillsandinter.entity;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import luowei.fengxskillsandinter.spell.spells.LuminousDrill;

public class LuminousDrillEntity extends SpellEntity {

    private static final double BOX_EXPAND = 0.15;
    private static final double BLOCK_POS_EPSILON = 1.0E-7;
    /** 低于此值的硬度视为不可破坏（原版为负值），跳过。 */
    private static final float UNBREAKABLE_HARDNESS_THRESHOLD = 0.0F;
    private static final int PARTICLE_COUNT = 2;
    private static final double PARTICLE_DELTA = 0.01;

    public LuminousDrillEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.damage = (float) LuminousDrill.DAMAGE;
    }

    public LuminousDrillEntity(EntityType<? extends ProjectileEntity> entityType, World world, List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
        this.damage = (float) LuminousDrill.DAMAGE;
    }

    @Override
    public void configureFromSpell(float spellDamage, double spellGravity) {
        super.configureFromSpell(spellDamage, 0.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder dataTracker) {
        super.initDataTracker(dataTracker);
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
            if (state.isAir()) {
                continue;
            }
            if (state.getHardness(sw, pos) < UNBREAKABLE_HARDNESS_THRESHOLD) {
                continue;
            }
            sw.breakBlock(pos, false, this);
        }
    }

    
}
