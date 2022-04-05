package net.ld.ld50.data.tables;

import net.lintford.library.core.box2d.BasePhysicsData;

public class PropPhysicsData extends BasePhysicsData {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final long serialVersionUID = 7099813715546227017L;

	public static final int FIXTURE_NO_UID = -1;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public String fixtureName;
	public int fixtureUid;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PropPhysicsData() {
		super(0);

		reset();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void reset() {
		fixtureUid = FIXTURE_NO_UID;
	}
}
