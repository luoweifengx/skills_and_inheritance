package luowei.fengxskillsandinter.util;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 使用数据包注册的 {@code fengx-skills-and-inheritance:spell} 伤害类型（已加入 {@code #minecraft:bypasses_cooldown}），
 * 由原版逻辑绕过受伤后无敌帧，无需改 {@link LivingEntity} 内部字段。
 */
public final class SpellDamageUtil {

    public static final RegistryKey<DamageType> SPELL_DAMAGE_TYPE =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "spell"));

    private SpellDamageUtil() {
    }

    public static DamageSource spellProjectileSource(ServerWorld world, ProjectileEntity projectile) {
        RegistryEntry<DamageType> type = world.getRegistryManager()
                .getOrThrow(RegistryKeys.DAMAGE_TYPE)
                .getOrThrow(SPELL_DAMAGE_TYPE);
        return new DamageSource(type, projectile, projectile.getOwner());
    }

    public static void applySpellProjectileDamage(ServerWorld world, LivingEntity target, ProjectileEntity projectile, float amount) {
        target.damage(world, spellProjectileSource(world, projectile), amount);
    }
}
