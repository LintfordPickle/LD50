package net.ld.ld50.data.particles;

import net.lintford.library.core.entity.instances.PreAllocatedInstanceData;
import net.lintford.library.core.graphics.Color;

public class Particle extends PreAllocatedInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 820164057821427990L;

	public static final int DO_NOT_DESPAWN_LIFETIME = -1;

	public static final int TYPE_NONE = -1;
	public static final int TYPE_SPARK = 0;
	public static final int TYPE_TRAIL = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public int particleType;
	private boolean mIsFree;
	public float timeSinceStart;
	private float mLifeTime;

	public float width;
	public float height;
	public float sx, sy, sw, sh;

	public float rox;
	public float roy;

	public float odx, ody;
	public float dx, dy, dr;
	public float r, g, b, a;
	public float scale;

	public float baseWorldPositionX;
	public float baseWorldPositionY;

	public float worldPositionX;
	public float worldPositionY;

	public float rotationInRadians;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAssigned() {
		return !mIsFree;
	}

	public float lifeTime() {
		return mLifeTime;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Particle() {
		reset();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setupSourceTexture(float pSX, float pSY, float pSW, float pSH) {
		sx = pSX;
		sy = pSY;
		sw = pSW;
		sh = pSH;
	}

	public void setupDestTexture(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;

	}

	public void spawnParticle(int pParticleType, float pWorldX, float pWorldY, float pVX, float pVY, float pLife) {
		particleType = pParticleType;
		mIsFree = false;
		mLifeTime = pLife;
		timeSinceStart = 0;
		scale = 1;
		sx = sy = 1;
		r = 1.f;
		g = 1.f;
		b = 1.f;
		a = 1.f;

		odx = pVX;
		ody = pVY;
		dx = odx;
		dy = ody;
		baseWorldPositionX = pWorldX;
		baseWorldPositionY = pWorldY;

		worldPositionX = baseWorldPositionX;
		worldPositionY = baseWorldPositionY;
	}

	public void reset() {
		mIsFree = true;
		particleType = TYPE_NONE;
		mLifeTime = 0;
		timeSinceStart = 0;
		scale = 1f;
		baseWorldPositionX = 0;
		baseWorldPositionY = 0;
		worldPositionX = 0;
		worldPositionY = 0;
		odx = 0f;
		ody = 0f;
		dx = 0f;
		dy = 0f;
	}
}
