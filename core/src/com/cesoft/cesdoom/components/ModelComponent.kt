package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.cesoft.cesdoom.RenderUtils.FrustumCullingData


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ModelComponent(model: Model, pos: Vector3) : Component {

	var instance: ModelInstance = ModelInstance(model, Matrix4().setToTranslation(pos))
	var blendingAttribute:BlendingAttribute? = null

	///---- Frusturm Culling
	var frustumCullingData: FrustumCullingData = FrustumCullingData()

}
