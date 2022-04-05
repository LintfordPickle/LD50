package net.ld.ld50.data;

public class GameState {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int MAX_LIVES = 3;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private IGameStateListener listener;

	public void setStateListener(IGameStateListener pListener) {
		listener = pListener;
	}

	private boolean mIsStarted;
	private boolean mHasEnded;
	private boolean mShowingEndingScreen;
	private int mScore;
	private int mLives;

	// -------
	public boolean mFuelSecured;
	public boolean mIsFuelValveOpen;
	public int mNumFuelLoaded;

	public boolean mIsCraneEnabled;
	public boolean mCargo0Loaded;
	public boolean mCargo1Loaded;
	public boolean mCargo2Loaded;
	public boolean mAllCargoSecure;

	public int mNumAsteroidsDestroyed;
	public boolean mIsLaunched;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isFuelSecured() {
		return mFuelSecured;
	}

	public boolean isAllCargoHit() {
		return mCargo0Loaded && mCargo1Loaded & mCargo2Loaded;
	}

	public boolean isReadyForLaunch() {
		return isCargoSecure() && isFuelSecured() && mNumAsteroidsDestroyed >= 10;
	}

	public boolean isCargoSecure() {
		return mAllCargoSecure;
	}

	public boolean isLaunched() {
		return mIsLaunched;
	}

	public boolean isStarted() {
		return mIsStarted;
	}

	public boolean hasEnded() {
		return mHasEnded;
	}

	public int score() {
		return mScore;
	}

	public int lives() {
		return mLives;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public GameState() {
		reset();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void startGame() {
		reset();
		mIsStarted = true;
		if (listener != null) {
			listener.newGameStarted();
		}
	}

	public void reset() {
		mScore = 0;
		mLives = MAX_LIVES;
		mIsStarted = false;
		mHasEnded = false;
		mShowingEndingScreen = false;

		mIsFuelValveOpen = false;
		mNumFuelLoaded = 0;

		mIsCraneEnabled = false;
		mCargo0Loaded = false;
		mCargo1Loaded = false;
		mCargo2Loaded = false;
		mAllCargoSecure = false;
		mFuelSecured = false;

		mNumAsteroidsDestroyed = 0;
		mIsLaunched = false;
	}

	public void increaseScore(int pAmt) {
		mScore += pAmt;
	}

	public void reduceLives() {
		if (lives() <= 0)
			mHasEnded = true;
		else
			mLives--;
	}

	public boolean isGameWon() {
		return mHasEnded && lives() > 0;
	}

	public void wonGame() {
		mHasEnded = true;
	}

	public boolean isShowingEndingScreen() {
		return mShowingEndingScreen;
	}

	public void setShowingEndingScreen() {
		mShowingEndingScreen = true;
	}
}
