package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.UI.GunFireWidget
import com.cesoft.cesgame.components.AnimationComponent
import com.cesoft.cesgame.components.AnimationParams
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GunFactory
{
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private var models = mutableMapOf<GunComponent.TYPE, Model>()
	private var files = mutableMapOf<GunComponent.TYPE, FileHandle>()

	init {
		files[GunComponent.TYPE.CZ805] = Gdx.files.getFileHandle("weapons/cz805/a.g3db", Files.FileType.Internal)
		files[GunComponent.TYPE.AK47] = Gdx.files.getFileHandle("weapons/ak47/a.g3db", Files.FileType.Internal)
	}

	//______________________________________________________________________________________________
	fun dispose()
	{
		for((_, model) in models)
			model.dispose()
		models = mutableMapOf()
	}

	//______________________________________________________________________________________________
	fun createModel(type: GunComponent.TYPE): Model {
		val model: Model = modelLoader.loadModel(files[type])
		when(type) {
			GunComponent.TYPE.CZ805 -> {
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.03f)
			}
			GunComponent.TYPE.AK47 -> {}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun create(type: GunComponent.TYPE): Entity {
		val entity = Entity()

		val gun = GunComponent(type)
		entity.add(gun)

		if(models[type] == null)
			models[type] = createModel(type)
		when(type) {
			GunComponent.TYPE.CZ805 -> {
				val modelComponent = ModelComponent(models[type]!!, Vector3(25f, -10f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
				GunFireWidget.setPosition(30f, -60f)

			}
			GunComponent.TYPE.AK47 -> {
				val modelComponent = ModelComponent(models[type]!!, Vector3(25f, 0f, -5f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 100f)
				//modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
				GunFireWidget.setPosition(20f, -100f)
			}
		}

		return entity
	}

	//______________________________________________________________________________________________
	fun animate(entity: Entity, action: GunComponent.ACTION) {
		val type = entity.getComponent(GunComponent::class.java).type

		/// Muzzle Flash
		if(action == GunComponent.ACTION.SHOOT)
		{
			GunFireWidget.draw()
		}

		val animParams = getAnimationParams(type, action)
		if(animParams.id.isEmpty())return

		if( ! animParams.id.isEmpty())
		entity.getComponent(AnimationComponent::class.java).animate(animParams)
	}
	//______________________________________________________________________________________________
	private fun getAnimationParams(type: GunComponent.TYPE, action: GunComponent.ACTION) : AnimationParams {
		when(type) {
			GunComponent.TYPE.CZ805 ->
				return when(action) {
						//var id: String, var loop: Int = 1, var speed: Float = 1f, var duration: Float = 0f, var offset: Float = -1f)
					GunComponent.ACTION.IDLE -> AnimationParams("cz|idle")
					GunComponent.ACTION.SHOOT -> AnimationParams("cz|shoot")
					GunComponent.ACTION.RELOAD -> AnimationParams("cz|reload")
					GunComponent.ACTION.DRAW -> AnimationParams("cz|draw")
				}
			GunComponent.TYPE.AK47 ->
				return when(action) {
					GunComponent.ACTION.IDLE -> AnimationParams("")
					GunComponent.ACTION.SHOOT -> AnimationParams("")
					GunComponent.ACTION.RELOAD -> AnimationParams("Take 001")
					GunComponent.ACTION.DRAW -> AnimationParams("")
				}
		}
	}

}