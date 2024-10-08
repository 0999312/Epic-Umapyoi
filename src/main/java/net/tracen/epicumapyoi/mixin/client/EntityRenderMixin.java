package net.tracen.epicumapyoi.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import cn.mcmod_mmf.mmlib.utils.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import net.tracen.epicumapyoi.client.BedrockModelTransformer;
import net.tracen.epicumapyoi.client.BedrockModelTransformer.BedrockModelPartition;
import net.tracen.umapyoi.api.UmapyoiAPI;
import net.tracen.umapyoi.client.model.UmaPlayerModel;
import net.tracen.umapyoi.client.renderer.UmaSoulRenderer;
import net.tracen.umapyoi.utils.ClientUtils;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.mixin.MixinEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(PatchedLivingEntityRenderer.class)
public abstract class EntityRenderMixin<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject(method = "render", at = @At(value = "HEAD"), remap = false, cancellable = true)
	public void renderUMA(E entity, T entitypatch, R renderer, MultiBufferSource buffer, PoseStack poseStack,
			int packedLight, float partialTicks, CallbackInfo ci) {
		if(!UmapyoiAPI.getRenderingUmaSoul(entity).isEmpty()) {
			ItemStack umasoul = UmapyoiAPI.getRenderingUmaSoul(entity);
			
			RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(entity, entity.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
			MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			
			MixinEntityRenderer entityRendererAccessor = (MixinEntityRenderer)renderer;
			
			if ((entityRendererAccessor.invokeShouldShowName(entity) || renderNameplateEvent.getResult() == Result.ALLOW) && renderNameplateEvent.getResult() != Result.DENY) {
				entityRendererAccessor.invokeRenderNameTag(entity, renderNameplateEvent.getContent(), poseStack, buffer, packedLight);
			}
			
//			super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
			
//			Minecraft mc = Minecraft.getInstance();
//			MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer)renderer;
//			
//			boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(entity);
//			boolean isVisibleToPlayer = !isVisible && !entity.isInvisibleTo(mc.player);

			Armature armature = entitypatch.getArmature();
			poseStack.pushPose();
			((PatchedLivingEntityRenderer) (Object) this).mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
			
			OpenMatrix4f[] poseMatrices = ((PatchedLivingEntityRenderer) (Object) this).getPoseMatrices(entitypatch, armature, partialTicks, false);
//			AnimatedMesh mesh = BedrockModelTransformer.bakeMeshFromCubes(boxes);
//			if (renderType != null) {
//				AM mesh = (AM) (((PatchedLivingEntityRenderer) (Object) this).getMeshProvider(entitypatch)).get();
//				((PatchedLivingEntityRenderer) (Object) this).prepareModel(mesh, entity, entitypatch, renderer);
//				
//				PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
//				
//				if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
//					mesh.draw(poseStack, buffer, renderType, packedLight, 1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, poseMatrices);
//				}
//			}
//			
//			if (!entity.isSpectator()) {
//				this.renderLayer(renderer, entitypatch, entity, poseMatrices, buffer, poseStack, packedLight, partialTicks);
//			}
			UmaPlayerModel<LivingEntity> humanoidModel = new UmaPlayerModel<>();
			
	        ResourceLocation renderTarget = UmaSoulRenderer.getRenderTarget(umasoul, entity);
	        var pojo = ClientUtil.getModelPOJO(renderTarget);
	        if (humanoidModel.needRefresh(pojo))
	        	humanoidModel.loadModel(pojo);

			List<BedrockModelPartition> boxes = Lists.newArrayList();

			// Remove entity animation
			humanoidModel.head.loadPose(humanoidModel.head.getInitialPose());
			humanoidModel.hat.loadPose(humanoidModel.hat.getInitialPose());
			humanoidModel.body.loadPose(humanoidModel.body.getInitialPose());
			humanoidModel.leftArm.loadPose(humanoidModel.leftArm.getInitialPose());
			humanoidModel.rightArm.loadPose(humanoidModel.rightArm.getInitialPose());
			humanoidModel.leftLeg.loadPose(humanoidModel.leftLeg.getInitialPose());
			humanoidModel.rightLeg.loadPose(humanoidModel.rightLeg.getInitialPose());

			boxes.add(new BedrockModelPartition(BedrockModelTransformer.HEAD, humanoidModel.head, "head"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.HEAD, humanoidModel.hat, "hat"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.CHEST, humanoidModel.body, "body"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.RIGHT_ARM, humanoidModel.rightArm, "right_arm"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.LEFT_ARM, humanoidModel.leftArm, "left_arm"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.LEFT_LEG, humanoidModel.leftLeg, "left_leg"));
			boxes.add(new BedrockModelPartition(BedrockModelTransformer.RIGHT_LEG, humanoidModel.rightLeg, "right_leg"));
			
//			boxes.add(new BedrockModelPartition(BedrockModelTransformer.HEAD, humanoidModel.leftEar, "right_ear"));
//			boxes.add(new BedrockModelPartition(BedrockModelTransformer.HEAD, humanoidModel.rightEar, "left_ear"));
//			boxes.add(new BedrockModelPartition(BedrockModelTransformer.LEFT_FEET, humanoidModel.leftFoot, "left_foot"));
//			boxes.add(new BedrockModelPartition(BedrockModelTransformer.RIGHT_FEET, humanoidModel.rightLeg, "right_foot"));
			
			AnimatedMesh mesh = BedrockModelTransformer.bakeMeshFromCubes(boxes);
	        
			if (mesh != null) {

				mesh.draw(poseStack, buffer,
						RenderType.entityTranslucent(
								ClientUtils.getTexture(renderTarget), true),
						packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, poseMatrices);
		        if(humanoidModel.isEmissive()) {
					mesh.draw(poseStack, buffer,
							RenderType.entityTranslucentEmissive(
									ClientUtils.getTexture(renderTarget), true),
							packedLight, 1.0F, 1.0F, 1.0F, 1.0F, this.getOverlayCoord(entity, entitypatch, partialTicks), armature, poseMatrices);
		        }
		        
				if (!entity.isSpectator()) {
					this.invokeRenderLayer(renderer, entitypatch, entity, poseMatrices, buffer, poseStack, packedLight, partialTicks);
				}
		        
				if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
					for (Layer layer : entitypatch.getClientAnimator().getAllLayers()) {
						AnimationPlayer animPlayer = layer.animationPlayer;
						float playTime = animPlayer.getPrevElapsedTime() + (animPlayer.getElapsedTime() - animPlayer.getPrevElapsedTime()) * partialTicks;
						animPlayer.getAnimation().renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
					}
				}
				
				
			}
			
			poseStack.popPose();
			
			ci.cancel();
		}
	}
	
	protected int getOverlayCoord(E entity, T entitypatch, float partialTicks) {
		return OverlayTexture.pack(0, OverlayTexture.v(entity.hurtTime > 5));
	}

	@Invoker(value = "renderLayer", remap = false)
	public abstract void invokeRenderLayer(LivingEntityRenderer<E, M> renderer, T entitypatch, E entity, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks);
}
