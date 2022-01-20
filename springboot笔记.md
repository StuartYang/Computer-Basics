
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
       1. this.getAutoConfigurationEntry(annotationMetadata)得到批量注册组件，加载工厂加载器

    2. @Import: 用来导入包
   

定制化的两种方式：
1. 用户自己@Bean替换底层的组件（再选）
2. 用户重写properties中的值（首选）

XXXAutoConfig
如何查看那些组件没有被查看？
debug = true 

打印出Negative matches以下的就是咩有匹配的。

springboot 文档配置

lombok插件

```xml
<dependency>
   <groupId>org.projectlombok</groupId>
   <artifactId>lombok</artifactId>
   <version>1.18.20</version>
   <scope>provided</scope>
</dependency>
```
并在idea中搜索lombok插件


```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<version>2.5.7</version>
		</dependency>
```
devtools安装，每次Ctrl+F9或 Build中的锤子图标——>静态页

```xml
	<!--mvn package 用来打包，把环境也给打进入-->
	<!--jar -jar xxxx.jar 启动-->
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```

## 第二季

### yml配置文件
注意点：
1. 字符串默认不用加上单引号或者双引号，如果加上 “”双引号 ， ‘’单引号 ，也没事
   1. name: “zhangsan \n lisi”：输出；zhangsan 换行 lisi

```yaml
person:
  # 普通值
  name: “zhangsan \n lisi” # 输出；zhangsan 换行 lisi ,双引号中的内容会转义
  aliasName: Tom 
  #Map（属性和值）
  friends: {lastName: zhangsan,age: 18}
  # 数组（List、Set）
  pets: [cat,dog,pig]
  #对象
  pet:
      name: jack
      age: 2
```

yaml中无提示解决：加完包，启动一下，就有提示

```xml

<!--		yaml配置有提示-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
<!--					不要将yaml提示给打包进入-->
					<excludes>
						<exclude>
							<groupId>org.springframework.boot</groupId>
							<artifactId>spring-boot-configuration-processor</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

```
多Profile文件

- 我们在主配置文件编写的时候，文件名可以是 application-{profile}.properties/yml
- 默认使用application.properties的配置；
- 在配置文件中指定 spring.profiles.active=dev
- 命令行： java -jar spring-boot-02-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

```yaml
spring:
  profiles:
    active: prod
```

@Value获取值和@ConfigurationProperties获取值比较

- 如果说，我们只是在某个业务逻辑中需要获取一下配置文件中的某项值，使用@Value；
- 如果说，我们专门编写了一个javaBean来和配置文件进行映射，我们就直接使用@ConfigurationProperties；

|——|@ConfigurationProperties|@Value|
|----|----|----|
|功能|批量注入配置文件中的属性|一个个指定|
|松散绑定（松散语法）|支持|不支持|
|SpEL|不支持|支持|
|JSR303数据校验|支持|不支持|
|复杂类型封装|支持|不支持|


## Web开发

