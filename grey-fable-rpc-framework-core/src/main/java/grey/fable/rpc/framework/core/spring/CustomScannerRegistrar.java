package grey.fable.rpc.framework.core.spring;

import grey.fable.rpc.framework.core.annotation.RpcScan;
import grey.fable.rpc.framework.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 扫描和筛选指定的注解.
 *
 * @author GreyFable
 * @since 2024/8/29 15:17
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String SPRING_BEAN_BASE_PACKAGE = "grey.fable";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取 RpcScan 注解的属性和值
        String[] rpcScanBasePackages = new String[0];
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        if (rpcScanAnnotationAttributes != null) {
            // 获取 basePackage 属性的值
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        // 扫描 RpcService 注解
        CustomScanner rpcServiceScanner = new CustomScanner(beanDefinitionRegistry, RpcService.class);
        // 扫描 Component 注解
        CustomScanner springBeanScanner = new CustomScanner(beanDefinitionRegistry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);
    }
}
