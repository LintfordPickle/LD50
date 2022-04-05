package net.ld.ld50.controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.lwjgl.glfw.GLFW;

import net.ld.ld50.data.ConstantsGame;
import net.ld.ld50.data.tables.PropPhysicsData;
import net.ld.ld50.data.tables.TableHitter;
import net.ld.ld50.data.tables.TableLight;
import net.ld.ld50.data.tables.TableKicker;
import net.ld.ld50.data.tables.TableManager;
import net.ld.ld50.data.tables.TableProp;
import net.ld.ld50.data.tables.TableSink;
import net.ld.unstable.controllers.SoundFxController;
import net.lintford.library.ConstantsPhysics;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.box2d.instance.Box2dRevoluteInstance;
import net.lintford.library.core.maths.MathHelper;

public class TableController extends BaseController {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Table Controller";

	// Matches the names in the pbject file
	public static final String TABLE_COMPONENT_NAME_PLUNGER_BALL = "PLUNGER_BALL";
	public static final String TABLE_COMPONENT_NAME_FIXTURE_PLUNGER = "FIXTURE_PLUNGER";
	public static final String TABLE_COMPONENT_NAME_FIXTURE_PIT = "FIXTURE_PIT";

	public static final String TABLE_COMPONENT_NAME_MAIN_FLIPPER_LEFT = "FLIPPER_L";
	public static final String TABLE_COMPONENT_NAME_MAIN_FLIPPER_RIGHT = "FLIPPER_R";

	public static final String TABLE_COMPONENT_NAME_BODY_SINK = "SINK";
	public static final String TABLE_COMPONENT_NAME_BODY_HITTER = "HITTER";
	public static final String TABLE_COMPONENT_NAME_BODY_KICKER = "KICKER";

	public static final String TABLE_COMPONENT_NAME_FIXTURE_SINK = "FIXTURE_SINK";
	public static final String TABLE_COMPONENT_NAME_FIXTURE_BUMPER = "FIXTURE_BUMPER";
	public static final String TABLE_COMPONENT_NAME_FIXTURE_KICKER = "FIXTURE_KICKER";
	public static final String TABLE_COMPONENT_NAME_FIXTURE_IMAGE = "FIXTURE_IMAGE";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TableManager mTableManager;

	private Box2dWorldController mBox2dWorldController;
	private ResourceController mResourceController;
	private SoundFxController mSoundFxController;

	// lookup table of component positions on the table
	private Box2dRevoluteInstance mLeftFlipperJoint;
	private Box2dRevoluteInstance mRightFlipperJoint;

	private Box2dBodyInstance mPlungerBallBody;
	private Box2dBodyInstance mFlipperLeftBody;
	private Box2dBodyInstance mFlipperRightBody;

	private final List<TableProp> mTableProps = new ArrayList<>();
	private int mPropUidCounter = 0;
	private boolean flipperLeftSoundPlayed = false;
	private boolean flipperRightSoundPlayed = false;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public TableProp getTablePropByUid(int pTablePropUid) {
		final int lNumTableProps = mTableProps.size();
		for (int i = 0; i < lNumTableProps; i++) {
			if (mTableProps.get(i).fixtureUid == pTablePropUid)
				return mTableProps.get(i);
		}
		return null;
	}

	public TableProp getTablePropByName(String pTablePropName) {
		final int lNumTableProps = mTableProps.size();
		for (int i = 0; i < lNumTableProps; i++) {
			final var lTableProp = mTableProps.get(i);

			if (lTableProp.name != null && lTableProp.name.equals(pTablePropName))
				return mTableProps.get(i);
		}
		return null;
	}

	public int getNewPropUid() {
		return mPropUidCounter++;
	}

	public List<TableProp> tableProps() {
		return mTableProps;
	}

	public Box2dBodyInstance leftFlipper() {
		return mFlipperLeftBody;
	}

	public Box2dBodyInstance rightFlipper() {
		return mFlipperRightBody;
	}

	public TableManager tableManager() {
		return mTableManager;
	}

	public Vec2 getPlungerBallPosition() {
		return mPlungerBallBody.mBody.getWorldCenter();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TableController(ControllerManager pControllerManager, TableManager pTableManager, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);

		mTableManager = pTableManager;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		super.initialize(pCore);

		mSoundFxController = (SoundFxController) mControllerManager.getControllerByNameRequired(SoundFxController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);
		mBox2dWorldController = (Box2dWorldController) mControllerManager.getControllerByNameRequired(Box2dWorldController.CONTROLLER_NAME, entityGroupID());
		mResourceController = (ResourceController) mControllerManager.getControllerByNameRequired(ResourceController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		setupTable();
	}

	@Override
	public void unload() {

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		final float lMaxTorque = 1000.f;
		final float lSpeed = MathHelper.toRadians(1000 * 360);

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_A)) {
			if (flipperLeftSoundPlayed == false) {
				mSoundFxController.playSound(SoundFxController.AUDIO_NAME_FLIPPER);
				flipperLeftSoundPlayed = true;
			}

			RevoluteJoint leftFlipper = (RevoluteJoint) mLeftFlipperJoint.joint;
			leftFlipper.enableMotor(true);
			leftFlipper.setMaxMotorTorque(lMaxTorque);
			leftFlipper.setMotorSpeed(lSpeed);
		} else {
			flipperLeftSoundPlayed = false;

			RevoluteJoint leftFlipper = (RevoluteJoint) mLeftFlipperJoint.joint;
			leftFlipper.enableMotor(false);
			leftFlipper.setMaxMotorTorque(lMaxTorque);
			leftFlipper.setMotorSpeed(-lSpeed);
		}

		if (pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_D)) {
			if (flipperRightSoundPlayed == false) {
				mSoundFxController.playSound(SoundFxController.AUDIO_NAME_FLIPPER);
				flipperRightSoundPlayed = true;
			}

			RevoluteJoint rightFlipper = (RevoluteJoint) mRightFlipperJoint.joint;
			rightFlipper.enableMotor(true);
			rightFlipper.setMaxMotorTorque(lMaxTorque);
			rightFlipper.setMotorSpeed(-lSpeed);
		} else {
			flipperRightSoundPlayed = false;
			RevoluteJoint rightFlipper = (RevoluteJoint) mRightFlipperJoint.joint;
			rightFlipper.enableMotor(true);
			rightFlipper.setMaxMotorTorque(lMaxTorque);
			rightFlipper.setMotorSpeed(lSpeed);
		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		final int lPropCount = mTableProps.size();
		for (int i = 0; i < lPropCount; i++) {
			final var lProp = mTableProps.get(i);
			lProp.update(pCore);

			if (lProp instanceof TableLight) {
				updatePropImages(pCore, (TableLight) lProp);
			}
		}
	}

	private void updatePropImages(LintfordCore pCore, TableLight pImage) {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void setupTable() {
		final var lResourceManager = mResourceController.resourceManager();
		final var lBox2dWorld = mBox2dWorldController.world();

		final var lPObjectInstance = lResourceManager.pobjectManager().getNewInstanceFromPObject(lBox2dWorld, "POBJECT_TABLE");
		final var lTableInstance = mTableManager.createTable();

		mLeftFlipperJoint = (Box2dRevoluteInstance) lPObjectInstance.getJointByName(TABLE_COMPONENT_NAME_MAIN_FLIPPER_LEFT);
		mLeftFlipperJoint.enableMotor = false;
		mRightFlipperJoint = (Box2dRevoluteInstance) lPObjectInstance.getJointByName(TABLE_COMPONENT_NAME_MAIN_FLIPPER_RIGHT);
		mRightFlipperJoint.enableMotor = false;

		mPlungerBallBody = (Box2dBodyInstance) lPObjectInstance.getBodyByName(TABLE_COMPONENT_NAME_PLUNGER_BALL);
		mFlipperLeftBody = (Box2dBodyInstance) lPObjectInstance.getBodyByName(TABLE_COMPONENT_NAME_MAIN_FLIPPER_LEFT);
		mFlipperRightBody = (Box2dBodyInstance) lPObjectInstance.getBodyByName(TABLE_COMPONENT_NAME_MAIN_FLIPPER_RIGHT);

		lPObjectInstance.setAllFixturesCategory(ConstantsGame.CATEGORY_TABLE);
		lPObjectInstance.setAllFixturesBitMask(ConstantsGame.CATEGORY_BALL);

		lTableInstance.setPhysicsObject(lPObjectInstance);
		lTableInstance.loadPhysics(lBox2dWorld, false);

		setupTableProps(lPObjectInstance);
	}

	private void setupTableProps(JBox2dEntityInstance pTableBox2dEntity) {
		final var lBodies = pTableBox2dEntity.bodies();
		final int lNumberBodies = lBodies.size();
		for (int i = 0; i < lNumberBodies; i++) {
			final var lBody = lBodies.get(i);
			if (lBody == null)
				continue;

			final var lWorldPosition = lBody.mBody.getWorldCenter();
			if (lBody.name.startsWith("LIGHT_")) {
				addImage(lBody.name, lBody, lWorldPosition.x * ConstantsPhysics.UnitsToPixels(), lWorldPosition.y * ConstantsPhysics.UnitsToPixels(), lBody.mBody.getAngle());
			} else if (lBody.name.equals(TABLE_COMPONENT_NAME_BODY_KICKER)) {
				addKicker(lBody, lWorldPosition.x * ConstantsPhysics.UnitsToPixels(), lWorldPosition.y * ConstantsPhysics.UnitsToPixels(), lBody.mBody.getAngle());
			} else if (lBody.name.equals(TABLE_COMPONENT_NAME_BODY_HITTER)) {
				addHitter(lBody, lWorldPosition.x * ConstantsPhysics.UnitsToPixels(), lWorldPosition.y * ConstantsPhysics.UnitsToPixels(), (float) Math.toDegrees(lBody.objectAngleInRadians));
			} else if (lBody.name.equals(TABLE_COMPONENT_NAME_BODY_SINK)) {
				addSink(lBody, lWorldPosition.x * ConstantsPhysics.UnitsToPixels(), lWorldPosition.y * ConstantsPhysics.UnitsToPixels(), (float) Math.toDegrees(lBody.objectAngleInRadians));
			}
		}
	}

	private void addImage(String pName, Box2dBodyInstance pBody, float pWorldPositionX, float pWorldPositionY, float pAngle) {
		final var lNewImage = new TableLight();
		lNewImage.fixtureUid = getNewPropUid();
		lNewImage.worldPositionX = pWorldPositionX;
		lNewImage.worldPositionY = pWorldPositionY;

		lNewImage.name = pName;

		mTableProps.add(lNewImage);

	}

	private void addKicker(Box2dBodyInstance pBody, float pWorldPositionX, float pWorldPositionY, float pAngle) {
		final var lNewKicker = new TableKicker();
		lNewKicker.fixtureUid = getNewPropUid();
		lNewKicker.worldPositionX = pWorldPositionX;
		lNewKicker.worldPositionY = pWorldPositionY;
		lNewKicker.angleInDegrees = pAngle;

		mTableProps.add(lNewKicker);

		final var lHitterPhysicsData = new PropPhysicsData();
		lHitterPhysicsData.fixtureUid = lNewKicker.fixtureUid;

		pBody.mBody.setUserData(lHitterPhysicsData);
	}

	private void addHitter(Box2dBodyInstance pBody, float pWorldPositionX, float pWorldPositionY, float pAngleInDegrees) {
		final var lNewHitter = new TableHitter();
		lNewHitter.fixtureUid = getNewPropUid();
		lNewHitter.worldPositionX = pWorldPositionX;
		lNewHitter.worldPositionY = pWorldPositionY;
		lNewHitter.angleInDegrees = -pAngleInDegrees;

		mTableProps.add(lNewHitter);

		final var lHitterPhysicsData = new PropPhysicsData();
		lHitterPhysicsData.fixtureUid = lNewHitter.fixtureUid;

		pBody.mBody.setUserData(lHitterPhysicsData);
	}

	private void addSink(Box2dBodyInstance pBody, float pWorldPositionX, float pWorldPositionY, float pAngleInDegrees) {
		final var lNewSink = new TableSink();
		lNewSink.fixtureUid = getNewPropUid();
		lNewSink.worldPositionX = pWorldPositionX;
		lNewSink.worldPositionY = pWorldPositionY;
		lNewSink.angleInDegrees = -pAngleInDegrees;

		mTableProps.add(lNewSink);

		final var lSinkPhysicsData = new PropPhysicsData();
		lSinkPhysicsData.fixtureUid = lNewSink.fixtureUid;

		pBody.mBody.setUserData(lSinkPhysicsData);
	}

}
