package net.ld.ld50.data.tables;

import net.lintford.library.core.LintfordCore;

public class TableProp {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float HIT_FLASH_TIME_MS = 1000;

	public enum PropStatus {
		Disabled, Idle, Activated,
	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public String name;

	public int fixtureUid;
	public float worldPositionX;
	public float worldPositionY;
	public float angleInDegrees;

	public boolean isEnabled = true;
	protected float mHitTimer;
	public ITablePropListener listener;

	public boolean isFlash;
	public float flashTimer;

	public float cooldownTime = 0;
	public float cooldownTimer;

	private PropStatus mPropStatus = PropStatus.Disabled;

	// ---------------------------------------------+
	// Properties
	// ---------------------------------------------

	public boolean isCooldownElapsed() {
		return cooldownTimer <= 0.f;
	}

	public PropStatus propStatus() {
		return mPropStatus;
	}

	public void propStatus(PropStatus pPropStatus) {
		mPropStatus = pPropStatus;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void update(LintfordCore pCore) {
		final var lElapsedTimeMs = pCore.gameTime().elapsedTimeMilli();

		if (isCooldownElapsed() == false) {
			cooldownTimer -= lElapsedTimeMs;
		}

		if (mHitTimer > 0) {
			mHitTimer -= lElapsedTimeMs;
			flashTimer -= lElapsedTimeMs;
			if (flashTimer < 0) {
				flashTimer = 100;
				isFlash = !isFlash;
			}

		} else {
			isFlash = false;
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void hit() {
		if (isCooldownElapsed() == false)
			return;

		if (isEnabled) {
			mHitTimer = HIT_FLASH_TIME_MS;
		}

		cooldownTimer = cooldownTime;

		if (listener != null) {
			listener.onHit(fixtureUid);
		}
	}

}
