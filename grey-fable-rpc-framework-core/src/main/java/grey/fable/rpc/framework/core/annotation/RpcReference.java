package grey.fable.rpc.framework.core.annotation;

import java.lang.annotation.*;

/**
 * RPC 引用注解，自动装配服务实现类.
 *
 * @author GreyFable
 * @since 2024/8/29 15:24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
