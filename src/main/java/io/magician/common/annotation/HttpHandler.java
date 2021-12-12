package io.magician.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpHandler {

    /**
     * 路径
     * @return
     */
    String path();
}
