package net.ld.ld50.controllers;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.entity.WorldEntity;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;

public class CameraChaseShakeController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Camera Chase Shake Controller";

	private static final float CAMERA_MAN_MOVE_SPEED = 0.2f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mGameCamera;
	private WorldEntity mTrackedEntity;
	private boolean mAllowManualControl;
	private boolean mIsTrackingPlayer;

	private final Vector2f mVelocity = new Vector2f();
	private final Vector2f mDesiredPosition = new Vector2f();
	private final Vector2f mPosition = new Vector2f();
	private final Vector2f mLookAhead = new Vector2f();
	private final Vector2f mShakeOffset = new Vector2f();

	protected float mShakeMag;
	protected float mShakeDur;
	protected float mShakeTimer;

	private float mStiffness = 30.0f;
	private float mDamping = 4.0f;
	private float mMass = .5f;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public boolean trackPlayer() {
		return mIsTrackingPlayer;
	}

	public void trackPlayer(boolean pNewValue) {
		mIsTrackingPlayer = pNewValue;
	}

	public boolean allowManualControl() {
		return mAllowManualControl;
	}

	public void allowManualControl(boolean pNewValue) {
		mAllowManualControl = pNewValue;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraChaseShakeController(ControllerManager pControllerManager, ICamera pCamera, WorldEntity pTrackEntity, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mPosition.x = pTrackEntity.worldPositionX;
		mPosition.y = pTrackEntity.worldPositionY;

		mGameCamera = pCamera;
		mTrackedEntity = pTrackEntity;
		mIsTrackingPlayer = true;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mGameCamera == null)
			return false;

		if (mAllowManualControl) {
			final float speed = CAMERA_MAN_MOVE_SPEED;

			// Just listener for clicks - couldn't be easier !!?!
			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
				mVelocity.x -= speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
				mVelocity.x += speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_S)) {
				mVelocity.y += speed;
				mIsTrackingPlayer = false;

			}

			if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_W)) {
				mVelocity.y -= speed;
				mIsTrackingPlayer = false;

			}

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		if (mGameCamera == null)
			return;

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_P)) {
			shakeCamera(100.f, 10.f);
		}

		if (mTrackedEntity != null) {
			updateShake(pCore);
			updateSpring(pCore);

			mGameCamera.setPosition(-mPosition.x + mShakeOffset.x, -mPosition.y + mShakeOffset.y);
		}
	}

	private void updateSpring(LintfordCore pCore) {
		updatewWorldPositions(pCore);

		final float elapsed = (float) pCore.appTime().elapsedTimeSeconds();

		final float stretchX = mPosition.x - mDesiredPosition.x;
		final float stretchY = mPosition.y - mDesiredPosition.y;

		final float forceX = -mStiffness * stretchX - mDamping * mVelocity.x;
		final float forceY = -mStiffness * stretchY - mDamping * mVelocity.y;

		// Apply acceleration
		float accelerationX = forceX / mMass;
		float accelerationY = forceY / mMass;

		mVelocity.x += accelerationX * elapsed;
		mVelocity.y += accelerationY * elapsed;

		mPosition.x += mVelocity.x * elapsed;
		mPosition.y += mVelocity.y * elapsed;
	}

	private void updatewWorldPositions(LintfordCore pCore) {
		mLookAhead.x = mTrackedEntity.worldPositionX + mVelocity.x;
		mLookAhead.y = mTrackedEntity.worldPositionY + mVelocity.y;

		mDesiredPosition.x = mTrackedEntity.worldPositionX;// + mShakeOffset.x;
		mDesiredPosition.y = mTrackedEntity.worldPositionY;// + mShakeOffset.y;
	}

	private void updateShake(LintfordCore pCore) {
		if (mShakeTimer > 0.f) {
			mShakeTimer -= pCore.appTime().elapsedTimeMilli();

			// normal time
			float progress = mShakeTimer / mShakeDur;
			float lMagnitude = mShakeMag * (1f - (progress * progress));

			mShakeOffset.x = RandomNumbers.random(-1.f, 1.f) * lMagnitude;
			mShakeOffset.y = RandomNumbers.random(-1.f, 1.f) * lMagnitude;
		} else {
			mShakeOffset.x = 0.f;
			mShakeOffset.y = 0.f;
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void shakeCamera(float pDuration, float pMagnitude) {
		// don't interrupt large shakes with little ones
		if (mShakeTimer > 0.f) {
			if (mShakeMag > pMagnitude)
				return;
		}

		mShakeMag = pMagnitude;
		mShakeDur = Math.max(pDuration, mShakeDur);

		mShakeTimer = pDuration;
	}

	public void zoomIn(float pZoomFactor) {
		mGameCamera.setZoomFactor(pZoomFactor);

	}
}