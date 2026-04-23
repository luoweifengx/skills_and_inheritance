package luowei.fengxskillsandinter.util;

import java.util.ArrayList;
import java.util.List;

import luowei.fengxskillsandinter.config.ItemAttributeRegistry;
import luowei.fengxskillsandinter.config.ItemAttributeVector;
import luowei.fengxskillsandinter.item.WandItem;
import net.minecraft.item.ItemStack;

public class GenerateWand {

    public static final float ZHI = 0.3f;

    /** 与 {@link ItemAttributeRegistry#DIMENSION_LABELS} 一致：0 容量 1 蓝量 2 回蓝 3 施放数 4 充能 5 施放延迟 */
    public static void generateWand(ItemStack stack, List<ItemStack> items) {
        List<Double> raw = calculateNumToAttribute(items);
        List<Double> attributes = mapRegistryRawToSetWandList(raw);
        WandItem.setWand(stack, attributes);
    }

    /**
     * 将合成得到的六维（注册表顺序）映射为 {@link WandItem#setWand} 所需顺序：
     * [容量, 施法延迟, 充能延迟, 回蓝速度, 法力上限, 施放数]
     */
    public static List<Double> mapRegistryRawToSetWandList(List<Double> raw) {
        if (raw.size() != ItemAttributeVector.DIMENSIONS) {
            return List.of(2.0, 3.0, 3.0, 10.0, 50.0, 1.0);
        }
        double Lcap = clampLevel(raw.get(0));
        double Lcast = clampLevel(raw.get(1));
        double Lrech = clampLevel(raw.get(2));
        double LmanaChargeSpeed = clampLevel(raw.get(3));
        double Lmana = clampLevel(raw.get(4));
        double Ldraw = clampLevel(raw.get(5));
        

        int capacity = capacityFromLevel(Lcap);
        double castingDelay = delaySecondsFromLevel(Lcast);
        double rechargeDelay = delaySecondsFromLevel(Lrech);
        double maxMana = linearManaFromLevel(Lmana);
        double manaChargeSpeed = linearManaChargeSpeedFromLevel(LmanaChargeSpeed);
        int drawCount = (int) Math.round(Ldraw);

        List<Double> out = new ArrayList<>(6);
        out.add((double) capacity);
        out.add(castingDelay);
        out.add(rechargeDelay);
        out.add(manaChargeSpeed);
        out.add(maxMana);
        out.add((double) drawCount);
        return out;
    }

    /** 等级型输入限制在 [1, 10] */
    private static double clampLevel(double v) {
        if (v < 1.0) {
            return 1.0;
        }
        if (v > 10.0) {
            return 10.0;
        }
        return v;
    }

    /** 类 1/x：L=1→3s，L=10→0.05s */
    private static double delaySecondsFromLevel(double L) {
        return 59.0 / (18.0 * L) - 5.0 / 18.0;
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
            get = getValuesFromItem(items.get(i));
            for (int j = 0; j < att.size(); j++) {
                att.set(j, formula(get.get(j), att.get(j), ZHI));
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
