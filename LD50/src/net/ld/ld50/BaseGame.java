package net.ld.ld50;

import org.lwjgl.opengl.GL11;

import net.ld.ld50.screens.DemoScreen;
import net.ld.ld50.screens.GameScreen;
import net.ld.unstable.controllers.SoundFxController;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.screenmanager.ScreenManager;

public class BaseGame extends LintfordCore {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final String WINDOW_TITLE = "Lintford's Pinball";
	private static final String APPLICATION_NAME = "Pinball";

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 800;

	private static final String POBJECTS_META = "res/pobjects/_meta.json";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ScreenManager mScreenManager;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BaseGame(GameInfo pGameInfo, String[] pArgs, boolean pHeadless) {
		super(pGameInfo, pArgs, pHeadless);

		mIsFixedTimeStep = true;
		mScreenManager = new ScreenManager(this);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected void oninitializeGL() {
		super.oninitializeGL();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	@Override
	protected void onInitializeApp() {
		super.onInitializeApp();

		mResourceManager.pobjectManager().definitionRepository().loadDefinitionsFromMetaFile(POBJECTS_META);

		mScreenManager.initialize();
	}

	@Override
	protected void onInitializeBitmapFontSources(BitmapFontManager pFontManager) {
		super.onInitializeBitmapFontSources(pFontManager);

		BitmapFontManager.CoreFonts.AddOrUpdate(BitmapFontManager.SYSTEM_FONT_CORE_TEXT_NAME, "res/fonts/fontCoreText.json");
		BitmapFontManager.CoreFonts.AddOrUpdate(BitmapFontManager.SYSTEM_FONT_CORE_TITLE_NAME, "res/fonts/fontCoreTitle.json");

		pFontManager.loadBitmapFont("FONT_SCOREBOARD", "res/fonts/fontHardPixelScore.json");
	}

	@Override
	protected void onLoadResources() {
		super.onLoadResources();

		var lSoundFxController = new SoundFxController(mControllerManager, mResourceManager.audioManager(), CORE_ENTITY_GROUP_ID);
		lSoundFxController.initialize(this);

		final var lAudioManager = mResourceManager.audioManager();
		lAudioManager.loadAudioFilesFromMetafile("res/audio/_meta.json");

		mScreenManager.addScreen(new GameScreen(mScreenManager));
		mScreenManager.addScreen(new DemoScreen(mScreenManager, null));

		mScreenManager.loadResources(mResourceManager);
	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

		mScreenManager.handleInput(this);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mScreenManager.update(this);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		GL11.glClearColor(3.0f / 255.0f, 9.0f / 255.0f, 37.0f / 255.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		mScreenManager.draw(this);
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
