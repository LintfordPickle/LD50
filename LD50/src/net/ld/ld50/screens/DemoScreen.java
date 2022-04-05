package net.ld.ld50.screens;

import org.lwjgl.glfw.GLFW;

import net.ld.ld50.data.GameState;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class DemoScreen extends Screen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Texture mIntroTextTexture;
	private Texture mAnykeyTextTexture;

	private boolean mIsInDemoMode;
	private float mCurrentPositionY;

	private GameState mCurrentGameState;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DemoScreen(ScreenManager pScreenManager, GameState pGameState) {
		super(pScreenManager);

		mShowBackgroundScreens = true;
		mCurrentGameState = pGameState;

		mIsInDemoMode = true;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		mIntroTextTexture = pResourceManager.textureManager().loadTexture("TEXTURE_INTRO_TEXT", "res//textures//textureIntroText.png", entityGroupID());
		mAnykeyTextTexture = pResourceManager.textureManager().loadTexture("TEXTURE_ANYKEY_TEXT", "res//textures//textureAnyKeyText.png", entityGroupID());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mIntroTextTexture = null;
		mAnykeyTextTexture = null;
	}

	@Override
	public void handleInput(LintfordCore pCore) {
		super.handleInput(pCore);

		final float lScrollSpeed = .125f;

		// TODO: :( hard-coded
		final float lScoreboardHeight = 100 * .5f;
		final float lTableHeight = 1280 * .5f;
		final float lWindowHeight = 800 * .5f;

		final float lTableExtents = lTableHeight - lWindowHeight + lScoreboardHeight;

		mCurrentPositionY = (float) pCore.gameTime().totalTimeMilli() * 0.001f;
		float y = (float) Math.sin(mCurrentPositionY * lScrollSpeed) * lTableExtents;
		y -= lScoreboardHeight;

		if (pCore.gameCamera() != null) {
			pCore.gameCamera().setPosition(0, y);
		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			screenManager.exitGame();
			return;
		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_SPACE) || pCore.input().mouse().isMouseLeftButtonDown()) {
			exitScreen();
			mIsInDemoMode = false;
			if (mCurrentGameState != null) {
				mCurrentGameState.startGame();
			}

			return;
		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		if (mIsInDemoMode == false)
			return;

		final var lTextureBatch = rendererManager.uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());

		lTextureBatch.draw(mIntroTextTexture, 0, 0, 800, 800, -400, -400, 800, 800, -0.01f, ColorConstants.WHITE);

		lTextureBatch.end();

	}

}
