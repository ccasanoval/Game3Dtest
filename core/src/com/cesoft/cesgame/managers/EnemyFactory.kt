package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.components.AnimationComponent
import com.cesoft.cesgame.components.EnemyComponent
import com.cesoft.cesgame.components.ModelComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyFactory
{
	private val modelLoaderJSON = G3dModelLoader(JsonReader())
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private var models = mutableMapOf<EnemyComponent.TYPE, Model>()
	private var files = mutableMapOf<EnemyComponent.TYPE, FileHandle>()

	init {
		files[EnemyComponent.TYPE.ZOMBIE1] = Gdx.files.getFileHandle("armas/zombie1/a.g3db", Files.FileType.Internal)
		files[EnemyComponent.TYPE.MONSTER1] = Gdx.files.getFileHandle("armas/monster1/a.g3db", Files.FileType.Internal)
	}

	//______________________________________________________________________________________________
	fun dispose()
	{
		for((_, model) in models)
			model.dispose()
	}

	//______________________________________________________________________________________________
	fun createModel(type: EnemyComponent.TYPE): Model {
		val model: Model
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 -> {
				model = modelLoader.loadModel(files[type])
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.03f)
			}
			EnemyComponent.TYPE.MONSTER1 -> {
				model = modelLoaderJSON.loadModel(files[type])
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.03f)
			}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun create(type: EnemyComponent.TYPE) : Entity
	{
		val entity = Entity()

		val enemy = EnemyComponent(type)
		entity.add(enemy)

		if(models[type] == null)
			models[type] = createModel(type)
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 -> {
				val modelComponent = ModelComponent(models[type]!!, Vector3(25f, -10f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
			}
			EnemyComponent.TYPE.MONSTER1 -> {
				val modelComponent = ModelComponent(models[type]!!, Vector3(25f, -10f, -15f))
				modelComponent.instance.transform.rotate(0f, 1f, 0f, 185f)
				modelComponent.instance.transform.rotate(1f, 0f, 0f, -7f)
				entity.add(modelComponent).add(AnimationComponent(modelComponent.instance))
			}
		}

		return entity
	}

	//______________________________________________________________________________________________
	fun animate(entity: Entity, action: EnemyComponent.ACTION, loops: Int = 1, speed: Int = 3)
	{
		val type = entity.getComponent(EnemyComponent::class.java).type
		val anim = getAnimation(type, action)
		entity.getComponent(AnimationComponent::class.java).animate(anim, loops, speed)
	}

	//______________________________________________________________________________________________
	private fun getAnimation(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) : String
	{
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 ->
				when(action) {
					EnemyComponent.ACTION.IDLE -> return "Idle"
					EnemyComponent.ACTION.DYING -> return "Dying"
					EnemyComponent.ACTION.ATTACKING -> return "Attacking"
					EnemyComponent.ACTION.WALKING -> return "Walking"
					EnemyComponent.ACTION.REINCARNATING -> return "Reincarnating"
				}
			EnemyComponent.TYPE.MONSTER1 ->
				when(action) {
					EnemyComponent.ACTION.IDLE -> return "MilkShape3D Skele|DefaultAction"
					EnemyComponent.ACTION.DYING -> return "MilkShape3D Skele|DefaultAction.001"
					EnemyComponent.ACTION.ATTACKING -> return "MilkShape3D Skeleton|DefaultAction"
					EnemyComponent.ACTION.WALKING -> return "MilkShape3D Skeleton|DefaultAction.001"
					EnemyComponent.ACTION.REINCARNATING -> return ""
					//"MilkShape3D Skele|DefaultAction"
					//"MilkShape3D Skele|DefaultAction.001",
					//"MilkShape3D Skeleton|DefaultAction",
					//"MilkShape3D Skeleton|DefaultAction.001",
				}
		}
	}
}