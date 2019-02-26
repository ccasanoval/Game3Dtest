package com.cesoft.cesdoom.renderUtils

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
	private fun getCenter(): Vector3//TODO convertir en propiedad?
	{
		return if(instance != null) {
			instance!!.transform.getTranslation(posTemp)
			posTemp.add(_center)
		}
		else
			_center.cpy()
	}

	//______________________________________________________________________________________________
	fun isVisible(camera: Camera): Boolean =
		when(type) {
			TYPE.NONE -> true
			TYPE.BOX -> camera.frustum.boundsInFrustum(getCenter(), _dimension)
			TYPE.SPHERE -> camera.frustum.sphereInFrustum(getCenter(), radius)
			//camera.frustum.pointInFrustum(getCenter())
		}

	//______________________________________________________________________________________________
	companion object {
		private val posTemp = Vector3()
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

}
