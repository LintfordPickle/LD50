package net.ld.ld50.data.tables;

import net.lintford.library.core.ResourceManager;

public class TableManager {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private Table mTable;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public TableManager() {

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void onLoadResources(ResourceManager pResourceManager) {

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public Table createTable() {
		if (mTable == null) {
			mTable = new Table(0);
		}

		return mTable;
	}
}
