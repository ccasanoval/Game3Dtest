package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.collision.BoundingBox




////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ModelComponent(model: Model, pos: Vector3, val isMustShow:Boolean=false) : Component {
	var instance: ModelInstance = ModelInstance(model, Matrix4().setToTranslation(pos))
	var blendingAttribute:BlendingAttribute? = null

	/// 4 Frusturm Culling
	var radius: Float = 0f
	val center: Vector3 = Vector3()
	val dimensions: Vector3 = Vector3()
	init {
		if( ! isMustShow) {
			val bounds = BoundingBox()
			instance.calculateBoundingBox(bounds)
			bounds.getCenter(center)
			bounds.getDimensions(dimensions)
			radius = dimensions.len() / 2f
		}
	}
}
