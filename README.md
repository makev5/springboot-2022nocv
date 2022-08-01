## 一、基于Springboot+MybatisPlus+Echarts+Mysql实现校园疫情管理系统

【**Coding路人王：从0到1**】

**项目简介：**

​	该项目基于springboot框架实现了疫情背景下的校园管理，主要涵盖了中国疫情确诊分布地图(对接腾讯API接口)、中国实时疫情新闻播报、以及对疫情数据的饼图、折线图和柱状图展示。系统角色可以进行增删改查，为角色分配菜单权限，大致分为学生、教师、院系级系统管理员，集成了shiro框架实现了不同的角色可以赋予不同的菜单栏权限，可以完成菜单栏的动态增删改查，实现了动态的权限设置。

​	在校园疫情数据管理中，基于mybatiplus框架实现了疫情数据的带有条件查询及分页的增删改查，并实现了拖拽式上传excel数据和导出疫情数据。其中相关功能主要包含疫情数据管理，疫情新闻管理，疫情图表展示管理，学生健康打卡管理，院系管理、班级管理、核酸检测管理，疫苗接种管理，学生请假管理等等。系统管理中主要包含了用户管理、角色管理和菜单管理。

​	在数据库设计中，主要设计了用户与角色之间的多对多实体关系，角色与菜单之间的多对多关系设计。其中，学生请假功能设计了详细的审批流，学生提交审批流后，教师审批后方为院系审批，为串行审批流程，设计了审批节点的状态与审批流的审批逻辑。

​	在缓存设计中，主要将访问量较高的中国疫情地图和新闻播报首页进行了redis数据缓存，为了保证redis缓存与mysql数据的一致性，每当定时任务触发去解析腾讯API接口数据时候，每更新一次数据库数据，就要删除一次redis缓存，这样就可以保证客户端每次查询数据查不到就可以进行访问数据库更新最新的缓存数据。

​	在接口设计中，采用了RestFul风格的架构，使请求路径变得更加简洁，传递、获取参数值更加方便，通过请求路径中直接传递参数值，不会暴露传递给方法的参数变量名，接口也变得更加安全。

**开发环境：**

**Java：**JDK1.8

**开发工具：**Idea、Navicat、Maven3.6

**前端框架：**layui框架

**后端框架：**Springboot、MybatisPlus、Shiro、Httpclient爬虫

**数据库：**Mysql5.7及其以上

源码:
讲解地址：https://www.bilibili.com/video/BV1aY411c7d1?share_source=copy_web
### 1.1 构建springboot项目

```xml
	<dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-boot-starter</artifactId>
      <version>3.1.1</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.47</version>
    </dependency>
```

```
spring:
 datasource:
  username: root
  password: 123456
  url: jdbc:mysql://localhost:3306/nocv?serverTimezone=UTC&useSSL=false&characterEncoding=utf-8
  driver-class-name: com.mysql.jdbc.Driver
```

### 1.2 引入Echarts地图

1.官网：https://echarts.apache.org/zh/ 下载JS文件引入项目
2.查看图例
3.快速使用

```html
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8" />
  <!-- 引入刚刚下载的 ECharts 文件 -->
  <script src="echarts.js"></script>
 </head>
</html>
```



```html
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8" />
  <title>ECharts</title>
  <!-- 引入刚刚下载的 ECharts 文件 -->
  <script src="echarts.js"></script>
 </head>
 <body>
  <!-- 为 ECharts 准备一个定义了宽高的 DOM -->
  <div id="main" style="width: 600px;height:400px;"></div>
  <script type="text/javascript">
   // 基于准备好的dom，初始化echarts实例
   var myChart = echarts.init(document.getElementById('main'));

   // 指定图表的配置项和数据
   var option = {
    title: {
     text: 'ECharts 入门示例'
    },
    tooltip: {},
    legend: {
     data: ['销量']
    },
    xAxis: {
     data: ['衬衫', '羊毛衫', '雪纺衫', '裤子', '高跟鞋', '袜子']
    },
    yAxis: {},
    series: [
     {
      name: '销量',
      type: 'bar',
      data: [5, 20, 36, 10, 10, 20]
     }
    ]
   };
   // 使用刚指定的配置项和数据显示图表。
   myChart.setOption(option);
  </script>
 </body>
</html>

```

**地图社区图例**：http://www.isqqw.com/

### 1.3 创建数据库

```sql
DROP TABLE IF EXISTS `nocv_data`;
CREATE TABLE `nocv_data` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `name` varchar(255) DEFAULT NULL,
 `value` int(11) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of nocv_data
-- ----------------------------
INSERT INTO `nocv_data` VALUES ('1', '澳门', '95');
INSERT INTO `nocv_data` VALUES ('2', '香港', '35');
INSERT INTO `nocv_data` VALUES ('3', '台湾', '153');
INSERT INTO `nocv_data` VALUES ('4', '新疆', '56');
INSERT INTO `nocv_data` VALUES ('5', '宁夏', '26');
INSERT INTO `nocv_data` VALUES ('6', '青海', '26');
```

### 1.4 编写代码

springboot

contRoller:  /query

service:

dao:

entity:

### 1.5 展示数据

```
$.ajax({
    url: "/query",
    dataType: "json",
    success: function (data) {
      // 某种意义上来说，数组也是object
      for (let i in data) {
        dataList[i] = data[i];
      }

      myChart.setOption({
        series: [
          {
            name: "确诊病例",
            type: "map",
            geoIndex: 0,
            data: dataList
          }
       ]
      });
    }
  });

```
## 七、中国疫情地图增删改查

#### 7.1 分页配置MybatisPlusConfig

```java
@Configuration
@ConditionalOnClass(value = {PaginationInterceptor.class})
public class MybatisPlusConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return  new PaginationInterceptor();
    }
}
```

#### 7.2 layui返回的数据格式 DataView

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataView {
    private Integer code = 0;
    private String msg = "";
    private Long count = 0L;
    private Object data;

    public DataView(Long count,Object data){
        this.count = count;
        this.data = data;
    }

    public DataView(Object data){
        this.data = data;
    }
}
```

## 八、疫情打卡上报管理

### 8.1 引入静态打卡HTML页面



## 九、实现拖拽Excel导入疫情数据功能

### 9.1 引入pom依赖

```xml
<!--引入poi-->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>4.0.0</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>4.0.0</version>
</dependency>
```

### 9.2 引入layui 的拖拽上传Excel组件

```
* Excel的拖拽或者点击上传
* 1.前台页面发送一个请求，上传文件MutilpartFile HTTP
* 2.Controller,上传文件MutilpartFile 参数
* 3.POI 解析文件，里面的数据一行一行全部解析出来
* 4.每一条数据插入数据库
* 5.mybatiplus 批量saveBatch(list)
```

```html
<div class="layui-upload-drag" id="test10">
    <i class="layui-icon"></i>
    <p>点击上传，或将文件拖拽到此处</p>
    <div class="layui-hide" id="uploadDemoView">
        <hr>
        <img src="" alt="上传成功后渲染" style="max-width: 196px">
    </div>
</div>

layui.use(['upload','jquery'],function(){
        var layer = layui.layer //弹层
            ,$ = layui.jquery
            ,upload = layui.upload
            
//拖拽上传
        upload.render({
            elem: '#test10'
            ,url: '/excelImport'
            ,accept: 'file' //普通文件
            ,done: function(res){
                layer.msg('上传成功');
                console.log(res);
            }
        });
```

### 9.2 编写Controller

```java
// Excel数据导入
@RequestMapping(value = "/excelImport", method = RequestMethod.POST)
@ResponseBody
public DataView uploadExcel(@RequestParam("file") MultipartFile file) {
   
    DataView dataView = new DataView();
    if (file.isEmpty()) {
        dataView.setMsg("文件为空");
        return dataView;
    }
    try {

        //根据路径获取这个操作excel的实例
        HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = wb.getSheetAt(0);

        //实体类集合
        List<NocvData> listData = new ArrayList<>();
        HSSFRow row = null;

        //循环sesheet页中数据从第二行开始，第一行是标题
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {

            //获取每一行数据
            row = sheet.getRow(i);
            NocvData data = new NocvData();
            data.setName(row.getCell(0).getStringCellValue());
            data.setValue(Integer.valueOf((int) row.getCell(1).getNumericCellValue()));
            listData.add(data);

        }

        //循环展示导入的数据，实际应用中应该校验并存入数据库
        indexService.saveBatch(listData);
        dataView.setCode(200);
        dataView.setMsg("导入成功");
        return dataView;
    } catch (Exception e) {
        e.printStackTrace();
    }
    dataView.setCode(100);
    dataView.setMsg("导入失败");
    return dataView;
}
```

### 9.4 数据导出Excel功能【中国疫情数据】



1.前端发送请求  /

2.后端查询数据库，封装数据Excel实体

3.返回数据建立输出，写出浏览器文件



```java
// 导出疫情数据
form.on("submit(doExport)",function () {
    window.location.href="/excelOutportChina";//这里是接口的地址
})

<button type="button" class="layui-btn layui-btn-sm layui-btn-radius" lay-submit="" lay-filter="doExport"><i class="layui-icon layui-icon-search layui-icon-normal"></i>导出中国疫情数据Excel
</button>
                
                
                
@RequestMapping("/excelOutportChina")
    @ResponseBody
    public void excelOutportChina(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        List<NocvData> list = indexService.list();
        HSSFWorkbook wb = new HSSFWorkbook();
        //2-创建sheet页,设置sheet页的名字
        HSSFSheet sheet = wb.createSheet("中国数据表");
        //3-创建标题行
        HSSFRow titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("城市名称");
        titleRow.createCell(1).setCellValue("确诊数量");
        //4-遍历将数据集合将数据放到对应的列中
        for (NocvData data : list){
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum()+1);
            dataRow.createCell(0).setCellValue(data.getName());
            dataRow.createCell(1).setCellValue(data.getValue());
        }
        // 5.建立输出
        OutputStream os = null;

        try{
            //6-设置Excel的名称
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String("中国疫情数据表".getBytes(),"iso-8859-1") + ".xls");
            os = response.getOutputStream();
            wb.write(os);
            os.flush();
        }catch(Exception e){
            e.printStackTrace();
            
        } finally {
            try {
                if(os != null){
                    os.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
```
## 十、对接腾讯API接口实现疫情数据实时更新

### 十、打开腾讯数据网址[腾讯实时疫情](https://news.qq.com/zt2020/page/feiyan.htm#/global)

**主页网址：**https://news.qq.com/zt2020/page/feiyan.htm#/global

![1654132815620](C:\Users\15067\AppData\Local\Temp\1654132815620.png)

**腾讯数据接口：**https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5

**网易数据接口：**https://c.m.163.com/ug/api/wuhan/app/data/list-total

### 10.1 网络爬虫对接 【腾讯】API接口

```xml
<!--httpClient客户端-->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.56</version>
</dependency>
```

```java
@Component
public class HttpUtils {

    @Bean
    public String getData() throws IOException {
        //请求参数设置
        RequestConfig requestConfig = RequestConfig.custom()
                //读取目标服务器数据超时时间
                .setSocketTimeout(10000)
                //连接目标服务器超时时间
                .setConnectTimeout(10000)
                //从连接池获取连接的超时时间
                .setConnectionRequestTimeout(10000)
                .build();

        CloseableHttpClient httpClient = null;
        HttpGet request = null;
        CloseableHttpResponse response = null;
        try {
            //创建HttpClient
            httpClient = HttpClients.createDefault();
            //使用url构建get请求
            request = new HttpGet("https://c.m.163.com/ug/api/wuhan/app/data/list-total");
            //填充请求设置
            request.setConfig(requestConfig);
            //发送请求，得到响应
            response = httpClient.execute(request);

            //获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            //状态码200 正常
            if (statusCode == 200) {
                //解析响应数据
                HttpEntity entity = response.getEntity();
                //字符串格式数据
                String string = EntityUtils.toString(entity, "UTF-8");
                System.out.println("字符串格式：" + string);
                return string;

            } else {
                throw new HttpResponseException(statusCode, "响应异常");
            }
        } finally {
            if (response != null) {
                response.close();
            }
            if (request != null) {
                request.releaseConnection();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        }

    }

}
```

### 10.2 对接定时程序数据入库

```java
@Controller
public class TengXunApi {

    public static void main(String[] args) throws Exception {
        HttpUtils httpUtils = new HttpUtils();
        String string = httpUtils.getData();
        System.out.println("string:"+string);
        //json格式数据
        JSONObject jsonObject = JSONObject.parseObject(string);
        Object data = jsonObject.get("data");

        System.out.println(data.toString());
        System.out.println("====================================");
        //=========================
        JSONObject jsonObject1 = JSONObject.parseObject(data.toString());
        ChinaTotal chinaTotal = (ChinaTotal) jsonObject1.get("chinaTotal");
        System.out.println(chinaTotal);


    }
}
```
### 10.3 对接腾讯API实现省份数据的自动刷新





![1654302358239](C:\Users\15067\AppData\Local\Temp\1654302358239.png)

![1654302429447](C:\Users\15067\AppData\Local\Temp\1654302429447.png)



![1654303017808](C:\Users\15067\AppData\Local\Temp\1654303017808.png)







```
// 各个省份的数据
// 3.世界各个国家及地区的所有数据
Object areaTree = jsonObjectData.get("areaTree");
System.out.println("areaTree:"+areaTree);

JSONArray areaTree1 = jsonObjectData.getJSONArray("areaTree");
Object[] objects = areaTree1.toArray();
// 所有国家的名字
for (int i = 0; i < objects.length; i++) {
    JSONObject jsonObject1 = JSONObject.parseObject(objects[i].toString());
    Object name = jsonObject1.get("name");
    //System.out.println(name);
}

// 数组中第三个为中国省份数据
JSONObject jsonObject1 = JSONObject.parseObject(objects[2].toString());
JSONArray children1 = jsonObject1.getJSONArray("children");
Object[] objects1 = children1.toArray();

// 遍历中国地区的数据
List<NocvData> list = new ArrayList<>();
SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

for (int i = 0; i < objects1.length; i++) {
    NocvData nocvData = new NocvData();
    JSONObject jsonObject2 = JSONObject.parseObject(objects1[i].toString());
    Object name = jsonObject2.get("name");//省份名称
    Object total2 = jsonObject2.get("total");
    JSONObject totalJson = JSONObject.parseObject(total2.toString());
    Object confirm2 = totalJson.get("confirm");//quzhen数量
    System.out.println(name+"："+confirm2);
    //获取省份更新的时间
    Object extData = jsonObject2.get("extData");
    JSONObject extDataJson = JSONObject.parseObject(extData.toString());
    Object lastUpdateTime1 = extDataJson.get("lastUpdateTime");
    //封装数据
    nocvData.setName(name.toString());
    nocvData.setValue(Integer.parseInt(confirm2.toString()));
    String s = String.valueOf(lastUpdateTime1);
    if (lastUpdateTime1 == null){

        nocvData.setUpdateTime(new Date());

    }else {

        nocvData.setUpdateTime(format2.parse(s));
    }

    list.add(nocvData);
}

// 插入数据库各个省份的数据
indexService.saveBatch(list);
```
## 十一、完成系统登录和验证码

### 11.1 简单登录过程

前台：User：   username  password    【**javabean**】

后台：SQL：select * from user where username = ? and password = ?

USER: ID   USERNAME  PASSWORD IMG ROLE  BANJI

**session** 浏览器第一次访问程序服务端，会产生一个session,会帮你生成一个唯一的ID 【缓存、数据库】

浏览器：cookies, sessionid   -------   sesssion

缺点：

1.服务端压力【存储】

session保存在服务端，一个用户保存，百万级别的用户，都不保存在服务端。 session sessionid，浏览器保存id

2.局限性，浏览器禁用cookie



问题：user信息

token: 没有状态，username password  **字符串**

### 11.2 Shiro框架

登录：认证，，授权

**认证**：登录，学生---》学校 大门口进门

**授权：**学生=男生  【男生宿舍 男生厕所】

**role【学生 老师 管理员】**

学生：查看 

老师：修改

管理员：删除

### 11.3 验证码比较难？

1.先要判断验证码对不对？

2.username password  SQL

3.SHIRO  权限【角色】

吃饭时候：跑外面 砍一棵大树  一双筷子

使用。

### 11.2 验证码逻辑

### 推荐

```xml
		<!--hutool-->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>4.6.8</version>
        </dependency>
```

```
@RequestMapping("/getCode")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
        //HuTool定义图形验证码的长和宽,验证码的位数，干扰线的条数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 36,4,10);
        session.setAttribute("code",lineCaptcha.getCode());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            lineCaptcha.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

### 不推荐 麻烦

```java
@WebServlet("/checkCode")
public class CheckCodeServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		//服务器通知浏览器不要缓存
		response.setHeader("pragma","no-cache");
		response.setHeader("cache-control","no-cache");
		response.setHeader("expires","0");
		
		//在内存中创建一个长80，宽30的图片，默认黑色背景
		//参数一：长
		//参数二：宽
		//参数三：颜色
		int width = 80;
		int height = 30;
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		
		//获取画笔
		Graphics g = image.getGraphics();
		//设置画笔颜色为灰色
		g.setColor(Color.GRAY);
		//填充图片
		g.fillRect(0,0, width,height);
		
		//产生4个随机验证码，12Ey
		String checkCode = getCheckCode();
		//将验证码放入HttpSession中
		request.getSession().setAttribute("CHECKCODE_SERVER",checkCode);
		
		//设置画笔颜色为黄色
		g.setColor(Color.YELLOW);
		//设置字体的小大
		g.setFont(new Font("黑体",Font.BOLD,24));
		//向图片上写入验证码
		g.drawString(checkCode,15,25);
		
		//将内存中的图片输出到浏览器
		//参数一：图片对象
		//参数二：图片的格式，如PNG,JPG,GIF
		//参数三：图片输出到哪里去
		ImageIO.write(image,"PNG",response.getOutputStream());
	}
	/**
	 * 产生4位随机字符串 
	 */
	private String getCheckCode() {
		String base = "0123456789ABCDEFGabcdefg";
		int size = base.length();
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for(int i=1;i<=4;i++){
			//产生0到size-1的随机值
			int index = r.nextInt(size);
			//在base字符串中获取下标为index的字符
			char c = base.charAt(index);
			//将c放入到StringBuffer中去
			sb.append(c);
		}
		return sb.toString();
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request,response);
	}
}
```

## 十一、 集成Shiro完成登录和资源控制

### 11.1 简介

**登陆之前：除了登录页面和静态资源之外全部拦截掉**

**Subject****：**主体，应用代码直接交互的对象是 Subject，也就是说 Shiro 的对外 API 核心就是 Subject , 代表了当前“用户”，这个用户不一定是一个具体的人，与当前应用交互的任何东西都是Subject，如网络爬虫，机器人等；即一个抽象概念；所有Subject都绑定到SecurityManager，与Subject的所有交互都会委托给SecurityManager；可以把Subject认为是一个门面；SecurityManager才是实际的执行者；

**SecurityManager****：**安全管理器；即所有与安全有关的操作都会与SecurityManager交互；且它管理着所有Subject；可以看出它是Shiro的核心，它负责与后边介绍的其他组件进行交互，如果学习过SpringMVC，你可以把它看成DispatcherServlet前端控制器；

**Realm****：**域，Shiro从从Realm获取安全数据（如用户、角色、权限），就是说SecurityManager要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作；可以把Realm看成DataSource，即安全数据源。



### 11.2 pom依赖

```xml
<!--shiro-->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.2</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.4.2</version>
</dependency>
<!--shiro和thymeleaf集成的扩展依赖，为了能在页面上使用xsln:shiro的标签-->
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version>
</dependency>
```



### 11.3 编写UserRealm 

```java
public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }


    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //1.查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",token.getPrincipal().toString());
        User user = userService.getOne(queryWrapper);
        if (null != user){
            //盐 时用户uuid生成的
            //ByteSource salt = ByteSource.Util.bytes(user.getSalt());
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,user.getPassword(),this.getName());
            return info;
        }
        return null;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        return null;
    }


}
```

### 11.4 ShiroConfig

```java
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(value = { SecurityManager.class })
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroAutoConfiguration {

    private static final String SHIRO_DIALECT = "shiroDialect";
    private static final String SHIRO_FILTER = "shiroFilter";
    // 加密方式
    private String hashAlgorithmName = "md5";
    // 散列次数
    private int hashIterations = 2;
    // 默认的登陆页面
    private String loginUrl = "/index.html";

    private String[] anonUrls; // 放行的路径
    private String logOutUrl; // 登出的地址
    private String[] authcUlrs; // 拦截的路径

    /**
     * 声明凭证匹配器
     */
    /*@Bean("credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(hashAlgorithmName);
        credentialsMatcher.setHashIterations(hashIterations);
        return credentialsMatcher;
    }*/

    /**
     * 声明userRealm
     */
    @Bean("userRealm")
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        return userRealm;
    }

    /**
     * 配置SecurityManager
     */
    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入userRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 配置shiro的过滤器
     */
    @Bean(SHIRO_FILTER)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);
        // 设置未登陆的时要跳转的页面
        factoryBean.setLoginUrl(loginUrl);
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        // 设置放行的路径
        if (anonUrls != null && anonUrls.length > 0) {
            for (String anon : anonUrls) {
                filterChainDefinitionMap.put(anon, "anon");
                System.out.println(anon);
            }
        }
        // 设置登出的路径
        if (null != logOutUrl) {
            filterChainDefinitionMap.put(logOutUrl, "logout");
        }
        // 设置拦截的路径
        if (authcUlrs != null && authcUlrs.length > 0) {
            for (String authc : authcUlrs) {
                filterChainDefinitionMap.put(authc, "authc");
            }
        }
        Map<String, Filter> filters=new HashMap<>();
        factoryBean.setFilters(filters);
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return factoryBean;
    }

    /**
     * 注册shiro的委托过滤器，相当于之前在web.xml里面配置的
     * @return
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<DelegatingFilterProxy>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName(SHIRO_FILTER);
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

   /* 加入注解的使用，不加入这个注解不生效--开始 */
    /**
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
   /* 加入注解的使用，不加入这个注解不生效--结束 */

    /**
     * 这里是为了能在html页面引用shiro标签，上面两个函数必须添加，不然会报错
     *
     * @return
     */
    @Bean(name = SHIRO_DIALECT)
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
```

### 11.5 static下的静态index.html跳转页面

```
<!--用来跳转-->
<script type="text/javascript">
    window.location.href="/toLogin";
</script>
```

### 11.6 编写yml配置过滤路径

```xml
#shiro的配置
shiro:
  anon-urls:
    - /toLogin*
    - /login.html*
    - /login/login
    - /login/getCode
    - /css/**
    - /echarts/**
    - /images/**
    - /layui/**
  login-url: /index.html
  log-out-url: /login/logout*
  authc-ulrs:
    - /**
```
## 十二、设计菜单、角色、班级、学院、老师等数据库设计

### 12.1 数据库设计

**1.menu 菜单**

| id   | pid  | type | title  | premission  | icon | href  | open | ordernum | available |
| ---- | ---- | ---- | ------ | ----------- | ---- | ----- | ---- | -------- | --------- |
| 1    | 0    | menu | 疫管理 | menu:select |      | /menu | 1    | 1        | 1         |
| 2    | 1    | menu | 饼图   | menu:select |      | /pie  | 0    | 2        | 1         |

**2.role 角色**

| id   | name       | remark       |
| ---- | ---------- | ------------ |
| 1    | 超级管理员 | 拥有所有权限 |
| 2    | 老师       | 查看新增修改 |
| 3    | 学生       | 查看         |

**3.role_menu 关联关系表**

| rid  | mid  |
| ---- | ---- |
| 1    | 1    |
| 1    | 2    |

**4.user 用户表【老师，学生，管理员】**

| id   | username | password | ...  | role_id | ban_ji_id | xue_yuan_id | teacher_id |
| ---- | -------- | -------- | ---- | ------- | --------- | ----------- | ---------- |
| 1    | admin    | 123456   |      | 1       | 1         | 1           | 0          |

![1654489985017](C:\Users\15067\AppData\Local\Temp\1654489985017.png)

**5.ban_ji 班级表**

| id   | name        | xue_yuan_id |
| ---- | ----------- | ----------- |
| 1    | 软件工程1班 | 1           |

![1654490055131](C:\Users\15067\AppData\Local\Temp\1654490055131.png)

**6.xue_yuan学院表**

| id   | name     |
| ---- | -------- |
| 1    | 计算机系 |

### 12.2 Java实体编写

## 十三、 修改index主题样式、菜单的增删改查

### 13.1 【dtree属性菜单和下拉实现查询所有】

![1654494627502](C:\Users\15067\AppData\Local\Temp\1654494627502.png)



```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

### 13.1 菜单的插入

TreeNode

```java
package com.example.demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:
 * @Date: 2019/11/22 15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeNode {

    private Integer id;
    @JsonProperty("parentId") //返回的json的名称 parentId ,为了确定层级关系
    private Integer pid;
    private String title;
    private String icon;
    private String href;
    private Boolean spread;
    private List<TreeNode> children = new ArrayList<TreeNode>();

    /**
     * 0为不选中  1为选中
     */
    private String checkArr="0";

    /**
     * 首页左边导航菜单的构造器
     */
    public TreeNode(Integer id, Integer pid, String title, String icon, String href, Boolean spread) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.icon = icon;
        this.href = href;
        this.spread = spread;
    }

    /**
     * 部门 dtree的构造器
     * @param id id
     * @param pid 父亲parentId
     * @param title 名称
     * @param spread 是否展开
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
    }

    /**
     * 给角色分配权限的构造器
     */
    public TreeNode(Integer id, Integer pid, String title, Boolean spread, String checkArr) {
        this.id = id;
        this.pid = pid;
        this.title = title;
        this.spread = spread;
        this.checkArr = checkArr;
    }
}
```

TreeBuilder

```java
public class TreeNodeBuilder {
    public static List<TreeNode> build(List<TreeNode> treeNodes, Integer topPid) {
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        for (TreeNode n1 : treeNodes) {
            if (n1.getPid()==topPid){
                nodes.add(n1);
            }
            for (TreeNode n2 : treeNodes) {
                if (n1.getId()==n2.getPid()){
                    n1.getChildren().add(n2);
                }
            }
        }
        return nodes;
    }
}
```

### 13.2 dtree菜单下拉回显和展示所有

**父级菜单ID为：0【必填】**

### 13.3 菜单栏的编辑

```java
menuService.updateById(menu);
```

### 13.4 菜单栏的删除

删除逻辑的时候：

```java
@RequestMapping("/checkMenuHasChildrenNode")
    @ResponseBody
    public Map<String,Object> checkChildrenNode(Menu menu){
        Map<String,Object> map = new HashMap<>();
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",menu.getId());
        List<Menu> list = menuService.list(queryWrapper);
        if (list.size()>0){
            map.put("value",true);
        }else {
            map.put("value",false);

        }
        return map;
    }
```

1.子类ID，不能删除

2.没有子类ID，直接删掉

![1654516334725](C:\Users\15067\AppData\Local\Temp\1654516334725.png)

真正的删除

```java
@RequestMapping("/deleteMenu")
@ResponseBody
public DataView deleteMenu(Menu menu){
    menuService.removeById(menu.getId());
    DataView dataView = new DataView();
    dataView.setCode(200);
    dataView.setMsg("删除菜单成功！");
    return dataView;
}
```

![1654521334106](C:\Users\15067\AppData\Local\Temp\1654521334106.png)

### 13.5 修改index主菜单栏为动态查库

![1654561883304](C:\Users\15067\AppData\Local\Temp\1654561883304.png)

**1.修改样式 引入 js css**

**2.配置yml放行js包**

**3.原项目修改index.html 为 china.html     删除 commonmenu.html  引入 静态资源包里面的 index.html**

**4.去掉其它页面 的 引入，添加 <body class="childrenBody">**

去掉

```
<!--layui公共模块-->
<div th:include="commonmenu :: menu"></div>

class="layui-body"
```

**5.修改 indexcontroller 的请求/路径，添加一个/toChina**

**6.修改数据库 /toChina**

**7.编写Controller**

```java
/**
 * 加载最外层index菜单
 */
@RequestMapping("loadIndexLeftMenuJson")
@ResponseBody
public DataView loadIndexLeftMenuJson(Menu permissionVo){
    //查询所有菜单
    QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
    List<Menu> list = menuService.list();

    List<TreeNode> treeNodes = new ArrayList<>();
    for (Menu p : list) {
        Integer id =p.getId();
        Integer pid = p.getPid();
        String title = p.getTitle();
        String icon = p.getIcon();
        String href = p.getHref();
        Boolean spread = p.getOpen().equals(1)?true:false;
        treeNodes.add(new TreeNode(id,pid,title,icon,href,spread));
    }

    //构造层级关系
    List<TreeNode> list2 = TreeNodeBuilder.build(treeNodes,0);
    return new DataView(list2);

}
```

## 十四、角色【管理员、学生、老师】CRUD，分配菜单权限

### 14.1 角色的增删改查【条件查询带有分页】

1.引入role的静态页面

页面进行菜单的增加

2....

3...

4....

### 14.2 为角色分配菜单权限

1.分配权限  **menu**【菜单的操作资源】id

2.分配角色 **role**【用户 管理员 学生 教师】id

**3.关联表role_menu：**【**全都可以为空，不能有主键，都是外键属性**】

**rid  mid**

1     1

1     2

select **mid** from role_menu where rid = ?

**List<Integer>** 所具有的菜单栏权限

```java

```

![1654588068035](C:\Users\15067\AppData\Local\Temp\1654588068035.png)

```java
/**
 * 1.初始化下拉列表的权限
 */
@Autowired
private MenuService menuService;

@RequestMapping("/initPermissionByRoleId")
@ResponseBody
public DataView initPermissionByRoleId(Integer roleId){
    //查询所有菜单和权限
    QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
    List<Menu> allPermissions = menuService.list();
    //1.首先根据角色id查询出当前角色所拥有的所有菜单的ID和权限的ID
    List<Integer> currentRolePermissions = roleService.queryRolePermissionIdsByRid(roleId);
    //2.根据查询出来的菜单ID和权限ID，再查询出菜单的数据和权限的数据
    List<Menu> currentPermissions = null;

    //如果根据角色id查询出来了菜单ID或权限ID，就去查询
    if (currentRolePermissions.size()>0){
        queryWrapper.in("id",currentRolePermissions);
        currentPermissions = menuService.list(queryWrapper);
    }else {
        currentPermissions = new ArrayList<>();
    }
    //3.构造List<TreeNode>
    List<TreeNode> nodes = new ArrayList<>();
    for (Menu allPermission : allPermissions) {
        String checkArr = "0";
        for (Menu currentPermission : currentPermissions) {
            if (allPermission.getId().equals(currentPermission.getId())){
                checkArr = "1";
                break;
            }
        }
        Boolean spread = (allPermission.getOpen()==null||allPermission.getOpen()==1)?true:false;
        nodes.add(new TreeNode(allPermission.getId(),allPermission.getPid(),allPermission.getTitle(),spread,checkArr));
    }
    return new DataView(nodes);
}
```

```java
@Select("select mid from role_menu where rid = #{roleId}")
List<Integer> queryRolePermissionIdsByRid(Integer roleId);
```

```java
分配菜单权限【角色与菜单之间的关系】

// 1.分配菜单栏之前删除所有的rid数据
@Delete("delete from role_menu where rid = #{rid}")
void deleteRoleByRid(Integer rid);

// 2.保存分配 角色 与 菜单 的关系
@Insert("insert into role_menu(rid,mid) values (#{rid},#{mid})")
void saveRoleMenu(Integer rid, Integer mid);
```

![1654592413047](C:\Users\15067\AppData\Local\Temp\1654592413047.png)

![1654592505578](C:\Users\15067\AppData\Local\Temp\1654592505578.png)

## 用户管理【增删改查、分配角色】

### 14.1 用户的增删改查，上传头像

![1654602632710](C:\Users\15067\AppData\Local\Temp\1654602632710.png)

![1654602643229](C:\Users\15067\AppData\Local\Temp\1654602643229.png)

![1654602658000](C:\Users\15067\AppData\Local\Temp\1654602658000.png)

1.引入以页面

2.编写代码

**查询所有带有分页 带有查询条件**



**第一种办法：如何连表查询？？？？？？？？**

自定义方法：

```java
// 1.第一种办法
//if (StringUtils.isNotBlank(userVo.getUsername())){
//    userService.loadUserByLeftJoin(userVo.getUsername(),userVo.getPage(),userVo.getLimit());
//}
// 2.mapper
//@Select("select a.username,b.name FROM user as a where a.username = #{} LEFT JOIN ban_ji as b ON a.ban_ji_id = b.id limit #{},#{}")
```

![1654658187253](C:\Users\15067\AppData\Local\Temp\1654658187253.png)

sql:

![1654658432315](C:\Users\15067\AppData\Local\Temp\1654658432315.png)



// 2.第二种办法

1.ipage【User所有数据】---> banjiID      ----->ban_ji 表  名字给ipage对象进行赋值

2.添加属性

```java
// 非数据库列 班级名字
@TableField(exist = false)
private String banJiName;

// 非数据库列 学院名字
@TableField(exist = false)
private String xueYuanName;

// 非数据库列 老师名字
@TableField(exist = false)
private String teacherName;
```

```java
// 2.第二种办法
// 查到所有的数据 1 1 1
for (User user : iPage.getRecords()){
            // 为班级名字进行赋值
            if (user.getBanJiId()!=null){
                // 班级banJiService查库
                BanJi banji = banJiService.getById(user.getBanJiId());
                user.setBanJiName(banji.getName());
            }
            // 为学院名字进行赋值
            if (user.getXueYuanId()!=null){
                XueYuan xueYuan = xueYuanService.getById(user.getXueYuanId());
                user.setXueYuanName(xueYuan.getName());
            }
            // 为老师名字进行赋值
            if (user.getTeacherId()!=null){
                User teacher = userService.getById(user.getTeacherId());
                user.setTeacherName(teacher.getUsername());
            }
        }
```

![1654660307518](C:\Users\15067\AppData\Local\Temp\1654660307518.png)

### 14.2 用户新增或编辑和删除

![1654665124918](C:\Users\15067\AppData\Local\Temp\1654665124918.png)

填充数据【下拉列表】

```html
//初始化下拉列表【班级】
$.get("/user/listAllBanJi",function (res) {
    var banji = res;
    var dom_banji=$("#banji");
    var html = "<option value=''>选择班级</option>";
    $.each(banji,function (index,item) {
        html+="<option value='"+item.id+"'>"+item.name+"</option>";
    });
    dom_banji.html(html);
    form.render("select");
})

//初始化下拉列表【学院】
$.get("/user/listAllXueYuan",function (res) {
    var xueyuan = res;
    var dom_xueyuan=$("#xueyuan");
    var html = "<option value=''>选择学院</option>";
    $.each(xueyuan,function (index,item) {
        html+="<option value='"+item.id+"'>"+item.name+"</option>";
    });
    dom_xueyuan.html(html);
    form.render("select");
})
```

```java
/**
 * 初始化下拉列表的数据【班级】
 */
@RequestMapping("/listAllBanJi")
@ResponseBody
public List<BanJi> listAllBanJi(){
    List<BanJi> list = banJiService.list();
    return list;
}
/**
 * 初始化下拉列表的数据【学院】
 */
@RequestMapping("/listAllXueYuan")
@ResponseBody
public List<XueYuan> listAllXueYuan(){
    List<XueYuan> list = xueYuanService.list();
    return list;
}
```

熙增编辑和删除

![1654669217432](C:\Users\15067\AppData\Local\Temp\1654669217432.png)

### 14.2 重置密码、修改密码

![1654669197299](C:\Users\15067\AppData\Local\Temp\1654669197299.png)



### 14.3 给用户分配角色【一个用户可能多重角色 1:m】



![1654680325599](C:\Users\15067\AppData\Local\Temp\1654680325599.png)

![1654680700125](C:\Users\15067\AppData\Local\Temp\1654680700125.png)

### 1.创建一张角色与用户的维护表 user_role

![1654680810232](C:\Users\15067\AppData\Local\Temp\1654680810232.png)

### 2.初始化点击的角色列表

![1654682183307](C:\Users\15067\AppData\Local\Temp\1654682183307.png)

```java
//controller
@RequestMapping("/initRoleByUserId")
    @ResponseBody
    public DataView initRoleByUserId(Integer id){
        // 1.查询所有角色
        List<Map<String, Object>> listMaps = roleService.listMaps();
        // 2.查询当前登录用户所拥有的角色
        List<Integer> currentUserRoleIds = roleService.queryUserRoleById(id);
        // 3.让你的前端 变为选中状态
        for (Map<String,Object> map : listMaps){
            Boolean LAY_CHECKED = false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : currentUserRoleIds){
                if (rid.equals(roleId)){
                    LAY_CHECKED = true;
                    break;
                }
            }
            map.put("LAY_CHECKED",LAY_CHECKED);
        }
        return new DataView(Long.valueOf(listMaps.size()),listMaps);
    }



// mapper 根据用户id查询所有的角色
@Select("select rid from user_role where uid = #{id}")
List<Integer> queryUserRoleById(Integer id);
```

### 3.确认分配角色保存角色分配关系

**1.删除之前的用户与角色关系**

**2.保存用户与角色的关系**

```java
public void saveUserRole(Integer uid, Integer[] ids) {
    roleMapper.deleteRoleUserByUid(uid);
    if (ids!=null&&ids.length>0){
        for (Integer rid : ids){
            roleMapper.saveUserRole(uid,rid);
        }
    }
}
```

```java
// 1. 先删除之前的用户与角色关系
@Delete("delete from user_role where uid = #{uid}")
void deleteRoleUserByUid(Integer uid);

//2. 保存分配的用户与角色之间的关系
@Insert("insert into user_role(uid,rid) values(#{uid},#{rid})")
void saveUserRole(Integer uid, Integer rid);
```

![1654683042820](C:\Users\15067\AppData\Local\Temp\1654683042820.png)

## 十五、不同的用户【角色】登录看到不同的菜单栏

 ![1654689887283](C:\Users\15067\AppData\Local\Temp\1654689887283.png)



**用户  ：  角色  ： 菜单**

id ---- List<Role>

role --- List<Menu>

![1654690134405](C:\Users\15067\AppData\Local\Temp\1654690134405.png)

![1654690116614](C:\Users\15067\AppData\Local\Temp\1654690116614.png)



**加载左侧主页菜单栏的时候进行条件查询【OK】**

![1654691392426](C:\Users\15067\AppData\Local\Temp\1654691392426.png)



![1654691492269](C:\Users\15067\AppData\Local\Temp\1654691492269.png)

**不同角色展现不同的菜单实现逻辑**

```java
// 查询的所有菜单栏 按照条件查询【管理员，学生 老师【条件查询】】
QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
List<Menu> list = null;

// 1.取出session中的用户ID
User user = (User) session.getAttribute("user");
Integer userId = user.getId();
// 2.根据用户ID查询角色ID
List<Integer> currentUserRoleIds = roleService.queryUserRoleById(userId);
// 3.去重
Set<Integer> mids = new HashSet<>();
for (Integer rid : currentUserRoleIds){
    // 3.1.根据角色ID查询菜单ID
    List<Integer> permissionIds = roleService.queryAllPermissionByRid(rid);
    // 3.2.菜单栏ID和角色ID去重
    mids.addAll(permissionIds);
}
// 4.根据角色ID查询菜单ID
if (mids.size()>0){
    queryWrapper.in("id",mids);
    list = menuService.list(queryWrapper);
}
```
## 十六、实时刷新china疫情新闻播报+轮播图

### 16.1 引入layui轮播图面板和轮播图

![1654731382207](C:\Users\15067\AppData\Local\Temp\1654731382207.png)

```html
<!--轮播图-->
<div class="layui-carousel" id="test1" lay-filter="test1">
    <div carousel-item="">
        <div><img style="height: 100%;width: 100%" th:src="@{/images/banner3.jpg}"></div>
        <div><img style="height: 100%;width: 100%" th:src="@{/images/banner2.jpg}"></div>
        <div><img style="height: 100%;width: 100%" th:src="@{/images/banner1.jpg}"></div>

    </div>
</div>

layui.use(['carousel','element', 'layer', 'util'], function(){
        var element = layui.element
            ,layer = layui.layer
            ,carousel = layui.carousel
            ,util = layui.util
            ,$ = layui.$;

        //头部事件
        util.event('lay-header-event', {
            //左侧菜单事件
            menuLeft: function(othis){
                layer.msg('展开左侧菜单的操作', {icon: 0});
            }
            ,menuRight: function(){
                layer.open({
                    type: 1
                    ,content: '<div style="padding: 15px;">处理右侧面板的操作</div>'
                    ,area: ['260px', '100%']
                    ,offset: 'rt' //右上角
                    ,anim: 5
                    ,shadeClose: true
                });
            }
        });
        //轮播图
        //常规轮播
        carousel.render({
            elem: '#test1'
            ,arrow: 'always'
            ,height:'220px'
            ,width:'350px'
        });

    });
```



### 16.2 实时播报新闻引入layui【时间线样式】

```java
@Data
@TableName("nocv_news")
public class NocvNews {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private String publishby;
    private Date createTime;

}
```



### 16.3 疫情新闻的增删改查

## 十七、校园系统管理

### 17.1 学院 班级 的快速增删改查

## 十八、校园防疫管理

### 18.1 健康打卡管理

### 18.2 核酸检测管理

时间选择器的使用【】

```html
<div class="layui-form">
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">检测时间</label>
            <div class="layui-input-inline">
                <input type="text" name="createTime" class="layui-input" lay-verify="required" id="test1" placeholder="yyyy-MM-dd">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">报告时间</label>
            <div class="layui-input-inline">
                <input type="text" name="updateTime" class="layui-input" id="test2" placeholder="yyyy-MM-dd">
            </div>
        </div>
    </div>
</div>


layui.extend({
        dtree: '/layui_ext/dtree/dtree'
    }).use(['jquery', 'form', 'layer', 'laydate', 'table', 'layedit','dtree'], function () {

        var laydate = layui.laydate;
```

### 18.3 疫苗接种管理

## 十九、引入Redis为项目增添光彩

![1655171447022](C:\Users\15067\AppData\Local\Temp\1655171447022.png)



### 19.1 为什么要引入redis数据库？

**1.单机并发量高 10W读写速度**  tomcat:?????  150？？？？Mysql数据库？？？**16384**

**2.安全起见，全部查询缓存，不要去查询mysql数据库**

**3.面试必问！！！！！！！！！**

如何学习？

### 19.2 引入的问题

1.有缓存：返回数据【基本】

2.没有缓存：没有--》查询mysql-->更新redis缓存【非常快！】；【全国人民】

3.对接腾讯疫情数据接口API【实时】

redis老数据  mysql【查询api接口--->插入mysql数据库】

**需要的时最新的疫情数据？？？？？？？？？**

**解决方案：更新数据---->删除缓存【最基本】**

### 19.3 redis客户端

#### 1.Jedis

```xml
		<dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>2.7.1</version><!--版本号可根据实际情况填写-->
         </dependency>
```

测试连接redis服务器

1.双击启动redis服务器

2.写程序

```java
package com.example.demo.redis;

import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Coding路人王
 * @date 2022/6/14 10:39
 */
public class TestConnRedis {
    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        // 1.String类型【确诊：222，死亡：00】
        jedis.set("nocv","Coding路人王");
        System.out.println(jedis.get("nocv"));

        // 2.List集合【新闻列表】
        // 存储数据到列表中
        jedis.lpush("site-list", "Runoob");
        jedis.lpush("site-list", "Google");
        jedis.lpush("site-list", "Taobao");
        // 获取存储的数据并输出
        List<String> list = jedis.lrange("site-list", 0 ,2);
        for(int i=0; i<list.size(); i++) {
            System.out.println("列表项为: "+list.get(i));
        }

        // 3.Set无序集合,去重
        jedis.sadd("nocvset","111");
        jedis.sadd("nocvset","111");
        jedis.sadd("nocvset","111");
        jedis.sadd("nocvset","111");
        jedis.sadd("nocvset","222");
        Set<String> nocvlist = jedis.smembers("nocvlist");
        for (String s : nocvlist){
            System.out.println(s);
        }

        // 4.Sorted Set 有序集合【排名，排序，获取排序码】
        jedis.zadd("nocvset2",86.9,"1111");
        jedis.zadd("nocvset2",56.8,"2222");
        jedis.zadd("nocvset2",86.5,"3333");
        jedis.zadd("nocvset2",88.9,"4444");
        jedis.zadd("nocvset2",100,"5555");
        Set<String> nocvset2 = jedis.zrange("nocvset2", 0, -1);
        for (String s : nocvset2){
            System.out.println(s);
        }

        Long nocvset21 = jedis.zrank("nocvset2", "4444");
        System.out.println(nocvset21);

        System.out.println("=========================");
        // 返回分数区间内的个数
        Long nocvset22 = jedis.zremrangeByScore("nocvset2", 88, 100);
        System.out.println(nocvset22);

        // 返回有序集中，成员的分数值
        Double nocvset23 = jedis.zscore("nocvset2", "5555");
        System.out.println(nocvset23); // 88.9

    }
}
```





### 2.springboot-starter

```xml
<!-- 自动版本控制，帮你引入了jedis客户端 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```



### 19.4 china页面完成redis的String缓存功能【全国区镇 死亡等数据缓存】

![1655186492688](C:\Users\15067\AppData\Local\Temp\1655186492688.png)



```java
/**
 * // redis查询数据库的逻辑
 * 1.查询redis缓存，【有数据，直接返回】【没有数据，查询Mysql数据库，更新缓存，返回客户端】
 */
Jedis jedis = new Jedis("127.0.0.1");
// 2.1拿到客户端连接【你没有使用redis】
if (jedis!=null){
    String confirm = jedis.get("confirm");
    String input = jedis.get("input");
    String heal = jedis.get("heal");
    String dead = jedis.get("dead");
    String updateTime = jedis.get("updateTime");
    // 2.2 缓存里面有数据
    if (StringUtils.isNotBlank(confirm)
        && StringUtils.isNotBlank(input)
        && StringUtils.isNotBlank(heal)
        && StringUtils.isNotBlank(dead)
        && StringUtils.isNotBlank(updateTime)){
        ChinaTotal chinaTotalRedis = new ChinaTotal();
        chinaTotalRedis.setConfirm(Integer.parseInt(confirm));
        chinaTotalRedis.setInput(Integer.parseInt(input));
        chinaTotalRedis.setHeal(Integer.parseInt(heal));
        chinaTotalRedis.setDead(Integer.parseInt(dead));
        // 格式调整 String ----> Date
        chinaTotalRedis.setUpdateTime(new Date());
        System.out.println("redis中的数据：" + chinaTotalRedis);
        // 扔回前台
        model.addAttribute("chinaTotal",chinaTotalRedis);
        // 3.疫情播报新闻
        List<NocvNews> newsList = nocvNewsService.listNewsLimit5();
        model.addAttribute("newsList",newsList);
        return "china";

    }else {
        // 2.3 缓存里面没有数据 查询数据
        ChinaTotal chinaTotal = chinaTotalService.getById(id);
        model.addAttribute("chinaTotal",chinaTotal);
        // 3.疫情播报新闻
        List<NocvNews> newsList = nocvNewsService.listNewsLimit5();
        model.addAttribute("newsList",newsList);
        // 2.4 更新缓存
                jedis.set("confirm",String.valueOf(chinaTotal.getConfirm()));
                jedis.set("input",String.valueOf(chinaTotal.getInput()));
                jedis.set("heal",String.valueOf(chinaTotal.getHeal()));
                jedis.set("dead",String.valueOf(chinaTotal.getDead()));
                jedis.set("updateTime",String.valueOf(chinaTotal.getUpdateTime()));
    
        return "china";
    }
}
```

```java
// 7.删除缓存【非常重要】mysql ---  redis 已执行的一个简单套路
        Jedis jedis = new Jedis("127.0.0.1");
        if (jedis!=null){
            jedis.flushDB();
        }
```

### 19.5 全国各个省份的数据库Redis缓存【redis的List数据结构】

```java
@RequestMapping("/query")
@ResponseBody
public List<NocvData> queryData() throws ParseException {
    // 每天更新一次的数据使用场景
    /*QueryWrapper<NocvData> queryWrapper = new QueryWrapper<>();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String format1 = format.format(new Date());
    queryWrapper.ge("update_time",format.parse(format1));*/

    // 1.先查redis缓存[List] 有数据返回即可
    Jedis jedis = new Jedis("127.0.0.1");
    if (jedis!=null){

        // 1.1 有缓存数据 返回数据即可
        List<String> listRedis = jedis.lrange("nocvdata", 0 ,33);
        List<NocvData> dataList = new ArrayList<>();
        if (listRedis.size()>0){
            for(int i=0; i<listRedis.size(); i++) {
                System.out.println("列表项为: "+listRedis.get(i));
                String s = listRedis.get(i);
                JSONObject jsonObject = JSONObject.parseObject(s);
                Object name = jsonObject.get("name");
                Object value = jsonObject.get("value");
                NocvData nocvData = new NocvData();
                nocvData.setName(String.valueOf(name));
                nocvData.setValue(Integer.parseInt(value.toString()));
                dataList.add(nocvData);
            }
            // 查询redis缓存数据库 返回的数据
            return dataList;
        }else{
            // 1.2 redis没有数据 查Mysql数据库,更新缓存
            List<NocvData> list = indexService.listOrderByIdLimit34();
            for (NocvData nocvData : list){
                jedis.lpush("nocvdata", JSONObject.toJSONString(nocvData));
            }
            // 返回的数据中的数据
            return list;
        }
    }

    // 默认没有连接redis的返回数据库【兼容有没有安装redis】
    List<NocvData> list = indexService.listOrderByIdLimit34();
    return list;
}
```
## 二十、RestFul API风格架构

RESTful就是资源定位和资源操作的风格。不是标准也不是协议。

REST即Representational State Transfer的缩写，可译为"表现层状态转化”。RESTful最大的特点为：资源、统一接口、URI和无状态。

rest式的web服务是一种ROA(The Resource-Oriented Architecture)(面向资源的架构)

### 20.1 说引入前后的比较

在Restful之前的操作：
<http://127.0.0.1/user/query/name=1> GET  根据用户id查询用户数据
<http://127.0.0.1/user/save> POST 新增用户
<http://127.0.0.1/user/update> POST 修改用户信息
<http://127.0.0.1/user/delete/1> GET/POST 删除用户信息

RESTful用法：
<http://127.0.0.1/user/1> GET  根据用户id查询用户数据
<http://127.0.0.1/user>  POST 新增用户
<http://127.0.0.1/user>  PUT 修改用户信息
<http://127.0.0.1/user>  DELETE 删除用户信息

**1.http请求地址的变化**

```java
@RestController
public class RESTfulController {

    //传统方式：http://localhost:8080/h1?a=1&b=11
    @RequestMapping("h1")
    public String test1(int a, int b , Model model){
        int rslt=a+b;
        model.addAttribute("msg", "结果为："+rslt);
        return "hello";
    }

    //RESTful:http://localhost:8080/h2/1/11
    @RequestMapping("h2/{a}/{b}")
    public String test2(@PathVariable int a, @PathVariable int b , Model model){
        int rslt=a+b;
        model.addAttribute("msg", "结果为："+rslt);
        return "hello";
    }
}

```

**2.url不变，使用method属性区分**

```java
@RequestMapping(value = "h3/{a}/{b}",method = RequestMethod.GET)
public String test3(@PathVariable int a, @PathVariable int b , Model model){
    int rslt=a+b;
    model.addAttribute("msg", "get结果为："+rslt);
    return "hello";
}

@RequestMapping(value = "h3/{a}/{b}",method = RequestMethod.POST)
public String test4(@PathVariable int a, @PathVariable int b , Model model){
    int rslt=a+b;
    model.addAttribute("msg", "post结果为："+rslt);
    return "hello";
}

```

**3.3、使用@GetMapping、@PostMapping**

```java
@GetMapping("h3/{a}/{b}")
public String test5(@PathVariable int a, @PathVariable int b , Model model){
    int rslt=a+b;
    model.addAttribute("msg", "get结果为："+rslt);
    return "hello";
}

@PostMapping( "h3/{a}/{b}")
public String test6(@PathVariable int a, @PathVariable int b , Model model){
    int rslt=a+b;
    model.addAttribute("msg", "post结果为："+rslt);
    return "hello";
}

```

### 总结

- 使请求路径变得更加简洁
- 传递、获取参数值更加方便，框架会自动进行类型转换
- 通过路径变量@PathVariable的类型，可以约束访问参数。
  - 若参数值与定义类型不匹配，则访问不到对应的方法，报错400错误的请求。
- 安全，请求路径中直接传递参数值，并用斜线/分隔，不会暴露传递给方法的参数变量名。

## 二十一、请假审批功能设计

### 21.1 规矩正式的审批流设计

1. 流程概念分析

  通常的流程一般分为2个层次来讲：**流程、节点**

流程就是某一具体的业务流程（如请假审批流程，财务报销审批流程），它由若干节点组成；
节点就是一种特定业务类型的封装，包括节点**基本信息、参与者、时间限制、工作任务信息、触发事件、启动策略等信息**。
2. 流程节点状态分析
  首先我们分析业务工单状态，从**操作人**、**流程**、**节点状态**这三个角度分析

3. 操作人
  **串行**： 上一处理人指定某一处理人时，其他拥有此步骤权限的操作员不可进行查看和操作，必须当前处理人处理完毕后，流程才能继续；
  **并行**： 由上一处理人指定固定多个处理人时，由任一员工处理即可，不分前后顺序，全部处理完成，进入下一步骤；

![并签、或签的逻辑](https://img-blog.csdnimg.cn/20210508115519873.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MDYwMTI2Nw==,size_16,color_FFFFFF,t_70)

2. **执行动作**
  一套完整的审批流程参考钉钉审批事件信息的处理上包含如下5类：

申请：针对当前业务表单信息发起申请，开始审批流程的传递。分为2部分：由申请提交人手动发起申请、由程序自动判断满足触发规则的数据自动发起申请；另外还要注意的2点：是否允许提交人撤销（是、否）、记录编辑（不可编辑、管理员可编辑、管理员和审批人都可编辑 ）；
通过：当前步骤处理通过，进入下一步骤，若为末步骤，则流程处理完成；
退回：将步骤退回至上一步骤，即返回至上一处理人处，若为首步骤，则不进行退回；
否决：将步骤直接结束，执行结束动作拒绝活动，不再进行操作，或者回退至第一步骤；
撤回：若当前步骤已处理，下一处理人未处理的情况下可进行撤回操作。

![节点流程描述](https://img-blog.csdnimg.cn/20210508144110633.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MDYwMTI2Nw==,size_16,color_FFFFFF,t_70)



3. **节点状态**
  **提交人**： 未提交、已提交、处理中、已结束。【枚举类型】
  **处理人**： 待处理、已处理【通过，驳回】。

### 21.2 请假流程分析

**流程：**学生发起请假申请---->老师审核----->院系审核【结束】

**操作人：**学生，老师，院系【角色】

节点：

**学生**【未提交、已提交、处理中、已结束】

**老师、院系**【待处理、已处理】

### 21.2 数据库设计

**请假审批流程表【approval_process】**

| id   | uid  | reason   | address | day  | phone | node_status | create_time | update_time |
| ---- | ---- | -------- | ------- | ---- | ----- | ----------- | ----------- | ----------- |
| id   | uid  | 请假理由 | 去向    | 天数 | 电话  | 节点状态    | 创建时间    | 修改时间    |

**【node_status 列属性】**

0：学生未提交

1：学生已提交

2：老师审核通过

3：老师驳回

4：院系审核通过

5：院系驳回

6：已结束

## 二十二、 正规军的日志打印

### 22.1 日志是什么？

起源：航海领域---->航空领域--->互联网

### log4j、logback、Log4j2简介

1.log4j是apache实现的一个开源日志组件
2.logback同样是由log4j的作者设计完成的，拥有更好的特性，用来取代log4j的一个日志框架，是slf4j的原生实现
3.Log4j2是log4j 1.x和logback的改进版，采用了一些新技术（无锁异步、等等），使得日志的吞吐量、性能比log4j 1.x提高10倍，并解决了一些死锁的bug，而且配置更加简单灵活

### 22.2 为神魔要使用日志？

原因：**1.记录操作轨迹   2.监控整个系统的运行  3.回溯系统原因**

内存 CPU状况，log，60%，调优   监控系统手机日志【线上 定位问题】

### 22.3 使用日志的规范？

![1656895334623](C:\Users\15067\AppData\Local\Temp\1656895334623.png)



**appName_logType_logName.log**



**日志的级别？？？？？**

**debug 调试程序帮助信息**

info 运行现场指导信息

**warn 运行的时候 潜在错误信息**

**ERROR 错误时候 打印的信息 ，不会影响程序的执行。**

**FATAL 非诚错误的状况，导致程序中断。**



### 22.4 有哪些方式使用日志？

### slf4j+log4j和直接用log4j的区别

slf4j是对所有日志框架制定的一种规范、标准、接口，并不是一个框架的具体的实现，因为接口并不能独立使用，需要和具体的日志框架实现配合使用（如log4j、logback），使用接口的好处是当项目需要更换日志框架的时候，只需要更换jar和配置，不需要更改相关java代码。

### slfj+logback【新工程推荐使用】

### log4j、logback、log4j2都是一种日志具体实现框架，所以既可以单独使用也可以结合slf4j一起搭配使用

### 22.5 springboot集成日志

### 一、使用log4j

#### 1.如项目中有导入spring-boot-starter-web依赖包记得去掉spring自带的日志依赖spring-boot-starter-logging，springboot默认使用的是logback日志。

```xml
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
 <exclusions>
  <exclusion>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-logging</artifactId>
  </exclusion>
 </exclusions>
</dependency>

<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

```

#### 2.springboot项目中需导入log4j

```
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-log4j</artifactId>
 <version>1.3.8.RELEASE</version>
</dependency>
```

### 3.直接在resource下方新建log4j.properties即可。

```
log4j.rootCategory=ERROR, CONSOLE # 整体级别
log4j.logger.com.demo.mapper=DEBUG  # 某个包下的日志级别
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%C %d{YYYY-MM-dd hh:mm:ss}  %m %n

log4j.appender.LOGFILE=org.apache.log4j.FileAppender
log4j.appender.LOGFILE.File=E:/my.log  #日志输出目录
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%m %n
```

## 二、集成slf4j+log4j/log4j2

### 1.添加依赖，排除默认日志

```xml
<!--像一中一样，去掉springboot的默认logging-->
<!--增加log4j2依赖↓-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>

		<!--log4j 日志具体实现-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j</artifactId>
            <version>1.3.8.RELEASE</version>
        </dependency>

        <!--slf4j 日志门面-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <!--日志适配器-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </dependency>
```

### 2.如果有冲突，添加配置

```
<exclusions>
    <exclusion>
        <groupId>log4j</groupId>
        <artifactId>*</artifactId>
    </exclusion>
    <exclusion>
        <groupId>org.slf4j</groupId>
        <artifactId>*</artifactId>
    </exclusion>
    <exclusion>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>*</artifactId>
    </exclusion>
</exclusions>
```

### 3.创建log4j2.xml放在resources目录下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    6个优先级从高到低依次为：OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE、 ALL。
    如果设置优先级为WARN，那么OFF、FATAL、ERROR、WARN 4个级别的log能正常输出
    设置为OFF 表示不记录log4j2本身的日志，
 -->

<!-- status：用来指定log4j本身的打印日志级别,monitorInterval:指定log4j自动重新配置的监测间隔时间 -->
<configuration status="INFO" monitorInterval="30">
    <!-- 自己设置属性，后面通过${}来访问 -->
<!--    <properties>
        <property name="LOG_HOME">${web:rootDir}/logs</property>
    </properties>-->
    <appenders>
        <!--Appender 1. 输出到Console控制台，指定输出格式和过滤器等级为INFO -->
        <Console name="Console" target="SYSTEM_OUT">
            <!--ThresholdFilter指定日志消息的输出最低层次-->
            <ThresholdFilter level="ALL" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
        </Console>

        <!--Appender 2. 输出到滚动保存的文件, 触发保存日志文件的条件是日志文件大于3KB，只保存最新的10个日志-->
        <File name="allLog" fileName="${LOG_HOME}/all.log">
            <ThresholdFilter level="ALL" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout charset="UTF-8" pattern="%d{yyyy.MM.dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>
        </File>


        <!--Appender 3. 输出到滚动保存的文件, 触发保存日志文件的条件是日志文件大于3KB，只保存最新的10个日志-->
        <RollingFile name="debugLog" fileName="${LOG_HOME}/debug.log" filePattern="${log.path}/debug-%i.log">
            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout charset="UTF-8" pattern="[%-5level][%d{yyyy-MM-dd HH:mm:ss}][%F:%L] - %m%n"/>
            <SizeBasedTriggeringPolicy size="3KB"/>
            <!-- DefaultRolloverStrategy 中的参数max，可以限制 SizeBasedTriggeringPolicy中size超出后，只保留max个存档-->
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>

        <!--Appender 4. 输出到滚动保存的文件, 触发保存日志文件的条件是每分钟第一次的日志事件。ERROR日志是按分钟产生日志 -->
        <RollingFile name="errorLog" fileName="${LOG_HOME}/error.log"
                     filePattern="${log.path}/error-%d{yyyy-MM-dd_HH-mm}.log">
            <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout charset="UTF-8" pattern="[%-5level][%d{yyyy-MM-dd HH:mm:ss}][%C:%F:%L] - %m%n"/>
            <TimeBasedTriggeringPolicy/>
        </RollingFile>

        <RollingFile name="RollingFile" fileName="${LOG_HOME}/rar.log"
                     filePattern="${LOG_HOME}/$${date:yyyy-MM}/${FILE_NAME}-%d{MM-dd-yyyy}-%i.log.gz">
            <PatternLayout charset="UTF-8" pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>
            <!--日志文件最大值 第二天压缩-->
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10 MB"/>
            </Policies>
        </RollingFile>

    </appenders>
    <!--root 默认加载-->
    <loggers>
        <root level="DEBUG">
            <appender-ref ref="Console"/>
            <!--<appender-ref ref="allLog"/>-->
            <!--<appender-ref ref="debugLog"/>-->
            <!--<appender-ref ref="errorLog"/>-->
            <!--<appender-ref ref="RollingFile"/>-->
        </root>
    </loggers>
</configuration>
```



### 三、slf4j+logback 非常推荐使用的

引入依赖，新建logback-spring.xml

```xml
<!--logback 桥接-->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
</dependency>

<!--logback 日志具体实现-->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
</dependency>

<!--slf4j 日志门面-->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>
```

```xml

<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="chapters.configuration" level="INFO"/>

    <!-- Strictly speaking, the level attribute is not necessary since -->
    <!-- the level of the root level is set to DEBUG by default.       -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
    </root>

</configuration>
```

### 4.测试

```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    public final Logger logger=LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        log.trace("trace");
        log.debug("debug");
        log.warn("warn");
        log.info("info");
        log.error("error");
    }
}
```
