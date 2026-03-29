package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class PublicEntityRenderer<T extends Entity> extends EntityRenderer<T,EntityRenderState>{

    public static final Identifier DIRECTORY = Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/");
    public static final Identifier SPARK_PROJECTILE = Identifier.of(DIRECTORY.toString() + "spark_projectile.png");
    public static final Identifier CHAINSAW = Identifier.of(DIRECTORY.toString() + "chainsaw.png");
    public static final Identifier LUMINOUS_DRILL = Identifier.of(DIRECTORY.toString() + "luminous_drill.png");
    public static final Identifier NUKE = Identifier.of(DIRECTORY.toString() + "nuke.png");
    public static final Identifier LIGHT_BULLET = Identifier.of(DIRECTORY.toString() + "light_bullet.png");

    public PublicEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    /*public record SpellRenderConfig(
        Identifier texture,
        float scale
        
    ){
        public static final SpellRenderConfig LIGHT_BULLET = new SpellRenderConfig(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/light_bullet.png"),
            1.0f
        );
        public static final SpellRenderConfig CHAINSAW = new SpellRenderConfig(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/chainsaw.png"),
            1.5f
        );
    }*/
}
