package com.dan.veildimension;

import com.dan.veildimension.entity.ShadeStalkerEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class VeilDimensionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.SHADE_STALKER, ShadeStalkerRenderer::new);

		VeilDimension.LOGGER.info("Shade Stalker renderer registered!");
	}

	// Renderer that uses zombie model
	private static class ShadeStalkerRenderer extends MobEntityRenderer<ShadeStalkerEntity, ZombieEntityModel<ShadeStalkerEntity>> {

		public ShadeStalkerRenderer(EntityRendererFactory.Context context) {
			super(context, new ZombieEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
		}

		@Override
		public Identifier getTexture(ShadeStalkerEntity entity) {
			return Identifier.of("veildimension", "textures/entity/shade_stalker.png");
		}
	}
}