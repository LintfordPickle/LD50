package net.ld.ld50.screens;

import net.ld.ld50.data.GameState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class GameOverScreen extends MenuScreen {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int BUTTON_RESTART = 0;
	private static final int BUTTON_EXIT_TO_MENU = 1;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameState mGameState;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public GameOverScreen(ScreenManager pScreenManager, GameState pGameState) {
		super(pScreenManager, "Game Over");

		mGameState = pGameState;

		final var lListLayout = new ListLayout(this);
		lListLayout.layoutFillType(FILLTYPE.TAKE_WHATS_NEEDED);

		final var lRestartEntry = new MenuEntry(pScreenManager, lListLayout, "Restart");
		lRestartEntry.horizontalFillType(FILLTYPE.TAKE_DESIRED_SIZE);
		lRestartEntry.registerClickListener(this, BUTTON_RESTART);
		lRestartEntry.desiredWidth(400.f);

		final var lExitToMenuEntry = new MenuEntry(pScreenManager, lListLayout, "Exit to Menu");
		lExitToMenuEntry.horizontalFillType(FILLTYPE.TAKE_DESIRED_SIZE);
		lExitToMenuEntry.registerClickListener(this, BUTTON_EXIT_TO_MENU);
		lExitToMenuEntry.desiredWidth(400.f);

		lListLayout.addMenuEntry(lRestartEntry);
		lListLayout.addMenuEntry(lExitToMenuEntry);
		addLayout(lListLayout);

		mShowBackgroundScreens = true;
		mIsPopup = true;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_RESTART:
			mGameState.startGame();
			exitScreen();
			break;

		case BUTTON_EXIT_TO_MENU:

			break;

		}
	}

}
