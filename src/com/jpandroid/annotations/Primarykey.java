package com.jpandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jpandroid.types.GenerationType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Primarykey
{
	public abstract String name() default "";

	public abstract GenerationType strategy() default GenerationType.NONE;

}
