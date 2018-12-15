package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.UI.GunFireWidget
import com.cesoft.cesdoom.components.AnimationComponent
import com.cesoft.cesdoom.components.AnimationParams
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.components.ModelComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GunFactory
{
	private var modelStatus = mutableMapOf<GunComponent.TYPE, Boolean>()

	//______________________________________________________________________________________________
	private fun createModel(model: Model, type: GunComponent.TYPE): Model {
		val init = modelStatus[type]?:false
		if(init)return model
		modelStatus[type] = true
		when(type) {
			GunComponent.TYPE.CZ805 -> {
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.03f)
			}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun create(model: Model, type: GunComponent.TYPE): Entity {
		val entity = Entity()

		val gun = GunComponent(type)
		entity.add(gun)

		createModel(model, type)//TODO:If changing weapons, remember to call it just once
		when(type) {
			GunComponent.TYPE.CZ805 -> {
				val modelComponent = ModelComponent(model, Vector3(25f, -10f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
				GunFireWidget.setPosition(30f, -60f)
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
					GunComponent.ACTION.IDLE -> AnimationParams("cz|idle")
					GunComponent.ACTION.SHOOT -> AnimationParams("cz|shoot")
					GunComponent.ACTION.RELOAD -> AnimationParams("cz|reload")
					GunComponent.ACTION.DRAW -> AnimationParams("cz|draw")
				}

		}
	}

	fun playSound(assets: Assets) {
		val sound = assets.getSoundCZ805()
		if( ! sound.isPlaying)
			sound.play()
	}

}