[toc]

## JSR303校验

> JSR(Java Specification [Requests](https://so.csdn.net/so/search?q=Requests&spm=1001.2101.3001.7020))是Java界的重要标准；JSR又细分很多标准，其中JSR303就代表Bean Validation。

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

```java
import javax.validation.constraints.*;
```



| 注解         | 功能                                                         |
| ------------ | ------------------------------------------------------------ |
| @AssertFalse | 可以为null,如果不为null的话必须为false                       |
| @AssertTrue  | 可以为null,如果不为null的话必须为true                        |
| @DecimalMax  | 设置不能超过最大值                                           |
| @DecimalMin  | 设置不能超过最小值                                           |
| @DecimalMin  | 设置不能超过最小值                                           |
| @Future      | 日期必须在当前日期的未来                                     |
| @Past        | 日期必须在当前日期的过去                                     |
| @Max         | 最大不得超过此最大值                                         |
| @NotNull     | 不能为null，可以是空                                         |
| @Pattern     | 必须满足指定的正则表达式                                     |
| @Pattern     | 必须满足指定的正则表达式                                     |
| @Email       | 必须是email格式                                              |
| @Length      | 长度必须在指定范围内                                         |
| @NotBlank    | 字符串不能为null,字符串trim()后也不能等于“”                  |
| NotEmpty     | 不能为null，集合、数组、map等size()不能为0；字符串trim()后可以等于“” |
| @Range       | 值必须在指定范围内                                           |
| @Range       | 值必须在指定范围内                                           |
| @Min         | 最大不得小于此最小值                                         |

```java
import org.springframework.validation.annotation.Validated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Component
@ConfigurationProperties(prefix = "dog")
@Validated// 数据校验开启
public class Dog {

    private String name;
    // @Email
    private String lastName; // 松散绑定

    // @NotNull
    private Integer age;
    private Student student; // 可以不用@Component
    private LinkedList<String> stringLinkedList;
    private HashMap<String,String> map;

}
```



## 多环境配置及配置文件位置

```yaml
spring:
  profiles:
    active: dev
```

- application-dev.yml

## 自动配置源码分析

### 底层注解

#### @Configuration, @Bean 注册Bean

> 告诉spring，这是一个配置类

**没有springboot时候注册bean**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans   xmlns="http://www.springframework.org/schema/beans" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.springframework.org/schema/beans 
         http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
    
    <!--注册bean -->
    <!--id为这个Bean的名称，通过容器的getBean("car")即可获取对应的Bean-->
     <bean id="car"  class="com.yxd.pojo.Car"></bean>  
    
    
</beans>
```

**springboot 注册bean**

```java
import org.springframework.context.annotation.Configuration;
/**
* 被@Configuration注解后的配置类本身就是一个代理配置bean
* proxyBeanMethods = true ： 默认为true ， springboot总会检查注册在该配置类下的组件是否在容器中* 有，如果有就不会新创，即保证单实例
**/
@Configuration(proxyBeanMethods = true)
public class MyConfiguration{
    
    @Bean("wl") // 自定义设置JavaBean名称
    public Car car(){
        return new Car("五菱",23);
    }
}

```

- proxyBeanMethods 5.2的更新
  - @Configuration(proxyBeanMethods = true) ： springboot总会检查注册在该配置类下的组件是否在容器中有，如果有就不会新创，即保证Javabean单实例
  - @Configuration(proxyBeanMethods = false) ： 获取即创建，Javabean变成单实例
    - 使用推荐：如果仅仅是注册组件，但是其他不与其他组件进行依赖（别的组件不使用），推荐设置为false

**@Configuration源码**

```java
public @interface Configuration {
    
   @AliasFor(annotation = Component.class)
   String value() default "";
   
    /**
    * 是不是代理 ，默认是true
    */
    
   boolean proxyBeanMethods() default true;

}
```

#### @Import 注册Bean

```java
import org.springframework.context.annotation.Import;

// 默认组件的名字是全类名
@Import({Dog.class, Student.class})
public class MyConfiguration{
    
}

```

#### @Conditional* 条件注册Bean

```java
@Configuration
  /**
   * Dog 组件存在时，MyConfiguration配置类才生效
   */
 @ConditionalOnBean(name = "dog")
public class MyConfiguration{
    //  @Bean
    public Dog dog() {
        return new Dog();
    }
    /**
     * Dog 组件存在时，才注册Student组件
     */
    @ConditionalOnBean(name = "dog")
    public Student student() {
        return new Student();
    }
}

```

#### @ImportResource

> 用来兼容xml注册Bean 和 注解注册Bean

```java
@Configuration
@ImportResource("classpath:beans.xml") // beans.xml中的Bean解析进springboot容器中
public class MyConfiguration{
    //  @Bean
    public Dog dog() {
        return new Dog();
    }
}

```

#### @Component+@ConfigurationProperties= @EnableConfigurationProperties

```java
@Component // 只用配置进spring的组件，才能使用@PropertySource
@ConfigurationProperties(prefix = "dog")
public class Student {
    String name;

    String gender;

    Integer age;

}
```

```java
/**
 * 注意：只能放在@Configuration标注的配置类上
 * 1. 开启Dog的属性配置功能
 * 2. 把Dog组件注册到spring容器中
 */
@EnableConfigurationProperties(Dog.class)
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    
}
```

- @EnableConfigurationProperties：用去第三方包 头上没有@Component+@PropertySource组件，就使用@EnableConfigurationProperties

#### @PropertySource

> 自定义指定properties配置文件

```java
@Component
@PropertySource(value = "classpath:yxd.properties",encoding = "utf-8")
public class Student {
    // 支持spEL ； 一个一个绑定
    @Value("${name}") // 如果只是短暂的获取一个值，@value就行
    String name;

    String gender;

    Integer age;

}

```

#### @SpringBootApplication

> @SpringBootApplication = @SpringBootConfiguration+@EnableAutoConfiguration+@ComponentScan

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
      @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
    
}
```

##### 1. @SpringBootConfiguration

> 标志这是一个springboot的核心配置类

```java
@Configuration
public @interface SpringBootConfiguration {
    
}
```

##### 2. @ComponentScan

> 定义**扫描的路径**从中找出标识了**需要装配**的类自动装配到spring的bean容器中

```java
/*
basePackages = value : 对应的包扫描路径 可以是单个路径，也可以是扫描的路径数组
useDefaultFilters() default true : 是否对带有@Component @Repository @Service @Controller注解的类开启检测,默认是开启的
*/
@ComponentScan(value="com.yxd",useDefaultFilters=true)
@Configuration
public class MainScanConfig {

}

```



- @Controller，@Service，@Repository注解，查看其源码会发现，他们中有一个共同的注解@Component
- 自定扫描路径下边带有@Controller，@Service，@Repository，@Component注解加入spring容器
- 通过includeFilters加入扫描路径下没有以上注解的类加入spring容器
- 通过excludeFilters过滤出不用加入spring容器的类

##### 3. 核心：@EnableAutoConfiguration

```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	/**
	 * Environment property that can be used to override when auto-configuration is
	 * enabled.
	 */
	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	String[] excludeName() default {};

}

```

- @AutoConfigurationPackage:自动配置包

  ```java
  @Import(AutoConfigurationPackages.Registrar.class) // 给容器中导入一个Registrar组件
  public @interface AutoConfigurationPackage {
     String[] basePackages() default {};
     Class<?>[] basePackageClasses() default {};
  }
  
  ===========================================================================
      
      
     /*
     ImportBeanDefinitionRegistrar to store the base package from the importing configuration.
     */
  static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
  
          /*
          将Main程序所在的包下的所有组件全部注册进入spring
          */
  		@Override
  		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
  			register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
  		}
  
  		@Override
  		public Set<Object> determineImports(AnnotationMetadata metadata) {
  			return Collections.singleton(new PackageImports(metadata));
  		}
  
  	}
      
  ```

  - Registrar：将Main程序所在的包下的所有组件全部注册进入spring，所有 就算Main入口程序上没有使用@ComponentScan,也达到了组件注册入spring的功效。

- @Import(AutoConfigurationImportSelector.class)

  - ```java
    public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
          ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
              
        @Override
    	public String[] selectImports(AnnotationMetadata annotationMetadata) {
    		if (!isEnabled(annotationMetadata)) {
    			return NO_IMPORTS;
    		}
            // 核心 getAutoConfigurationEntry(annotationMetadata);
    		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
    		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
    	}
        
        // 核心     
        protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
    		if (!isEnabled(annotationMetadata)) {
    			return EMPTY_ENTRY;
    		}
    		
            AnnotationAttributes attributes = getAttributes(annotationMetadata);
            // 核心getCandidateConfigurations：获取候选配置类 
    		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    		
            configurations = removeDuplicates(configurations);
    		
            Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    		
            checkExcludedClasses(configurations, exclusions);
    		
            configurations.removeAll(exclusions);
    		
            configurations = getConfigurationClassFilter().filter(configurations);
    		
            fireAutoConfigurationImportEvents(configurations, exclusions);
    		
            return new AutoConfigurationEntry(configurations, exclusions);
    	}      
              
              
       protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
    				getBeanClassLoader());
    		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
    				+ "are using a custom packaging, make sure that file is correct.");
    		return configurations;
    	}
          
              
          }
    ```

  - ```java
    public final class SpringFactoriesLoader {
        
        public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
        
        static final Map<ClassLoader, Map<String, List<String>>> cache = new ConcurrentReferenceHashMap();
    
    public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader;
        String factoryTypeName = factoryType.getName();
        return (List)loadSpringFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList());
    }
    
      
    private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
        Map<String, List<String>> result = (Map)cache.get(classLoader);
        if (result != null) {
            return result;
        } else {
            HashMap result = new HashMap();
            try {
                Enumeration urls = classLoader.getResources("META-INF/spring.factories");
                while(urls.hasMoreElements()) {
                    URL url = (URL)urls.nextElement();
                    UrlResource resource = new UrlResource(url);
                    Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    Iterator var6 = properties.entrySet().iterator();
    
                    while(var6.hasNext()) {
                        Entry<?, ?> entry = (Entry)var6.next();
                        String factoryTypeName = ((String)entry.getKey()).trim();
                        String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String)entry.getValue());
                        String[] var10 = factoryImplementationNames;
                        int var11 = factoryImplementationNames.length;
    
                        for(int var12 = 0; var12 < var11; ++var12) {
                            String factoryImplementationName = var10[var12];
                            ((List)result.computeIfAbsent(factoryTypeName, (key) -> {
                                return new ArrayList();
                            })).add(factoryImplementationName.trim());
                        }
                    }
                }
    
                result.replaceAll((factoryType, implementations) -> {
                    return (List)implementations.stream().distinct().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
                });
                cache.put(classLoader, result);
                return result;
            } catch (IOException var14) {
                throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", var14);
            }
        }
    }
    }
    ```

  - 利用` getAutoConfigurationEntry(annotationMetadata)`给容器中批量导入一些组件。

  - 调用`List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes)`获取到所有需要导入到容器中的配置类。

  - 调用工厂加载器`SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),getBeanClassLoader())`得到所有的配置类 // 反射

    - 默认扫描所有包下的 META-INF/spring.factories文件 ，看一下 spring-boot-autoconfigure-2.6.2.jar

    - ```properties
      # Auto Configure
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
      org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration,\
      org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration,\
      org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration,\
      org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration
      ```

    - 可以看到，文件中已经写死了，springboot一启动就要给容器中加载所有的配置类

  - 虽然127个场景的所有自动配置**默认全部加载，但是最终会按需配置**

    - spring-boot-autoconfigure-2.6.2.jar中会有多个`XXX-AutoConfiguration`, @Conditional注解这这些配置类。

##### 自动装配的流程

>  springboot的设计：一个xxAutoConfiguration 搭配一个xxProperties。xxProperties与配置文件做了绑定

定制化配置两个办法：

1. @Bean替换组件
2. yaml修改xxProperties
