package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.components.AnimationComponent
import com.cesoft.cesgame.components.GunComponent
import com.cesoft.cesgame.components.ModelComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GunFactory
{
	//______________________________________________________________________________________________
	fun create(type: GunComponent.TYPE) : Entity
	{
		val entity = Entity()

		val gun = GunComponent(type)
		entity.add(gun)

		when(type) {
			GunComponent.TYPE.CZ805 -> {
				val modelLoader = G3dModelLoader(UBJsonReader())
				val model = modelLoader.loadModel(Gdx.files.getFileHandle("data/cz805/a.g3db", Files.FileType.Internal))
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.03f)
				val modelComponent = ModelComponent(model, Vector3(25f, -10f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
			}
		}

		return entity
	}

	//______________________________________________________________________________________________
	fun animate(entity: Entity, action: GunComponent.ACTION, loops: Int = 1, speed: Int = 3)
	{
		val type = entity.getComponent(GunComponent::class.java).type
		val anim = getAnimation(type, action)
		entity.getComponent(AnimationComponent::class.java).animate(anim, loops, speed)
	}

	//______________________________________________________________________________________________
	fun getAnimation(type: GunComponent.TYPE, action: GunComponent.ACTION) : String
	{
		when(type) {
			GunComponent.TYPE.CZ805 ->
				when(action) {
					GunComponent.ACTION.IDLE -> return "cz|idle"
					GunComponent.ACTION.SHOOT -> return "cz|shoot"
					GunComponent.ACTION.RELOAD -> return "cz|reload"
					GunComponent.ACTION.DRAW -> return "cz|draw"
				}
			GunComponent.TYPE.COLT1911 -> return ""
			GunComponent.TYPE.AK47 -> return ""
			////gun.getComponent(AnimationComponent::class.java).animate("Armature|shoot", 1, 3)
		}
	}
}