package net.ld.ld50.data.tables;

public class TableKicker extends TableProp {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final float HIT_FLASH_TIME_MS = 50;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void hit() {
		if (isEnabled) {
			mHitTimer = HIT_FLASH_TIME_MS;
			flashTimer = 0;
		}

		if (listener != null) {
			listener.onHit(fixtureUid);
		}
	}
}
