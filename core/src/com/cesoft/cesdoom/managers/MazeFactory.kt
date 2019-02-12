package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.SwitchComponent
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object MazeFactory {

	//480x1 == 240x2 == 160x3 == 120x4
	const val lng = WallFactory.LONG
	const val lng2 = 2*WallFactory.LONG
	const val mapWidth = 3f*(5f*lng2)
	const val mapHeight = 3f*(5f*lng2)
	const val scale = 5
	val mapFactory = MapGraphFactory(mapWidth, mapHeight, scale)


	//______________________________________________________________________________________________
	fun create(engine: Engine) {
		WallFactory.iniMaterials(CesDoom.instance.assets)

//		createSector(engine, 0f)
//		createSector(engine, +RampFactory.LONG+5.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-5.5f*WallFactory.LONG+3)
//		createSector(engine, +RampFactory.LONG+11.5f*WallFactory.LONG-3)
//		createSector(engine, -RampFactory.LONG-11.5f*WallFactory.LONG+3)

		createTest(engine)

		mapFactory.compile()


		//----- TEST
		/*
		mapFactory.print()
		Log.e("AAA", "")
		Log.e("AAA", "")
		mapFactory.print2()
		Log.e("Maze", "--------------------------------------")
		Log.e("Maze", "-------------------------------------- (0,-250)")
		Log.e("Maze", "--------------------------------------")
		val path = mapFactory.map.findPath(Vector2(0f, -250f), Vector2(0f, 0f))
		for(step in path) {
			Log.e("Maze", step.toString())
		}
		*/
		//----- TEST

	}
	private fun createTest(e: Engine) {

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
        for(z in -11..11 step 2) {
            wf.create(mapFactory, e, Vector3(+7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
            wf.create(mapFactory, e, Vector3(-7f * lng2, 0f, z * lng), 00f, WallFactory.Type.GRILLE)
        }
        for(x in -13..13 step 2) {
            if (x == 1) {
				// SALIDA 0
				var id = " A "
				SwitchFactory.create(e, Vector3(0f,0f, -WallFactory.LONG-SwitchComponent.SIZE/2), 90f, id)
				GateFactory.create(mapFactory, e, Vector3(x*lng, 0f, +6f * lng2), 90f, id)
				YouWinFactory.create(e, Vector3(x*lng, 0f, +6f * lng2 + (2*YouWinFactory.SIZE+GateComponent.THICK)))
				// SALIDA 1
				id = " B "
				SwitchFactory.create(e, Vector3(0f,0f, +WallFactory.LONG+SwitchComponent.SIZE/2), 90f, id)
                GateFactory.create(mapFactory, e, Vector3(x*lng, 0f, -6f * lng2), 90f, id)
				YouWinFactory.create(e, Vector3(x*lng, 0f, -6f * lng2 - (2*YouWinFactory.SIZE+GateComponent.THICK)))
                continue
            }
            wf.create(mapFactory, e, Vector3(x * lng, 0f, +6f * lng2), 90f, WallFactory.Type.GRILLE)
            wf.create(mapFactory, e, Vector3(x * lng, 0f, -6f * lng2), 90f, WallFactory.Type.GRILLE)
        }
		// Extra Outer Wall ------------------

		/// Extra Gates
		GateFactory.create(mapFactory, e, Vector3(+GateComponent.LONG+.2f, 0f, 0f), 0f, " C ").unlock()
		GateFactory.create(mapFactory, e, Vector3(-GateComponent.LONG-.2f, 0f, 0f), 0f, " D ").unlock()
		//YouWinFactory.create(Vector3(-2*GateComponent.LONG, 0f, 0f), e)


		// AMMO ------------------
		val ammoModel = CesDoom.instance.assets.getAmmo()
		AmmoFactory.create(Vector3(+6f * lng2, 0f, 0f), ammoModel, e)
		AmmoFactory.create(Vector3(-6f * lng2, 0f, 0f), ammoModel, e)

		// HEALTH  ------------------
		val healthModel = CesDoom.instance.assets.getHealth()
		HealthFactory.create(Vector3(+6f * lng2, 0f, +5f * lng2), healthModel, e)
		HealthFactory.create(Vector3(-6f * lng2, 0f, -5f * lng2), healthModel, e)

		// RAMPS ------------------
		val rampFactory = RampFactory(CesDoom.instance.assets)
		rampFactory.create(mapFactory, e, Vector3(+4*lng2, 2f*WallFactory.HIGH, 0f), angleY=90f, angleZ=90f, type=false)
		rampFactory.create(mapFactory, e, Vector3(-4*lng2, 2f*WallFactory.HIGH, 0f), angleY=90f, angleZ=90f, type=false)
		rampFactory.create(mapFactory, e, Vector3(+3*lng2, WallFactory.HIGH+2f, 0f), angleX=90f, angleY=-45f)
		rampFactory.create(mapFactory, e, Vector3(-3*lng2, WallFactory.HIGH+2f, 0f), angleX=90f, angleY=+45f)

		//createSectorTest(engine, +1, +1)
		//createSectorTest(engine, -1, +1)
		//createSectorTest(engine, +1, -1)
		//createSectorTest(engine, -1, -1)
	}




	/*private fun createSectorTest(engine: Engine, x: Int, z: Int) {
		val cc = WallFactory.LONG / 2
		val long = WallFactory.LONG
		val lng2 = 2*WallFactory.LONG
		//
		WallFactory.create(mapFactory, Vector3(x*(cc+long), 0f, z*(cc)), 90f, engine)
		/*WallFactory.create(mapFactory, Vector3(x*(cc+long), 0f, z*(cc)), 90f, engine)
		WallFactory.create(mapFactory, Vector3(x*(cc), 0f, z*(cc+long)), 0f))
		WallFactory.create(mapFactory, Vector3(x*(cc+lng2), 0f, z*(cc+long)), 0f))
		//
		WallFactory.create(mapFactory, Vector3(x*(cc+2*lng2), 0f, z*(0*cc)), 0f))
		WallFactory.create(mapFactory, Vector3(x*(cc+2*lng2), 0f, z*(2*long)), 0f))
		WallFactory.create(mapFactory, Vector3(x*(cc+2*lng2), 0f, z*(2*long+cc)), 0f))
		//
		WallFactory.create(mapFactory, Vector3(x*(0*cc), 0f, z*(6*cc)), 90f, engine)*/
	}
	//______________________________________________________________________________________________
	private fun createSector(engine: Engine, x: Float)
	{
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, +5*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, +5*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, +3*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, +3*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x-2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), +45f))
		WallFactory.create(mapFactory, Vector3(x+2.2f*RampFactory.LONG, 0f, +WallFactory.LONG+12), -45f))
		//
		WallFactory.create(mapFactory, Vector3(x-2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), -45f))
		WallFactory.create(mapFactory, Vector3(x+2.2f*RampFactory.LONG, 0f, -WallFactory.LONG-12), +45f))
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, -3f*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, -3f*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG+1, 0f, -5f*WallFactory.LONG)))
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG-1, 0f, -5f*WallFactory.LONG)))
		//
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, -WallFactory.LONG/2), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+2.45f*WallFactory.LONG, 0f, +WallFactory.LONG/2), +90f, engine)
		//
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, +6*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+0f, 				   0f, +7*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+2f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+4f*WallFactory.LONG, 0f, +7*WallFactory.LONG), +90f, engine)
		//
		WallFactory.create(mapFactory, Vector3(x-RampFactory.LONG-1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+RampFactory.LONG+1.0f*WallFactory.LONG, 0f, -6*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+0f, 				   0f, -7*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+2f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f, engine)
		WallFactory.create(mapFactory, Vector3(x+4f*WallFactory.LONG, 0f, -7*WallFactory.LONG), +90f, engine)

		//Log.e(tag, "---------------GameWorld:init:5-----------------------")

		/// RAMPAS
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +7*RampFactory.HIGH), angleY=90f, angleZ=90f, engine)
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, +13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=-45f))
		RampFactory.create(mapFactory, Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, +4.8f*RampFactory.LONG), angleX=90f, angleY=+45f))
		//
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -1*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -3*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -5*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -7*RampFactory.HIGH), angleY=90f, angleZ=90f, engine)
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -9*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -11*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x, 2f*WallFactory.HIGH, -13*RampFactory.HIGH), angleY=90f, angleZ=90f, type=false))
		RampFactory.create(mapFactory, Vector3(x-2*RampFactory.LONG+4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=-45f))
		RampFactory.create(mapFactory, Vector3(x+2*RampFactory.LONG-4.5f, WallFactory.HIGH+1, -4.6f*RampFactory.LONG), angleX=90f, angleY=+45f))
	}*/
}