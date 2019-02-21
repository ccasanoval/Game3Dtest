package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.components.SwitchComponent
import com.cesoft.cesdoom.entities.Ammo
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object MazeFactory {
    val tag: String = MazeFactory::class.java.simpleName
	const val MAX_LEVEL = 2

	//480x1 == 240x2 == 160x3 == 120x4
	const val lng = WallFactory.LONG
	const val lng2 = WallFactory.LONG*2
	const val high = WallFactory.HIGH
	const val high2 = WallFactory.HIGH*2
	private const val mapWidth = 3*(5*lng2)
	private const val mapHeight = 3*(5*lng2)
	private const val scale = 5
	val mapFactory = MapGraphFactory(mapWidth, mapHeight, scale)


	//______________________________________________________________________________________________
	fun create(engine: Engine, assets: Assets) {
		WallFactory.iniMaterials(assets)

		when (PlayerComponent.currentLevel) {
			0 -> createLevel0(engine, assets)
			1 -> createLevel1(engine, assets)
			2 -> createLevel2(engine, assets)
		}
		mapFactory.compile()

		//----- TEST
//		mapFactory.print()
//		Log.e(tag, "")
//		Log.e(tag, "")
//		mapFactory.print2()
//        Log.e(tag, "----------------------------------------------------------------------------")
//        Log.e(tag, " LEVEL ACCESSES:LEVEL(0) "+ mapFactory.map[0].levelAccess.size)
//        for(access in mapFactory.map[0].levelAccess) {
//            Log.e(tag, "access----------------------- $access")
//        }
//		Log.e(tag, "----------------------------------------------------------------------------")
//		Log.e(tag, " LEVEL ACCESSES:LEVEL(1) "+ mapFactory.map[1].levelAccess.size)
//		for(access in mapFactory.map[1].levelAccess) {
//			Log.e(tag, "access----------------------- $access")
//		}
//		//
//		Log.e(tag, "----------------------------------------------------------------------------")
//		Log.e(tag, "--------------------------- ${PlayerComponent.currentLevel} ------------------------------------------")
//		Log.e(tag, "----------------------------------------------------------------------------")
		//
		/*
		val path = mapFactory.map.findPath(Vector2(0f, -250f), Vector2(0f, 0f))
		for(step in path) {
			Log.e("Maze", step.toString())
		}
		*/
		//----- TEST
	}


	//TODO: Level constructor !!!


	//______________________________________________________________________________________________
	private fun createLevelX(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory

		/// Shapes -------------
		addShapesX(level, e)


		/// First Wall -------------
		//---
		for(x in -7..+7 step 2) {
			wf.create(mapFactory, e, Vector3(x*lng, 0f, +4*lng2), 90f)
			wf.create(mapFactory, e, Vector3(x*lng, 0f, -4*lng2), 90f)
		}
		for(z in -7..+7 step 2) {
			if(z != +7 && z != -7)
				wf.create(mapFactory, e, Vector3(+4*lng2, 0f, z*lng), 0f)
			if(z != +7 && z != -7)
				wf.create(mapFactory, e, Vector3(-4*lng2, 0f, z*lng), 0f)
		}
		//---
		// First Wall -------------


		/// Outer Wall ------------------
		for(z in -11..11 step 2) {
			wf.create(mapFactory, e, Vector3(+7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
		}
		for(x in -13..13 step 2) {
			if (x == 1) {
				// SALIDA 0
				var id = " A "
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, +6f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, +6f * lng2 + (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				// SALIDA 1
				id = " B "
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, -6f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, -6f * lng2 - (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				//
				addSwitchesLevelX(level, e, assets)
				continue
			}
			wf.create(mapFactory, e, Vector3(x * lng, 0f, +6f * lng2), 90f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(x * lng, 0f, -6f * lng2), 90f, WallFactory.Type.GRILLE)
		}
		// Outer Wall ------------------

		/// Extra Gates
		GateFactory.create(mapFactory, e, Vector3(+GateComponent.LONG + .2f, 0f, 0f), 0f, " C ", assets).unlock()
		GateFactory.create(mapFactory, e, Vector3(-GateComponent.LONG - .2f, 0f, 0f), 0f, " D ", assets).unlock()

		// RAMPS ------------------
		val rampFactory = RampFactory(assets)
		rampFactory.create(mapFactory, e, Vector3(+4 * lng2, high2, 0f), angleY = 90f, angleZ = 90f, type = RampFactory.Type.GRID)
		rampFactory.create(mapFactory, e, Vector3(-4 * lng2, high2, 0f), angleY = 90f, angleZ = 90f, type = RampFactory.Type.GRID)
		rampFactory.create(mapFactory, e, Vector3(+3 * lng2, high + 2f, 0f), angleX = 90f, angleY = -45f)
		rampFactory.create(mapFactory, e, Vector3(-3 * lng2, high + 2f, 0f), angleX = 90f, angleY = +45f)

		// AMMO ------------------
		addAmmoLevelX(level, e, assets)

		// HEALTH  ------------------
		addHealthLevelX(level, e, assets)
	}
	private fun addShapesX(level: Int, e: Engine) {
		val wf = WallFactory

		for(y in 0..level) {
			/// U Shapes -------------
			wf.create(mapFactory, e, Vector3(+0*lng, y*high2, +.5f*lng2), 90f)
			wf.create(mapFactory, e, Vector3(+0*lng, y*high2, -.5f*lng2), 90f)
			wf.create(mapFactory, e, Vector3(+1*lng, y*high2, +2*lng), 00f)
			wf.create(mapFactory, e, Vector3(-1*lng, y*high2, +2*lng), 00f)
			wf.create(mapFactory, e, Vector3(+1*lng, y*high2, -2*lng), 00f)
			wf.create(mapFactory, e, Vector3(-1*lng, y*high2, -2*lng), 00f)

			/// S Shapes -------------
			wf.create(mapFactory, e, Vector3(+5*lng, y*high2, +2*lng2), 90f)
			wf.create(mapFactory, e, Vector3(-5*lng, y*high2, +2*lng2), 90f)
			wf.create(mapFactory, e, Vector3(+5*lng, y*high2, -2*lng2), 90f)
			wf.create(mapFactory, e, Vector3(-5*lng, y*high2, -2*lng2), 90f)
			//
			wf.create(mapFactory, e, Vector3(+3*lng, y*high2, +3*lng2), 90f)
			wf.create(mapFactory, e, Vector3(-3*lng, y*high2, +3*lng2), 90f)
			wf.create(mapFactory, e, Vector3(+3*lng, y*high2, -3*lng2), 90f)
			wf.create(mapFactory, e, Vector3(-3*lng, y*high2, -3*lng2), 90f)
			//
			wf.create(mapFactory, e, Vector3(+2*lng2, y*high2, +5*lng), 00f)
			wf.create(mapFactory, e, Vector3(-2*lng2, y*high2, +5*lng), 00f)
			wf.create(mapFactory, e, Vector3(+2*lng2, y*high2, -5*lng), 00f)
			wf.create(mapFactory, e, Vector3(-2*lng2, y*high2, -5*lng), 00f)
		}
	}
	//______________________________________________________________________________________________
	private fun addSwitchesLevelX(level: Int, e: Engine, assets: Assets) {
		when(level) {
			0 -> {
				SwitchFactory.create(e, Vector3(0f, 0f, -WallFactory.LONG - SwitchComponent.SIZE / 2), 90f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, 0f, +WallFactory.LONG + SwitchComponent.SIZE / 2), 90f, " B ", assets)
			}
			1,2 -> {
				SwitchFactory.create(e, Vector3(0f, high2, -WallFactory.LONG - SwitchComponent.SIZE / 2), 90f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, high2, +WallFactory.LONG + SwitchComponent.SIZE / 2), 90f, " B ", assets)
			}
		}
	}
	private fun addAmmoLevelX(level: Int, e: Engine, assets: Assets) {
		val ammoModel = assets.getAmmo()
		when(level) {
			0 -> {
				Ammo(Vector3(+6*lng2, 0f, 0f), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, 0f), ammoModel, e)
			}
			1,2 -> {
				Ammo(Vector3(+6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(Vector3(+6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(Vector3(+3*lng2, high2, +3*lng2), ammoModel, e)
				Ammo(Vector3(-3*lng2, high2, -3*lng2), ammoModel, e)
			}
		}
	}
	private fun addHealthLevelX(level: Int, e: Engine, assets: Assets) {
		val healthModel = assets.getHealth()
		when(level) {
			0 -> {
				Health(Vector3(+4*lng2, high2, 0f), healthModel, e)
				Health(Vector3(-4*lng2, high2, 0f), healthModel, e)
			}
			1,2 -> {
				Health(Vector3(+3.5f*lng2, 0f, 0f), healthModel, e)
				Health(Vector3(-3.5f*lng2, 0f, 0f), healthModel, e)
				Health(Vector3(+4*lng2, high2, -3*lng2), healthModel, e)
				Health(Vector3(-4*lng2, high2, +3*lng2), healthModel, e)
			}
		}
	}



	//______________________________________________________________________________________________
	private fun createLevel0(e: Engine, assets: Assets) {
		createLevelX(0, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel1(e: Engine, assets: Assets) {
		createLevelX(1, e, assets)
		createLevel1FirstFloor(e, assets)
	}
	private fun createLevel1FirstFloor(e: Engine, assets: Assets) {
		val rampFactory = RampFactory(assets)
		val cx = RampFactory.LONG*2
		val cz = RampFactory.HIGH*2
		for(z in -5..+5) {
			for(x in -4..+4) {
				if(z == 0 && (x == 0 || (x < -1 && x > -4) || (x > +1 && x < +4))) continue
				val type = if(x > -2 && z < +2 && z > -3 && z < +4) RampFactory.Type.STEEL else RampFactory.Type.GRID
				rampFactory.create(mapFactory, e, Vector3(x*cx, high2, z*cz), angleX=0f, angleY=90f, angleZ=90f, type=type)
			}
		}
	}



	//______________________________________________________________________________________________
	private fun createLevel2(e: Engine, assets: Assets) {
		createLevelX(2, e, assets)
		createLevel2FirstFloor(e, assets)
	}
	private fun createLevel2FirstFloor(e: Engine, assets: Assets) {
		val rampFactory = RampFactory(assets)
		val cx = RampFactory.LONG*2
		val cz = RampFactory.HIGH*2
		for(z in -5..+5 step 1) {
			for(x in -4..+4 step 1) {
				if(z == 0 && (x == 0 || (x < -1 && x > -4) || (x > +1 && x < +4))) continue
				val type = RampFactory.Type.STEEL //else RampFactory.Type.GRID
				rampFactory.create(mapFactory, e, Vector3(x*cx, high2, z*cz), angleX=0f, angleY=90f, angleZ=90f, type=type)
			}
		}
	}
}