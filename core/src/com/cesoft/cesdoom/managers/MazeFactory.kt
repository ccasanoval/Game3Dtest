package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.EnemyComponent
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.entities.Ammo
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
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

	private const val GATE_A = " A "
	private const val GATE_B = " B "
	private const val GATE_C = " C "
	private const val GATE_D = " D "
	private const val GATE_E = " E "
	private const val GATE_F = " F "

	//Reduce memory allocation
	private val pos = Vector3(0f,0f,0f)
	private val size = Vector3(0f,0f,0f)
	private val sizeX = Vector3(0f,0f,0f)
	private val sizeZ = Vector3(0f,0f,0f)
	private val size2D = Vector2(0f,0f)

	fun findPath(floorEnemy: Int, pos: Vector2, target: Vector2, enemy: EnemyComponent): ArrayList<Vector2> {
		val map = MazeFactory.mapFactory.map[floorEnemy]
		return map.findPath(pos, target, enemy)
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

//TODO: Reduce number of allocated memory, cos when released, gc takes too much time
		//-verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDetails
		WallFactory.endMaterials()
		rampFactory.endMaterials()
		System.gc()


		//----- TEST
//		mapFactory.print()
//		com.cesoft.cesdoom.util.Log.e(tag, "")
//		com.cesoft.cesdoom.util.Log.e(tag, "")

//		mapFactory.printMap()

//		Log.e(tag, "----------------------------------------------------------------------------")
//		Log.e(tag, " LEVEL ACCESSES:LEVEL(0) "+ mapFactory.map[0].floorAccess.size)
//        for(access in mapFactory.map[0].floorAccess) {
//			Log.e(tag, "access----------------------- $access")
//        }

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
	}

	//______________________________________________________________________________________________
	private fun createLevelX(level: Int, e: Engine, assets: Assets) {

		/// GANG WAYS ------------------
		addGangWays(level, e, assets)
		/// INNER SHAPES -------------
		addShapesX(level, e, assets)

		System.gc()//-------------------------------------------------------------------------------

		/// INNER WALL
		addInnerWall(level, e, assets)
		/// INNER GATES
		addInnerGates(level, e, assets)
		/// INNER RAMPS
		addInnerRamps(level, e, assets)

		System.gc()//-------------------------------------------------------------------------------

		/// OUTER WALL ------------------
		addOuterWall(e, assets)
		/// OUTER GATES ------------------
		addOuterGates(level, e, assets)

		System.gc()//-------------------------------------------------------------------------------

		// AMMO ------------------
		addAmmoLevelX(level, e, assets)
		// HEALTH  ------------------
		addHealthLevelX(level, e, assets)

		System.gc()//-------------------------------------------------------------------------------
	}

	//______________________________________________________________________________________________
	private fun addGangWays(level: Int, e: Engine, assets: Assets) {
		if(level != 0)return
		/// WANG WAYS
		size2D.set(7*lng2, high2)
		pos.set(0f, high2, -lng2)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, -90f, RampFactory.Type.GRILLE, true)

		pos.set(0f, high2, -4*lng2)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, -90f, RampFactory.Type.GRILLE, true)
		pos.set(0f, high2, +4*lng2)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, -90f, RampFactory.Type.GRILLE, true)

		size2D.set(high2, 9f*lng2)
		pos.set(+4*lng2, high2, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, -90f, RampFactory.Type.GRILLE, true)
		pos.set(-4*lng2, high2, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, -90f, RampFactory.Type.GRILLE, true)

		// ACCESSES
		for(x in -3..+3) {
			mapFactory.addFloorAccess(1, x*lng2, -9f*lng-2*mapFactory.scale)
			mapFactory.addFloorAccess(1, x*lng2, -7f*lng+2*mapFactory.scale)
			mapFactory.addFloorAccess(1, x*lng2, -3f*lng-2*mapFactory.scale)
			//
			if(x != -3 && x != +3)
				mapFactory.addFloorAccess(1, x*lng2, -1*lng+2*mapFactory.scale)
			mapFactory.addFloorAccess(1, x*lng2, +7f*lng-2*mapFactory.scale)
			mapFactory.addFloorAccess(1, x*lng2, +9*lng+2*mapFactory.scale)
		}
		for(z in -9..+9) {
			if(z in 2 until 7 || z in -4 downTo -6) {
				mapFactory.addFloorAccess(1, -3.3f*lng2, z*lng)
				mapFactory.addFloorAccess(1, +3.3f*lng2, z*lng)
			}
			mapFactory.addFloorAccess(1, -4.75f*lng2+mapFactory.scale, z*lng)
			mapFactory.addFloorAccess(1, +4.7f*lng2, z*lng)
		}
	}
	//______________________________________________________________________________________________
	private fun addOuterWall(e: Engine, assets: Assets) {
		val wf = WallFactory

		/// GRILLE
		size2D.set(13*lng2, high2)
		pos.set(+6.7f*lng2, 0f, 0f)
		wf.createGrille(mapFactory, e, assets, size2D, pos, -90f)
		pos.set(-6.7f*lng2, 0f, 0f)
		wf.createGrille(mapFactory, e, assets, size2D, pos, +90f)
		//
		size2D.set(5.85f*lng2, high2)
		pos.set(-3.6f*lng2, 0f, -6.5f*lng2)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 0f)
		pos.set(+3.6f*lng2, 0f, -6.5f*lng2)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 0f)
		pos.set(-3.6f*lng2, 0f, +6.5f*lng2)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 180f)
		pos.set(+3.6f*lng2, 0f, +6.5f*lng2)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 180f)
	}
	//______________________________________________________________________________________________
	private fun addOuterGates(level: Int, e: Engine, assets: Assets) {
		size.set(10f, high2, 30f)
		// EXIT 0
		pos.set(0f, 0f, +6.5f*lng2)
		GateFactory.create(e, pos, 90f, GATE_A, assets)
		pos.set(0f, 0f, +7.5f*lng2 + (2*YouWinFactory.SIZE + GateComponent.THICK))
		YouWinFactory.create(e, pos, 180f, assets)
		pos.set(-GateComponent.LONG-size.x/2, 0f, +6.6f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		pos.set(+GateComponent.LONG+size.x/2, 0f, +6.6f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		// EXIT 1
		pos.set(0f, 0f, -6.5f*lng2)
		GateFactory.create(e, pos, 90f, GATE_B, assets)
		pos.set(0f, 0f, -7.5f*lng2 - (2*YouWinFactory.SIZE + GateComponent.THICK))
		YouWinFactory.create(e, pos, 0f, assets)
		pos.set(-GateComponent.LONG-size.x/2, 0f, -6.6f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		pos.set(+GateComponent.LONG+size.x/2, 0f, -6.6f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		// 4 corner columns
		size.set(15f, high2, 15f)
		pos.set(-6.6f*lng2, 0f, -6.4f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		pos.set(+6.6f*lng2, 0f, -6.4f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		pos.set(-6.6f*lng2, 0f, +6.4f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		pos.set(+6.6f*lng2, 0f, +6.4f*lng2)
		ColumnFactory.add(e, mapFactory, assets, size, pos)
		//
		addSwitchesLevelX(level, e, assets)
	}
	//______________________________________________________________________________________________
	private fun addInnerRamps(level: Int, e: Engine, assets: Assets) {
		pos.set(+3f * lng2, high, 0f)
		rampFactory.create(mapFactory, e, pos, angleX = 90f, angleY = -45f)
		pos.set(-3f * lng2, high, 0f)
		rampFactory.create(mapFactory, e, pos, angleX = 90f, angleY = +45f)
	}
	//______________________________________________________________________________________________
	private fun addInnerGates(level: Int, e: Engine, assets: Assets) {
		pos.set(+GateComponent.LONG + .2f, 0f, 0f)
		GateFactory.create(e, pos, 0f, GATE_C, assets).unlock()
		pos.set(-GateComponent.LONG - .2f, 0f, 0f)
		GateFactory.create(e, pos, 180f, GATE_D, assets).unlock()
	}
	//______________________________________________________________________________________________
	private fun addInnerWall(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory
		if(level >= 4)return

		// Horizontal
		size.set(12*lng, high2, thick2)
		pos.set(0f, 0f, +4*lng2)
		wf.createWall(mapFactory, e, assets, size, pos)
		size.set(11*lng, high2, thick2)
		pos.set(0f, 0f, -4*lng2)
		wf.createWall(mapFactory, e, assets, size, pos)

		// Vertical
		size.set(thick2, high2, 10*lng)
		pos.set(+8*lng, 0f, 1.5f*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-8*lng, 0f, 1.5f*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		//x+ z-
		size.set(lng2, high2, thick2)
		pos.set(+6.3f*lng, 0f, -7.3f*lng)
		wf.createWall(mapFactory, e, assets, size, pos, +45f)
		pos.set(+8.6f*lng, 0f, -4*lng)
		wf.createWall(mapFactory, e, assets, size, pos, -45f)
		//x- z-
		pos.set(-6.3f*lng, 0f, -7.3f*lng)
		wf.createWall(mapFactory, e, assets, size, pos, -45f)
		pos.set(-8.6f*lng, 0f, -4*lng)
		wf.createWall(mapFactory, e, assets, size, pos, +45f)
	}
	//______________________________________________________________________________________________
	private fun addShapesX(level: Int, e: Engine, assets: Assets) {
		val wf = WallFactory
		size.set(15f, high2, 15f)
		sizeX.set(lng2, high2, thick2)
		sizeZ.set(thick2, high2, lng2)
		val ini = if(level >= 4) 1 else 0
		val end = if(level >= 4) 3 else level
        for(y in ini..end) {
            /// U Shapes -------------
			pos.set(0f, y*high2, +.5f*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos, 0f, WallFactory.Type.CIRCUITS)
			pos.set(0f, y*high2, -.5f*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos, 0f, WallFactory.Type.CIRCUITS)
			pos.set(+1*lng, y*high2, +2*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
			pos.set(-1*lng, y*high2, +2*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
			pos.set(+1*lng, y*high2, -2*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
			pos.set(-1*lng, y*high2, -2*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)

            /// S Shapes -------------
            //+x +z
			pos.set(+5*lng-thick, y*high2, +2*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(+3*lng+thick, y*high2, +3*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(+2*lng2, y*high2, +5*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
            //+x -z
			pos.set(+5*lng-thick, y*high2, -2*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(+3*lng+thick, y*high2, -3*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(+2*lng2, y*high2, -5*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
            //-x +z
			pos.set(-5*lng+thick, y*high2, +2*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(-3*lng-thick, y*high2, +3*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(-2*lng2, y*high2, +5*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)
            //-x -z
			pos.set(-5*lng+thick, y*high2, -2*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(-3*lng-thick, y*high2, -3*lng2)
			wf.createWall(mapFactory, e, assets, sizeX, pos)
			pos.set(-2*lng2, y*high2, -5*lng)
			wf.createWall(mapFactory, e, assets, sizeZ, pos)

            /// Columns
            if(y < 2) {
				pos.set(0f, y*high2, +3.1f*lng2)
				ColumnFactory.add(e, mapFactory, assets, size, pos)
				pos.set(0f, y*high2, -3.1f*lng2)
				ColumnFactory.add(e, mapFactory, assets, size, pos)
			}
        }
	}

	//______________________________________________________________________________________________
	private fun addSwitchesLevelX(level: Int, e: Engine, assets: Assets) {
		val z = +lng+thick+1
		val posA: Vector3
		val posB: Vector3
		when(level) {
			0 -> {
				posA = Vector3(0f, 0f, -z)
				posB = Vector3(0f, 0f, +z)
			}
			1,2 -> {
				posA = Vector3(0f, level*high2, -z)
				posB = Vector3(0f, level*high2, +z)
			}
			else -> {
				posA = Vector3(0f, 2*high2, -z)
				posB = Vector3(0f, 2*high2, +z)
			}
		}
		SwitchFactory.create(e, posA, 180f, GATE_A, assets)
		SwitchFactory.create(e, posB, 0f, GATE_B, assets)
	}

	//______________________________________________________________________________________________
	private fun addAmmoLevelX(level: Int, e: Engine, assets: Assets) {
		val ammoModel = assets.getAmmo()
		when(level) {
			0 -> {
				Ammo(pos.set(+6*lng2, 0f, 0f), ammoModel, e)
				Ammo(pos.set(-6*lng2, 0f, 0f), ammoModel, e)
			}
			1 -> {
				Ammo(pos.set(-6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(pos.set(+6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(pos.set(+3*lng2, high2, +3*lng2), ammoModel, e)
				Ammo(pos.set(-3*lng2, high2, -3*lng2), ammoModel, e)
			}
			2 -> {
				Ammo(pos.set(-6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(pos.set(+6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(pos.set(-6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(pos.set(-3*lng2, high2, -3*lng2), ammoModel, e)
			}
			3 -> {
				Ammo(pos.set(+6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(pos.set(-6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(pos.set(+6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(pos.set(-6*lng2, 0f, -3*lng2), ammoModel, e)
				Ammo(pos.set(+3*lng2, high2, +3*lng2), ammoModel, e)
				Ammo(pos.set(-3*lng2, high2, -3*lng2), ammoModel, e)
			}
			4 -> {
				Ammo(pos.set(+6*lng2, 0f, +3*lng2), ammoModel, e)
				Ammo(pos.set(-6*lng2, 0f, -3*lng2), ammoModel, e)
			}
		}
	}

	//______________________________________________________________________________________________
	private fun addHealthLevelX(level: Int, e: Engine, assets: Assets) {
		val healthModel = assets.getHealth()
		when(level) {
			0 -> {
				Health(pos.set(+4*lng2, high2, 0f), healthModel, e)
				Health(pos.set(-4*lng2, high2, 0f), healthModel, e)
			}
			1 -> {
				Health(pos.set(-3.5f*lng2, 0f, 0f), healthModel, e)
				Health(pos.set(+4*lng2, high2, -3*lng2), healthModel, e)
			}
			2 -> {
				Health(pos.set(+3.5f*lng2, 0f, 0f), healthModel, e)
				Health(pos.set(-4*lng2, high2, -3*lng2), healthModel, e)
			}
			3 -> {
				Health(pos.set(+3.5f*lng2, 0f, 0f), healthModel, e)
				Health(pos.set(-3.5f*lng2, 0f, 0f), healthModel, e)
			}
		}
	}


	//______________________________________________________________________________________________
	private fun createFirstFloorAccess(level: Int) {
		val cx = RampFactory.LONG_GROUND
		val cz = RampFactory.LONG_GROUND
		when(level) {
			1 -> {
				for(n in -9..+9) {
					if(n != +4)
						mapFactory.addFloorAccess(1, n*cx, -9.25f*cz)
					if(n != -4)
						mapFactory.addFloorAccess(1, n*cx, +9.5f*cz)
					mapFactory.addFloorAccess(1, -9.5f*cx, n*cz)
					mapFactory.addFloorAccess(1, +9.5f*cx, n*cz)
				}
			}
			2 -> {
				for(n in -5..+5) {
					mapFactory.addFloorAccess(1, -9.5f*cx, n*cz)
					mapFactory.addFloorAccess(1, +9.5f*cx, n*cz)
				}
			}
			else -> {
				for(n in -5..+5) {
					if(n == 0) continue
					mapFactory.addFloorAccess(1, -9.5f*cx, n*cz)
					mapFactory.addFloorAccess(1, +9.5f*cx, n*cz)
				}
			}
		}
	}
	private fun createFirstFloorGround(level: Int, e: Engine, assets: Assets) {
		val groundTypeA: RampFactory.Type
		val groundTypeB: RampFactory.Type
		val groundTypeC: RampFactory.Type
		when(level) {
			1 -> {
				groundTypeA = RampFactory.Type.GRILLE
				groundTypeB = RampFactory.Type.STEEL
				groundTypeC = RampFactory.Type.STEEL
			}
			2 -> {
				groundTypeA = RampFactory.Type.STEEL
				groundTypeB = RampFactory.Type.GRILLE
				groundTypeC = RampFactory.Type.GRILLE
			}
			else -> {
				groundTypeA = RampFactory.Type.STEEL
				groundTypeB = RampFactory.Type.GRILLE
				groundTypeC = RampFactory.Type.STEEL
			}
		}
		// A
		val angle = -90f
		size2D.set(9*lng2, 4*lng2)
		pos.set(0f, high2+1, -2.5f*lng2)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeA, true)
		pos.set(0f, high2+1, +2.5f*lng2)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeA, true)
		// B
		size2D.set(lng2, lng2)
		pos.set(-4*lng2, high2+1, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeB, true)
		pos.set(+4*lng2, high2+1, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeB, true)
		// C
		size2D.set(1.5f*lng2, lng2)
		pos.set(-1.25f*lng2, high2+1, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeC, true)
		pos.set(+1.25f*lng2, high2+1, 0f)
		rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, groundTypeC, true)
	}
	private fun createFirstFloorExtras(e: Engine, assets: Assets) {
		/// Extra Gates
		pos.set(+GateComponent.LONG + .2f, high2, 0f)
		GateFactory.create(e, pos, 0f, GATE_E, assets).unlock()
		pos.set(-GateComponent.LONG - .2f, high2, 0f)
		GateFactory.create(e, pos, 0f, GATE_F, assets).unlock()
		/// Extra Ramps
		pos.set(-2*lng2, high, +5f*lng2)
		rampFactory.create(mapFactory, e, pos, angleX = +45f, angleZ = 90f)
		pos.set(+2*lng2, high, -5f*lng2)
		rampFactory.create(mapFactory, e, pos, angleX = -45f, angleZ = 90f)
	}
	private fun createFirstFloorWalls(level: Int, e: Engine, assets: Assets) {
		if(level <= 1)return
		val wf = WallFactory
		//-X +X
		size2D.set(6*lng, high2)
		pos.set(-9f*lng, high2, +6f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, +90f, true)
		pos.set(-9f*lng, high2, -6f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, +90f, true)
		pos.set(+9f*lng, high2, +6f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, -90f, true)
        pos.set(+9f*lng, high2, -6f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, -90f, true)
		//-Z +Z
		size2D.set(12f*lng, high2)
		pos.set(-3f*lng, high2, -9f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 0f, true)
		pos.set(+3f*lng, high2, +9f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 180f, true)
		size2D.set(4f*lng, high2)
		pos.set(+7f*lng, high2, -9f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 0f, true)
		pos.set(-7f*lng, high2, +9f*lng)
		wf.createGrille(mapFactory, e, assets, size2D, pos, 180f, true)

		if(level >= 3) {
			size2D.set(3f*lng, high2)
			pos.set(-9f*lng, high2, 0f)
			wf.createGrille(mapFactory, e, assets, size2D, pos, +90f, true)
			pos.set(+9f*lng, high2, 0f)
			wf.createGrille(mapFactory, e, assets, size2D, pos, +90f, true)
		}
	}
	//______________________________________________________________________________________________
	private fun createFirstFloor(level: Int, e: Engine, assets: Assets) {
		createFirstFloorGround(level, e, assets)
		createFirstFloorAccess(level)
		createFirstFloorExtras(e, assets)
		createFirstFloorWalls(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createSecondFloor(level: Int, e: Engine, assets: Assets) {
		val angle = -90f
		val len = 2*RampFactory.LONG_GROUND
		size2D.set(len, len)
		for(z in -1..+1) {
			for (x in -1..+1) {
				if (x == 0 && z == 0) continue
				pos.set(x*len, 2*high2, z*len)
				rampFactory.createGround2(mapFactory, e, assets, size2D, pos, angle, RampFactory.Type.GRILLE, true)
			}
		}
		/// Extra Ramps
		pos.set(0f, high2+high, +2f*lng2)
		rampFactory.create(mapFactory, e, pos, angleX = +45f, angleZ = 90f, type = RampFactory.Type.GRILLE)
		pos.set(0f, high2+high, -2f*lng2)
		rampFactory.create(mapFactory, e, pos, angleX = -45f, angleZ = 90f, type = RampFactory.Type.GRILLE)
	}


	//______________________________________________________________________________________________
	const val MAX_LEVEL = 4
	private fun createLevel(engine: Engine, assets: Assets) {
		mapFactory.clear()
		mapFactory.collideAll(1)
		when(PlayerComponent.currentLevel) {
			0 -> createLevel0(engine, assets)
			1 -> createLevel1(engine, assets)
			2 -> createLevel2(engine, assets)
			3 -> createLevel3(engine, assets)
			4 -> createLevel4(engine, assets)
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
		createFirstFloor(level, e, assets)
		createLevelX(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel2(e: Engine, assets: Assets) {
		val level = 2
		createFirstFloor(level, e, assets)
		createSecondFloor(level, e, assets)
		createLevelX(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel3(e: Engine, assets: Assets) {
		val level = 3//TODO: something new?
		createFirstFloor(level, e, assets)
		createSecondFloor(level, e, assets)
		createLevelX(level, e, assets)
	}

	//______________________________________________________________________________________________
	private fun createLevel4(e: Engine, assets: Assets) {
		createLevelX(4, e, assets)
		createFirstFloor(3, e, assets)
		createSecondFloor(3, e, assets)
		createFloorZeroMaze(e, assets)
	}

	private fun createFloorZeroMaze(e: Engine, assets: Assets) {
		val wf = WallFactory

		//X
		size.set(lng2, high2, thick2)
		pos.set(0f, 0f, -lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(0f, 0f, +lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(3*lng2, high2, thick2)
		pos.set(+2*lng, 0f, -3*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-2*lng, 0f, +3*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(2*lng2+thick2, high2, thick2)
		pos.set(+5*lng, 0f, +3*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-5*lng, 0f, -3*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(4*lng2, high2, thick2)
		pos.set(+5*lng, 0f, +5*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-5*lng, 0f, -5*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(2*lng2, high2, thick2)
		pos.set(+5*lng, 0f, +1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-5*lng, 0f, -1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(4*lng2, high2, thick2)
		pos.set(+5*lng, 0f, -5*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-5*lng, 0f, +5*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(4*lng2, high2, thick2)
		pos.set(+0*lng, 0f, -7*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-0*lng, 0f, +7*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(15f, high2, 15f)
		for(i in 6..9) {
			pos.set(-i*lng, 0f, -7*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
			pos.set(-i*lng, 0f, +7*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
			pos.set(+i*lng, 0f, -7*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
			pos.set(+i*lng, 0f, +7*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
		}
		for(i in -6..+6 step 2) {
			pos.set(+11*lng, 0f, i*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
			pos.set(-11*lng, 0f, i*lng)
			ColumnFactory.add(e, mapFactory, assets, size, pos)
		}

		//Z
		size.set(thick2, high2, 2*lng2)
		pos.set(+3*lng, 0f, +1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-3*lng, 0f, -1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(thick2, high2, lng2)
		pos.set(+1*lng, 0f, +4*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-1*lng, 0f, -4*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(thick2, high2, 3*lng2)
		pos.set(+7*lng+thick, 0f, +0*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-7*lng-thick, 0f, -0*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(thick2, high2, 4*lng2)
		pos.set(+9*lng, 0f, +1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-9*lng, 0f, -1*lng)
		wf.createWall(mapFactory, e, assets, size, pos)

		size.set(thick2, high2, lng2)
		pos.set(+5*lng, 0f, -2*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
		pos.set(-5*lng, 0f, +2*lng)
		wf.createWall(mapFactory, e, assets, size, pos)
	}

}