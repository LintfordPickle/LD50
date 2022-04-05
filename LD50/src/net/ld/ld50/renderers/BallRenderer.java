package net.ld.ld50.renderers;

import net.ld.ld50.controllers.BallController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class BallRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Ball Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mBallTexture;
	private BallController mBallController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mBallController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BallRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mBallController = (BallController) lControllerManager.getControllerByNameRequired(BallController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		mBallTexture = pResourceManager.textureManager().loadTexture("TEXTURE_BALLS", "res//textures//textureBalls.png", entityGroupID());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mBallTexture = null;
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (isInitialized() == false)
			return;
		final var lTextureBatch = rendererManager().uiTextureBatch();

		final var lWidth = 16;
		final var lHeight = 16;

		lTextureBatch.begin(pCore.gameCamera());

		final var lActiveBalls = mBallController.ballManager().instances();
		final int lNumActiveBalls = lActiveBalls.size();
		for (int i = 0; i < lNumActiveBalls; i++) {
			final var lBall = lActiveBalls.get(i);

			final float lWorldPosX = lBall.worldPositionX;
			final float lWorldPosY = -lBall.worldPositionY;

			final var lShadowColor = ColorConstants.getBlackWithAlpha(0.25f);
			lTextureBatch.draw(mBallTexture, 0, 0, lWidth, lHeight, lWorldPosX + -lWidth - 2, lWorldPosY + -lHeight + 4, 32, 32, -0.25f, lShadowColor);
			lTextureBatch.draw(mBallTexture, 0, 0, lWidth, lHeight, lWorldPosX + -lWidth, lWorldPosY + -lHeight, 32, 32, -0.25f, ColorConstants.WHITE);

		}

		lTextureBatch.end();
	}

}
