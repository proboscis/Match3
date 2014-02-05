package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.gl.ShaderHandler
import com.glyph._scala.lib.util.{Logging, Timing}

/**
 * @author glyph
 */
class ExplosionTest extends
AppliedTrailTest(
  1000,
  new StripBatch(1000 * 20 * 2),
  ShaderHandler("shader/default.vert", "shader/color.frag"),
  () => new TestTrail(10)
) with ConfiguredScreen with Logging with Timing

