package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.map.MapPathFinder

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
	private val mapWidth = 1000
	private val mapHeight = 1000
	private val mapData = BooleanArray(mapWidth * mapHeight)
	lateinit var map: MapPathFinder

	//______________________________________________________________________________________________
	fun create(assets: Assets, engine: Engine)
	{
		WallFactory.texture = assets.getWallMetal1()
		RampFactory.init(assets.getWallMetal2(), assets.getWallMetal3())
		createSector(engine, 0f)
		createSector(engine, +RampFactory.LONG+5.5f*WallFactory.LONG-3)
		createSector(engine, -RampFactory.LONG-5.5f*WallFactory.LONG+3)
		createSector(engine, +RampFactory.LONG+11.5f*WallFactory.LONG-3)
		createSector(engine, -RampFactory.LONG-11.5f*WallFactory.LONG+3)

		map = MapPathFinder(100, 100, mapData)
	}
	//______________________________________________________________________________________________
	private fun createSector(engine: Engine, x: Float)
	{
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG+1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG-1, 0f, +5*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG+1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG-1, 0f, +3*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x-2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), +45f))
		engine.addEntity(WallFactory.create(Vector3(x+2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), -45f))
		//
		engine.addEntity(WallFactory.create(Vector3(x-2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), -45f))
		engine.addEntity(WallFactory.create(Vector3(x+2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), +45f))
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG+1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG-1, 0f, -3f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG+1, 0f, -5f*WallFactory.LONG)))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG-1, 0f, -5f*WallFactory.LONG)))
		//
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, -WallFactory.LONG/2), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, +WallFactory.LONG/2), +90f))
		//
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+0f, 				   0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+2f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+4f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f))
		//
		engine.addEntity(WallFactory.create(Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+0f, 				   0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+2f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))
		engine.addEntity(WallFactory.create(Vector3(x+4f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f))

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