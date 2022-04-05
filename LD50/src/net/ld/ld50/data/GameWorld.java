package net.ld.ld50.data;

import org.jbox2d.dynamics.World;

import net.ld.ld50.data.balls.BallManager;
import net.ld.ld50.data.tables.TableManager;
import net.lintford.library.core.entity.WorldEntity;

public class GameWorld {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private World mBox2World;
	private TableManager mTableManager;
	private BallManager mBallManager;
	private TrackedEntity mTrackedObject;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public WorldEntity trackedCameraEntity() {
		return mTrackedObject;
	}

	public World box2dWorld() {
		return mBox2World;
	}

	public TableManager tableManager() {
		return mTableManager;
	}

	public BallManager ballManager() {
		return mBallManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameWorld() {
		mBox2World = new World(ConstantsGame.GRAVITY);

		mTableManager = new TableManager();
		mBallManager = new BallManager();

		mTrackedObject = new TrackedEntity(0);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}
