package com.akon.areateleport.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Cmd {

	String value();

	String[] aliases() default {};

	String[] params() default {};

	String description() default "";
}
