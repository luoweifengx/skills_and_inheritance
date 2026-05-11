package luowei.fengxskillsandinter.entity;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
//跟实体注册相关
public class ModEntities {
    private static final int PROJECTILE_TRACKING_RANGE = 8;
    private static final int PROJECTILE_TRACKING_INTERVAL = 1;

    public static RegistryKey<EntityType<?>> SPARK_PROJECTILE_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "spark_projectile"));
    public static RegistryKey<EntityType<?>> NUKE_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "nuke"));
    public static RegistryKey<EntityType<?>> CHAINSAW_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "chainsaw"));
    public static RegistryKey<EntityType<?>> SPARK_PROJECTILE_TRIGGER_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "spark_projectile_trigger"));
    public static RegistryKey<EntityType<?>> LUMINOUS_DRILL_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "luminous_drill"));
    public static RegistryKey<EntityType<?>> DISC_BULLET_BIG_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "disc_bullet_big"));
    public static RegistryKey<EntityType<?>> BUBBLE_SHOT_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "bubble_shot"));
    public static RegistryKey<EntityType<?>> TELEPORT_PROJECTILE_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "teleport_projectile"));
    public static RegistryKey<EntityType<?>> REGENERATION_FIELD_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "regeneration_field"));
    public static RegistryKey<EntityType<?>> BLACK_HOLE_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(FengxSkillsAndInheritance.MOD_ID, "black_hole"));
    //RegistryKey.of(RegistryKeys.ITEM, Identifier.of(FengxSkillsAndInheritance.MOD_ID, name));
    
    public static final EntityType<LuminousDrillEntity> LUMINOUS_DRILL = 
        EntityType.Builder.<LuminousDrillEntity>create(
            LuminousDrillEntity::new,
            SpawnGroup.MISC
        )
        .dimensions(0.05f, 0.5f)  // 宽度、高度
        .maxTrackingRange(4) //最大追踪距离
        .trackingTickInterval(20) //追踪间隔
        .build(LUMINOUS_DRILL_KEY);
    public static final EntityType<SparkProjectileEntity> SPARK_PROJECTILE = 
        EntityType.Builder.<SparkProjectileEntity>create(
            SparkProjectileEntity::new,
            SpawnGroup.MISC
        )
        .dimensions(0.5f, 0.5f)  // 宽度、高度
        .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
        .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
        .build(SPARK_PROJECTILE_KEY);

    public static final EntityType<DiscBulletBigEntity> DISC_BULLET_BIG =
            EntityType.Builder.<DiscBulletBigEntity>create(DiscBulletBigEntity::new, SpawnGroup.MISC)
                    .dimensions(0.85f, 0.35f)
                    .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
                    .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
                    .build(DISC_BULLET_BIG_KEY);

    public static final EntityType<BubbleShotEntity> BUBBLE_SHOT =
            EntityType.Builder.<BubbleShotEntity>create(BubbleShotEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3f, 0.3f)
                    .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
                    .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
                    .build(BUBBLE_SHOT_KEY);

    public static final EntityType<NukeEntity> NUKE = 
        EntityType.Builder.<NukeEntity>create(
            NukeEntity::new,
            SpawnGroup.MISC
        )
        .dimensions(1.0f, 1.0f)  // 宽度、高度
        .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
        .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
        .build(NUKE_KEY);

    public static final EntityType<ChainsawEntity> CHAINSAW = 
        EntityType.Builder.<ChainsawEntity>create(
            ChainsawEntity::new,
            SpawnGroup.MISC
        )
        .dimensions(1.0f, 1.5f)  // 略增大判定盒：生成点更近时仍能覆盖脚边木方块
        .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
        .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
        .build(CHAINSAW_KEY);

    public static final EntityType<TeleportProjectileEntity> TELEPORT_PROJECTILE =
            EntityType.Builder.<TeleportProjectileEntity>create(TeleportProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.35f, 0.35f)
                    .maxTrackingRange(PROJECTILE_TRACKING_RANGE)
                    .trackingTickInterval(PROJECTILE_TRACKING_INTERVAL)
                    .build(TELEPORT_PROJECTILE_KEY);

    public static final EntityType<RegenerationFieldEntity> REGENERATION_FIELD_ENTITY =
            EntityType.Builder.<RegenerationFieldEntity>create(RegenerationFieldEntity::new, SpawnGroup.MISC)
                    .dimensions(0.2f, 0.2f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(1)
                    .build(REGENERATION_FIELD_ENTITY_KEY);

    public static final EntityType<BlackHoleEntity> BLACK_HOLE_ENTITY =
            EntityType.Builder.<BlackHoleEntity>create(BlackHoleEntity::new, SpawnGroup.MISC)
                    .dimensions(2f, 2f)
                    .maxTrackingRange(64)
                    .trackingTickInterval(1)
                    .build(BLACK_HOLE_KEY);
    // public static final EntityType<SparkProjectileTriggerEntity> SPARK_PROJECTILE_TRIGGER = 
    //     EntityType.Builder.<SparkProjectileTriggerEntity>create(
    //         SparkProjectileTriggerEntity::new,
    //         SpawnGroup.MISC
    //     )
    //     .dimensions(0.5f, 0.5f)  // 宽度、高度
    //     .maxTrackingRange(4) //最大追踪距离
    //     .trackingTickInterval(20) //追踪间隔
    //     .build(SPARK_PROJECTILE_TRIGGER_KEY);

    public static void registerModEntities() {
        //Identifier id = Identifier.of(FengxSkillsAndInheritance.MOD_ID, "spark_projectile");
        registerModEntity(SPARK_PROJECTILE, "spark_projectile");
        registerModEntity(DISC_BULLET_BIG, "disc_bullet_big");
        registerModEntity(BUBBLE_SHOT, "bubble_shot");
        registerModEntity(NUKE, "nuke");
        registerModEntity(CHAINSAW, "chainsaw");
        registerModEntity(LUMINOUS_DRILL, "luminous_drill");
        registerModEntity(TELEPORT_PROJECTILE, "teleport_projectile");
        registerModEntity(REGENERATION_FIELD_ENTITY, "regeneration_field");
        registerModEntity(BLACK_HOLE_ENTITY, "black_hole");
        //registerModEntity(SPARK_PROJECTILE_TRIGGER, "spark_projectile_trigger");
    }
    //公用通道，暂无用途
    public static EntityType<?> registerModEntity(EntityType<?> Type, String name) {
        Identifier id = Identifier.of(FengxSkillsAndInheritance.MOD_ID, name);
        return Registry.register(Registries.ENTITY_TYPE, id, Type);
        
    }
}