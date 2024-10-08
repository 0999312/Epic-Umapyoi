package net.tracen.epicumapyoi.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.ints.IntList;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.transformer.HumanoidModelTransformer;

@Mixin(value = HumanoidModelTransformer.PartTransformer.class)
public interface PartTransformerInvoker {
	@Invoker(value = "triangluatePolygon", remap = false)
	public static void invokeTriangluatePolygon(
			Map<MeshPartDefinition, IntList> indices, 
			MeshPartDefinition partDefinition, 
			HumanoidModelTransformer.PartTransformer.IndexCounter indexCounter) {
		throw new AssertionError();
	}
}
