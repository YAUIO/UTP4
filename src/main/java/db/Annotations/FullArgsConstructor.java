package db.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Just an annotation to make reflection usage easier
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FullArgsConstructor {
}
