package net.ld.ld50.screens;

import org.lwjgl.glfw.GLFW;

import net.ld.ld50.controllers.BallController;
import net.ld.ld50.controllers.CameraChaseShakeController;
import net.ld.ld50.controllers.CameraTrackerController;
import net.ld.ld50.controllers.GameCollisionController;
import net.ld.ld50.controllers.GameStateController;
import net.ld.ld50.controllers.ParticleController;
import net.ld.ld50.controllers.PlungerController;
import net.ld.ld50.controllers.TableController;
import net.ld.ld50.data.GameState;
import net.ld.ld50.data.GameWorld;
import net.ld.ld50.data.particles.ParticleManager;
import net.ld.ld50.renderers.BallRenderer;
import net.ld.ld50.renderers.ParticleRenderer;
import net.ld.ld50.renderers.ScoreboardRenderer;
import net.ld.ld50.renderers.TableRenderer;
import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.renderers.debug.DebugBox2dDrawer;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;

public class GameScreen extends BaseGameScreen {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// Data
	private GameWorld mGameWorld;
	private GameState mGameState;
	private ParticleManager mParticleManager;

	// Controllers
	private ParticleController mParticleController;
	private Box2dWorldController mBox2dWorldController;
	private TableController mTableController;
	private BallController mBallController;
	private GameCollisionController mGameCollisionController;
	private PlungerController mPlungerController;
	private GameStateController mGameStateController;
	private CameraTrackerController mCameraTrackerController;
	private CameraChaseShakeController mCameraController;

	// Renderers
	private ParticleRenderer mParticleRenderer;
	private DebugBox2dDrawer mDebugBox2dDrawer;
	private TableRenderer mTableRenderer;
	private BallRenderer mBallRenderer;
	private ScoreboardRenderer mScoreboardRenderer;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mGameWorld = new GameWorld();
		mGameState = new GameState();
		mParticleManager = new ParticleManager(1024);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		createControllers();
		initializeControllers();

		mGameStateController.startNewGame();
	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		pResourceManager.textureManager().loadTexturesFromMetafile("res//textures//_meta.json", entityGroupID());

		createRenderers();
	}

	@Override
	public void handleInput(LintfordCore pCore) {

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_R)) {
			mDebugBox2dDrawer.isActive(!mDebugBox2dDrawer.isActive());
		}

		super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (pCoveredByOtherScreen)
			return;

		if (mGameState.isStarted()) {
			if (mGameState.hasEnded() && mGameState.isShowingEndingScreen() == false) {
				screenManager.addScreen(new GameOverScreen(screenManager, mGameState));
				mGameState.setShowingEndingScreen();
			}
		}

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			screenManager.addScreen(new DemoScreen(screenManager, mGameState));
			return;
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void createControllers() {
		final var lControllerManager = screenManager.core().controllerManager();

		mBox2dWorldController = new Box2dWorldController(lControllerManager, mGameWorld.box2dWorld(), entityGroupID());
		mTableController = new TableController(lControllerManager, mGameWorld.tableManager(), entityGroupID());
		mBallController = new BallController(lControllerManager, mGameWorld.ballManager(), entityGroupID());
		mCameraTrackerController = new CameraTrackerController(lControllerManager, mGameWorld.trackedCameraEntity(), entityGroupID());
		mGameCollisionController = new GameCollisionController(lControllerManager, mGameWorld.box2dWorld(), entityGroupID());
		mCameraController = new CameraChaseShakeController(lControllerManager, mGameCamera, mGameWorld.trackedCameraEntity(), entityGroupID());
		mPlungerController = new PlungerController(lControllerManager, entityGroupID());
		mParticleController = new ParticleController(lControllerManager, mParticleManager, entityGroupID());
		mGameStateController = new GameStateController(lControllerManager, mGameState, entityGroupID());
	}

	private void initializeControllers() {
		final var lCore = screenManager.core();

		mBox2dWorldController.initialize(lCore);
		mTableController.initialize(lCore);
		mBallController.initialize(lCore);
		mCameraTrackerController.initialize(lCore);
		mCameraController.initialize(lCore);
		mGameCollisionController.initialize(lCore);
		mPlungerController.initialize(lCore);
		// mScreenShakeController.initialize(lCore);
		mGameStateController.initialize(lCore);
		mParticleController.initialize(lCore);
	}

	private void createRenderers() {
		final var lRendererManager = rendererManager;
		final var lCore = screenManager.core();

		mTableRenderer = new TableRenderer(lRendererManager, entityGroupID());
		mTableRenderer.initialize(lCore);
		mParticleRenderer = new ParticleRenderer(lRendererManager, entityGroupID());
		mParticleRenderer.initialize(lCore);
		mBallRenderer = new BallRenderer(lRendererManager, entityGroupID());
		mBallRenderer.initialize(lCore);

		mDebugBox2dDrawer = new DebugBox2dDrawer(lRendererManager, mGameWorld.box2dWorld(), entityGroupID());

		mScoreboardRenderer = new ScoreboardRenderer(lRendererManager, entityGroupID());
		mScoreboardRenderer.initialize(lCore);
	}

}
