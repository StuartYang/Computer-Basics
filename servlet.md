## 生命周期

- Servlet 初始化后调用 **init ()** 方法。要求一个Servlet 做一件事情，职能单一化。
- Servlet 调用 **service()** 方法来处理客户端的请求。
- Servlet 销毁前调用 **destroy()** 方法。
- 最后，Servlet 是由 JVM 的垃圾回收器进行垃圾回收的。

### init() 方法 

> 1. `Servlet 创建`于用户第一次调用对应于该 Servlet 的 URL 时，但是您也可以指定 Servlet 在服务器第一次启动时被加载。
> 2. init 方法被设计成只调用一次;它在`第一次创建 Servlet 时被调用`，在后续每次用户请求时不再调用;
> 3. init() 方法简单地创建或加载一些数据，这些数据将被用于 Servlet 的整个生命周期。

init 方法的定义如下：

```java
public void init() throws ServletException {
  // 初始化代码...
}
```

#### service() 方法

> 1. service() 方法是执行实际任务的主要方法。Servlet 容器（即 Web 服务器）调用 service() 方法来处理来自客户端（浏览器）的请求，并把格式化的响应写回给客户端。
> 2. 每次服务器接收到一个 Servlet 请求时，服务器会产生一个新的线程并调用服务。service() 方法检查 HTTP 请求类型（GET、POST、PUT、DELETE 等），并在适当的时候调用 doGet、doPost、doPut，doDelete 等方法。
> 3. service() 方法由容器调用，service 方法在适当的时候调用 doGet、doPost、doPut、doDelete 等方法。所以，您不用对 service() 方法做任何动作，您只需要根据来自客户端的请求类型来重写 doGet() 或 doPost() 即可。

```java
public void service(ServletRequest request, 
                    ServletResponse response) 
      throws ServletException, IOException{
}
```

#### doGet() 方法

> GET请求，或者来自于一个**未指定 METHOD 的 HTML 表单**，它由 doGet() 方法处理。

```java
public void doGet(HttpServletRequest request,
                  HttpServletResponse response)
    throws ServletException, IOException {
    // Servlet 代码
}
```

#### doPost() 方法

> POST 请求来自于一个**特别指定了 METHOD 为 POST 的 HTML 表单**，它由 doPost() 方法处理。
>
> post将信息作为一个单独的消息。消息以标准输出的形式传到后台程序

```java
public void doPost(HttpServletRequest request,
                   HttpServletResponse response)
    throws ServletException, IOException {
    // Servlet 代码
}
```

#### destroy() 方法

> 1. destroy() 方法只会被调用一次，在 Servlet 生命周期结束时被调用。
> 2. destroy() 方法可以让您的 Servlet 关闭数据库连接、停止后台线程、把 Cookie 列表或点击计数器写入到磁盘，并执行其他类似的清理活动。
> 3. 在调用 destroy() 方法之后，servlet 对象被标记为垃圾回收。

```java
  public void destroy() {
    // 终止化代码...
  }
```

![image-20220211152804672](servlet.assets/image-20220211152804672.png) 	



## http请求

![img](https://www.runoob.com/wp-content/uploads/2014/07/40F2D53A-2831.png)

| 头信息          | 描述                                                         |
| :-------------- | :----------------------------------------------------------- |
| User-Agent      | 这个头信息识别发出请求的浏览器或其他客户端，并可以向不同类型的浏览器返回不同的内容。 |
| Accept-Charset  | 浏览器可以用来显示信息的字符集。例如 ISO-8859-1。。          |
| Accept-Encoding | 这个头信息指定浏览器知道如何处理的编码类型。值 **gzip** 或 **compress** 是最常见的两种可能值。 |

## http响应

| 头信息           | 描述                                                         |
| :--------------- | :----------------------------------------------------------- |
| Content-Encoding | 不是字符集，是编码类型。例如：**gzip**                       |
| Content-Type     | text/html; charset=utf-8                                     |
| Content-Length   | 响应中的字节数。只有当浏览器使用**持久（keep-alive）HTTP 连接时才需要**这些信息。 |

## 常见状态码

| 代码 | 消息      | 描述                                                         |
| :--- | :-------- | :----------------------------------------------------------- |
| 302  | Found     | 所请求的页面重定向到一个新的 URL。                           |
| 403  | Forbidden | 服务器接收到了请求，但是拒绝提供服务，服务器通常会提供原因。 |

## Servlet的继承关系

![image-20220212095557463](servlet.assets/image-20220212095557463.png)

- 一个Servlet就是一个进程，一个请求就是一个线程；

- ServletRequest和ServletResponse是一般的请求和响应，说明哪怕不是http类型也行。
- HttpServletRequest和HttpServletResponse是http的请求和响应，使用必须是http类型的Request，Response。

## get请求

主要方法：

|              方法              |                 描述                 |
| :----------------------------: | :----------------------------------: |
| setCharacterEncoding("utf-8"); | post请求设置 ；tomcat7.0以下 get设置 |
|     getParameter("name");      |           拿string类型的值           |
|   setAttribute("key",value);   |      转向中可以存储任意类型的值      |
|      getAttribute("key");      |      转向中可以获取任意类型的值      |

## Get请求乱码(tomcat7.0以下存在的问题)

Tomcat/conf 目录下 server.xml

```xml
<Connector port="8080" protocol="HTTP/1.1"   
    connectionTimeout="20000"   
    redirectPort="8444"   
    useBodyEncodingForURI="true" URIEncoding="UTF-8"/>  
```

```java
    /**
    tomcat7.0 get乱码修复
    */
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)  {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        // 或者不指定setCharacterEncoding
                String name = new String(request.getParameter("name").getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);

        System.out.println(name);
    }
```



- useBodyEncodingForURI参数表示是否用`request.setCharacterEncoding`参数对URL提交的数据和表单中GET方式提交的数据进行重新编码，在默认情况下，该参数为false。
- URIEncoding参数指定对**所有GET方式**请求进行统一的重新编码（解码）的编码。
- tomcat7以上的版本现在的get编码为utf-8，即 request.getParameter("name")即可

## post请求乱码问题（目前所有版本的tomcat【目前使用9.0】）

由于客户端是utf-8，所以服务器页需要设置为utf-8编码进行接收。

```java
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        System.out.println(name+"-"+password);
    }
```

- 在dopost中修改` request.setCharacterEncoding("utf-8");`
- 不需要修改tomcat配置

## 响应HttpServletResponse

主要方法：

|                 方法                 |                描述                |
| :----------------------------------: | :--------------------------------: |
|        setHeader(name, value)        |           设置响应信息头           |
|     setContentType(String type)      | 设置响应文件类型，响应式的编码格式 |
| setCharacterEncoding(String charset) |  设置服务器响应内容字符集编码格式  |
|             getWriter()              |           获取字符输出流           |

```java
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.println("注册成功");
    }
```



## 响应乱码

响应乱码的原因：tomcat是iso8859-1的字符集编码

```java
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // response.setCharacterEncoding("utf-8"); //设置服务器传送的字符集编码
        // response.setHeader("Content-Type","text/html;charset=utf-8"); //设置响应界面的字符集编码
        response.setContentType("text/html;charset=utf-8"); //可以同时设置服务器传送的字符集编码 和 响应界面的字符集编码
        PrintWriter printWriter = response.getWriter(); // 设置字符集要在输出流之前
        printWriter.println("注册成功");
    }

```

## 转发

> 转发发生在servlet的服务器端，将请求发送给服务器上的其他资源，以共同完成一次请求的处理。

```j
 request.getRequestDispatcher("/showPath").forward(request,response);
```

- 使用forward跳转时，是在服务器内部跳转的，地址栏不发生变化，属于同一请求。
- forward表示一次请求，是在服务器内部跳转，可以共享同一次request作用域中的数据。
  - request作用域：拥有存储数据的空间，可以存储**任何数据**
  - 存数据：`request.setAttribute(key,value);`
  - 取数据：`request.getAttribute(key);`
- 转发的特点：
  - 转发是服务器行为
  - 转发是浏览器只做了一次请求
  - 转发浏览器地址不变
  - 转发只能将请求发给**同一个web应用**中的组件

![image-20220212115631589](servlet.assets/image-20220212115631589.png)

## 重定向

> 重定向发生在客户端，客户端请求发送服务端后，服务器响应给客户端一个新的请求地址，客户端更新发送请求。

```java
response.sendRedirect("/showPath?username=张三&password=12345");
```

- 目标地址：如果是本项目-/项目名/路径 ； 如果是其他web项目-http://域名/请求
- 地址栏发生了变化
- 只能传输get请求，get参数（string字串）
- 重定向两次传输的request信息会丢失

![image-20220212115638868](servlet.assets/image-20220212115638868.png)
