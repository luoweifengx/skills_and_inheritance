package luowei.fengxskillsandinter.spell;

import java.util.List;
import java.util.ArrayList;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.item.WandItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpellCaster {

    public static void castSpells(ItemStack wandStack, PlayerEntity player, World world) {
        SpellCastContext ctx = new SpellCastContext(wandStack);
        List<Integer> spellListIndices = new ArrayList<>();
        List<Spell> spellList = preSpellList(ctx, spellListIndices);
        if (spellList.isEmpty()) {
            return;
        }
        List<SpellNode> castRoots = sortSpellList(ctx, spellList, 0, wandStack);
        if (castRoots.isEmpty()) {
            return;
        }

        long totalCastingDelayTicks = (long) Math.ceil(ctx.castingDelay * 20);
        if (totalCastingDelayTicks <= 0) {
            totalCastingDelayTicks = 1;
        }
        WandItem.setCastingDelayEndsAt(wandStack, world.getTime() + totalCastingDelayTicks);
        WandItem.setCurrentCastingDelay(wandStack, Math.max(0.0, ctx.castingDelay));
        WandItem.setCurrentMana(wandStack, ctx.currentMana);

        if (spellListIndices.get(ctx.lastConsumedSpellListIndex) == -1) {
            long totalRechargeDelayTicks = (long) Math.ceil(ctx.currentRechargeDelay * 20);
            if (totalRechargeDelayTicks <= 0) {
                totalRechargeDelayTicks = 1;
            }
            WandItem.setRechargeDelayEndsAt(wandStack, world.getTime() + totalRechargeDelayTicks);
            WandItem.setCurrentRechargeDelay(wandStack, Math.max(0.0, ctx.currentRechargeDelay));
            WandItem.setSpellCachePointer(wandStack, 0);
        } else {
            WandItem.setCurrentRechargeDelay(wandStack, Math.max(0.0, ctx.currentRechargeDelay));
            WandItem.setSpellCachePointer(wandStack, spellListIndices.get(ctx.lastConsumedSpellListIndex) + 1);
        }

        cast(ctx, castRoots, player, world, player);
        ctx.resetModifiers();
    }

    private static List<Spell> preSpellList(SpellCastContext ctx, List<Integer> spellListIndices) {
        boolean flag = true;
        boolean flag_end = false;
        List<Spell> spellList = new ArrayList<>();
        
        if (ctx.spellCachePointer != 0) {
            for (int i = ctx.spellCachePointer; i != ctx.spellCachePointer || flag; i++) {
                flag = false;
                if (i == ctx.spells.size()) {
                    i = 0;
                    if (!spellListIndices.isEmpty()) {
                        spellListIndices.remove(spellListIndices.size() - 1);
                        spellListIndices.add(-1);
                    }
                    flag_end = true;
                }
                if (ctx.spells.get(i) != null && !ctx.spells.get(i).isEmpty()) {
                    spellList.add(SpellRegistry.getSpell(ctx.spells.get(i)));
                    if (flag_end) {
                        spellListIndices.add(-1);
                    } else {
                        spellListIndices.add(i);
                    }
                }  
            }
        } else {
            for (int i = 0; i < ctx.spells.size(); i++) {
                if (ctx.spells.get(i) != null && !ctx.spells.get(i).isEmpty()) {
                    spellList.add(SpellRegistry.getSpell(ctx.spells.get(i)));
                    spellListIndices.add(i);
                }
            }
            if (!spellListIndices.isEmpty()) {
                spellListIndices.remove(spellListIndices.size() - 1);
                spellListIndices.add(-1);
            }
        }
        return spellList;
    }

    /**
     * 将扁平槽位序列解析为施法树根节点列表（含嵌套触发子树）。
     */
    private static List<SpellNode> sortSpellList(SpellCastContext ctx, List<Spell> spellList, int i, ItemStack wandStack) {
        List<SpellNode> roots = new ArrayList<>();
        for (; ctx.drawCount > 0 && i < spellList.size(); ) {
            Spell spell = spellList.get(i);
            if (spell == null) {
                i++;
                continue;
            }
            int cost = spell.getDrawCost(wandStack);
            double spellcastingDelay = spell.getCastingDelay(wandStack);
            double spellRechargeDelay = spell.getRechargeDelay(wandStack);
            double manaCost = spell.getManaCost(wandStack);
            boolean isTrigger = spell.isTrigger();
            if (ctx.currentMana - manaCost < 0) {
                i++;
                continue;
            }
            ctx.currentMana -= manaCost;
            ctx.drawCount -= cost;
            ctx.castingDelay += spellcastingDelay;
            ctx.currentRechargeDelay += spellRechargeDelay;
            ctx.lastConsumedSpellListIndex = i;
            i++;
            if (isTrigger) {
                List<SpellNode> childTree = new ArrayList<>();
                i = sortSpellListTrigger(ctx, spellList, i, wandStack, childTree);
                roots.add(new SpellNode(spell, childTree));
            } else {
                roots.add(new SpellNode(spell, List.of()));
            }
        }
        return roots;
    }

    private static int sortSpellListTrigger(SpellCastContext ctx, List<Spell> spellList, int i,
            ItemStack wandStack, List<SpellNode> outNodes) {
        int triggerDrawCost = 1;
        for (; triggerDrawCost > 0 && i < spellList.size(); ) {
            Spell spell = spellList.get(i);
            if (spell == null) {
                i++;
                continue;
            }
            int cost = spell.getDrawCost(wandStack);
            double spellRechargeDelay = spell.getRechargeDelay(wandStack);
            double manaCost = spell.getManaCost(wandStack);
            if (ctx.currentMana - manaCost < 0) {
                i++;
                continue;
            }
            ctx.currentMana -= manaCost;
            triggerDrawCost -= cost;
            ctx.currentRechargeDelay += spellRechargeDelay;
            ctx.lastConsumedSpellListIndex = i;
            i++;
            if (spell.isTrigger()) {
                List<SpellNode> nested = new ArrayList<>();
                i = sortSpellListTrigger(ctx, spellList, i, wandStack, nested);
                outNodes.add(new SpellNode(spell, nested));
            } else {
                outNodes.add(new SpellNode(spell, List.of()));
            }
        }
        return i;
    }

    /**
     * 按深度优先顺序执行施法树：触发法术收到其子树，不再使用共享栈。
     */
    public static void cast(SpellCastContext ctx, List<SpellNode> roots, Entity caster, World world, Entity owner) {
        List<Spell> effectSpellList = castEffect(roots);
        for (SpellNode node : roots) {
            castNode(ctx, node, caster, world, effectSpellList);
        }
    }

    private static void castNode(SpellCastContext ctx, SpellNode node, Entity caster, World world, List<Spell> effectSpellList) {
        
        if (node == null || node.spell() == null) {
            return;
        }
        Spell spell = node.spell();
        if (spell.isTrigger()) {
            spell.cast(caster, world, node.children(), ctx, effectSpellList);
        } else {
            spell.cast(caster, world, ctx, effectSpellList);
        }
        FengxSkillsAndInheritance.LOGGER.info("spell: {}", spell.getClass().getName());
    }

    public static List<Spell> castEffect(List<SpellNode> roots) {
        List<Spell> effectSpellList = new ArrayList<>();
        collectEffects(roots, effectSpellList);
        return effectSpellList;
    }

    private static void collectEffects(List<SpellNode> nodes, List<Spell> out) {
        for (SpellNode n : nodes) {
            if (n.spell() != null && n.spell().isEffect()) {
                out.add(n.spell());
            }
            //collectEffects(n.children(), out);
        }
    }
}
