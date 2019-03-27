package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.renderUtils.FrustumCullingData


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object DecalFactory {


    //______________________________________________________________________________________________
    // Creates a decal or billboard looking to +Z
    //TODO: Change the way we create the objects to a more efficient way (Meshes...) so to increase FPS
    fun addDecal(texture: Texture, engine: Engine,
                         size: Vector2, position: Vector3, angleX: Float, angleY: Float,
                         color: Color = Color.WHITE) {

        val entity = Entity()
        entity.add(createDecal(texture, size, position, angleX, angleY, color))
        engine.addEntity(entity)
    }
    //______________________________________________________________________________________________
    private fun createDecal(texture: Texture, size: Vector2,
                            position: Vector3, angleX: Float, angleY: Float,
                            color: Color = Color.WHITE) : ModelComponent {

        val material = Material(ColorAttribute.createDiffuse(color))
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
        val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
        material.set(textureAttribute)
        material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

        return createDecal(material, size, position, angleX, angleY)
    }
    //______________________________________________________________________________________________
    fun createDecal(material: Material, size: Vector2,
                            position: Vector3, angleX: Float, angleY: Float) : ModelComponent {

        val cx = size.x / 2
        val cy = size.y
        val dim = Vector3(size.x, size.y, size.x)

        /// MODEL
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val attributes = VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong() or VertexAttributes.Usage.TextureCoordinates.toLong()
        //
        val partName = modelBuilder.hashCode().toString()
        val meshBuilder = modelBuilder.part(partName, GL20.GL_TRIANGLES, attributes, material)
        val v1 = MeshPartBuilder.VertexInfo().setPos(-cx, 0f, 0f).setNor(0f, 0f, 1f).setCol(null).setUV(0f, 1f)
        val v2 = MeshPartBuilder.VertexInfo().setPos(+cx, 0f, 0f).setNor(0f, 0f, 1f).setCol(null).setUV(1f, 1f)
        val v3 = MeshPartBuilder.VertexInfo().setPos(+cx, cy, 0f).setNor(0f, 0f, 1f).setCol(null).setUV(1f, 0f)
        val v4 = MeshPartBuilder.VertexInfo().setPos(-cx, cy, 0f).setNor(0f, 0f, 1f).setCol(null).setUV(0f, 0f)
        meshBuilder.rect(v1, v2, v3, v4)
        val model = modelBuilder.end()
        //
        val modelComponent = ModelComponent(model, position)
        modelComponent.instance.transform.rotate(Vector3.X, angleX)
        modelComponent.instance.transform.rotate(Vector3.Y, angleY)
        modelComponent.frustumCullingData = FrustumCullingData.create(position, dim)

        return modelComponent
    }

}