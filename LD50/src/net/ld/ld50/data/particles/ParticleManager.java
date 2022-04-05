package net.ld.ld50.data.particles;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;

public class ParticleManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<Particle> mProjectiles;
	private int mCapacity;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<Particle> projectiles() {
		return mProjectiles;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleManager(int pCap) {
		super();

		mCapacity = pCap;

		mProjectiles = new ArrayList<>();
		for (int i = 0; i < mCapacity; i++) {
			mProjectiles.add(new Particle());
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		for (int i = 0; i < mCapacity; i++) {
			final var p = mProjectiles.get(i);

			if (!p.isAssigned())
				continue;

			p.timeSinceStart += pCore.appTime().elapsedTimeMilli();
			if (p.timeSinceStart >= p.lifeTime()) {
				p.reset();

			}
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Spawns a new {@link Particle} and applys the {@link IParticleinitializer} attached to this {@link ParticleSystemInstance}. */
	public Particle spawnParticle(int pType, float pX, float pY, float pVX, float pVY, float pLife) {
		for (int i = 0; i < mCapacity; i++) {
			Particle lSpawnedParticle = mProjectiles.get(i);
			if (lSpawnedParticle.isAssigned())
				continue;

			lSpawnedParticle.spawnParticle(pType, pX, pY, pVX, pVY, pLife);

			return lSpawnedParticle;
		}

		return null;
	}

	public void reset() {
		for (int i = 0; i < mCapacity; i++) {
			Particle p = mProjectiles.get(i);
			p.reset();
		}
	}
}
