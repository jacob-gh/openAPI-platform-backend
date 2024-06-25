# OpenAPI

背景：

1. 前端开发需要用到后端接口

2. 使用现成的系统的功能

   

做一个api开放平台：

1. 防止攻击（安全性）
2. 不能随便调用（限制、开通）
3. 统计调用次数
4. 计费
5. 流量保护
6. API接入

## 项目介绍

做一个提供API接口调用的平台，用户可以注册登录，开通接口调用权限。用户可以使用接口，并且每次调用会进行统计。管理员可以发布接口、下线接口、接入接口，以及可视化的调用情况（数据）



### 业务流程

![image-20240620162553961](C:\Users\jacoe\AppData\Roaming\Typora\typora-user-images\image-20240620162553961.png)

### 技术选型

#### 后端

- Java Spring Boot
- Spring Boot Starter（SDK开发）
- Dubbo（RPC）
- Nacos
- Spring Cloud Gateway（网关，限流，日志实现）

### 需求分析

1. 管理员可以对接口信息进行增删改查
2. 用户可以访问前台，查看接口信息



## 数据库表设计

### 接口信息表

id

name

userId 创建人id

url 接口地址

method 请求类型

requestHeader 请求头

respondHeader 响应头

status 接口状态（0-关闭 1-开启）

isDelete

createTime

updateTime





## 业务实现



### 接口功能实现





#### 调用接口

几种HTTP调用方式：

1. HttpClient
2. RestTemplate
3. 第三方库（OKHTTP、HuTool[Hutool🍬一个功能丰富且易用的Java工具库，涵盖了字符串、数字、集合、编码、日期、文件、IO、加密、数据库JDBC、JSON、HTTP客户端等功能。](https://hutool.cn/)）

```java
private static final String GATEWAY_HOST = "http://localhost:8090";
 public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
```







### API签名认证

#### 本质：

1. 签发签名

   > 可以用户注册时分配签名
   >
   > 扩展：用户可以申请更换签名

2. 校验签名

为什么需要？

1. 保证安全性，不能随便调用（进行限制）



#### 实现：

- **accessKey**：调用的标识 user（尽量复杂、无序、无规律）

- **secretKey**：密钥（该参数不能放到请求头中）

  类似用户名密码，区别：ak/sk无状态，每次请求都必须要有

  **！！！千万不要把密钥直接在服务器之间传递（可能会被拦截，泄露密钥）**

- **用户参数**

- **sign**：签名

  加密方式：对称加密、非对称加密、md5签名（不可解密）

  用户参数+密钥=>签名算法（MD5、HMac、Sha1）=>不可解密的值（怎么确定密钥对不对？服务端用同样的参数和算法去生成签名，校验和客户端传入的签名是否一致）

- **nonce**：随机数

- **timestamp**：时间戳

  **防重放**

  加nonce随机数，只能用一次（服务端要保存用过的随机数）

  加timestamp时间戳，校验时间戳是否过期

```java
public class YuApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    public YuApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
```

API签名认证是一个很灵活的设计，具体有哪些参数，参数名如何一定要根据场景来。比如（userId，appId，version，固定值等）

​	

### 开发一个简单易用的sdk

> 开发者只需关心调用哪些接口、传递哪些参数，就跟调用自己的代码一样简单

开发starter的好处：开发者引入之后，可以直接在application.yml中写配置，自动创建客户端



spring-boot-configuration-professor的作用是自动生成配置的代码提示



创建starter步骤

1. 创建spring-boot项目（引入spring-configuration-professor）

2. 删除配置文件中的以下部分：

   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
               <configuration>
                   <excludes>
                       <exclude>
                           <groupId>org.projectlombok</groupId>
                           <artifactId>lombok</artifactId>
                       </exclude>
                   </excludes>
               </configuration>
           </plugin>
       </plugins>
   </build>
   ```

3. 删除启动类

4. 创建配置类

   ```java
   @Configuration
   @ConfigurationProperties("yuapi.client")//@ConfigurationProperties和@Value注解用于获取配置文件中的属性定义并绑定到Java Bean或属性中
   @Data
   @ComponentScan//这个注解会让spring去扫描某些包及其子包中所有的类，然后将满足一定条件的类作为bean注册到spring容器容器中。
   public class YuApiClientConfig {
   
       private String accessKey;
   
       private String secretKey;
   
       @Bean
       public YuApiClient yuApiClient() {
           return new YuApiClient(accessKey, secretKey);
       }
   
   }
   ```



5. 创建META-INF文件

   ![image-20240624105624239](C:\Users\jacoe\AppData\Roaming\Typora\typora-user-images\image-20240624105624239.png)

   并指定第4步创建的配置类的包引用

6. 打包

   ![image-20240624110334237](C:\Users\jacoe\AppData\Roaming\Typora\typora-user-images\image-20240624110334237.png)

7. 在其他项目中引入该依赖

可以把打好的包发到maven仓库中，这样其他人就可以通过引入依赖的方式来使用该sdk



### 开发接口发布/上线的功能（管理员）

发布接口：

1. 校验该接口是否存在
2. 判断该接口是否可以调用
3. 修改接口数据库中的状态字段为1



下线接口：

1. 校验接口是否存在

2. 修改接口数据库中的状态字段为0

   

### 在线调用接口

请求参数的类型（直接用json格式，更灵活）：

``` json
[
    {"name":"username","type":"string"}
]
```



调用流程：

![image-20240624151811678](C:\Users\jacoe\AppData\Roaming\Typora\typora-user-images\image-20240624151811678.png)

1. 前端将用户输入的请求参数和要测试的接口id发给后端
2. 在调用前可以做一些校验
3. 后端去调用模拟接口
