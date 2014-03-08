package com.glyph
import scala.language.experimental.macros
import scala.reflect.macros.Context

trait ClassMacro{
	/**
	* implicitly returns a class of specified type!
	**/
	implicit def getClassMacro[T]:Class[T] = macro ClassMacroImpl.getClassMacroImpl[T]
}
object ClassMacro extends ClassMacro
object ClassMacroImpl{
	def getClassMacroImpl[T:c.WeakTypeTag](c:Context):c.Expr[Class[T]] ={
		import c.universe._
		val typ = c.weakTypeOf[T]
		val sym = typ.typeSymbol
		/*
		println(showRaw(typ))
		println(showRaw(sym))
		println(showRaw(reify{classOf[Int]}))
*/
		c.Expr[Class[T]](Literal(Constant(typ)))
	}
}