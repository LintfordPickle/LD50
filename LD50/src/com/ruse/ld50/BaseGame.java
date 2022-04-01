package com.ruse.ld50;

import org.lwjgl.opengl.GL11;

import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class BaseGame extends LintfordCore {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "LintfordCore Game";
	private static final String APPLICATION_NAME = "Game Template";

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private FontUnit mCoreText;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BaseGame(GameInfo pGameInfo, String[] pArgs, boolean pHeadless) {
		super(pGameInfo, pArgs, pHeadless);

		mIsFixedTimeStep = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void oninitializeGL() {
		super.oninitializeGL();

		// Enable depth testing
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		// Set the clear color to corn flower blue
		GL11.glClearColor(0.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);

	}

	@Override
	protected void onInitializeApp() {
		super.onInitializeApp();

	}

	@Override
	protected void onInitializeBitmapFontSources(BitmapFontManager pFontManager) {
		super.onInitializeBitmapFontSources(pFontManager);

		BitmapFontManager.CoreFonts.AddOrUpdate(BitmapFontManager.SYSTEM_FONT_CORE_TEXT_NAME, "res/fonts/fontCoreText.json");
		BitmapFontManager.CoreFonts.AddOrUpdate(BitmapFontManager.SYSTEM_FONT_CORE_TITLE_NAME, "res/fonts/fontCoreTitle.json");
	}

	@Override
	protected void onLoadResources() {
		super.onLoadResources();

		mCoreText = mResourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CORE_TITLE_NAME);
	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

	}

	@Override
	protected void onDraw() {
		super.onDraw();

		final var lTextScale = 1.f;
		final var lText = "Hello LintfordLibGame";
		final var lTextWidth = mCoreText.getStringWidth(lText, lTextScale);

		final float lTimeMod = 0.0005f;
		final float sin = (float) Math.sin(mGameTime.totalTimeMilli() * lTimeMod);
		final float cos = (float) Math.cos(mGameTime.totalTimeMilli() * lTimeMod);

		mCoreText.begin(mHUD);
		final float lMagnitude = 200.f;
		mCoreText.drawText(lText, (int) (-lTextWidth * .5f + (sin * lMagnitude)), (int) (0 + (sin * cos * lMagnitude)), -1.01f, 1.f);
		mCoreText.end();
	}

	// -------------------------------
	// Entry Point
	// -------------------------------

	public static void main(String[] args) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String windowTitle() {
				return WINDOW_TITLE;

			}

			@Override
			public String applicationName() {
				return APPLICATION_NAME;

			}

			@Override
			public int minimumWindowWidth() {
				return WINDOW_WIDTH;
			}

			@Override
			public int minimumWindowHeight() {
				return WINDOW_HEIGHT;
			}

			@Override
			public int baseGameResolutionWidth() {
				return WINDOW_WIDTH;
			}

			@Override
			public int baseGameResolutionHeight() {
				return WINDOW_HEIGHT;
			}

			@Override
			public boolean stretchGameResolution() {
				return false;
			}

			@Override
			public boolean windowResizeable() {
				return false;
			}

		};

		var lBaseGame = new BaseGame(lGameInfo, args, false);
		lBaseGame.createWindow();
	}
}
