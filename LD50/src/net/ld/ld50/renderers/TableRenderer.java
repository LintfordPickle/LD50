package net.ld.ld50.renderers;

import org.jbox2d.common.Vec2;

import net.ld.ld50.controllers.PlungerController;
import net.ld.ld50.controllers.TableController;
import net.ld.ld50.data.tables.TableHitter;
import net.ld.ld50.data.tables.TableKicker;
import net.ld.ld50.data.tables.TableLight;
import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.SpriteInstance;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class TableRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Table Renderer";

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private TableController mTableController;
	private PlungerController mPlungerController;
	private SpriteSheetDefinition mPropsSpritesheet;
	private Texture mTableTexture;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return false;
	}

	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public TableRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mTableController = (TableController) lControllerManager.getControllerByNameRequired(TableController.CONTROLLER_NAME, entityGroupID());
		mPlungerController = (PlungerController) lControllerManager.getControllerByNameRequired(PlungerController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		mPropsSpritesheet = pResourceManager.spriteSheetManager().loadSpriteSheet("res//spritesheets//spritesheetProps.json", entityGroupID());
		mTableTexture = pResourceManager.textureManager().loadTexture("TEXTURE_TABLE_00", "res//textures//tables//textureTable00.png", entityGroupID());
	}

	@Override
	public void unloadResources() {
		super.unloadResources();
	}

	@Override
	public void draw(LintfordCore pCore) {
		final var lTextureBatch = rendererManager().uiTextureBatch();
		final var lSpriteBatchRenderer = rendererManager().uiSpriteBatch();

		final var lWidth = mTableTexture.getTextureWidth();
		final var lHeight = mTableTexture.getTextureHeight();

		lSpriteBatchRenderer.begin(pCore.gameCamera());
		drawPlunger(pCore, lSpriteBatchRenderer);
		lSpriteBatchRenderer.end();

		lTextureBatch.begin(pCore.gameCamera());
		lTextureBatch.draw(mTableTexture, 0, 0, lWidth, lHeight, -lWidth * .5f, -lHeight * .5f, lWidth, lHeight, -0.5f, ColorConstants.WHITE);
		lTextureBatch.end();

		final var lFlipperLeft = mTableController.leftFlipper();
		final var lFlipperRight = mTableController.rightFlipper();

		lSpriteBatchRenderer.begin(pCore.gameCamera());

		drawFlipper(pCore, lSpriteBatchRenderer, lFlipperLeft, false);
		drawFlipper(pCore, lSpriteBatchRenderer, lFlipperRight, true);

		drawProps(pCore, lSpriteBatchRenderer);

		lSpriteBatchRenderer.end();
	}

	// ---------------------------------------------

	private void drawFlipper(LintfordCore pCore, SpriteBatch pSpriteBatch, Box2dBodyInstance pFlipper, boolean pFlipSprite) {
		final var lWorldCenter = pFlipper.mBody.getWorldPoint(new Vec2(0, 0));

		final var lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTUREFLIPPER");
		lSpriteInstance.setPosition(lWorldCenter.x * ConstantsPhysics.UnitsToPixels(), -lWorldCenter.y * ConstantsPhysics.UnitsToPixels());

		lSpriteInstance.flipHorizontal(pFlipSprite);
		lSpriteInstance.rotateAbs(-pFlipper.mBody.getAngle());
		lSpriteInstance.setScale(2.f, 2.f);

		pSpriteBatch.draw(mPropsSpritesheet, lSpriteInstance, lSpriteInstance, -0.3f, ColorConstants.WHITE);

	}

	private void drawPlunger(LintfordCore pCore, SpriteBatch pSpriteBatch) {
		final var lTableWidth = mTableTexture.getTextureWidth() * .5f;
		final var lTableHeight = mTableTexture.getTextureHeight() * .5f;

		final float lPlungerYOffset = mPlungerController.plungerPower() * 64.f;

		final var lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTUREPLUNGER");
		lSpriteInstance.setPosition(lTableWidth - 32.f, lTableHeight - 32.f + lPlungerYOffset);
		lSpriteInstance.setScale(2.f, 2.f);
		pSpriteBatch.draw(mPropsSpritesheet, lSpriteInstance, lSpriteInstance, -0.6f, ColorConstants.WHITE);
	}

	private void drawProps(LintfordCore pCore, SpriteBatch pSpriteBatch) {
		final var lProps = mTableController.tableProps();
		final int lNumTableProps = lProps.size();
		for (int i = 0; i < lNumTableProps; i++) {
			final var lTableProp = lProps.get(i);

			if (lTableProp instanceof TableHitter) {
				final var lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTUREHITTERIDLE");
				lSpriteInstance.setPosition(lTableProp.worldPositionX, -lTableProp.worldPositionY);
				lSpriteInstance.rotateAbs((float) Math.toRadians(lTableProp.angleInDegrees) - (float) Math.toRadians(90));
				lSpriteInstance.setScale(2.f, 2.f);

				var lColor = ColorConstants.WHITE;
				if (lTableProp.isFlash) {
					lColor = ColorConstants.getColorWithRGBAMod(1, 1, 1, 1, 10);
				}

				pSpriteBatch.draw(mPropsSpritesheet, lSpriteInstance, lSpriteInstance, -0.3f, lColor);
			} else if (lTableProp instanceof TableKicker) {
				final var lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTUREBUMPER");
				lSpriteInstance.setPosition(lTableProp.worldPositionX, -lTableProp.worldPositionY);
				lSpriteInstance.rotateAbs((float) Math.toRadians(lTableProp.angleInDegrees) - (float) Math.toRadians(90));
				lSpriteInstance.setScale(2.f, 2.f);

				var lColor = ColorConstants.WHITE;
				if (lTableProp.isFlash) {
					lColor = ColorConstants.getColorWithRGBAMod(1, 1, 1, 1, 10);
				}

				pSpriteBatch.draw(mPropsSpritesheet, lSpriteInstance, lSpriteInstance, -0.3f, lColor);
			} else if (lTableProp instanceof TableLight) {
				final var lLight = (TableLight) lTableProp;
				SpriteInstance lSpriteInstance = null;

				switch (lLight.lightState) {
				default:
				case 0:
					lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTURELIGHTGREY");
					break;
				case 1:
					lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTURELIGHTYELLOW");
					break;
				case 2:
					lSpriteInstance = mPropsSpritesheet.getSpriteInstance("TEXTURELIGHTGREEN");
					break;
				}

				if (lSpriteInstance == null) {
					return;
				}

				lSpriteInstance.setPosition(lTableProp.worldPositionX, -lTableProp.worldPositionY);
				lSpriteInstance.rotateAbs((float) Math.toRadians(lTableProp.angleInDegrees));
				lSpriteInstance.setScale(2.f, 2.f);

				var lColor = ColorConstants.WHITE;
				if (lTableProp.isFlash) {
					lColor = ColorConstants.getColorWithRGBAMod(1, 1, 1, 1, 10);
				}

				pSpriteBatch.draw(mPropsSpritesheet, lSpriteInstance, lSpriteInstance, -0.3f, lColor);
			}

		}
	}

}
