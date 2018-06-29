package v0id.core.util.itemmodel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface AutoModelProvider
{
	Class<? extends IAutoModelProvider> provider();
}