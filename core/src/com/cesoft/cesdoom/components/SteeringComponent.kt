package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.BulletLocation


////////////////////////////////////////////////////////////////////////////////////////////////////
//
// https://github.com/libgdx/gdx-ai/wiki/Steering-Behaviors#the-steering-system-api
//
class SteeringComponent(private val body: btRigidBody, private val _boundingRadius: Float)
	: Steerable<Vector3>, Component {

	private var _maxLinearSpeed: Float = 0f
	private var _maxLinearAcceleration: Float = 0f
	private var _maxAngularSpeed: Float = 0f
	private var _maxAngularAcceleration: Float = 0f
	private var _tagged: Boolean = false

	private val tmpQuaternion = Quaternion()
	private val tmpMatrix4 = Matrix4()
	private val tmpVector3 = Vector3()

	//private var independentFacing: Boolean = false
	//private var steeringBehavior: SteeringBehavior<Vector3>? = null
	//private val steeringOutput = SteeringAcceleration(Vector3())

	//______________________________________________________________________________________________
	init {
		body.angularFactor = Vector3(0f, 1f, 0f)
	}

	//______________________________________________________________________________________________

	//fun procesar(steeringBehavior: SteeringBehavior<Vector3>) : SteeringAcceleration<Vector3> = steeringBehavior.calculateSteering(steeringOutput)
	/*fun update(delta: Float) {
		if(steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior!!.calculateSteering(steeringOutput)
			return steeringOutput

			// Apply steering acceleration
			//applySteering(steeringOutput, deltaTime)
			// Update position and linear velocity
			if( !steeringOutput.linear.isZero) {
				// this method internally scales the force by deltaTime
				body.applyCentralForce(steeringOutput.linear)
			}
		}
	}*/

	//______________________________________________________________________________________________
	override fun newLocation(): Location<Vector3> = BulletLocation()
	override fun getBoundingRadius(): Float = _boundingRadius

	override fun getPosition(): Vector3
	{
		body.motionState.getWorldTransform(tmpMatrix4)
		return tmpMatrix4.getTranslation(tmpVector3)
	}
	override fun getLinearVelocity(): Vector3 = body.linearVelocity
	override fun getAngularVelocity(): Float {
		val angularVelocity = body.angularVelocity
		return angularVelocity.y
	}

	override fun setOrientation(valor: Float) {
		val transform = body.worldTransform.cpy()
		transform.setToRotationRad(0f, 1f, 0f, valor)
		body.worldTransform = transform
	}
	override fun getOrientation(): Float {
		body.worldTransform.getRotation(tmpQuaternion, true)
		return tmpQuaternion.yawRad
	}

	override fun getZeroLinearSpeedThreshold(): Float = 0.001f
	override fun setZeroLinearSpeedThreshold(valor: Float) {
		//System.err.println("SteeringComponent: setZeroLinearSpeedThreshold: ------------$valor")
		throw UnsupportedOperationException()
	}

	override fun setTagged(valor: Boolean) { _tagged = valor }
	override fun isTagged(): Boolean = _tagged

	override fun setMaxAngularSpeed(valor: Float) { _maxAngularSpeed = valor }
	override fun getMaxAngularSpeed(): Float = _maxAngularSpeed

	override fun setMaxAngularAcceleration(valor: Float) { _maxAngularAcceleration = valor }
	override fun getMaxAngularAcceleration(): Float = _maxAngularAcceleration

	override fun setMaxLinearAcceleration(valor: Float) { _maxLinearAcceleration = valor }
	override fun getMaxLinearAcceleration(): Float = _maxLinearAcceleration

	override fun setMaxLinearSpeed(valor: Float) { _maxLinearSpeed = valor }
	override fun getMaxLinearSpeed(): Float = _maxLinearSpeed

	override fun vectorToAngle(vector: Vector3): Float = Math.atan2(-vector.z.toDouble(), vector.x.toDouble()).toFloat()
	override fun angleToVector(outVector: Vector3, angle: Float): Vector3 {
		//val outVector = Vector3()
		outVector.z = -Math.sin(angle.toDouble()).toFloat()
		outVector.y = 0f
		outVector.x = Math.cos(angle.toDouble()).toFloat()
		return outVector
	}


	//______________________________________________________________________________________________
	/*companion object {
		fun vectorToAngle(vector: Vector3) = Math.atan2(-vector.z.toDouble(), vector.x.toDouble()).toFloat()
		fun angleToVector(angle: Float): Vector3 {
			val outVector = Vector3()
			outVector.z = -Math.sin(angle.toDouble()).toFloat()
			outVector.y = 0f
			outVector.x = Math.cos(angle.toDouble()).toFloat()
			return outVector
		}
	}*/

}



		/*var currentMode = SteeringState.WANDER	// stores which state the entity is currently in
		var body: Body? = null					// stores a reference to our Box2D body

		// Steering data
		private var maxLinearSpeed = 2f    // stores the max speed the entity can go
		private var maxLinearAcceleration = 5f    // stores the max acceleration
		private var maxAngularSpeed = 50f        // the max turning speed
		private var maxAngularAcceleration = 5f// the max turning acceleration
		private var zeroThreshold = 0.1f    // how accurate should checks be
		private var steeringBehavior: SteeringBehavior<Vector2>? = null // stors the action behaviour
		private val boundingRadius = 1f   // the minimum radius size for a circle required to cover whole object
		private var tagged = true        // This is a generic flag utilized in a variety of ways. (never used this myself)
		private var isIndependentFacing = false // defines if the entity can move in a direction other than the way it faces)

		enum class SteeringState {
			WANDER, SEEK, FLEE, ARRIVE, NONE
		}

		//______________________________________________________________________________________________
		override fun reset() {
			currentMode = SteeringState.NONE
			body = null
			steeringBehavior = null

		}

		//______________________________________________________________________________________________
		fun update(delta: Float) {
			if(steeringBehavior != null) {
				steeringBehavior!!.calculateSteering(steeringOutput)
				applySteering(/*steeringOutput, */delta)
			}
		}

		//______________________________________________________________________________________________
		private fun applySteering(/*steering: SteeringAcceleration<Vector2>, */deltaTime: Float) {
			var anyAccelerations = false

			// Update position and linear velocity.
			if(!steeringOutput.linear.isZero) {
				// this method internally scales the force by deltaTime
				body!!.applyForceToCenter(steeringOutput.linear, true)
				anyAccelerations = true
			}

			// Update orientation and angular velocity
			if(isIndependentFacing) {
				if(steeringOutput.angular != 0f) {
					// this method internally scales the torque by deltaTime
					body!!.applyTorque(steeringOutput.angular, true)
					anyAccelerations = true
				}
			}
			else {
				// If we haven't got any velocity, then we can do nothing.
				val linVel = linearVelocity
				if(!linVel.isZero(zeroLinearSpeedThreshold)) {
					val newOrientation = vectorToAngle(linVel)
					body!!.angularVelocity = (newOrientation - angularVelocity) * deltaTime // this is superfluous if independentFacing is always true
					body!!.setTransform(body!!.position, newOrientation)
				}
			}

			if(anyAccelerations) {
				// Cap the linear speed
				val velocity = body!!.linearVelocity
				val currentSpeedSquare = velocity.len2()
				val maxLinearSpeed = getMaxLinearSpeed()
				if(currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
					body!!.linearVelocity = velocity.scl(maxLinearSpeed / Math.sqrt(currentSpeedSquare.toDouble()).toFloat())
				}
				// Cap the angular speed
				val maxAngVelocity = getMaxAngularSpeed()
				if(body!!.angularVelocity > maxAngVelocity) {
					body!!.angularVelocity = maxAngVelocity
				}
			}
		}


		//______________________________________________________________________________________________
		override fun getPosition(): Vector2 = body!!.position
		override fun getOrientation(): Float = body!!.angle

		override fun setOrientation(orientation: Float) {
			body!!.setTransform(position, orientation)
		}

		override fun vectorToAngle(vector: Vector2): Float =
			Math.atan2(-vector.x.toDouble(), vector.y.toDouble()).toFloat()

		override fun angleToVector(outVector: Vector2, angle: Float): Vector2 {
			outVector.x = -Math.sin(angle.toDouble()).toFloat()
			outVector.y = Math.cos(angle.toDouble()).toFloat()
			return outVector
		}
		public static float vectorToAngle (Vector3 vector) {
	// return (float)Math.atan2(vector.z, vector.x);
			return (float)Math.atan2(-vector.z, vector.x);
		}

		public static Vector3 angleToVector (Vector3 outVector, float angle) {
	// outVector.set(MathUtils.cos(angle), 0f, MathUtils.sin(angle));
			outVector.z = -(float)Math.sin(angle);
			outVector.y = 0;
			outVector.x = (float)Math.cos(angle);
			return outVector;

			override fun newLocation(): Location<Vector2>? = null//Box2DLocation<Vector2>()

		override fun getZeroLinearSpeedThreshold(): Float = zeroThreshold
		override fun setZeroLinearSpeedThreshold(value: Float) { zeroThreshold = value }
		override fun getMaxLinearSpeed(): Float = this.maxLinearSpeed
		override fun setMaxLinearSpeed(maxLinearSpeed: Float) {	this.maxLinearSpeed = maxLinearSpeed }
		override fun getMaxLinearAcceleration(): Float = this.maxLinearAcceleration
		override fun setMaxLinearAcceleration(maxLinearAcceleration: Float) { this.maxLinearAcceleration = maxLinearAcceleration }
		override fun getMaxAngularSpeed(): Float = this.maxAngularSpeed
		override fun setMaxAngularSpeed(maxAngularSpeed: Float) { this.maxAngularSpeed = maxAngularSpeed }
		override fun getMaxAngularAcceleration(): Float = this.maxAngularAcceleration
		override fun setMaxAngularAcceleration(maxAngularAcceleration: Float) {	this.maxAngularAcceleration = maxAngularAcceleration }
		override fun getLinearVelocity(): Vector2 = body!!.linearVelocity
		override fun getAngularVelocity(): Float = body!!.angularVelocity
		override fun getBoundingRadius(): Float = this.boundingRadius
		override fun isTagged(): Boolean = this.tagged
		override fun setTagged(tagged: Boolean) { this.tagged = tagged }

		companion object {
			private val steeringOutput = SteeringAcceleration(Vector2())
		}*/
