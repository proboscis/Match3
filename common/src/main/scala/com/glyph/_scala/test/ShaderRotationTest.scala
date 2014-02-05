package com.glyph._scala.test

import com.badlogic.gdx.graphics._
import com.glyph._scala.lib.libgdx.gl.{SRTrail, BaseTrail, BaseStripBatch, ShaderHandler}
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import java.util
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.glyph._scala.lib.util.Logging

class ShaderRotationTest extends AppliedTrailTest(
  1000,
  new BaseStripBatch(1000 * 10 * 2, SRTrail.ATTRIBUTES),
  ShaderHandler("shader/rotate.vert", "shader/color.frag"),
  () => new SRTrail(10)
)



