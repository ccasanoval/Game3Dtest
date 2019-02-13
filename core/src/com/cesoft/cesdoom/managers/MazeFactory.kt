package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.SwitchComponent
import com.cesoft.cesdoom.entities.Ammo
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object MazeFactory {

	const val MAX_LEVEL = 1


	//480x1 == 240x2 == 160x3 == 120x4
	const val lng = WallFactory.LONG
	const val lng2 = 2*WallFactory.LONG
	private const val mapWidth = 3f*(5f*lng2)
	private const val mapHeight = 3f*(5f*lng2)
	private const val scale = 5
	val mapFactory = MapGraphFactory(mapWidth, mapHeight, scale)


	//______________________________________________________________________________________________
	fun create(engine: Engine, assets: Assets) {
		WallFactory.iniMaterials(assets)

		createLevel0(engine, assets)
		//createLevel1(engine, assets)
		mapFactory.compile()


		//----- TEST
//		mapFactory.print()
//		Log.e("AAA", "")
//		Log.e("AAA", "")
//		mapFactory.print2()

		Log.e("Maze", "----------------------------------------------------------------------------")
		Log.e("Maze", "----------------------------------------------------------------------------")

		/*
		val path = mapFactory.map.findPath(Vector2(0f, -250f), Vector2(0f, 0f))
		for(step in path) {
			Log.e("Maze", step.toString())
		}
		*/
		//----- TEST

	}


	//TODO: Level constructor !!!
	private fun createLevel0(e: Engine, assets: Assets) {
		val wf = WallFactory

		/// Inside Wall -------------
		wf.create(mapFactory, e, Vector3(0 * lng, 0f, +.5f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(0 * lng, 0f, -.5f * lng2), 90f)
		//
		wf.create(mapFactory, e, Vector3(+1f * lng, 0f, +2 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-1f * lng, 0f, +2 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+1f * lng, 0f, -2 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-1f * lng, 0f, -2 * lng), 00f)


		/// Middle Wall -------------
		wf.create(mapFactory, e, Vector3(+5 * lng, 0f, +2f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-5 * lng, 0f, +2f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+5 * lng, 0f, -2f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-5 * lng, 0f, -2f * lng2), 90f)

		wf.create(mapFactory, e, Vector3(+3 * lng, 0f, +3f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-3 * lng, 0f, +3f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+3 * lng, 0f, -3f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-3 * lng, 0f, -3f * lng2), 90f)

		wf.create(mapFactory, e, Vector3(+2 * lng2, 0f, +5f * lng), 00f)
		wf.create(mapFactory, e, Vector3(-2 * lng2, 0f, +5f * lng), 00f)
		wf.create(mapFactory, e, Vector3(+2 * lng2, 0f, -5f * lng), 00f)
		wf.create(mapFactory, e, Vector3(-2 * lng2, 0f, -5f * lng), 00f)


		/// Outer Wall -------------
		//---
		wf.create(mapFactory, e, Vector3(+1 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+3 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+5 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+7 * lng, 0f, +4f * lng2), 90f)
		//
		wf.create(mapFactory, e, Vector3(-1 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-3 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-5 * lng, 0f, +4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-7 * lng, 0f, +4f * lng2), 90f)
		//
		wf.create(mapFactory, e, Vector3(+1 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+3 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+5 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(+7 * lng, 0f, -4f * lng2), 90f)
		//
		wf.create(mapFactory, e, Vector3(-1 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-3 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-5 * lng, 0f, -4f * lng2), 90f)
		wf.create(mapFactory, e, Vector3(-7 * lng, 0f, -4f * lng2), 90f)

		//---
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, +1 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, +3 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, +5 * lng), 00f)
		//
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, +1 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, +3 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, +5 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, +7 * lng), 00f)
		//
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, -1 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, -3 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, -5 * lng), 00f)
		wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, -7 * lng), 00f)
		//
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, -1 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, -3 * lng), 00f)
		wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, -5 * lng), 00f)
		//---
		// Outer Wall -------------


		/// Extra Outer Wall ------------------
		for (z in -11..11 step 2) {
			wf.create(mapFactory, e, Vector3(+7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
		}
		for (x in -13..13 step 2) {
			if (x == 1) {
				// SALIDA 0
				var id = " A "
				SwitchFactory.create(e, Vector3(0f, 0f, -WallFactory.LONG - SwitchComponent.SIZE / 2), 90f, id, assets)
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, +6f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, +6f * lng2 + (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				// SALIDA 1
				id = " B "
				SwitchFactory.create(e, Vector3(0f, 0f, +WallFactory.LONG + SwitchComponent.SIZE / 2), 90f, id, assets)
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, -6f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, -6f * lng2 - (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				continue
			}
			wf.create(mapFactory, e, Vector3(x * lng, 0f, +6f * lng2), 90f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(x * lng, 0f, -6f * lng2), 90f, WallFactory.Type.GRILLE)
		}
		// Extra Outer Wall ------------------

		/// Extra Gates
		GateFactory.create(mapFactory, e, Vector3(+GateComponent.LONG + .2f, 0f, 0f), 0f, " C ", assets).unlock()
		GateFactory.create(mapFactory, e, Vector3(-GateComponent.LONG - .2f, 0f, 0f), 0f, " D ", assets).unlock()
		//YouWinFactory.create(Vector3(-2*GateComponent.LONG, 0f, 0f), e)


		// AMMO ------------------
		val ammoModel = assets.getAmmo()
		Ammo(Vector3(+6f * lng2, 0f, 0f), ammoModel, e)
		Ammo(Vector3(-6f * lng2, 0f, 0f), ammoModel, e)

		// HEALTH  ------------------
		val healthModel = assets.getHealth()
		Health(Vector3(+4f * lng2, 2f * WallFactory.HIGH, 0f), healthModel, e)
		Health(Vector3(-4f * lng2, 2f * WallFactory.HIGH, 0f), healthModel, e)

		// RAMPS ------------------
		val rampFactory = RampFactory(assets)
		rampFactory.create(mapFactory, e, Vector3(+4 * lng2, 2f * WallFactory.HIGH, 0f), angleY = 90f, angleZ = 90f, type = false)
		rampFactory.create(mapFactory, e, Vector3(-4 * lng2, 2f * WallFactory.HIGH, 0f), angleY = 90f, angleZ = 90f, type = false)
		rampFactory.create(mapFactory, e, Vector3(+3 * lng2, WallFactory.HIGH + 2f, 0f), angleX = 90f, angleY = -45f)
		rampFactory.create(mapFactory, e, Vector3(-3 * lng2, WallFactory.HIGH + 2f, 0f), angleX = 90f, angleY = +45f)
	}

}