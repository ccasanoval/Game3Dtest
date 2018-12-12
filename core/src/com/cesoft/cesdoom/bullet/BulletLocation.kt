package com.cesoft.cesdoom.bullet

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector3

class BulletLocation(
	private var _position: Vector3 = Vector3(),
	private var _orientation: Float = 0f)
	: Location<Vector3>
{
	override fun getPosition(): Vector3 = _position

	override fun getOrientation(): Float = _orientation
	override fun setOrientation(orientation: Float) {
		_orientation = orientation
	}

	override fun newLocation(): Location<Vector3> = BulletLocation()

	override fun vectorToAngle(vector: Vector3): Float
		= Math.atan2(-vector.z.toDouble(), vector.x.toDouble()).toFloat()

	override fun angleToVector(outVector: Vector3, angle: Float): Vector3
	{
		//val outVector = Vector3()
		outVector.z = -Math.sin(angle.toDouble()).toFloat()
		outVector.y = 0f
		outVector.x = Math.cos(angle.toDouble()).toFloat()
		return outVector
	}
}