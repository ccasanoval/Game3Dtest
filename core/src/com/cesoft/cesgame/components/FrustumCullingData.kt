package com.cesoft.cesgame.components

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class FrustumCullingData
{
	enum class TYPE { NONE, BOX, SPHERE }
	private var type: TYPE = TYPE.NONE
	private val _center = Vector3()
	private val _dimension = Vector3()
	private var radius: Float = -1f
	private var instance: ModelInstance ?= null


	//______________________________________________________________________________________________
	companion object {
		fun create(center: Vector3, dimension: Vector3, instance: ModelInstance ?= null) : FrustumCullingData
		{
			val obj = FrustumCullingData()
			obj._center.set(center)
			obj._dimension.set(dimension)
			obj.instance = instance
			obj.type = TYPE.BOX
			return obj
		}
		fun create(bounds: BoundingBox, instance: ModelInstance ?= null) : FrustumCullingData
		{
			val obj = FrustumCullingData()
			bounds.getCenter(obj._center)
			bounds.getDimensions(obj._dimension)
			obj.radius = obj._dimension.len() / 2f
			obj.instance = instance
			obj.type = TYPE.SPHERE
			return obj
		}
	}

	//______________________________________________________________________________________________
	private fun getCenter(): Vector3//TODO convertir en propiedad?
	{
		if(instance != null)
		{
			val pos = Vector3()
			instance!!.transform.getTranslation(pos)
			return pos.add(_center)
		}
		else
			return _center.cpy()
	}

	//______________________________________________________________________________________________
	fun isVisible(camera: Camera): Boolean =
		when(type) {
			FrustumCullingData.TYPE.NONE -> true
			FrustumCullingData.TYPE.BOX -> camera.frustum.boundsInFrustum(getCenter(), _dimension)
			FrustumCullingData.TYPE.SPHERE -> camera.frustum.sphereInFrustum(getCenter(), radius)
			//camera.frustum.pointInFrustum(getCenter())
		}

}
