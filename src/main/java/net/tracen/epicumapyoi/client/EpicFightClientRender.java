package net.tracen.epicumapyoi.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.tracen.umapyoi.events.client.RenderingUmaSoulEvent;
import yesman.epicfight.client.ClientEngine;

@EventBusSubscriber(Dist.CLIENT)
public class EpicFightClientRender {
	@SubscribeEvent
	public static void renderEpicFightSoul(RenderingUmaSoulEvent.Pre event) {
		ClientEngine clientEngine = ClientEngine.getInstance();
		if(clientEngine.isBattleMode()) {
			event.getModel().setAllVisible(false);
			event.setCanceled(true);
		}
	}

//	@SubscribeEvent
//	public static void modifyLayer(PatchedRenderersEvent.Modify event) {
//		if (event.get(EntityType.PLAYER) instanceof PatchedLivingEntityRenderer patchedlivingrenderer) {
//			patchedlivingrenderer.
//		}
//	}
}
