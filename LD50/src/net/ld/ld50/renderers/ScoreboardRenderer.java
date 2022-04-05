package net.ld.ld50.renderers;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.ld.ld50.controllers.BallController;
import net.ld.ld50.controllers.GameStateController;
import net.ld.ld50.data.RTCamera;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.geometry.TexturedQuad;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class ScoreboardRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Scoreboard Renderer";

	public static final int SCOREBOARD_WIDTH = 800;
	public static final int SCOREBOARD_HEIGHT = 100;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameStateController mGameStateController;

	private FontUnit mScoreFont;
	private TexturedQuad mTexturedQuad;
	private BallController mBallController;
	private ShaderMVP_PT mScoreboardShader;

	private RenderTarget mScoreboardTarget;

	private RTCamera mRtCamera;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mBallController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public ScoreboardRenderer(RendererManager pRendererManager, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mTexturedQuad = new TexturedQuad();
		mScoreboardShader = new ShaderMVP_PT("SHADER_SCXOREBOARD", "/res/shaders/shader_basic_pt.vert", "res//shaders//shaderScoreboardPCT.frag");
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

		mBallController = (BallController) lControllerManager.getControllerByNameRequired(BallController.CONTROLLER_NAME, entityGroupID());
		mGameStateController = (GameStateController) lControllerManager.getControllerByNameRequired(GameStateController.CONTROLLER_NAME, entityGroupID());
	}

	@Override
	public void loadResources(ResourceManager pResourceManager) {
		super.loadResources(pResourceManager);

		mTexturedQuad.loadResources(pResourceManager);

		mScoreboardShader.loadResources(pResourceManager);
		mScoreboardTarget = mRendererManager.createRenderTarget("SceneBackgroundRT", SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT, 1f, GL11.GL_NEAREST, true);
		mRtCamera = new RTCamera(SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT);
		mScoreFont = pResourceManager.fontManager().getFontUnit("FONT_SCOREBOARD");
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mTexturedQuad.unloadResources();
		mRendererManager.unloadRenderTarget(mScoreboardTarget);

		mScoreboardTarget = null;
	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_U)) {
			mScoreboardShader.recompile();
		}

		return super.handleInput(pCore);
	}

	@Override
	public void update(LintfordCore pCore) {
		mRtCamera.update(pCore);

		super.update(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (isInitialized() == false)
			return;

		renderScoreboardIntoTexture(pCore);
		pCore.gameCamera().applyGameViewport();

		drawScoreboard(pCore, 0, -400 + SCOREBOARD_HEIGHT * .5f, SCOREBOARD_WIDTH, SCOREBOARD_HEIGHT, -1f, mScoreboardTarget);
	}

	private void renderScoreboardIntoTexture(LintfordCore pCore) {
		mScoreboardTarget.bind();

		GL11.glClearColor(0.1f, 0.12f, 0.04f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		final var lScore = mGameStateController.gameState().score();
		final var lBallsRemaining = mGameStateController.gameState().lives();

		mScoreFont.begin(mRtCamera);
		mScoreFont.drawText("SCORE: " + lScore, -400, 0 - mScoreFont.fontHeight(), -0.1f, 1.f);

		final var lBallsRemainingText = "BALLS: " + lBallsRemaining;
		final float lBallsRemainingTextWidth = mScoreFont.getStringWidth(lBallsRemainingText);
		mScoreFont.drawText(lBallsRemainingText, 400 - lBallsRemainingTextWidth, 0 - mScoreFont.fontHeight(), -0.1f, 1.f);

		if (mGameStateController.showStateMessage() && mGameStateController.messageFlash()) {
			final var lStateMessage = mGameStateController.stateMessage();
			final var lMessageWidthHalf = mScoreFont.getStringWidth(lStateMessage) * .5f;
			mScoreFont.drawText(lStateMessage, -lMessageWidthHalf, -10, -0.1f, 1.f);
		}

		mScoreFont.end();

		mScoreboardTarget.unbind();
	}

	public void drawScoreboard(LintfordCore pCore, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, RenderTarget pRenderTarget) {
		if (pRenderTarget == null)
			return;

		mScoreboardShader.bind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pRenderTarget.colorTextureID());

		mScoreboardShader.projectionMatrix(pCore.HUD().projection());
		mScoreboardShader.viewMatrix(pCore.HUD().view());

		mTexturedQuad.createModelMatrix(pDestinationPositionX, pDestinationPositionY, pDestinationWidth, pDestinationHeight, -.01f);
		mScoreboardShader.modelMatrix(mTexturedQuad.modelMatrix());

		final int lTimeLocation = GL20.glGetUniformLocation(mScoreboardShader.shaderID(), "time");
		if (lTimeLocation != -1) {
			GL20.glUniform1f(lTimeLocation, (float) pCore.gameTime().totalTimeMilli());
		}

		mTexturedQuad.draw(pCore);
		mScoreboardShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0); //
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

}
