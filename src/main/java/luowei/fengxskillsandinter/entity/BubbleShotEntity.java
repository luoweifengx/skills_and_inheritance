package luowei.fengxskillsandinter.entity;

import java.util.List;

import luowei.fengxskillsandinter.spell.SpellCastContext;
import luowei.fengxskillsandinter.spell.SpellNode;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;


public class BubbleShotEntity extends SpellEntity {

    public BubbleShotEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BubbleShotEntity(EntityType<? extends ProjectileEntity> entityType, World world,
            List<SpellNode> triggerChildren, SpellCastContext context) {
        super(entityType, world, triggerChildren, context);
    }
}
