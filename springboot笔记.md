
pom中父项目是做依赖管理
在自己pom文件中相对应的

官方： spring-boot-starter-*
第三方：*-spring-boot-starter

主程序所在的包和主程序下面的子包都能被扫描

properties是按需加载的，引入那些场景这个场景的自动配置才会生效

```java

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Component/@EnableConfigurationProperties 二选一 + @ConfigurationProperties
 */
@Component // 是自己写的配置类，就可以用这个 
@EnableConfigurationProperties(User.class) // 标注在配置文件上，当引入第三方包时@Component 可能不能用，就用这个
@ConfigurationProperties(prefix = "user")
public class User() {
    String name;
    
}

//使用

public class Main() { 
    @Autowired
   private User user;
}
```

## SpringBootApplication详细解释
```java

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan()
public @interface SpringBootApplication {
    
    
}
```

```java
@Configuration
public @interface SpringBootConfiguration {
}

```

```java
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
    
}
```

```java
@Import({AutoConfigurationPackages.Registrar.class})
public @interface AutoConfigurationPackage {
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
```

1. @SpringBootConfiguration = @Configuration： 说明是一个配置类
2. @ComponentScan() : 包扫描
3. @EnableAutoConfiguration * 
    1. @AutoConfigurationPackage: 用Registrar批量注册组件，将Main程序所在的包下的所有组件都扫描进spring
    2. @Import: 用来导入包
