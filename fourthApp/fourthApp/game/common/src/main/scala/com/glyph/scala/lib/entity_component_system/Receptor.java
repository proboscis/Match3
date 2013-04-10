package com.glyph.scala.lib.entity_component_system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * if you want to create an adapter, use this annotation<br>
 * for each of the adapter's field<br>
 * so that the entity container can create a filter for each adapters.
 * @author glyph
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Receptor {

}
