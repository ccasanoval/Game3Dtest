package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ModelComponent(model: Model, x: Float, y: Float, z: Float) : Component {
	var matrix4: Matrix4 = Matrix4()
	var instance: ModelInstance = ModelInstance(model, matrix4.setToTranslation(x, y, z))
}
