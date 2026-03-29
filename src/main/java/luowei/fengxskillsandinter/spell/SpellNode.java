package luowei.fengxskillsandinter.spell;

import java.util.List;

/**
 * 施法树节点：非触发法术 {@code children} 为空；触发法术的 {@code children} 为其触发子树（有序）。
 */
public record SpellNode(Spell spell, List<SpellNode> children) {
    public SpellNode {
        children = List.copyOf(children);
    }
}
