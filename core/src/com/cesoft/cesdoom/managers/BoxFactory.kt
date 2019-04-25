package com.cesoft.cesdoom.managers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.managers.WallFactory.HIGH
import com.cesoft.cesdoom.managers.WallFactory.LONG
import com.cesoft.cesdoom.renderUtils.FrustumCullingData


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object BoxFactory {

    //______________________________________________________________________________________________
    // No usamos texturas en los bordes de los muros => no parece que renderice mas rapido...
    fun createBox(type: WallFactory.Type = WallFactory.Type.CONCRETE, assets: Assets,
                  size: Vector3, position: Vector3, angleX: Float, angleY: Float,
                  color: Color = Color.WHITE, color2: Color = Color.DARK_GRAY) : ModelComponent {

        val cx = size.x / 2
        val cy = size.y / 2
        val cz = size.z / 2
        val dim = Vector3(size.x, size.y, size.z)

        val attributes = VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong() or VertexAttributes.Usage.TextureCoordinates.toLong()
        val material2 = Material(ColorAttribute.createDiffuse(color2))
        //val material = Material(ColorAttribute.createDiffuse(color))
        //texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
        //val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
        //material.set(textureAttribute)
        //material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

        /// MATERIAL
        val length = if(size.x > size.z) size.x else size.z
        val texture = when(type) {
            WallFactory.Type.CONCRETE -> assets.getWallConcrete()
            WallFactory.Type.STEEL -> assets.getWallSteel()
            WallFactory.Type.GRILLE -> assets.getWallGrille()
            WallFactory.Type.CIRCUITS -> assets.getWallCircuits()
        }
        val scale =  when(type) {
            WallFactory.Type.CONCRETE -> .5f
            WallFactory.Type.STEEL -> 1f
            WallFactory.Type.GRILLE -> 1f
            WallFactory.Type.CIRCUITS -> .5f
        }
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute.scaleU = scale*size.y/HIGH
		textureAttribute.scaleV = textureAttribute.scaleU*scale*length/LONG
		val material = Material(ColorAttribute.createDiffuse(color))
		material.set(textureAttribute)

        val mat1 = if(cx > cz) material else material2
        val mat2 = if(cx > cz) material2 else material

        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        modelBuilder.part("front ", GL20.GL_TRIANGLES, attributes, mat1)
                .rect(-cx, -cy, -cz, -cx, +cy, -cz, +cx, +cy, -cz, +cx, -cy, -cz, +0f, +0f, -1f)
        modelBuilder.part("back  ", GL20.GL_TRIANGLES, attributes, mat1)
                .rect(-cx, +cy, +cz, -cx, -cy, +cz, +cx, -cy, +cz, +cx, +cy, +cz, +0f, +0f, +1f)
        modelBuilder.part("bottom", GL20.GL_TRIANGLES, attributes, material2)
                .rect(-cx, -cy, +cz, -cx, -cy, -cz, +cx, -cy, -cz, +cx, -cy, +cz, +0f, -1f, +0f)
        modelBuilder.part("top   ", GL20.GL_TRIANGLES, attributes, material2)
                .rect(-cx, +cy, -cz, -cx, +cy, +cz, +cx, +cy, +cz, +cx, +cy, -cz, +0f, +1f, +0f)
        modelBuilder.part("left  ", GL20.GL_TRIANGLES, attributes, mat2)
                .rect(-cx, -cy, +cz, -cx, +cy, +cz, -cx, +cy, -cz, -cx, -cy, -cz, -1f, +0f, +0f)
        modelBuilder.part("right ", GL20.GL_TRIANGLES, attributes, mat2)
                .rect(+cx, -cy, -cz, +cx, +cy, -cz, +cx, +cy, +cz, +cx, -cy, +cz, +1f, +0f, +0f)
        val model = modelBuilder.end()

        val modelComponent = ModelComponent(model, position)
        modelComponent.instance.transform.rotate(Vector3.X, angleX)
        modelComponent.instance.transform.rotate(Vector3.Y, angleY)
        modelComponent.frustumCullingData = FrustumCullingData.create(position, dim)

        return modelComponent
    }

}