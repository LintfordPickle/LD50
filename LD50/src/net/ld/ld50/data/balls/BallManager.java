package net.ld.ld50.data.balls;

import net.lintford.library.core.entity.instances.PoolInstanceManager;

public class BallManager extends PoolInstanceManager<Ball> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = -3229704275200508025L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mEntityUidCounter;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getNewBallsAlive() {
		return mInstances.size();
	}

	public int getNewEntityUid() {
		return mEntityUidCounter++;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	protected Ball createPoolObjectInstance() {
		return new Ball(getNewEntityUid());
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public Ball addNewBallToTable() {
		final var lNewBallInstance = getFreePooledItem();

		if (mInstances.contains(lNewBallInstance) == false) {
			mInstances.add(lNewBallInstance);
		}

		return lNewBallInstance;
	}

}
