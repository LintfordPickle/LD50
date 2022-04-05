package net.ld.ld50.renderers;

import net.ld.ld50.controllers.ParticleController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class ParticleRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Particle Renderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleController mParticleController;
	private Texture mParticlesTexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return mParticleController != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mParticleController = (ParticleController) lControllerManager.getControllerByNameRequired(ParticleController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		mParticlesTexture = pResourceManager.textureManager().getTexture("TEXTURE_PARTICLES", entityGroupID());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mParticlesTexture = null;
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isInitialized())
			return;

		final var lTextureBatch = rendererManager().uiTextureBatch();

		final var lProjectiles = mParticleController.projectileManager().projectiles();
		final int lProjectileCount = lProjectiles.size();
		for (int i = 0; i < lProjectileCount; i++) {
			final var lProjectile = lProjectiles.get(i);
			if (lProjectile.isAssigned() == false)
				continue;

			final float lSrcX = lProjectile.sx;
			final float lSrcY = lProjectile.sy;
			final float lSrcW = lProjectile.sw;
			final float lSrcH = lProjectile.sh;

			final float lDstX = lProjectile.worldPositionX;
			final float lDstY = lProjectile.worldPositionY;
			final float lDstW = 16;
			final float lDstH = 16;

			final float angle = lProjectile.rotationInRadians;

			// This needs work - this is the center of the object on the screenm (i.e. the ball) ??
			final float lOffX = 16;
			final float lOffY = 16;

			final var lColor = ColorConstants.getColorWithRGBAMod(lProjectile.r, lProjectile.g, lProjectile.b, lProjectile.a, 1.f);

			// @formatter:off
			lTextureBatch.begin(pCore.gameCamera());
			lTextureBatch.drawAroundCenter(mParticlesTexture, 
					lSrcX, lSrcY, lSrcW, lSrcH, 
					lDstX - lOffX, lDstY - lOffY, lDstW,lDstH, 
					-0.4f, 
					angle, -lOffX, -lOffY,
					lProjectile.scale, 
					lColor);
			lTextureBatch.end();
			// @formatter:on

		}
	}
}
