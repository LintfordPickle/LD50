package net.ld.ld50.data.tables;

import net.lintford.library.core.LintfordCore;

public class TableSink extends TableProp {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float SINKHOLE_HOLD_TIME_MS = 3000;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public boolean sinkHoleEnabled = true; // turned on

	public boolean sinkHoleActive; // contains ball
	public float sinkHoleTimeLeft;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean sinkHoleActive() {
		return sinkHoleActive;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TableSink() {
		cooldownTime = 3000;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (sinkHoleActive) {
			sinkHoleTimeLeft -= pCore.gameTime().elapsedTimeMilli();

			if (sinkHoleTimeLeft < 0) {
				sinkHoleActive = false;
				cooldownTimer = cooldownTime;
			}
		}
	}

	@Override
	public void hit() {
		if (isEnabled == false)
			return;

		if (isCooldownElapsed() == false)
			return;

		if (sinkHoleActive) {
			return;
		}

		sinkHoleActive = true;
		sinkHoleTimeLeft = SINKHOLE_HOLD_TIME_MS;

		if (listener != null) {
			listener.onHit(fixtureUid);
		}
	}
}
