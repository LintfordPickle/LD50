package net.ld.ld50.controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.glfw.GLFW;

import net.ld.ld50.data.ConstantsGame;
import net.ld.ld50.data.balls.BallManager;
import net.ld.ld50.data.balls.BallPhysicsData;
import net.ld.ld50.data.tables.TableSink;
import net.lintford.library.ConstantsPhysics;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.JBox2dEntity;

public class BallController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Ball Controller";

	public static final float BALL_MAX_VELOCITY = 2.f;
	public static final float BALL_TRAIL_VELOCITY = 10.f;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private BallManager mBallManager;

	private TableController mTableController;
	private Box2dWorldController mBox2dWorldController;
	private ResourceController mResourceController;
	private GameStateController mGameStateController;
	private ParticleController mParticleController;

	JBox2dEntity mBallInstance;
	private List<JBox2dEntity> mBallUpdateList = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public BallManager ballManager() {
		return mBallManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BallController(ControllerManager pControllerManager, BallManager pBallManager, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mBallManager = pBallManager;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		mBox2dWorldController = (Box2dWorldController) mControllerManager.getControllerByNameRequired(Box2dWorldController.CONTROLLER_NAME, entityGroupID());
		mResourceController = (ResourceController) mControllerManager.getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		mTableController = (TableController) mControllerManager.getControllerByNameRequired(TableController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) mControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mParticleController = (ParticleController) mControllerManager.getControllerByNameRequired(ParticleController.CONTROLLER_NAME, entityGroupID());

		addBallToPlunger();
	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (ConstantsGame.IS_DEBUG && pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_T)) {
			final var lPlungerBody = mTableController.getPlungerBallPosition();
			final var lBallMainBody = mBallInstance.box2dEntityInstance().mainBody().mBody;
			lBallMainBody.setLinearVelocity(new Vec2(0, 0));
			mBallInstance.setPosition(lPlungerBody.x * ConstantsPhysics.UnitsToPixels(), lPlungerBody.y * ConstantsPhysics.UnitsToPixels());
		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);
		final var lActiveBalls = ballManager().instances();

		boolean ballActive = false;

		mBallUpdateList.clear();
		mBallUpdateList.addAll(lActiveBalls);

		final int lNumActiveBalls = mBallUpdateList.size();
		for (int i = 0; i < lNumActiveBalls; i++) {
			final var lBall = mBallUpdateList.get(i);

			if (lBall.hasPhysicsEntity()) {
				final var lBallBody = lBall.box2dEntityInstance().mainBody().mBody;
				final var lPhysicsData = lBall.box2dEntityInstance().userDataObject();

				if (lPhysicsData != null && lPhysicsData instanceof BallPhysicsData) {
					var lBallPhysicsData = (BallPhysicsData) lPhysicsData;
					if (lBallPhysicsData.ballAlive == false) {
						lActiveBalls.remove(lBall);

						mGameStateController.gameState().reduceLives();

						lBall.unloadPhysics();
						continue;
					}

					pullBallSinkHole(lBallBody, lBallPhysicsData);
				}

				limitBallVelocity(lBallBody, BALL_MAX_VELOCITY);
			}

			ballActive = true;
			lBall.updatePhysics(pCore);
		}

		// DEBUG
		if (ConstantsGame.IS_DEBUG && mBallInstance != null && pCore.input().mouse().isMouseLeftButtonDown()) {
			final var lMouseWorldX = pCore.gameCamera().getMouseWorldSpaceX();
			final var lMouseWorldY = -pCore.gameCamera().getMouseWorldSpaceY();
			mBallInstance.transformPObject(lMouseWorldX, lMouseWorldY, 0);
			mBallInstance.box2dEntityInstance().setLinearVelocity(0, 0);
		}

		if (ballActive == false && mGameStateController.gameState().isStarted() && mGameStateController.gameState().hasEnded() == false) {
			addBallToPlunger();
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void limitBallVelocity(Body pBallBody, float pMaximumVelocity) {

		final var lBallVelocity = new Vec2(pBallBody.getLinearVelocity());
		float lSpeed = lBallVelocity.normalize();

		if (lSpeed > pMaximumVelocity) {
			final var newVelocityWithDrag = new Vec2(lBallVelocity);

			newVelocityWithDrag.x = -newVelocityWithDrag.x * 0.25f;
			newVelocityWithDrag.y = -newVelocityWithDrag.y * 0.25f;

			pBallBody.applyForce(newVelocityWithDrag, pBallBody.getWorldCenter());

			if (lSpeed > BALL_TRAIL_VELOCITY) {
				final var lBallWorldPosition = pBallBody.getWorldCenter();
				final float wx = ConstantsPhysics.toPixels(lBallWorldPosition.x);
				final float wy = ConstantsPhysics.toPixels(-lBallWorldPosition.y);
				final float vx = lBallVelocity.x;
				final float vy = lBallVelocity.y;

				mParticleController.addTrail(wx, wy, vx, vy);
			}
		}

	}

	private void pullBallSinkHole(Body pBallBody, BallPhysicsData pBallPhysicsData) {
		if (pBallPhysicsData.inSinkHole == false)
			return;
		final var lSinkholeProp = mTableController.getTablePropByUid(pBallPhysicsData.targetFixtureUid);

		if (lSinkholeProp == null) {
			pBallPhysicsData.targetFixtureUid = -1;
			pBallPhysicsData.inSinkHole = false;
			return;
		}

		if (lSinkholeProp instanceof TableSink) {
			final var lSinkHole = (TableSink) lSinkholeProp;

			final boolean isGameAlreadyWon = mGameStateController.gameState().isGameWon();
			if (lSinkHole.sinkHoleActive == false && isGameAlreadyWon == false) {
				pBallPhysicsData.targetFixtureUid = -1;
				pBallPhysicsData.inSinkHole = false;
				return;
			}

			final var newImpulseToApply = new Vec2();

			final var lBallWorldPosition = pBallBody.getWorldCenter();

			final float lSinkUnitPositionX = ConstantsPhysics.PixelsToUnits() * lSinkholeProp.worldPositionX;
			final float lSinkUnitPositionY = ConstantsPhysics.PixelsToUnits() * lSinkholeProp.worldPositionY;

			newImpulseToApply.x = (lSinkUnitPositionX - lBallWorldPosition.x) * .1f;
			newImpulseToApply.y = (lSinkUnitPositionY - lBallWorldPosition.y) * .1f;

			pBallBody.applyLinearImpulse(newImpulseToApply, pBallBody.getWorldCenter(), true);

		} else {
			pBallPhysicsData.targetFixtureUid = -1;
			pBallPhysicsData.inSinkHole = false;
		}
	}

	public void startNewGame() {
		if (mBallInstance == null) {
			addBallToPlunger();
		} else {
			final var lPlungerPosition = mTableController.getPlungerBallPosition();
			mBallInstance.worldPositionX = ConstantsPhysics.toPixels(lPlungerPosition.x);
			mBallInstance.worldPositionY = ConstantsPhysics.toPixels(lPlungerPosition.y);
		}
	}

	private void addBallToPlunger() {
		final var lResourceManager = mResourceController.resourceManager();
		final var lBox2dWorld = mBox2dWorldController.world();

		final var lPlungerPosition = mTableController.getPlungerBallPosition();

		final var lPObjectInstance = lResourceManager.pobjectManager().getNewInstanceFromPObject(lBox2dWorld, "POBJECT_BALL");
		mBallInstance = mBallManager.addNewBallToTable();

		lPObjectInstance.setAllBodiesIsBullet(true);
		lPObjectInstance.setAllFixtureRestitution(.35f); // .25f
		lPObjectInstance.setAllFixtureDensity(.25f);
		lPObjectInstance.setAllFixtureFriction(.05f);
		lPObjectInstance.userDataObject(new BallPhysicsData());

		mBallInstance.worldPositionX = ConstantsPhysics.toPixels(lPlungerPosition.x);
		mBallInstance.worldPositionY = ConstantsPhysics.toPixels(lPlungerPosition.y);

		lPObjectInstance.setAllFixturesCategory(ConstantsGame.CATEGORY_BALL);
		lPObjectInstance.setAllFixturesBitMask(ConstantsGame.CATEGORY_TABLE);

		mBallInstance.setPhysicsObject(lPObjectInstance);
		mBallInstance.loadPhysics(lBox2dWorld);

		// TODO: Load Ball resources

	}

	public void applyPlungerPower(float pPower) {
		final var lUserData = (BallPhysicsData) mBallInstance.box2dEntityInstance().userDataObject();
		if (lUserData.inPlungePit) {
			mBallInstance.box2dEntityInstance().applyLinearImpulse(0.f, pPower * 10.f);
		}
	}
}
