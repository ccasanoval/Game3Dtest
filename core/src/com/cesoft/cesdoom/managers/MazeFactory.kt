package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.entities.Ammo
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Change the way we create the objects to a more efficient way (Meshes...) so to increase FPS
object MazeFactory {
    val tag: String = MazeFactory::class.java.simpleName

	//480x1 == 240x2 == 160x3 == 120x4
	private const val lng = WallFactory.LONG
	private const val lng2 = WallFactory.LONG*2
	private const val high = WallFactory.HIGH
	private const val high2 = WallFactory.HIGH*2
	private const val thick = WallFactory.THICK
	private const val thick2 = WallFactory.THICK*2
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
//        for(map in mapFactory.map) {
//            map.cx
//            val path = map.findPath(Vector2(0f, 0f), Vector2(map.width-1, map.height-1))
//            for (step in path) {
//                //Log.e("Maze", step.toString())
//            }
//        }
        Log.e(tag, "create---------------------------------------------------------------------------- END")
	}


	//TODO: Level constructor !!!


	//______________________________________________________________________________________________
	private fun createLevelX(level: Int, e: Engine, assets: Assets) {

		/// INTERIOR SHAPES -------------
		addShapesX(level, e, assets)

		/// INNER WALL
		addInnerWall(e, assets)

		/// OUTER WALL ------------------
		addOuterWall(level, e, assets)

		/// INTERIOR GATES
		GateFactory.create(e, Vector3(+GateComponent.LONG + .2f, 0f, 0f), 0f, " C ", assets).unlock()
		GateFactory.create(e, Vector3(-GateComponent.LONG - .2f, 0f, 0f), 0f, " D ", assets).unlock()

		// RAMPS ------------------
		rampFactory.create(mapFactory, e, Vector3(+3f*lng2, high, 0f), angleX = 90f, angleY = -45f)
		rampFactory.create(mapFactory, e, Vector3(-3f*lng2, high, 0f), angleX = 90f, angleY = +45f)

		// GANG WAYS ------------------
		addGangWays(level, e, assets)

		// AMMO ------------------
		addAmmoLevelX(level, e, assets)

		// HEALTH  ------------------
		addHealthLevelX(level, e, assets)
	}


	//TODO: Cuando crees un nivel sobre plataformas, hacerlo todo bloqueado (1) e ir poniendo (0) donde cubra cada gangway!!!!!!!!!!!
	private fun addGangWays(level: Int, e: Engine, assets: Assets) {
		if(level == 0) {
			mapFactory.collideAll(1)

			var size = Vector2(7*lng2, high2)
			var pos = Vector3(0f, high2, -lng2)
			rampFactory.createGround2(mapFactory, e, assets, size, pos, -90f, RampFactory.Type.GRILLE, true)

			pos = Vector3(0f, high2, -4*lng2)
			rampFactory.createGround2(mapFactory, e, assets, size, pos, -90f, RampFactory.Type.GRILLE, true)
			pos = Vector3(0f, high2, +4*lng2)
			rampFactory.createGround2(mapFactory, e, assets, size, pos, -90f, RampFactory.Type.GRILLE, true)

			size = Vector2(high2, 8.5f*lng2)
			pos = Vector3(+4*lng2, high2, 0f)
			rampFactory.createGround2(mapFactory, e, assets, size, pos, -90f, RampFactory.Type.GRILLE, true)
			pos = Vector3(-4*lng2, high2, 0f)
			rampFactory.createGround2(mapFactory, e, assets, size, pos, -90f, RampFactory.Type.GRILLE, true)

			//
			for(i in -3..+3)
				mapFactory.addFloorAccess(1, i*lng2, -3*lng-2*mapFactory.scale)
			for(i in -1..+1)
				mapFactory.addFloorAccess(1, i*lng2, -lng+2*mapFactory.scale)
			mapFactory.addFloorAccess(1, +4*lng2, -lng2)
			mapFactory.addFloorAccess(1, -4*lng2, -lng2)
		}
		else {
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(+4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, 0f), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, +2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			rampFactory.createGround(mapFactory, e, Vector3(-4*lng2, high2, -2*RampFactory.LONG_GROUND), RampFactory.Type.GRILLE)
			//
			mapFactory.addFloorAccess(1, +6*lng, 0f)
			mapFactory.addFloorAccess(1, -6*lng, 0f)//TODO: Mas accesos de 1 a 0
		}
	}
	//______________________________________________________________________________________________
	private fun addOuterWall(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory

		/// GRILLE
		var grilleSize = Vector2(13*lng2, high2)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(+6.7f*lng2, 0f, 0f), -90f)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(-6.7f*lng2, 0f, 0f), +90f)
		//
		grilleSize = Vector2(5.85f*lng2, high2)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(-3.6f*lng2, 0f, -6.5f*lng2), 0f)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(+3.6f*lng2, 0f, -6.5f*lng2), 0f)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(-3.6f*lng2, 0f, +6.5f*lng2), 180f)
		wf.createGrille(mapFactory, e, assets, grilleSize, Vector3(+3.6f*lng2, 0f, +6.5f*lng2), 180f)

		var columnSize = Vector3(10f, high2, 30f)
		// EXIT 0
		var id = " A "
		GateFactory.create(e, Vector3(0f, 0f, +6.5f*lng2), 90f, id, assets)
		YouWinFactory.create(e, Vector3(0f, 0f, +7.5f*lng2 + (2*YouWinFactory.SIZE + GateComponent.THICK)), 180f, assets)
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(-GateComponent.LONG-columnSize.x/2, 0f, +6.6f*lng2))
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(+GateComponent.LONG+columnSize.x/2, 0f, +6.6f*lng2))
		// EXIT 1
		id = " B "
		GateFactory.create(e, Vector3(0f, 0f, -6.5f*lng2), 90f, id, assets)
		YouWinFactory.create(e, Vector3(0f, 0f, -7.5f*lng2 - (2*YouWinFactory.SIZE + GateComponent.THICK)), 0f, assets)
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(-GateComponent.LONG-columnSize.x/2, 0f, -6.6f*lng2))
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(+GateComponent.LONG+columnSize.x/2, 0f, -6.6f*lng2))
		// 4 corner columns
		columnSize = Vector3(15f, high2, 15f)
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(-6.6f*lng2, 0f, -6.4f*lng2))
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(+6.6f*lng2, 0f, -6.4f*lng2))
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(-6.6f*lng2, 0f, +6.4f*lng2))
		ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(+6.6f*lng2, 0f, +6.4f*lng2))
		//
		addSwitchesLevelX(level, e, assets)
	}
	//______________________________________________________________________________________________
	private fun addInnerWall(e: Engine, assets: Assets) {
		val wf = WallFactory

		// Horizontal
		var size = Vector3(12*lng, high2, thick2)
		wf.createWall(mapFactory, e, assets, size, Vector3(0f, 0f, +4*lng2))
		size = Vector3(11*lng, high2, thick2)
		wf.createWall(mapFactory, e, assets, size, Vector3(0f, 0f, -4*lng2))

		// Vertical
		size = Vector3(thick2, high2, 10*lng)
		wf.createWall(mapFactory, e, assets, size, Vector3(+8*lng, 0f, 1.5f*lng))
		wf.createWall(mapFactory, e, assets, size, Vector3(-8*lng, 0f, 1.5f*lng))

		//x+ z-
		size = Vector3(lng2, high2, thick2)
		wf.createWall(mapFactory, e, assets, size, Vector3(+6.3f*lng, 0f, -7.3f*lng), +45f)
		wf.createWall(mapFactory, e, assets, size, Vector3(+8.6f*lng, 0f, -4*lng), -45f)
		//x- z-
		wf.createWall(mapFactory, e, assets, size, Vector3(-6.3f*lng, 0f, -7.3f*lng), -45f)
		wf.createWall(mapFactory, e, assets, size, Vector3(-8.6f*lng, 0f, -4*lng), +45f)
	}
	//______________________________________________________________________________________________
	private fun addShapesX(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory
		val columnSize = Vector3(15f, high2, 15f)
        for(y in 0..level) {
            /// U Shapes -------------
			val sizeX = Vector3(lng2, high2, thick2)
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(0f, y*high2, +.5f*lng2), 0f, WallFactory.Type.CIRCUITS)
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(0f, y*high2, -.5f*lng2), 0f, WallFactory.Type.CIRCUITS)
			val sizeZ = Vector3(thick2, high2, lng2)
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(+1*lng, y*high2, +2*lng))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(-1*lng, y*high2, +2*lng))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(+1*lng, y*high2, -2*lng))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(-1*lng, y*high2, -2*lng))

            /// S Shapes -------------
            //+x +z
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(+5*lng-thick, y*high2, +2*lng2))
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(+3*lng+thick, y*high2, +3*lng2))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(+2*lng2, y*high2, +5*lng))
            //+x -z
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(+5*lng-thick, y*high2, -2*lng2))
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(+3*lng+thick, y*high2, -3*lng2))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(+2*lng2, y*high2, -5*lng))
            //-x +z
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(-5*lng+thick, y*high2, +2*lng2))
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(-3*lng-thick, y*high2, +3*lng2))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(-2*lng2, y*high2, +5*lng))
            //-x -z
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(-5*lng+thick, y*high2, -2*lng2))
			wf.createWall(mapFactory, e, assets, sizeX, Vector3(-3*lng-thick, y*high2, -3*lng2))
			wf.createWall(mapFactory, e, assets, sizeZ, Vector3(-2*lng2, y*high2, -5*lng))

            /// Columns
            if(y < 2) {
				ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(0f, y*high2, +3.1f*lng2))
				ColumnFactory.add(e, mapFactory, assets, columnSize, Vector3(0f, y*high2, -3.1f*lng2))
			}
        }
	}

	//______________________________________________________________________________________________
	private fun addSwitchesLevelX(level: Int, e: Engine, assets: Assets) {
		val z = +lng+thick+1
		when(level) {
			0 -> {
				SwitchFactory.create(e, Vector3(0f, 0f, -z), 180f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, 0f, +z), 0f, " B ", assets)
			}
			1,2 -> {
				SwitchFactory.create(e, Vector3(0f, level*high2, -z), 180f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, level*high2, +z), 0f, " B ", assets)
			}
			3 -> {
				SwitchFactory.create(e, Vector3(0f, 2*high2, -z), 180f, " A ", assets)
				SwitchFactory.create(e, Vector3(0f, 2*high2, +z), 0f, " B ", assets)
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
	private fun createFirstFloorAccess(level: Int) {
		val cx = RampFactory.LONG_GROUND
		val cz = RampFactory.LONG_GROUND
		if(level < 2) {
			for(n in -10..+10) {
				mapFactory.addFloorAccess(1, n*cx, -10*cz)
				mapFactory.addFloorAccess(1, n*cx, +10*cz)
				mapFactory.addFloorAccess(1, -10*cx, n*cz)
				mapFactory.addFloorAccess(1, +10*cx, n*cz)
			}
		}
		else {
			for(n in -5..+5) {
				mapFactory.addFloorAccess(1, -10 * cx, n * cz)
				mapFactory.addFloorAccess(1, +10 * cx, n * cz)
			}
		}
	}
	private fun createFirstFloorExtras(e: Engine, assets: Assets) {
		/// Extra Gates
		GateFactory.create(e, Vector3(+GateComponent.LONG + .2f, high2, 0f), 0f, " E ", assets).unlock()
		GateFactory.create(e, Vector3(-GateComponent.LONG - .2f, high2, 0f), 0f, " F ", assets).unlock()
		/// Extra Ramps
		rampFactory.create(mapFactory, e, Vector3(-2*lng2, high, +5f*lng2), angleX = +45f, angleZ = 90f)
		rampFactory.create(mapFactory, e, Vector3(+2*lng2, high, -5f*lng2), angleX = -45f, angleZ = 90f)
	}
	private fun createFirstFloorGround(level: Int, e: Engine) {
		val cx = RampFactory.LONG_GROUND
		val cz = RampFactory.LONG_GROUND
		for(z in -8..+8 step 2) {
			if(z == 0) {
				val type = RampFactory.Type.STEEL
				rampFactory.createGround(mapFactory, e, Vector3(+2*cx, high2, 0f), type)//TODO: to mesh
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
	}
	private fun createFirstFloorWalls(e: Engine, assets: Assets) {
		val wf = WallFactory
		//-X +X
		var size = Vector2(6*lng, high2)
		wf.createGrille(mapFactory, e, assets, size, Vector3(-9f*lng, high2, +6f*lng), +90f, true)
		wf.createGrille(mapFactory, e, assets, size, Vector3(-9f*lng, high2, -6f*lng), +90f, true)
		wf.createGrille(mapFactory, e, assets, size, Vector3(+9f*lng, high2, +6f*lng), -90f, true)
		wf.createGrille(mapFactory, e, assets, size, Vector3(+9f*lng, high2, -6f*lng), -90f, true)
		//-Z +Z
		size = Vector2(12f*lng, high2)
		wf.createGrille(mapFactory, e, assets, size, Vector3(-3f*lng, high2, -9f*lng), 0f, true)
		wf.createGrille(mapFactory, e, assets, size, Vector3(+3f*lng, high2, +9f*lng), 180f, true)
		size = Vector2(4f*lng, high2)
		wf.createGrille(mapFactory, e, assets, size, Vector3(+7f*lng, high2, -9f*lng), 0f, true)
		wf.createGrille(mapFactory, e, assets, size, Vector3(-7f*lng, high2, +9f*lng), 180f, true)
	}
	//______________________________________________________________________________________________
	private fun createFirstFloor(level: Int, e: Engine, assets: Assets) {
		createFirstFloorAccess(level)
		createFirstFloorExtras(e, assets)
		createFirstFloorGround(level, e)
		if(level > 1)
			createFirstFloorWalls(e, assets)
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