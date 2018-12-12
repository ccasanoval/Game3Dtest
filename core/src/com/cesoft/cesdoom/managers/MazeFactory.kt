package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3

object MazeFactory
{
	//______________________________________________________________________________________________
	fun create(engine: Engine)
	{
		createSector(engine, 0f)
		createSector(engine, +RampFactory.LONG+5.5f*WallFactory.LONG-3)
		createSector(engine, -RampFactory.LONG-5.5f*WallFactory.LONG+3)
		createSector(engine, +RampFactory.LONG+11.5f*WallFactory.LONG-3)
		createSector(engine, -RampFactory.LONG-11.5f*WallFactory.LONG+3)
	}
	//______________________________________________________________________________________________
	fun createSector(engine: Engine, x: Float)
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

		//System.err.println("---------------GameWorld:init:5-----------------------")

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