package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
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

	//480x1 == 240x2 == 160x3 == 120x4
	private const val lng = WallFactory.LONG
	private const val lng2 = WallFactory.LONG*2
	private const val high = WallFactory.HIGH
	private const val high2 = WallFactory.HIGH*2
	private const val mapWidth = 3*(5*lng2)
	private const val mapHeight = 3*(5*lng2)
	const val scale = 5
	private lateinit var mapFactory: MapGraphFactory
	private lateinit var rampFactory: RampFactory

	fun findPath(floorEnemy: Int, pos: Vector2, target: Vector2, smooth: Boolean=false): ArrayList<Vector2> {
		val map = MazeFactory.mapFactory.map[floorEnemy]
		return map.findPath(pos, target, smooth)
	}
	fun getNearerFloorAccess(floorEnemy: Int, enemyPos2D: Vector2): Vector2 {
		val map = MazeFactory.mapFactory.map[floorEnemy]
		return map.getNearerFloorAccess(enemyPos2D)
	}

	//______________________________________________________________________________________________
	fun create(engine: Engine, assets: Assets) {
		WallFactory.iniMaterials(assets)
		mapFactory = MapGraphFactory(mapWidth, mapHeight, scale)
		rampFactory = RampFactory(assets)

		createLevel(engine, assets)

		mapFactory.compile()

		//----- TEST
//		mapFactory.print()
//		com.cesoft.cesdoom.util.Log.e(tag, "")
//		com.cesoft.cesdoom.util.Log.e(tag, "")

		mapFactory.print2()
		com.cesoft.cesdoom.util.Log.e(tag, "----------------------------------------------------------------------------")
		com.cesoft.cesdoom.util.Log.e(tag, " LEVEL ACCESSES:LEVEL(0) "+ mapFactory.map[0].floorAccess.size)
        for(access in mapFactory.map[0].floorAccess) {
			com.cesoft.cesdoom.util.Log.e(tag, "access----------------------- $access")
        }
//		com.cesoft.cesdoom.util.Log.e(tag, "----------------------------------------------------------------------------")
//		com.cesoft.cesdoom.util.Log.e(tag, " LEVEL ACCESSES:LEVEL(1) "+ mapFactory.map[1].floorAccess.size)
//		for(access in mapFactory.map[1].floorAccess) {
//			com.cesoft.cesdoom.util.Log.e(tag, "access----------------------- $access")
//		}

//		//
//		Log.e(tag, "----------------------------------------------------------------------------")
//		Log.e(tag, "--------------------------- ${PlayerComponent.currentLevel} ------------------------------------------")
//		Log.e(tag, "----------------------------------------------------------------------------")
		//
        //----- TEST

        /// La primera vez que se ejecuta el algoritmo, tarda un poco en cachear los nodos
        /// De modo que lo ejecutamos antes de que el juego empieze para que player no note el flick...
        for(map in mapFactory.map) {
            map.cx
            val path = map.findPath(Vector2(0f, 0f), Vector2(map.width-1, map.height-1))
            for (step in path) {
                //Log.e("Maze", step.toString())
            }
        }
        Log.e(tag, "create---------------------------------------------------------------------------- END")
	}


	//TODO: Level constructor !!!


	//______________________________________________________________________________________________
	private fun createLevelX(level: Int, e: Engine, assets: Assets) {

		/// INTERIOR SHAPES -------------
		addShapesX(level, e)

		/// INNER WALL
		addInnerWall(e)

		/// OUTER WALL ------------------
		addOuterWall(level, e, assets)

		/// INTERIOR GATES
		GateFactory.create(mapFactory, e, Vector3(+GateComponent.LONG + .2f, 0f, 0f), 0f, " C ", assets).unlock()
		GateFactory.create(mapFactory, e, Vector3(-GateComponent.LONG - .2f, 0f, 0f), 0f, " D ", assets).unlock()

		// RAMPS ------------------
		rampFactory.create(mapFactory, e, Vector3(+3f*lng2, high, 0f), angleX = 90f, angleY = -45f)
		rampFactory.create(mapFactory, e, Vector3(-3f*lng2, high, 0f), angleX = 90f, angleY = +45f)

		// GANG WAYS ------------------
		addGangWays(level, e)

		// AMMO ------------------
		addAmmoLevelX(level, e, assets)

		// HEALTH  ------------------
		addHealthLevelX(level, e, assets)
	}


	private fun addGangWays(level: Int, e: Engine) {
		if(level == 0) {
			for(x in -3..+3)
				rampFactory.createGround(mapFactory, e, Vector3(x*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE, xWay=true)

			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE, zWay=true)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE, zWay=true)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
		}
		else {
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
		}
	}
	//______________________________________________________________________________________________
	private fun addOuterWall(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory
		for (z in -12..12 step 2) {
			wf.create(mapFactory, e, Vector3(+7 * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-7 * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
		}
		for (x in -13..13 step 2) {
			if (x == 1) {
				// EXIT 0
				var id = " A "
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, +6.5f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, +6.5f * lng2 + (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				// EXIT 1
				id = " B "
				GateFactory.create(mapFactory, e, Vector3(x * lng, 0f, -6.5f * lng2), 90f, id, assets)
				YouWinFactory.create(e, Vector3(x * lng, 0f, -6.5f * lng2 - (2 * YouWinFactory.SIZE + GateComponent.THICK)))
				//
				addSwitchesLevelX(level, e, assets)
				continue
			}
			wf.create(mapFactory, e, Vector3(x * lng, 0f, +6.5f * lng2), 90f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(x * lng, 0f, -6.5f * lng2), 90f, WallFactory.Type.GRILLE)
		}
	}
	//______________________________________________________________________________________________
	private fun addInnerWall(e: Engine) {
		val wf = WallFactory
		for (x in -7..+7 step 2) {
			wf.create(mapFactory, e, Vector3(x * lng, 0f, +4 * lng2), 90f)
			wf.create(mapFactory, e, Vector3(x * lng, 0f, -4 * lng2), 90f)
		}
		for (z in -7..+7 step 2) {
			if (z != +7 && z != -7)
				wf.create(mapFactory, e, Vector3(+4 * lng2, 0f, z * lng), 0f)
			if (z != +7 && z != -7)
				wf.create(mapFactory, e, Vector3(-4 * lng2, 0f, z * lng), 0f)
		}
	}
	//______________________________________________________________________________________________
	private fun addShapesX(level: Int, e: Engine) {
		val wf = WallFactory
		when {
			level < 999 -> {
				for(y in 0..level) {
					/// U Shapes -------------
					wf.create(mapFactory, e, Vector3(+0*lng, y*high2, +.5f*lng2), 90f)
					wf.create(mapFactory, e, Vector3(+0*lng, y*high2, -.5f*lng2), 90f)
					wf.create(mapFactory, e, Vector3(+1*lng, y*high2, +2*lng), 00f)
					wf.create(mapFactory, e, Vector3(-1*lng, y*high2, +2*lng), 00f)
					wf.create(mapFactory, e, Vector3(+1*lng, y*high2, -2*lng), 00f)
					wf.create(mapFactory, e, Vector3(-1*lng, y*high2, -2*lng), 00f)

					/// S Shapes -------------
					wf.create(mapFactory, e, Vector3(+5*lng, y*high2, +2*lng2-wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(-5*lng, y*high2, +2*lng2-wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(+5*lng, y*high2, -2*lng2+wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(-5*lng, y*high2, -2*lng2+wf.THICK), 90f)
					//
					wf.create(mapFactory, e, Vector3(+3*lng, y*high2, +3*lng2+wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(-3*lng, y*high2, +3*lng2+wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(+3*lng, y*high2, -3*lng2-wf.THICK), 90f)
					wf.create(mapFactory, e, Vector3(-3*lng, y*high2, -3*lng2-wf.THICK), 90f)
					//
					wf.create(mapFactory, e, Vector3(+2*lng2, y*high2, +5*lng), 00f)
					wf.create(mapFactory, e, Vector3(-2*lng2, y*high2, +5*lng), 00f)
					wf.create(mapFactory, e, Vector3(+2*lng2, y*high2, -5*lng), 00f)
					wf.create(mapFactory, e, Vector3(-2*lng2, y*high2, -5*lng), 00f)
				}
			}
			else -> {
				for (y in 0..2) {
					wf.create(mapFactory, e, Vector3(+0 * lng, y * high2, +.5f * lng2), 90f)
				}
			}
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
				SwitchFactory.create(e, Vector3(0f, level*high2, -WallFactory.LONG - SwitchComponent.SIZE / 2), 90f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, level*high2, +WallFactory.LONG + SwitchComponent.SIZE / 2), 90f, " B ", assets)
			}
			3 -> {
				SwitchFactory.create(e, Vector3(0f, 2*high2, -WallFactory.LONG - SwitchComponent.SIZE / 2), 90f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, 2*high2, +WallFactory.LONG + SwitchComponent.SIZE / 2), 90f, " B ", assets)
			}
		}
	}

	//______________________________________________________________________________________________
	private fun addAmmoLevelX(level: Int, e: Engine, assets: Assets) {
		val ammoModel = assets.getAmmo()
		when(level) {
			0 -> {
				Ammo(Vector3(+6*lng2, 0f, 0f), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, 0f), ammoModel, e)
			}
			else -> {
				Ammo(Vector3(+6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(Vector3(+6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(Vector3(-6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(Vector3(+3*lng2, high2, +3*lng2), ammoModel, e)
				Ammo(Vector3(-3*lng2, high2, -3*lng2), ammoModel, e)
			}
		}
	}

	//______________________________________________________________________________________________
	private fun addHealthLevelX(level: Int, e: Engine, assets: Assets) {
		val healthModel = assets.getHealth()
		when(level) {
			0 -> {
				Health(Vector3(+4*lng2, high2, 0f), healthModel, e)
				Health(Vector3(-4*lng2, high2, 0f), healthModel, e)
			}
			else -> {
				Health(Vector3(+3.5f*lng2, 0f, 0f), healthModel, e)
				Health(Vector3(-3.5f*lng2, 0f, 0f), healthModel, e)
				Health(Vector3(+4*lng2, high2, -3*lng2), healthModel, e)
				Health(Vector3(-4*lng2, high2, +3*lng2), healthModel, e)
			}
		}
	}

	//______________________________________________________________________________________________
	private fun createFirstFloor(level: Int, e: Engine, assets: Assets) {
		val cx = RampFactory.LONG_GROUND
		val cz = RampFactory.LONG_GROUND
		for(z in -8..+8 step 2) {
			if(z == 0) {
				val type = RampFactory.Type.STEEL
				rampFactory.createGround(mapFactory, e, Vector3(+2*cx, high2, 0f), type)
				rampFactory.createGround(mapFactory, e, Vector3(-2*cx, high2, 0f), type)
				rampFactory.createGround(mapFactory, e, Vector3(+3*cx, high2, 0f), type)
				rampFactory.createGround(mapFactory, e, Vector3(-3*cx, high2, 0f), type)
			}
			else {
				for(x in -8..+8 step 2) {
					val type = if(level != 1 || z==+2 || z==-2) RampFactory.Type.STEEL else RampFactory.Type.GRILLE
					rampFactory.createGround(mapFactory, e, Vector3(x * cx, high2, z * cz), type)
				}
			}
		}
		/// Extra Gates
		GateFactory.create(mapFactory, e, Vector3(+GateComponent.LONG + .2f, high2, 0f), 0f, " E ", assets).unlock()
		GateFactory.create(mapFactory, e, Vector3(-GateComponent.LONG - .2f, high2, 0f), 0f, " F ", assets).unlock()
		/// Extra Ramps
		rampFactory.create(mapFactory, e, Vector3(-2*lng2, high, +5f*lng2), angleX = +45f, angleZ = 90f)
		rampFactory.create(mapFactory, e, Vector3(+2*lng2, high, -5f*lng2), angleX = -45f, angleZ = 90f)
		/// Extra Walls
		if(level == 3) {
			val wf = WallFactory
			for(x in -4..+4) {
				if(x != -2)
					wf.create(mapFactory, e, Vector3(x*lng2, high2, +4.5f*lng2), 90f, WallFactory.Type.GRILLE)
				if(x != +2)
					wf.create(mapFactory, e, Vector3(x*lng2, high2, -4.5f*lng2), 90f, WallFactory.Type.GRILLE)
			}
			wf.create(mapFactory, e, Vector3(-4.5f*lng2, high2, +4f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(+4.5f*lng2, high2, +4f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-4.5f*lng2, high2, -4f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(+4.5f*lng2, high2, -4f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-4.5f*lng2, high2, +3f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(+4.5f*lng2, high2, +3f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(-4.5f*lng2, high2, -3f*lng2), 0f, WallFactory.Type.GRILLE)
			wf.create(mapFactory, e, Vector3(+4.5f*lng2, high2, -3f*lng2), 0f, WallFactory.Type.GRILLE)
		}
	}
	//______________________________________________________________________________________________
	private fun createSecondFloor(level: Int, e: Engine, assets: Assets) {
		val cx = RampFactory.LONG_GROUND
		val cz = RampFactory.LONG_GROUND
		val type = RampFactory.Type.GRILLE
		for(z in -2..+2 step 2) {
			for (x in -2..+2 step 2) {
				if(x == 0 && z == 0) continue
				rampFactory.createGround(mapFactory, e, Vector3(x * cx, 2*high2, z * cz), type)
			}
		}
		/// Extra Ramps
		rampFactory.create(mapFactory, e, Vector3(0f, high2+high, +2f*lng2), angleX = +45f, angleZ = 90f, type = RampFactory.Type.GRILLE)
		rampFactory.create(mapFactory, e, Vector3(0f, high2+high, -2f*lng2), angleX = -45f, angleZ = 90f, type = RampFactory.Type.GRILLE)
	}


	//______________________________________________________________________________________________
	const val MAX_LEVEL = 3
	private fun createLevel(engine: Engine, assets: Assets) {
		mapFactory.clear()
		when(PlayerComponent.currentLevel) {
			0 -> createLevel0(engine, assets)
			1 -> createLevel1(engine, assets)
			2 -> createLevel2(engine, assets)
			3 -> createLevel3(engine, assets)
		}
	}
	//______________________________________________________________________________________________
	private fun createLevel0(e: Engine, assets: Assets) {
		val level = 0
		createLevelX(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel1(e: Engine, assets: Assets) {
		val level = 1
		createLevelX(level, e, assets)
		createFirstFloor(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel2(e: Engine, assets: Assets) {
		val level = 2
		createLevelX(level, e, assets)
		createFirstFloor(level, e, assets)
		createSecondFloor(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel3(e: Engine, assets: Assets) {
		val level = 3//TODO: something new?
		createLevelX(level, e, assets)
		createFirstFloor(level, e, assets)
		createSecondFloor(level, e, assets)
	}

}