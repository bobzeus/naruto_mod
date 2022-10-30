
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.block.Block;

import java.util.Iterator;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthGolem extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 407;
	public static final int ENTITYID_RANGED = 408;
	public EntityEarthGolem(ElementsNarutomodMod instance) {
		super(instance, 799);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "earth_golem"), ENTITYID)
						.name("earth_golem").tracker(64, 3, true).egg(-7835040, -11650001).build());
	}

	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while (itr.hasNext())
			ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderLiving(renderManager, new ModelRockGolem(), 1f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("narutomod:textures/golem_rock.png");
				}
			};
		});
	}
	public static class EntityCustom extends EntityMob {
		public EntityCustom(World world) {
			super(world);
			setSize(1f, 2.875f);
			experienceValue = 10;
			this.isImmuneToFire = false;
			setNoAI(!true);
			setCustomNameTag("Earth Golem");
			setAlwaysRenderNameTag(true);
			enablePersistence();
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIWander(this, 1));
			this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, (float) 6));
			this.tasks.addTask(3, new EntityAILookIdle(this));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		protected void playStepSound(BlockPos pos, Block blockIn) {
			this.playSound(
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.irongolem.step")),
					0.15f, 1);
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.LIGHTNING_BOLT)
				return false;
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			entity.startRiding(this);
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			ItemStack itemstack = entity.getHeldItem(hand);
			return true;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
			if (this.isBeingRidden()) {
				this.rotationYaw = entity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = entity.rotationPitch * 0.5F;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.jumpMovementFactor = this.getAIMoveSpeed() * 0.15F;
				this.renderYawOffset = entity.rotationYaw;
				this.rotationYawHead = entity.rotationYaw;
				this.stepHeight = 1.0F;
				if (entity instanceof EntityLivingBase) {
					this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
					float forward = ((EntityLivingBase) entity).moveForward;
					float strafe = ((EntityLivingBase) entity).moveStrafing;
					super.travel(strafe, 0, forward);
				}
				this.prevLimbSwingAmount = this.limbSwingAmount;
				double d1 = this.posX - this.prevPosX;
				double d0 = this.posZ - this.prevPosZ;
				float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
				if (f1 > 1.0F)
					f1 = 1.0F;
				this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
				this.limbSwing += this.limbSwingAmount;
				return;
			}
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.travel(ti, tj, tk);
		}
	}

	// Made with Blockbench 4.4.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelRockGolem extends ModelBase {
		private final ModelRenderer ironGolemHead;
		private final ModelRenderer hornRight;
		private final ModelRenderer hornLeft;
		private final ModelRenderer ironGolemBody;
		private final ModelRenderer ironGolemRightArm;
		private final ModelRenderer right_arm;
		private final ModelRenderer bone;
		private final ModelRenderer ironGolemLeftArm;
		private final ModelRenderer left_arm;
		private final ModelRenderer bone2;
		private final ModelRenderer ironGolemRightLeg;
		private final ModelRenderer right_leg;
		private final ModelRenderer right_leg2;
		private final ModelRenderer ironGolemLeftLeg;
		private final ModelRenderer left_leg;
		private final ModelRenderer left_leg2;
		public ModelRockGolem() {
			textureWidth = 128;
			textureHeight = 128;
			ironGolemHead = new ModelRenderer(this);
			ironGolemHead.setRotationPoint(0.0F, -12.0F, 0.0F);
			ironGolemHead.cubeList.add(new ModelBox(ironGolemHead, 32, 24, -4.0F, -10.0F, -7.5F, 8, 10, 8, 0.0F, false));
			hornRight = new ModelRenderer(this);
			hornRight.setRotationPoint(-1.35F, -10.5F, -5.4F);
			ironGolemHead.addChild(hornRight);
			setRotationAngle(hornRight, 0.0F, 0.0F, 0.5236F);
			hornRight.cubeList.add(new ModelBox(hornRight, 24, 24, -2.05F, -0.2F, -2.1F, 4, 2, 2, -0.01F, false));
			hornLeft = new ModelRenderer(this);
			hornLeft.setRotationPoint(1.35F, -10.5F, -5.4F);
			ironGolemHead.addChild(hornLeft);
			setRotationAngle(hornLeft, 0.0F, 0.0F, -0.5236F);
			hornLeft.cubeList.add(new ModelBox(hornLeft, 24, 24, -1.95F, -0.2F, -2.1F, 4, 2, 2, -0.01F, true));
			ironGolemBody = new ModelRenderer(this);
			ironGolemBody.setRotationPoint(0.0F, -12.0F, 0.0F);
			ironGolemBody.cubeList.add(new ModelBox(ironGolemBody, 0, 0, -9.0F, 0.0F, -6.0F, 18, 13, 11, 0.0F, false));
			ironGolemBody.cubeList.add(new ModelBox(ironGolemBody, 50, 52, -5.0F, 13.5F, -3.0F, 10, 8, 6, 0.5F, false));
			ironGolemRightArm = new ModelRenderer(this);
			ironGolemRightArm.setRotationPoint(-8.0F, -10.0F, -1.0F);
			right_arm = new ModelRenderer(this);
			right_arm.setRotationPoint(0.0F, 1.0F, 0.0F);
			ironGolemRightArm.addChild(right_arm);
			setRotationAngle(right_arm, 0.0F, -0.5236F, 0.1745F);
			right_arm.cubeList.add(new ModelBox(right_arm, 24, 42, -8.0F, -2.5F, -3.5F, 8, 8, 8, 0.0F, false));
			right_arm.cubeList.add(new ModelBox(right_arm, 48, 42, -6.0F, 5.5F, -2.5F, 4, 2, 6, 0.0F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-4.0F, 7.5F, 3.5F);
			right_arm.addChild(bone);
			setRotationAngle(bone, -0.2618F, 0.0F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 48, 42, -2.0F, 0.0F, -6.0F, 4, 2, 6, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 24, -4.0F, 2.0F, -7.0F, 8, 12, 8, 0.0F, false));
			ironGolemLeftArm = new ModelRenderer(this);
			ironGolemLeftArm.setRotationPoint(8.0F, -10.0F, -1.0F);
			left_arm = new ModelRenderer(this);
			left_arm.setRotationPoint(0.0F, 1.0F, 0.0F);
			ironGolemLeftArm.addChild(left_arm);
			setRotationAngle(left_arm, 0.0F, 0.5236F, -0.1745F);
			left_arm.cubeList.add(new ModelBox(left_arm, 24, 42, 0.0F, -2.5F, -3.5F, 8, 8, 8, 0.0F, true));
			left_arm.cubeList.add(new ModelBox(left_arm, 48, 42, 2.0F, 5.5F, -2.5F, 4, 2, 6, 0.0F, true));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(4.0F, 7.5F, 3.5F);
			left_arm.addChild(bone2);
			setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 48, 42, -2.0F, 0.0F, -6.0F, 4, 2, 6, 0.0F, true));
			bone2.cubeList.add(new ModelBox(bone2, 0, 24, -4.0F, 2.0F, -7.0F, 8, 12, 8, 0.0F, true));
			ironGolemRightLeg = new ModelRenderer(this);
			ironGolemRightLeg.setRotationPoint(-3.5F, 6.5F, 0.0F);
			right_leg = new ModelRenderer(this);
			right_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
			ironGolemRightLeg.addChild(right_leg);
			setRotationAngle(right_leg, -0.5236F, 0.5236F, 0.0F);
			right_leg.cubeList.add(new ModelBox(right_leg, 58, 0, -5.5F, 0.0F, -3.0F, 6, 10, 6, 0.0F, false));
			right_leg2 = new ModelRenderer(this);
			right_leg2.setRotationPoint(-2.5F, 10.0F, -3.0F);
			right_leg.addChild(right_leg2);
			setRotationAngle(right_leg2, 0.5236F, 0.0F, 0.0F);
			right_leg2.cubeList.add(new ModelBox(right_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.0F, false));
			ironGolemLeftLeg = new ModelRenderer(this);
			ironGolemLeftLeg.setRotationPoint(3.5F, 6.5F, 0.0F);
			left_leg = new ModelRenderer(this);
			left_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
			ironGolemLeftLeg.addChild(left_leg);
			setRotationAngle(left_leg, -0.5236F, -0.5236F, 0.0F);
			left_leg.cubeList.add(new ModelBox(left_leg, 58, 0, -0.5F, 0.0F, -3.0F, 6, 10, 6, 0.0F, true));
			left_leg2 = new ModelRenderer(this);
			left_leg2.setRotationPoint(2.5F, 10.0F, -3.0F);
			left_leg.addChild(left_leg2);
			setRotationAngle(left_leg2, 0.5236F, 0.0F, 0.0F);
			left_leg2.cubeList.add(new ModelBox(left_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.0F, true));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			ironGolemHead.render(f5);
			ironGolemBody.render(f5);
			ironGolemRightArm.render(f5);
			ironGolemLeftArm.render(f5);
			ironGolemRightLeg.render(f5);
			ironGolemLeftLeg.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		}
	}
}
