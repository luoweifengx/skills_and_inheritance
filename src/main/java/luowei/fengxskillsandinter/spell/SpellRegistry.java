package luowei.fengxskillsandinter.spell;

import java.util.HashMap;
import java.util.Map;

import luowei.fengxskillsandinter.item.ModItems;
import luowei.fengxskillsandinter.spell.spells.SparkProjectile;
import luowei.fengxskillsandinter.spell.spells.SparkProjectileTrigger;
import luowei.fengxskillsandinter.spell.spells.X2Spell;
import luowei.fengxskillsandinter.spell.spells.X3Spell;
import luowei.fengxskillsandinter.spell.spells.Chainsaw;
import luowei.fengxskillsandinter.spell.spells.DiscBulletBig;
import luowei.fengxskillsandinter.spell.spells.GravityAnti;
import luowei.fengxskillsandinter.spell.spells.BubbleShot;
import luowei.fengxskillsandinter.spell.spells.HeavySpread;
import luowei.fengxskillsandinter.spell.spells.HomingShooter;
import luowei.fengxskillsandinter.spell.spells.LarpaDownwards;
import luowei.fengxskillsandinter.spell.spells.Homing;
import luowei.fengxskillsandinter.spell.spells.StrongHoming;
import luowei.fengxskillsandinter.spell.spells.LuminousDrill;
import luowei.fengxskillsandinter.spell.spells.ManaSpell;
import luowei.fengxskillsandinter.spell.spells.Nuke;
import luowei.fengxskillsandinter.spell.spells.Recharge;
import luowei.fengxskillsandinter.spell.spells.BlackHole;
import luowei.fengxskillsandinter.spell.spells.RegenerationField;
import luowei.fengxskillsandinter.spell.spells.TeleportProjectile;
import luowei.fengxskillsandinter.spell.spells.X4Spell;
import net.minecraft.item.Item;

public class SpellRegistry {

    public static void registerSpells() {
        register("burst_2", new X2Spell(), ModItems.BURST_2);
        register("burst_3", new X3Spell(), ModItems.BURST_3);
        register("burst_4", new X4Spell(), ModItems.BURST_4);
        register("spark_projectile", new SparkProjectile(), ModItems.SPARK_PROJECTILE);
        register("chainsaw", new Chainsaw(), ModItems.CHAINSAW);
        register("spark_projectile_trigger", new SparkProjectileTrigger(), ModItems.SPARK_PROJECTILE_TRIGGER);
        register("heavy_spread", new HeavySpread(), ModItems.HEAVY_SPREAD);
        register("homing_shooter", new HomingShooter(), ModItems.HOMING_SHOOTER);
        register("homing", new Homing(), ModItems.HOMING);
        register("strong_homing", new StrongHoming(), ModItems.STRONG_HOMING);
        register("nuke", new Nuke(), ModItems.NUKE);
        register("luminous_drill", new LuminousDrill(), ModItems.LUMINOUS_DRILL);
        register("bubble_shot", new BubbleShot(), ModItems.BUBBLE_SHOT);
        register("disc_bullet_big", new DiscBulletBig(), ModItems.DISC_BULLET_BIG);
        register("teleport_projectile", new TeleportProjectile(), ModItems.TELEPORT_PROJECTILE);
        register("regeneration_field", new RegenerationField(), ModItems.REGENERATION_FIELD);
        register("black_hole", new BlackHole(), ModItems.BLACK_HOLE);
        register("recharge", new Recharge(), ModItems.RECHARGE);
        register("mana", new ManaSpell(), ModItems.MANA);
        register("gravity_anti", new GravityAnti(), ModItems.GRAVITY_ANTI);
        register("larpa_downwards", new LarpaDownwards(), ModItems.LARPA_DOWNWARDS);
    }

    private static final Map<String, Spell> map_spells = new HashMap<>(); 
    private static final Map<String, Item> map_items = new HashMap<>();

    public static void register(String id, Spell spell, Item item) {
        map_spells.put(id, spell);
        map_items.put(id, item);
    }

    public static Spell getSpell(String id) {
        return map_spells.get(id);
    }
    public static Item getItem(String id) {
        return map_items.get(id);
    }
}
