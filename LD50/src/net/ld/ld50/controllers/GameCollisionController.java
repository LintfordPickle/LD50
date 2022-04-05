package net.ld.ld50.controllers;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import net.ld.ld50.data.balls.BallPhysicsData;
import net.ld.ld50.data.tables.PropPhysicsData;
import net.ld.unstable.controllers.SoundFxController;
import net.lintford.library.ConstantsPhysics;
import net.lintford.library.controllers.box2d.Box2dContactController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class GameCollisionController extends Box2dContactController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Game Collision Controller";

	final static float BUMPER_REPEL_MULTI = 3.5f;
	final static float KICKER_REPEL_MULTI = 2.5f;

	final static int KICKER_SCORE = 100;
	final static int BUMPER_SCORE = 100;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ParticleController mParticleController;
	private TableController mTableController;
	private CameraChaseShakeController mScreenShakeController;
	private GameStateController mGameStateController;
	private SoundFxController mSoundFxController;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameCollisionController(ControllerManager pControllerManager, World pWorld, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pWorld, pEntityGroupUid);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		mTableController = (TableController) mControllerManager.getControllerByNameRequired(TableController.CONTROLLER_NAME, entityGroupID());
		mParticleController = (ParticleController) mControllerManager.getControllerByNameRequired(ParticleController.CONTROLLER_NAME, entityGroupID());
		mScreenShakeController = (CameraChaseShakeController) mControllerManager.getControllerByNameRequired(CameraChaseShakeController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) mControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
		mSoundFxController = (SoundFxController) mControllerManager.getControllerByNameRequired(SoundFxController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	@Override
	public void unload() {

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void beginContact(Contact pContact) {
		if (pContact.getFixtureA().getBody().getUserData() instanceof BallPhysicsData) {
			ballCollision(pContact, pContact.getFixtureA(), pContact.getFixtureB(), true);
		} else if (pContact.getFixtureB().getBody().getUserData() instanceof BallPhysicsData) {
			ballCollision(pContact, pContact.getFixtureB(), pContact.getFixtureA(), true);
		}
	}

	@Override
	public void endContact(Contact pContact) {
		if (pContact.getFixtureA().getBody().getUserData() instanceof BallPhysicsData) {
			ballCollision(pContact, pContact.getFixtureA(), pContact.getFixtureB(), false);
		} else if (pContact.getFixtureB().getBody().getUserData() instanceof BallPhysicsData) {
			ballCollision(pContact, pContact.getFixtureB(), pContact.getFixtureA(), false);
		}
	}

	@Override
	public void postSolve(Contact pContact, ContactImpulse arg1) {
		if (pContact.getFixtureA().getBody().getUserData() instanceof BallPhysicsData) {
			ballPostSolve(pContact, pContact.getFixtureA(), pContact.getFixtureB());
		} else if (pContact.getFixtureB().getBody().getUserData() instanceof BallPhysicsData) {
			ballPostSolve(pContact, pContact.getFixtureB(), pContact.getFixtureA());
		}
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {

	}

	private void ballPostSolve(Contact pContact, Fixture pBallFixture, Fixture pOtherFixture) {

	}

	private void ballCollision(Contact pContact, Fixture pBallFixture, Fixture pOtherFixture, boolean pCollides) {
		final var lBodyHasUserData = pOtherFixture.getBody().getUserData() != null;

		if (pOtherFixture.getUserData().toString().equals(TableController.TABLE_COMPONENT_NAME_FIXTURE_PIT)) {
			final var lBall = (BallPhysicsData) pBallFixture.getBody().getUserData();
			lBall.ballAlive = false;

			return;
		}

		if (pOtherFixture.getUserData().toString().equals(TableController.TABLE_COMPONENT_NAME_FIXTURE_PLUNGER)) {
			final var lBall = (BallPhysicsData) pBallFixture.getBody().getUserData();
			lBall.inPlungePit = pCollides;

			return;
		}

		// Sinkhole
		if (pCollides && pOtherFixture.getUserData().toString().equals(TableController.TABLE_COMPONENT_NAME_FIXTURE_SINK)) {
			final var lBall = (BallPhysicsData) pBallFixture.getBody().getUserData();

			if (pOtherFixture.getBody() != null) {
				final var lPropPhysicsData = (PropPhysicsData) pOtherFixture.getBody().getUserData();
				final var lTableProp = mTableController.getTablePropByUid(lPropPhysicsData.fixtureUid);

				lBall.inSinkHole = pCollides;
				lBall.targetFixtureUid = lPropPhysicsData.fixtureUid;

				lTableProp.hit();

				mSoundFxController.playSound(SoundFxController.AUDIO_NAME_BLACKHOLE);

				// TODO: This only works because there is 1 sinkhole
				mGameStateController.enteredBlackHole();
			}

			return;
		}

		// Kickers
		// if (pOtherFixture.getUserData().toString().equals(TableController.TABLE_COMPONENT_NAME_FIXTURE_KICKER)) {

		// TODO: FIX THIS - its a mess
		if (pCollides && lBodyHasUserData && pOtherFixture.getUserData().toString().startsWith("KICKER_")) {
			final var lBallBody = pBallFixture.getBody();
			final var lBallWorldCenter = lBallBody.getWorldCenter();

			final var lKickerBody = pOtherFixture.getBody();
			final var lKickerWorldCenter = lKickerBody.getWorldCenter();

			Vec2 tt = new Vec2(lBallWorldCenter.x - lKickerWorldCenter.x, lBallWorldCenter.y - lKickerWorldCenter.y);
			tt.normalize();

			tt.x *= KICKER_REPEL_MULTI;
			tt.y *= KICKER_REPEL_MULTI;

			pContact.setEnabled(false);

			lBallBody.setLinearVelocity(new Vec2(0, 0));
			lBallBody.applyLinearImpulse(tt, lBallWorldCenter, true);

			mGameStateController.gameState().increaseScore(KICKER_SCORE);

			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_KICKER);

			if (pOtherFixture.getBody().getUserData() instanceof PropPhysicsData) {
				final var lPropPhysicsData = (PropPhysicsData) pOtherFixture.getBody().getUserData();
				final var lTableProp = mTableController.getTablePropByUid(lPropPhysicsData.fixtureUid);

				if (lTableProp != null) {
					lTableProp.hit();
				}

				if (pOtherFixture.getUserData().toString().endsWith("ASTEROID")) {
					mGameStateController.asteroidDestroyed();
				}

				if (pOtherFixture.getUserData().toString().endsWith("FUEL")) {
					mGameStateController.fuelButtonHit();
				}
			}

			return;
		}

		if (pCollides && pOtherFixture.getUserData().toString().equals(TableController.TABLE_COMPONENT_NAME_FIXTURE_BUMPER)) {
			final var lBallBody = pBallFixture.getBody();
			final var lBallWorldCenter = lBallBody.getWorldCenter();

			final var lBumperBody = pOtherFixture.getBody();

			Vec2 tt = new Vec2((float) Math.cos(lBumperBody.getAngle()), (float) Math.sin(lBumperBody.getAngle()));
			tt.normalize();

			tt.x *= BUMPER_REPEL_MULTI;
			tt.y *= BUMPER_REPEL_MULTI;

			pContact.setEnabled(false);

			lBallBody.applyLinearImpulse(tt, lBallWorldCenter, true);
			
			mSoundFxController.playSound(SoundFxController.AUDIO_NAME_KICKER);

			mGameStateController.gameState().increaseScore(KICKER_SCORE);

			return;
		}

		// Hitters
		if (lBodyHasUserData && pCollides && pOtherFixture.getBody().getUserData() instanceof PropPhysicsData) {
			final var lPropPhysicsData = (PropPhysicsData) pOtherFixture.getBody().getUserData();
			final var lTableProp = mTableController.getTablePropByUid(lPropPhysicsData.fixtureUid);

			if (pOtherFixture.getUserData() != null) {
				if (pOtherFixture.getUserData().toString().equals("VALVE")) {
					mGameStateController.fuelValveHit();
				} else if (pOtherFixture.getUserData().toString().equals("CRANE")) {
					mGameStateController.craneHit();
				} else if (pOtherFixture.getUserData().toString().equals("CARGO_0")) {
					mGameStateController.cargoHit(0);
				} else if (pOtherFixture.getUserData().toString().equals("CARGO_1")) {
					mGameStateController.cargoHit(1);
				} else if (pOtherFixture.getUserData().toString().equals("CARGO_2")) {
					mGameStateController.cargoHit(2);
				} else if (pOtherFixture.getUserData().toString().equals("LAUNCH")) {
					mGameStateController.launchHit();
				}
			}

			if (lTableProp != null) {
				lTableProp.hit();
			}
		}

		if (pCollides /* TODO: only shake when hits walls */) {
			final var lBallBody = pBallFixture.getBody();
			final float wx = lBallBody.getWorldCenter().x * ConstantsPhysics.UnitsToPixels();
			final float wy = -lBallBody.getWorldCenter().y * ConstantsPhysics.UnitsToPixels();

			Vec2 tt = new Vec2(lBallBody.getLinearVelocity());
			final float lDist = tt.normalize();

			if (pOtherFixture.getUserData() != null) {
				if (pOtherFixture.getUserData().toString().equals("FLIPPER")) {
					
				} else {
					mSoundFxController.playSound(SoundFxController.AUDIO_NAME_HIT);
				}
			}

			mParticleController.hitWall(wx, wy, tt.x, tt.y, lDist);

			final float lShakeTol = 12.f;
			if (lDist > lShakeTol) {
				mScreenShakeController.shakeCamera(70.0f, 5.0f);
			}
		}
	}
}
