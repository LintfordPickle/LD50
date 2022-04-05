package net.ld.ld50.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.WorldEntity;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;

public class CameraTrackerController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Tracker Controller";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private WorldEntity mTrackedEntity;
	private BallController mBallController;

	protected final Vector2f mOffsetPosition = new Vector2f();
	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public WorldEntity trackedEntity() {
		return mTrackedEntity;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public CameraTrackerController(ControllerManager pControllerManager, WorldEntity pTrackedEntity, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mTrackedEntity = pTrackedEntity;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		final var lControllerManager = pCore.controllerManager();

		mBallController = (BallController) lControllerManager.getControllerByNameRequired(BallController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		// don't hard-code these
		final float lTableHeightHalf = 1280 * .5f;
		final float lWindowHeightHalf = 800 * .5f;

		float belowNoNo = lTableHeightHalf - lWindowHeightHalf + 50; // table front
		float aboveNoNo = lTableHeightHalf - lWindowHeightHalf + 100; // scoreboard

		mTrackedEntity.worldPositionX = 0;
		mTrackedEntity.worldPositionY = MathHelper.clamp(mBallController.mBallInstance.worldPositionY, -belowNoNo, aboveNoNo);
	}
}
