package luowei.fengxskillsandinter.util;

import java.util.ArrayList;
import java.util.List;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.config.ItemAttributeRegistry;
import luowei.fengxskillsandinter.config.ItemAttributeVector;
import luowei.fengxskillsandinter.item.WandItem;
import net.minecraft.item.ItemStack;

public class GenerateWand {

    public static final float ZHI = 0.3f;

    /** 与 {@link ItemAttributeRegistry#DIMENSION_LABELS} 原料六维顺序一致（非 {@link WandItem#setWand} 顺序）。 */
    public static void generateWand(ItemStack stack, List<ItemStack> items) {
        List<Double> raw = calculateNumToAttribute(items);
        List<Double> attributes = mapRegistryRawToSetWandList(raw);
        WandItem.setWand(stack, attributes, raw);
        //return raw;
    }

    /**
     * 将合成得到的六维（注册表顺序）映射为 {@link WandItem#setWand} 所需顺序：
     * [容量, 施法延迟, 充能延迟, 回蓝速度, 法力上限, 施放数]
     */
    public static List<Double> mapRegistryRawToSetWandList(List<Double> raw) {
        if (raw.size() != ItemAttributeVector.DIMENSIONS) {
            return List.of(2.0, 3.0, 3.0, 10.0, 50.0, 1.0);
        }
        double Lcap = levelAtLeastOne(raw.get(0));
        double Lcast = levelAtLeastOne(raw.get(1));
        double Lrech = levelAtLeastOne(raw.get(2));
        double LmanaChargeSpeed = levelAtLeastOne(raw.get(3));
        double Lmana = levelAtLeastOne(raw.get(4));
        double Ldraw = levelAtLeastOne(raw.get(5));

        int capacity = capacityFromLevel(Lcap);
        double castingDelay = delaySecondsFromLevel(Lcast);
        double rechargeDelay = delaySecondsFromLevel(Lrech);
        double maxMana = linearManaFromLevel(Lmana);
        double manaChargeSpeed = linearManaChargeSpeedFromLevel(LmanaChargeSpeed);
        int drawCount = drawCountFromLevel(Ldraw);

        List<Double> out = new ArrayList<>(6);
        out.add((double) capacity);
        out.add(castingDelay);
        out.add(rechargeDelay);
        out.add(manaChargeSpeed);
        out.add(maxMana);
        out.add((double) drawCount);
        return out;
    }

    /** 合成混标不低于 1；不再对上限 10 做截断。 */
    private static double levelAtLeastOne(double v) {
        return v < 1.0 ? 1.0 : v;
    }

    /**
     * L &lt; 13：沿用类 1/x；L ≥ 13：基础 0.05s，每高 1 级再减 0.05（可为负）。
     */
    private static double delaySecondsFromLevel(double L) {
        if (L >= 13.0) {
            return 0.05 - (L - 13.0) * 0.05;
        }
        return 59.0 / (18.0 * L) - 5.0 / 18.0;
    }

    /**
     * 1–3→1，4–6→2，7–9→3；L≥10：L=10→4、L=16→10（即 draw = round(L−6)，且不低于 4）。
     */
    private static int drawCountFromLevel(double L) {
        if (L < 4.0) {
            return 1;
        }
        if (L < 7.0) {
            return 2;
        }
        if (L < 10.0) {
            return 3;
        }
        return Math.max(4, (int) Math.round(L - 6.0));
    }

    /** 法力上限：L=1→50，L=10→2000（线性） */
    private static double linearManaFromLevel(double L) {
        return 50.0 + (L - 1.0) * (1950.0 / 9.0);
    }

    /** 法力充能速度：L=1→10，L=10→100（线性） */
    private static double linearManaChargeSpeedFromLevel(double L) {
        return 10.0 + (L - 1.0) * (90.0 / 9.0);
    }

    /** 容量：L=1 对应区间 [2,4] 中点；L=10 对应 [42,45] 中点的一次函数 */
    private static int capacityFromLevel(double L) {
        double capMin = 2.0 + (L - 1.0) * (40.0 / 9.0);
        double capMax = 4.0 + (L - 1.0) * (41.0 / 9.0);
        return (int) Math.round((capMin + capMax) / 2.0);
    }

    public static List<Double> calculateNumToAttribute(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return ItemAttributeRegistry.getDefaultVector().toList();
        }
        List<Double> get = getValuesFromItem(items.getFirst());
        List<Double> att = new ArrayList<>(get);
        for (int i = 1; i < items.size(); i++) {
            
            FengxSkillsAndInheritance.LOGGER.info("i: " + i);
            get = getValuesFromItem(items.get(i));
            for (int j = 0; j < att.size(); j++) {
                att.set(j, formula(get.get(j), att.get(j), ZHI));
                FengxSkillsAndInheritance.LOGGER.info("j: " + att.get(j));
            }
        }
        return att;
    }

    public static List<Double> getValuesFromItem(ItemStack item) {
        if (item.isEmpty()) {
            return ItemAttributeRegistry.getDefaultVector().toList();
        }
        return ItemAttributeRegistry.get(item).toList();
    }

    public static double formula(double x, double a, double b) {
        return Math.pow(2 * a, b) * Math.pow(x, 1 - b);
    }
}
