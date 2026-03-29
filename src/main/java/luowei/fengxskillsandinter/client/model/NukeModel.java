// Made with Blockbench 5.0.7
// Adapted for Minecraft 1.21.5 Fabric (Yarn mappings)
package luowei.fengxskillsandinter.client.model;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.Identifier;

public class NukeModel extends EntityModel<ProjectileEntityRenderState> {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
			Identifier.of(FengxSkillsAndInheritance.MOD_ID, "nuke"), "main");

	private final ModelPart bone4;
	private final ModelPart bone2;
	private final ModelPart bone;
	private final ModelPart bone3;
	public NukeModel(ModelPart root) {
		super(root);
		this.bone4 = root.getChild("bone4");
		this.bone2 = this.bone4.getChild("bone2");
		this.bone = this.bone4.getChild("bone");
		this.bone3 = this.bone4.getChild("bone3");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone4 = modelPartData.addChild("bone4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 24.0F, -2.0F, 0.0F, 0.0F, 0.0F));

		ModelPartData bone2 = bone4.addChild("bone2", ModelPartBuilder.create().uv(22, 29).cuboid(9.0F, -2.0F, -3.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(0, 23).cuboid(8.0F, -2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 40).cuboid(9.0F, -3.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(33, 26).cuboid(9.0F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(7, 40).cuboid(9.0F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(12, 44).cuboid(9.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(30, 16).cuboid(8.0F, -3.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(40, 30).cuboid(8.0F, -4.0F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(22, 34).cuboid(8.0F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(0, 35).cuboid(8.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(31, 29).cuboid(7.0F, -3.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(40, 26).cuboid(7.0F, -4.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(33, 21).cuboid(7.0F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(31, 34).cuboid(7.0F, 0.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(11, 23).cuboid(7.0F, -2.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(42, 20).cuboid(8.0F, -4.0F, -3.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(43, 34).cuboid(7.0F, -4.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F));

		ModelPartData bone = bone4.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.5F, -5.0F, 6.0F, 4.0F, 11.0F, new Dilation(0.0F))
		.uv(-1, 15).cuboid(-1.0F, -7.5F, -7.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F))
		.uv(14, 15).cuboid(-1.0F, -2.5F, -7.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F))
		.uv(22, 23).cuboid(-1.75F, -5.8F, 6.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(35, 38).cuboid(-1.0F, -6.5F, -7.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(2, 1).cuboid(-3.0F, -6.5F, -6.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 1).cuboid(2.0F, -6.5F, -6.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F));

		ModelPartData bone3 = bone4.addChild("bone3", ModelPartBuilder.create().uv(35, 0).cuboid(-10.0F, -2.0F, -3.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(0, 29).cuboid(-9.0F, -2.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(43, 40).cuboid(-10.0F, -3.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(17, 44).cuboid(-10.0F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 44).cuboid(-10.0F, -1.0F, -3.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(22, 44).cuboid(-10.0F, 0.0F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(35, 5).cuboid(-9.0F, -3.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(44, 0).cuboid(-9.0F, -4.0F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(9, 35).cuboid(-9.0F, -1.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(35, 10).cuboid(-9.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(39, 15).cuboid(-8.0F, -3.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(44, 4).cuboid(-8.0F, -4.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 39).cuboid(-8.0F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(27, 39).cuboid(-8.0F, 0.0F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
		.uv(11, 29).cuboid(-8.0F, -2.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(7, 44).cuboid(-9.0F, -4.0F, -3.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
		.uv(44, 8).cuboid(-8.0F, -4.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(ProjectileEntityRenderState state) {
		// Nuke 投射物目前无骨骼动画
	}
}