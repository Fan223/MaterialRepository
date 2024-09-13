package grey.fable.rpcframework.core.annotation;

import java.lang.annotation.*;

/**
 * RPC服务注解，标记在服务实现类上
 *
 * @author GreyFable
 * @since 2024/8/29 15:19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
