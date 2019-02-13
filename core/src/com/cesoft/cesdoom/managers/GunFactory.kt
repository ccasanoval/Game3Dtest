package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.cesoft.cesdoom.ui.GunFireWidget
import com.cesoft.cesdoom.components.AnimationComponent
import com.cesoft.cesdoom.components.AnimationParams
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.entities.Gun

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GunFactory {
	private val tag: String = GunFactory::class.java.simpleName
	private val modelStatus = mutableMapOf<GunComponent.TYPE, Boolean>()

	fun dispose() {
		modelStatus.clear()
	}

	//______________________________________________________________________________________________
	fun create(model: Model, type: GunComponent.TYPE, fire: Image): Gun {
		val entity = Gun()
		val gun = GunComponent(type)
		entity.add(gun)
		createModel(entity, fire, model, type)
		return entity
	}
	//______________________________________________________________________________________________
	private fun createModel(entity: Gun, fire: Image, model: Model, type: GunComponent.TYPE): Model {
		when(type) {
			GunComponent.TYPE.CZ805 -> {
				if(modelStatus[type] != true) {
					modelStatus[type] = true
					for(i in 0 until model.nodes.size - 1)
						model.nodes[i].scale.scl(0.03f)
				}
				val modelComponent = ModelComponent(model, Vector3(25f, -9f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
				entity.init(GunFireWidget(fire, 30f, -60f))//.fire = GunFireWidget(fire, 30f, -60f)
			}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun animate(entity: Entity, action: GunComponent.ACTION) {
		val type = GunComponent.get(entity).type

		if(action == GunComponent.ACTION.SHOOT) {
			(entity as Gun).fire.draw()/// Muzzle Flash
		}

		val animParams = getAnimationParams(type, action)
		if(animParams.id.isEmpty())return

		if( ! animParams.id.isEmpty())
			AnimationComponent.get(entity).animate(animParams)
	}
	//______________________________________________________________________________________________
	private fun getAnimationParams(type: GunComponent.TYPE, action: GunComponent.ACTION) : AnimationParams {
		when(type) {
			GunComponent.TYPE.CZ805 ->
				return when(action) {
					GunComponent.ACTION.IDLE -> AnimationParams("cz|idle")
					GunComponent.ACTION.SHOOT -> AnimationParams("cz|shoot")
					GunComponent.ACTION.RELOAD -> AnimationParams("cz|reload")//TODO:Cambiar velocidad para adecuar a audio...
					GunComponent.ACTION.DRAW -> AnimationParams("cz|draw")
				}
		}
	}

}