package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.map.MapGraph
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object MazeFactory
{

	// TODO: Steering MapPathFinder (must reset origin of MapPathFinder...) Constructor?
	// Wall : 00 -> z = 2*WallFactory.LONG
	// Wall : 90 -> x = 2*WallFactory.LONG
	// Wall : 45 -> x = 2*WallFactory.LONG * sin(45)  &  z = 2*WallFactory.LONG * sin(45)
	/*
	val height = (RampFactory.LONG+11.5*WallFactory.LONG).toInt()
	val width = (RampFactory.LONG+11.5*WallFactory.LONG).toInt()
	val MapPathFinder = ByteArray(width*height)
	*/
	//480x1 == 240x2 == 160x3 == 120x4
	private const val long = WallFactory.LONG
	private const val long2 = 2*WallFactory.LONG
	private const val mapWidth = 3f*(5f*long2)
	private const val mapHeight = 3f*(5f*long2)
	private const val scale = 2
	val mapFactory = MapGraphFactory(mapWidth, mapHeight, scale)


	//______________________________________________________________________________________________
	fun create(assets: Assets, engine: Engine)
	{
		WallFactory.texture = assets.getWallMetal1()
		RampFactory.init(assets.getWallMetal2(), assets.getWallMetal3())
//		createSector(engine, 0f)
//		createSector(engine, +RampFactory.LONG+5.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-5.5f*WallFactory.LONG+3)
//		createSector(engine, +RampFactory.LONG+11.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-11.5f*WallFactory.LONG+3)

		createTest(engine)

		mapFactory.compile()

		mapFactory.print()
		Log.e("AAA", "")
		Log.e("AAA", "")
		mapFactory.print2()


		Log.e("Maze", "--------------------------------------")
		Log.e("Maze", "-------------------------------------- (0,-250)")
		Log.e("Maze", "--------------------------------------")
		var path = mapFactory.map.findPath(Vector2(0f, -250f), Vector2(0f, 0f))
		for(step in path) {
			Log.e("Maze", step.toString())
		}
		/*Log.e("Maze", "--------------------------------------")
		Log.e("Maze", "-------------------------------------- ${path[9]}")
		Log.e("Maze", "--------------------------------------")
		path = mapFactory.map.findPath(path[9], Vector2(0f, 0f))
		for(step in path) {
			Log.e("Maze", step.toString())
		}*/
	}
	private fun createTest(engine: Engine) {

//		engine.addEntity(WallFactory.create(mapFactory, Vector3(0f, 0f, 0f), 90f))

//		engine.addEntity(WallFactory.create(mapFactory, Vector3(+long, 0f, 0f), 90f))
//		engine.addEntity(WallFactory.create(mapFactory, Vector3(0f, 0f, +long), 00f))
//		engine.addEntity(WallFactory.create(mapFactory, Vector3(-long, 0f, 0f), 90f))
//		engine.addEntity(WallFactory.create(mapFactory, Vector3(0f, 0f, -long), 00f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-long, 0f, -4*long2), 90f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, -2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, -2*long2), 90f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+2*long, 0f, +4*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-2*long, 0f, +4*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+2*long, 0f, -4*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-2*long, 0f, -4*long), 00f))

/*
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+5*long, 0f, 0f), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-5*long, 0f, 0f), 90f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long2, 0f, -3*long), 00f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+5*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-5*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+5*long, 0f, -2*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-5*long, 0f, -2*long2), 90f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, -3*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, -3*long2), 90f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(+2*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-2*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+2*long2, 0f, -5*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-2*long2, 0f, -5*long), 00f))

		engine.addEntity(WallFactory.create(mapFactory, Vector3(-1*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+1*long, 0f, -3*long2), 90f))


		//---
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+1*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+5*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+7*long, 0f, +4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-1*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-5*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-7*long, 0f, +4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+1*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+3*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+5*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+7*long, 0f, -4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-1*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-3*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-5*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-7*long, 0f, -4*long2), 90f))

		//---
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, +5*long), 00f))
		//engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, +7*long), 00f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, +7*long), 00f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, -5*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(+4*long2, 0f, -7*long), 00f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, -5*long), 00f))
		//engine.addEntity(WallFactory.create(mapFactory, Vector3(-4*long2, 0f, -7*long), 00f))
*/

		//createSectorTest(engine, +1, +1)
		//createSectorTest(engine, -1, +1)
		//createSectorTest(engine, +1, -1)
		//createSectorTest(engine, -1, -1)
	}
	/*private fun createSectorTest(engine: Engine, x: Int, z: Int) {
		val cc = WallFactory.LONG / 2
		val long = WallFactory.LONG
		val long2 = 2*WallFactory.LONG
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+long), 0f, z*(cc)), 90f))
		/*engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+long), 0f, z*(cc)), 90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc), 0f, z*(cc+long)), 0f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+long2), 0f, z*(cc+long)), 0f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+2*long2), 0f, z*(0*cc)), 0f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+2*long2), 0f, z*(2*long)), 0f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(cc+2*long2), 0f, z*(2*long+cc)), 0f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x*(0*cc), 0f, z*(6*cc)), 90f))*/
	}
	//______________________________________________________________________________________________
	private fun createSector(engine: Engine, x: Float)
	{
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), +45f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), -45f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), -45f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), +45f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, -5f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, -5f*WallFactory.LONG)))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, -WallFactory.LONG/2), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, +WallFactory.LONG/2), +90f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+0f, 				   0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+2f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+4f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		//
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+0f, 				   0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+2f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(mapFactory, Vector3(x+4f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))

		//Log.e(tag, "---------------GameWorld:init:5-----------------------")

		/// RAMPAS
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +7*RampFactory.HIGH), angleY=90f, angleZ=90f))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=-45f))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=+45f))
		//
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -7*RampFactory.HIGH), angleY=90f, angleZ=90f))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=-45f))
		engine.addEntity(RampFactory.create(mapFactory, Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=+45f))
	}*/
}