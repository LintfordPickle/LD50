package net.ld.ld50.controllers;

import java.util.ArrayList;
import java.util.List;

import net.ld.ld50.data.particles.Particle;
import net.ld.ld50.data.particles.ParticleManager;
import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;

public class ParticleController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Particle Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleManager mProjectileManager;

	private final List<Particle> projectileUpdateList = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleManager projectileManager() {
		return mProjectileManager;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleController(ControllerManager pControllerManager, ParticleManager pParticles, int pEntityGroupUid) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupUid);
		mProjectileManager = pParticles;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		final var lControllerManager = pCore.controllerManager();

	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		updateParticles(pCore);
	}

	private void updateParticles(LintfordCore pCore) {
		projectileUpdateList.clear();

		final var lProjectiles = projectileManager().projectiles();
		final int lNumProjectiles = lProjectiles.size();

		for (int i = 0; i < lNumProjectiles; i++) {
			projectileUpdateList.addAll(lProjectiles);
		}

		for (int i = 0; i < lNumProjectiles; i++) {
			final var lProjectile = projectileUpdateList.get(i);
			if (lProjectile.isAssigned() == false)
				continue;

			lProjectile.timeSinceStart += pCore.gameTime().elapsedTimeMilli();
			if (lProjectile.timeSinceStart > lProjectile.lifeTime()) {
				lProjectile.reset();
				continue;
			}

			switch (lProjectile.particleType) {
			case Particle.TYPE_TRAIL:
				updateTrail(pCore, lProjectile);
				break;
			default:
				updateSpark(pCore, lProjectile);
				break;
			}
		}
	}

	private void updateSpark(LintfordCore pCore, Particle pParticle) {
		if (pParticle == null)
			return;

		pParticle.dy += 0.048f;

		pParticle.worldPositionX += pParticle.dx;
		pParticle.worldPositionY += pParticle.dy;
		pParticle.rotationInRadians = (float) Math.atan2(pParticle.dy, pParticle.dx);
	}

	private void updateTrail(LintfordCore pCore, Particle pParticle) {
		if (pParticle == null)
			return;

		pParticle.a = 1.f - (pParticle.timeSinceStart / pParticle.lifeTime());
		pParticle.scale = 1.f - (pParticle.timeSinceStart / pParticle.lifeTime());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addTrail(float pwx, float pwy, float pvx, float pvy) {
		final var lParticle = mProjectileManager.spawnParticle(Particle.TYPE_TRAIL, pwx, pwy, 0, 0, 700.0f);
		lParticle.rotationInRadians = (float) Math.atan2(pvy, pvx);
		lParticle.setupSourceTexture(32, 0, 16, 16);
	}

	public void hitWall(float pwx, float pwy, float pvx, float pvy, float power) {
		System.out.println("spark power: " + power);
		if (power < 10)
			return;

		final int lNumSparks = RandomNumbers.random(1, 4);
		for (int i = 0; i < lNumSparks; i++) {
			float lAngle = (float) Math.atan2(pvy, pvx);
			final float angdev = (float) Math.toRadians(15.f);
			lAngle += RandomNumbers.random(-angdev, angdev);

			final float lVelMod = power / 10.f;
			final float vx = (float) Math.cos(lAngle) * lVelMod;
			final float vy = (float) Math.sin(lAngle) * lVelMod;

			final var lParticle = mProjectileManager.spawnParticle(Particle.TYPE_SPARK, pwx, pwy, vx, vy, 1000.0f);

			lParticle.rotationInRadians = lAngle;
			lParticle.setupSourceTexture(0, 0, 32, 16);
		}
	}

}
