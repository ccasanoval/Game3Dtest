package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.map.MapPathFinder

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
	private const val mapWidth = 2*160+20
	private const val mapHeight = 2*160+20
	private const val scale = 3
	lateinit var map: MapPathFinder


	//______________________________________________________________________________________________
	fun create(assets: Assets, engine: Engine)
	{
		map = MapPathFinder(mapWidth, mapHeight, scale)


		//MapPathFinder3.test(true, 5, 'a', 69, 6969, 7070, 7070.70f, 7070.70)

		WallFactory.texture = assets.getWallMetal1()
		RampFactory.init(assets.getWallMetal2(), assets.getWallMetal3())
//		createSector(engine, 0f)
//		createSector(engine, +RampFactory.LONG+5.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-5.5f*WallFactory.LONG+3)
//		createSector(engine, +RampFactory.LONG+11.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-11.5f*WallFactory.LONG+3)

		createTest(engine)

		map.compile(2f*Enemy.RADIO)
	}
	private fun createTest(engine: Engine) {
		val long = WallFactory.LONG
		val long2 = 2*WallFactory.LONG

//		engine.addEntity(WallFactory.create(map, Vector3(+long, 0f, 0f), 90f))
//		engine.addEntity(WallFactory.create(map, Vector3(0f, 0f, +long), 00f))
//		engine.addEntity(WallFactory.create(map, Vector3(-long, 0f, 0f), 90f))
//		engine.addEntity(WallFactory.create(map, Vector3(0f, 0f, -long), 00f))

		engine.addEntity(WallFactory.create(map, Vector3(+long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-long, 0f, -4*long2), 90f))

		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, -2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, -2*long2), 90f))

		engine.addEntity(WallFactory.create(map, Vector3(+2*long, 0f, +4*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-2*long, 0f, +4*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+2*long, 0f, -4*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-2*long, 0f, -4*long), 00f))
/*
		engine.addEntity(WallFactory.create(map, Vector3(+5*long, 0f, 0f), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-5*long, 0f, 0f), 90f))

		engine.addEntity(WallFactory.create(map, Vector3(+3*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long2, 0f, -3*long), 00f))

		engine.addEntity(WallFactory.create(map, Vector3(+5*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-5*long, 0f, +2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+5*long, 0f, -2*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-5*long, 0f, -2*long2), 90f))

		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, -3*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, -3*long2), 90f))

		engine.addEntity(WallFactory.create(map, Vector3(+2*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-2*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+2*long2, 0f, -5*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-2*long2, 0f, -5*long), 00f))

		engine.addEntity(WallFactory.create(map, Vector3(-1*long, 0f, +3*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+1*long, 0f, -3*long2), 90f))


		//---
		engine.addEntity(WallFactory.create(map, Vector3(+1*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+5*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+7*long, 0f, +4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(-1*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-5*long, 0f, +4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-7*long, 0f, +4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(+1*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+3*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+5*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(+7*long, 0f, -4*long2), 90f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(-1*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-3*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-5*long, 0f, -4*long2), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(-7*long, 0f, -4*long2), 90f))

		//---
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, +5*long), 00f))
		//engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, +7*long), 00f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, +1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, +3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, +5*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, +7*long), 00f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, -5*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(+4*long2, 0f, -7*long), 00f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, -1*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, -3*long), 00f))
		engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, -5*long), 00f))
		//engine.addEntity(WallFactory.create(map, Vector3(-4*long2, 0f, -7*long), 00f))
*/

		//createSectorTest(engine, +1, +1)
		//createSectorTest(engine, -1, +1)
		//createSectorTest(engine, +1, -1)
		//createSectorTest(engine, -1, -1)
	}
	private fun createSectorTest(engine: Engine, x: Int, z: Int) {
		val cc = WallFactory.LONG / 2
		val long = WallFactory.LONG
		val long2 = 2*WallFactory.LONG
		//
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc+long), 0f, z*(cc)), 90f))
		/*engine.addEntity(WallFactory.create(map, Vector3(x*(cc+long), 0f, z*(cc)), 90f))
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc), 0f, z*(cc+long)), 0f))
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc+long2), 0f, z*(cc+long)), 0f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc+2*long2), 0f, z*(0*cc)), 0f))
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc+2*long2), 0f, z*(2*long)), 0f))
		engine.addEntity(WallFactory.create(map, Vector3(x*(cc+2*long2), 0f, z*(2*long+cc)), 0f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x*(0*cc), 0f, z*(6*cc)), 90f))*/
	}
	//______________________________________________________________________________________________
	private fun createSector(engine: Engine, x: Float)
	{
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG+1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG-1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG+1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG-1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x-2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), +45f))
		engine.addEntity(WallFactory.create(map, Vector3(x+2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), -45f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x-2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), -45f))
		engine.addEntity(WallFactory.create(map, Vector3(x+2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), +45f))
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG+1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG-1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG+1, 0f, -5f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG-1, 0f, -5f*WallFactory.LONG)))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, -WallFactory.LONG/2), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, +WallFactory.LONG/2), +90f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+0f, 				   0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+2f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+4f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		//
		engine.addEntity(WallFactory.create(map, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+0f, 				   0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+2f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(map, Vector3(x+4f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))

		//Log.e(tag, "---------------GameWorld:init:5-----------------------")

		/// RAMPAS
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +7*RampFactory.HIGH), angleY=90f, angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, +13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=-45f))
		engine.addEntity(RampFactory.create(Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=+45f))
		//
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -7*RampFactory.HIGH), angleY=90f, angleZ=90f))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x, 2f*WallFactory.HIGH, -13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		engine.addEntity(RampFactory.create(Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=-45f))
		engine.addEntity(RampFactory.create(Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=+45f))
	}
}