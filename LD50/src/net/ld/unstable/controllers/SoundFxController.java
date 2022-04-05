package net.ld.unstable.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.AudioFireAndForgetManager;
import net.lintford.library.core.audio.AudioManager;

public class SoundFxController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Sound Fx Controller";

	public static final String AUDIO_NAME_EMPTY = "";
	public static final String AUDIO_NAME_HIT = "AUDIO_HIT";
	public static final String AUDIO_NAME_BLACKHOLE = "AUDIO_BLACKHOLE";
	public static final String AUDIO_NAME_FLIPPER = "AUDIO_FLIPPER";
	public static final String AUDIO_NAME_KICKER = "AUDIO_KICKER";
	public static final String AUDIO_NAME_PLUNGER = "AUDIO_PLUNGER";
	public static final String AUDIO_NAME_YES = "AUDIO_YES";
	public static final String AUDIO_NAME_NO = "AUDIO_NO";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private AudioFireAndForgetManager mAudioFireAndForgetManager;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mAudioFireAndForgetManager != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public SoundFxController(ControllerManager pControllerManager, AudioManager pAudioManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

		mAudioFireAndForgetManager = new AudioFireAndForgetManager(pAudioManager);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		mAudioFireAndForgetManager.acquireAudioSources(6);

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void playSound(String pSoundFxName) {
		mAudioFireAndForgetManager.play(pSoundFxName);

	}
}
