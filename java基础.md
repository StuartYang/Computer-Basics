[TOC]

## 面向对象

- **封装**：封装的意义在于明确标识出允许使用的所有成员函数和数据项内部细节对外调用透明，外部调用无需修改或者关系内部实现，外部调用无需修改或者关心内部实现。

  1. JavaBean的属性私有，提供Get/Set对外访问，因为属性的赋值或者获取逻辑只能由JavaBean本身决定，而不能由外部胡乱修改。

     ```java
     private String name;
     // name有自己的命名规则，明显不能由外部直接修改
     public void setName(){
         this.name = "tuling_"+name;
     }
     ```

  2. orm框架：操作数据库，我们不需要关系链接是如何建立的，sql是如何执行的，只需要引入mybatis，调用方法即可。

- **继承**：extends ，继承基类的方法，并作出自己的改变和扩展。

  - 子类共性的方法或者属性直接使用父类的，而不需要自己再定义，只需扩展自己个性的

- **多态**：基于对象所属类的不同，外部对同一方法的调用，实际执行的逻辑不同。

  - 多态的三个条件：继承，方法重写，父类引用指向子类对象。

    ```java
    父类类型 变量名 = new 子类对象(); // 更换其他子类时候，只需修改new后面的部分即可
    变量名.子类方法(); // 无法调用子类特有方法
    ```



## JDK ，JRE ， JVM三者之间的关系

- JDK(java develpment kit) : java开发工具
- JRE（Java Runtime Environment）：java执行环境
- JVM（Java Virtual Machine）：Java虚拟机
- JDK = JRE（bin文件夹（虚拟机） + lib文件夹） + Java工具（javac+java+jconsole）



*java文件 ——javac编译————> class文件 ——放到jvm上（linux，windows）——> 对应操作系统的操作码*



## == 和 equals

- ==：
  - 对比的是栈中的值；
  - 基本数据类型比较的是变量值；
  - 引用类型是堆中内存对象的地址；
- equals：
  - object中默认也是采用==比较
  - string中的equals会重写

## hashCode的通用使用指南

1. 对同一对象调用多次时，hashCode方法必须一致返回相同的整数。
2. equal相等的两个对象，返回的hashcode必须是一致的。

## 重写equals方法为什么要重写hashcode

- **因为Hash比equals方法的开销要小，速度更快**，所以在涉及到**hashcode的容器中**（比如HashSet），判断自己是否持有该对象时，会先检查hashCode是否相等，如果hashCode不相等，就会直接认为不相等，并存入容器中，不会再调用equals进行比较。

## final的作用

最终的

- 修饰类：不可继承
- 修饰方法：不可子类重写，但是可以重载
- 修饰变量：变量一旦赋值就不可以更改它的值
  - 修饰成员变量
    - 修饰的**类/静态变量**(类变量由所有实例共享)，只能在**静态初始块**中指定初始值 或者声明该类变量时就指定初始值。
    - 修饰成员变量，可以在**非静态初始化块**，声明 或者 构造器中执行初始化。
  - 修饰局部变量：局部变量只声明没有初始化不会报错，与final无关；使用前要进行初始化；不允许二次赋值
- 修饰基本数据类型的变量和引用类型的变量
  - 修饰基本数据类型：一旦赋值，不可更改
  - 修饰引用数据类型：初始化之后便不能再指向另外一个对象，但是**引用的值是可以变得**。

```java
public class Test {

    /**
     * 类变量：没初始化会报错
     */
    final static int a = 1; // 声明时就需要赋值，或者静态代码块中赋值

    final static int a1; // 静态代码块中赋值

    static {
        a1 = 0;
    }

    /**
     * 成员变量：没初始化会报错
     */
    final int b1 = 1; // 实例变量： 声明时候赋值

    final int b2;// 实例变量： 非静态代码块 中赋值

    final int b3;// 实例变量： 构造器中 赋值

    {
        b2 = 1;
    }

    public Test() {
        this.b3 = 3;
    }

    int d = 0;

    public void setAge(int num) {
        d += num;
    }

    public static void main(String[] args) {

        final int c1; //局部变量只声明没有初始化不会报错，与final无关
        c1 = 2; // 使用前要进行初始化
        // c1 = 3; // 不允许二次赋值
        System.out.println(a1 + "——" + a);
        final Test t1 = new Test();
        t1.setAge(2);
        System.out.println(t1.d);
        //final Test t1 = new Test(); 非法
        t1.setAge(12);
        System.out.println(t1.d);
    }

}
```

## 为什么局部内部类和匿名内部类只能访问局部final变量？

```java
public class Test {

    public static void main(String[] args) {
    }

    // 局部final变量a,b
    public void test(final int b) {
        final int a = 0;
        // 匿名内部类
        new Thread(() -> {
            System.out.println(a);
            System.out.println(b);
        }).start();
    }

}

/**
 * 编译之后形成两个 Class文件 ：OutClass和OutClass1 两个class文件
 */
class OutClass {
    private int age = 12;

    // 局部变量 x
    public void test1(final int x) {
        // 局部内部类:在方法中定义的内部类
        
        class innerClass { // 局部内部类前不加修饰符public或private
            //static int m = 20;//不可以定义静态变量
            public void InPrint() {
                System.out.println(x); // 访问x一定需要final修饰
                System.out.println(age); //访问成员变量不需要final修饰
            }
        }
        new innerClass().InPrint();
    }

    public static void main(String[] args) {
        new OutClass().test1(12);
    }
}
```



- 内部类不会因为定义在方法中就会随着方法的执行完毕而被销毁。
- 这里就会产生问题：当外部类的方法结束时，局部变量就会被销毁了，但是内部类对象可能还存在(只有没有人再引用它时，才会死亡)。这里就出现了一个矛盾：内部类对象访问了一个不存在的变量。为了解决这个问题，就将局部变量复制了一份作为内部类的成员变量，这样当局部变量死亡后，内部类仍可以访问它，实际访问的是局部变量的"**copy**"。这样就好像延长了局部变量的生命周期。我们可以通过反编译生成的 .class 文件来实验：

- 也就是如果我们在内部类中**修改了“copy”，外部类是无法感知的**，这该证明处理呢？

  就将局部变量设置为final，对它初始化后，我就不让你再去修改这个变量，就保证了内部类的成员变量和方法的局部变量的一致性。这实际上也是一种妥协。

- **若变量是final时：**

  - 若是基本类型，其值是不能改变的，就保证了copy与原始的局部变量的值是一样的；
  - 若是引用类型，其引用是不能改变的，保证了copy与原始的变量引用的是同一个对象。

## 非静态（static）内部类为什么不能有静态成员变量和静态方法

- static类型的属性和方法，在类加载的时候就会存在于内存中。
- 内部类的加载，是在外部类 实例化之后才加载的。

> 如果内部类可以用于static方法/变量，说明 任一时间都一个通过 内部类的类型进行调取 static方法/变量，然而内部类的加载，是在外部类 实例化之后才加载的。这显然是前后冲突的，不合理。



## string，stringBuffer，StringBuilder区别和使用场景

- String是final修饰的，不可变的，每次操作都会产生新的String对象；
- StringBuffer和StringBuilder是在源对象上操作，都继承了AbstractStringBuilder
- StringBuffer线程安全（方法被synchronized修饰），StringBuilder线程不安全。
- 性能上：StringBuilder>StringBuffer>String
- 场景：经常改变时候后两个，优先使用StringBuilder；如果多线程使用共享变量，使用StringBuffer



## 重载和重写的区别

- 重载：发生在同一个类中，方法名相同，重载**必须改变**参数列表。 **可以**改变方法类型，访问修饰符
- 重写：发生在父子类中，
  - 方法名，参数列表必须相同。
  - 返回值类型小于等于父类。
  - 抛出异常小于等于父类。
  - 访问修饰符范围大于等于父类（ 如果子类的*访问*权限*范围*更小的话，那么子类中重写以后的方法对于外部对象就不可*访问*了，这个就破坏了继承的含义），如果父类方法修饰符为`private`则子类不能重新。

```java
   /**
   错误重载，编译报错
   */
	public int add(int a ,int b){
        return 1;
    }

    public String add(int a ,int b){
        return "x";
    }
```



## 访问修饰符

| 修饰符      | 当前类 | 同一包内 | 子孙类(同一包) | 子孙类(不同包)                                               | 其他包 |
| :---------- | :----- | :------- | :------------- | :----------------------------------------------------------- | :----- |
| `public`    | Y      | Y        | Y              | Y                                                            | Y      |
| `protected` | Y      | Y        | Y              | Y/N（[说明](https://www.runoob.com/java/java-modifier-types.html#protected-desc)） | N      |
| `default`   | Y      | Y        | Y              | N                                                            | N      |
| `private`   | Y      | N        | N              | N                                                            | N      |

- 接口里的变量都隐式声明为 **public static final**
- 类和接口不能声明为 **private**
- protected**不能修饰类（内部类除外），接口及接口的成员变量和成员方法**

请注意以下方法继承的规则：

- 父类中声明为 public 的方法在子类中也必须为 public。
- 父类中声明为 protected 的方法在子类中要么声明为 protected，要么声明为 public，不能声明为 private。
- 父类中声明为 private 的方法，不能够被子类继承。

## protected 关键字

- 子类中不能 new基类，然后利用基类对象访问基类的protected方法。

## 接口和抽象类

- 抽象类可以存在普通方法，接口只能存在`public abstract`方法；
- 抽象类中的成员变量可以是各种类型的，而接口中的成员变量只能是`public static final`；
- 抽象类只能继承一个，接口可以多实现；

接口：

1. 设计目的：是对类的行为进行约束（能够做什么，有没有这个方法），但是不对 如何实现 进行限制。
2. 接口是对行为的抽象，表达的是like a的关系，比如bird like a Aricraft。但其本质还是鸟。接口的核心是定义行为，即实现类可以做什么，至于实现类主题是谁，如何实现，接口并不需要关系。

抽象类

1. 设计目的：是代码复用，当不同的类型具有某些相同的行为，且其中一部分行为的实现方式一致时，可以让这些类派生出一个抽象类。【是现有子类 后抽离公共部分形成构想类】，所以抽象类不可以被实例化出来（一部分方法是，没有实现的）
2. 抽象类是对类本质的抽象，表达的是is a的关系， 抽象类包含实现子类的通用特性，将子类存在差异化的特性进行抽象，交由子类实现。

使用场景：

- 当关注一个事物的本质时候，使用抽象类；
- 当关注一个操作的时候，使用接口。

区分：

抽象类的功能远远超过接口，但是定义抽象类的代价太大（每个类只能实现一个抽象类，在子类中必须继承，编写出所有子类的所有共性），虽然接口在功能上弱化许多，但是它只是针对一个动作的描述，而且你可以在一个类中实现多个接口，在设计阶段会降低难度。
