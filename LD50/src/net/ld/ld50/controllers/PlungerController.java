package net.ld.ld50.controllers;

import org.lwjgl.glfw.GLFW;

import net.ld.unstable.controllers.SoundFxController;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class PlungerController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Plunger Controller";

	// Matches the names in the pbject file

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private SoundFxController mSoundFxController;
	private BallController mBallController;

	private float mPlungerPower;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public float plungerPower() {
		return mPlungerPower;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PlungerController(ControllerManager pControllerManager, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		mSoundFxController = (SoundFxController) mControllerManager.getControllerByNameRequired(SoundFxController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		mBallController = (BallController) mControllerManager.getControllerByNameRequired(BallController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
			if (mPlungerPower < 1.0f)
				mPlungerPower += 0.001 * pCore.gameTime().elapsedTimeMilli();

			if (mPlungerPower > 1.f)
				mPlungerPower = 1.f;

			if (mPlungerPower < 0.f)
				mPlungerPower = 0.f;
		} else if (mPlungerPower > 0.01f) {
			mBallController.applyPlungerPower(mPlungerPower);
			mPlungerPower = 0.f;

			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_PLUNGER);
		}

		return super.handleInput(pCore);
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

}
