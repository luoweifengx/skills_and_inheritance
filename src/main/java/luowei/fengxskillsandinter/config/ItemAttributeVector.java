package luowei.fengxskillsandinter.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 物品六维原料系数，顺序与 {@link ItemAttributeRegistry#DIMENSION_LABELS} 一致。
 */
public record ItemAttributeVector(double a, double b, double c, double d, double e, double f) {

    public static final int DIMENSIONS = 6;

    public static ItemAttributeVector uniform(double v) {
        return new ItemAttributeVector(v, v, v, v, v, v);
    }

    public static ItemAttributeVector fromArray(double[] values) {
        if (values == null || values.length != DIMENSIONS) {
            throw new IllegalArgumentException("expected " + DIMENSIONS + " values");
        }
        return new ItemAttributeVector(values[0], values[1], values[2], values[3], values[4], values[5]);
    }

    public double get(int index) {
        return switch (index) {
            case 0 -> a;
            case 1 -> b;
            case 2 -> c;
            case 3 -> d;
            case 4 -> e;
            case 5 -> f;
            default -> throw new IndexOutOfBoundsException(index);
        };
    }

    public double[] toArray() {
        return new double[] { a, b, c, d, e, f };
    }

    /** 供 {@link luowei.fengxskillsandinter.util.GenerateWand} 等使用 */
    public List<Double> toList() {
        List<Double> list = new ArrayList<>(DIMENSIONS);
        for (int i = 0; i < DIMENSIONS; i++) {
            list.add(get(i));
        }
        return list;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }
}
