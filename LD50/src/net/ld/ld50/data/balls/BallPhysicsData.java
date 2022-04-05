package net.ld.ld50.data.balls;

import net.lintford.library.core.box2d.BasePhysicsData;

public class BallPhysicsData extends BasePhysicsData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 7099813715546227017L;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public boolean ballAlive;
	public boolean inPlungePit;
	public boolean inSinkHole;
	public int targetFixtureUid; 

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public BallPhysicsData() {
		super(0);

		reset();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void reset() {
		ballAlive = true;
	}
}
