package net.ld.ld50.controllers;

import net.ld.ld50.data.GameState;
import net.ld.ld50.data.IGameStateListener;
import net.ld.ld50.data.tables.TableLight;
import net.ld.unstable.controllers.SoundFxController;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.MathHelper;

public class GameStateController extends BaseController implements IGameStateListener {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Game State Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private final GameState mGameState;

	private TableController mTableController;
	private BallController mBallController;
	private SoundFxController mSoundFxController;

	private boolean mShowStateMessage;
	private String mStateMessage;
	private float mStateFlashTimer;
	private boolean mStateIsFlash;
	private float mStateMessageTimer;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean showStateMessage() {
		return mShowStateMessage;
	}

	public String stateMessage() {
		return mStateMessage;
	}

	public boolean messageFlash() {
		return mStateIsFlash;
	}

	public GameState gameState() {
		return mGameState;
	}

	// ---------------------------------------------

	public void enteredBlackHole() {
		if (mGameState.mIsLaunched) {
			setStateMessage("GRATS", 2000.f);
			mGameState.wonGame();
		} else {
			setStateMessage("LAUNCH FIRST", 2000.f);
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_NO);
		}
	}

	public void launchHit() {
		if (mGameState.isCargoSecure() == false) {
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_NO);
			setStateMessage("CARGO NEEDED FIRST !!", 2000.f);
			return;
		}

		if (mGameState.isFuelSecured() == false) {
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_NO);
			setStateMessage("MOAH FUEL NEEDED !!", 2000.f);
			return;
		}

		if (mGameState.mNumAsteroidsDestroyed < 10) {
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_NO);
			setStateMessage("CLEAR PATH OF ASTEROIDS !!", 2000.f);
			return;
		}

		setLightStatus("LIGHT_LAUNCH", TableLight.LIGHT_STATE_GREEN);
		setStateMessage("LAUNCHED !!", 4000.f);
		mGameState.mIsLaunched = true;

	}

	private void updateBlockholeStatus() {
		if (mGameState.isLaunched()) {
			if (mGameState.isGameWon()) {
				setLightStatus("LIGHT_STARS", TableLight.LIGHT_STATE_GREEN);
			} else {
				setLightStatus("LIGHT_STARS", TableLight.LIGHT_STATE_YELLOW);
			}
		} else {
			setLightStatus("LIGHT_STARS", TableLight.LIGHT_STATE_GREY);
		}
	}

	private void updateLaunchStatus() {
		if (mGameState.isLaunched()) {
			setLightStatus("LIGHT_LAUNCH", TableLight.LIGHT_STATE_GREEN);
			updateBlockholeStatus();
		} else if (mGameState.isReadyForLaunch()) {
			setLightStatus("LIGHT_LAUNCH", TableLight.LIGHT_STATE_YELLOW);
		} else {
			setLightStatus("LIGHT_LAUNCH", TableLight.LIGHT_STATE_GREY);
		}
	}

	public boolean isFuelValueOpen() {
		return mGameState.mIsFuelValveOpen;
	}

	public void fuelValveHit() {
		mGameState.mIsFuelValveOpen = !mGameState.mIsFuelValveOpen;
		if (mGameState.mIsFuelValveOpen) {
			setStateMessage("FUEL VALVE OPEN", 1000);
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_YES);
			if (mGameState.mFuelSecured) {
				setLightStatus("LIGHT_VALVE", TableLight.LIGHT_STATE_GREEN);
			} else {
				setLightStatus("LIGHT_VALVE", TableLight.LIGHT_STATE_YELLOW);
			}
		} else {
			setStateMessage("FUEL VALUE CLOSED !!", 1000);
			setLightStatus("LIGHT_VALVE", TableLight.LIGHT_STATE_GREY);
		}
	}

	private void setLightStatus(String pLightName, int pLightStatus) {
		final var lTableLight = (TableLight) mTableController.getTablePropByName(pLightName);
		if (lTableLight != null)
			lTableLight.lightState = pLightStatus;
	}

	public void fuelButtonHit() {
		if (mGameState.mIsFuelValveOpen == false) {
			setStateMessage("OPEN VALVE FIRST", 1500.f);
			return;
		}

		mGameState.mNumFuelLoaded++;

		if (mGameState.mNumFuelLoaded >= 10) {
			mGameState.mFuelSecured = true;
			setLightStatus("LIGHT_VALVE", TableLight.LIGHT_STATE_GREEN);
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_YES);
			updateLaunchStatus();
		}

		final int lScoreModifier = MathHelper.clampi(mGameState.mNumFuelLoaded, 0, 10);
		setStateMessage("FUEL ADDED (" + mGameState.mNumFuelLoaded + ")", 1000 * lScoreModifier);
	}

	public int numFuelLoaded() {
		return mGameState.mNumFuelLoaded;
	}

	public boolean isCraneEnabled() {
		return mGameState.mIsCraneEnabled;
	}

	public void craneHit() {
		if (mGameState.mAllCargoSecure) {
			return;
		}

		mGameState.mIsCraneEnabled = !mGameState.mIsCraneEnabled;

		if (mGameState.mIsCraneEnabled == false) {
			mGameState.mCargo0Loaded = false;
			mGameState.mCargo1Loaded = false;
			mGameState.mCargo2Loaded = false;

			setStateMessage("CRANE DISENGAGED - CARGO LOST", 1000.0f);
			setLightStatus("LIGHT_CRANE", TableLight.LIGHT_STATE_GREY);
			setLightStatus("LIGHT_CARGO_0", TableLight.LIGHT_STATE_GREY);
			setLightStatus("LIGHT_CARGO_1", TableLight.LIGHT_STATE_GREY);
			setLightStatus("LIGHT_CARGO_2", TableLight.LIGHT_STATE_GREY);

		} else {
			setStateMessage("CRANE ENGAGED", 1000.0f);
			setLightStatus("LIGHT_CRANE", TableLight.LIGHT_STATE_YELLOW);
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_YES);
		}
	}

	public boolean isCargo0Loaded() {
		return mGameState.mCargo0Loaded;
	}

	public boolean isCargo1Loaded() {
		return mGameState.mCargo1Loaded;
	}

	public boolean isCargo2Loaded() {
		return mGameState.mCargo2Loaded;
	}

	public void cargoHit(int cargoNumber) {
		if (mGameState.mIsCraneEnabled == false) {
			setStateMessage("ENGAGE CRANE FIRST !!", 2000);
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_NO);
			return;
		}

		switch (cargoNumber) {
		case 0:
			mGameState.mCargo0Loaded = !mGameState.mCargo0Loaded;

			if (mGameState.mCargo0Loaded) {
				setLightStatus("LIGHT_CARGO_0", TableLight.LIGHT_STATE_GREEN);
				checkAllCargoSecure();
			} else
				setLightStatus("LIGHT_CARGO_0", TableLight.LIGHT_STATE_GREY);
			break;
		case 1:
			mGameState.mCargo1Loaded = !mGameState.mCargo1Loaded;
			if (mGameState.mCargo1Loaded) {
				setLightStatus("LIGHT_CARGO_1", TableLight.LIGHT_STATE_GREEN);
				checkAllCargoSecure();
			} else
				setLightStatus("LIGHT_CARGO_1", TableLight.LIGHT_STATE_GREY);
			break;
		case 2:
			mGameState.mCargo2Loaded = !mGameState.mCargo2Loaded;
			if (mGameState.mCargo2Loaded) {
				setLightStatus("LIGHT_CARGO_2", TableLight.LIGHT_STATE_GREEN);
				checkAllCargoSecure();
			} else
				setLightStatus("LIGHT_CARGO_2", TableLight.LIGHT_STATE_GREY);
			break;

		default:
		}
	}

	private void checkAllCargoSecure() {
		if (mGameState.isAllCargoHit()) {
			mGameState.mAllCargoSecure = true;
			setLightStatus("LIGHT_CRANE", TableLight.LIGHT_STATE_GREEN);

			updateLaunchStatus();
			return;
		}
	}

	public int numAsteroidsDestroyed() {
		return mGameState.mNumAsteroidsDestroyed;
	}

	public void asteroidDestroyed() {
		mGameState.mNumAsteroidsDestroyed++;

		updateLaunchStatus();

		setStateMessage(mGameState.mNumAsteroidsDestroyed + " ASTEROIDS DESTROYED", 1500);
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameStateController(ControllerManager pControllerManager, GameState pGameState, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mGameState = pGameState;
		mGameState.setStateListener(this);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		mBallController = (BallController) mControllerManager.getControllerByNameRequired(BallController.CONTROLLER_NAME, entityGroupID());
		mTableController = (TableController) mControllerManager.getControllerByNameRequired(TableController.CONTROLLER_NAME, entityGroupID());
		mSoundFxController = (SoundFxController) mControllerManager.getControllerByNameRequired(SoundFxController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mShowStateMessage) {
			if (mStateMessageTimer < 0.f) {
				mShowStateMessage = false;
			} else {
				mStateMessageTimer -= pCore.gameTime().elapsedTimeMilli();
				mStateFlashTimer -= pCore.gameTime().elapsedTimeMilli();

				if (mStateFlashTimer < 0.f) {
					mStateIsFlash = !mStateIsFlash;
					mStateFlashTimer = 200.f;
				}
			}
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startNewGame() {
		mGameState.startGame();
		setStateMessage("STARTING GAME", 5000);
		mBallController.startNewGame();
	}

	public void setStateMessage(String pMessage, float pMessageTimer) {
		mStateMessage = pMessage;
		mStateMessageTimer = pMessageTimer;
		mStateFlashTimer = 0;
		mStateIsFlash = true;
		mShowStateMessage = true;
	}

	@Override
	public void newGameStarted() {
		setLightStatus("LIGHT_VALVE", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_CRANE", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_CARGO_0", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_CARGO_1", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_CARGO_2", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_LAUNCH", TableLight.LIGHT_STATE_GREY);
		setLightStatus("LIGHT_STARS", TableLight.LIGHT_STATE_GREY);

	}
}
