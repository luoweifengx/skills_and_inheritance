package luowei.fengxskillsandinter.entity;

import java.util.List;

import luowei.fengxskillsandinter.util.SpellDamageUtil;
import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;


public class DiscBulletBigEntity extends SpellEntity {

    public DiscBulletBigEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public DiscBulletBigEntity(EntityType<? extends ProjectileEntity> entityType, World world,
            List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
    }

    @Override
    protected boolean canHit(Entity entity) {
        if(entity == this) {
            return false;
        }
        if (entity instanceof SpellEntity) {
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
    public void killEntity() {
        if(this.age >= 2000){
            this.discard();
        }
    }
    
}
