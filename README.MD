# 项目说明

## 模块说明
 - xy-cloud-eureka
   - 说明
     - 项目是Netflix Eureka注册中心的实现，主要作用为实现分布式而搭建的分布式服务中心
     
   - 模块性质
     - 子模块，可启动
     
   - 端口号
     - 8080
     
   - 项目启动Profile说明
     - 无
     
     
 - xy-cloud-zuul
   - 说明
     - 项目是Netflix Zuul的实现，主要作用是用作API网关的拦截等功能
     
   - 模块性质
     - 子模块，可启动
     
   - 端口号
     - 10001，10002，10003（视不同的Profile而不同）
     
   - 项目启动Profile说明
     - 项目启动需要带入两个profile-A,B
     - A需要从port1、port2、port3中取一个，分别对应[1000x]端口
     - B需要从devel、test、prod中选取一个，分别对应开发、测试、生产环境
 
 
 - xy-common
   - 说明
     - 项目是新元统一身份认证系统的通用模块，包含了对各种配置文件的管理
     - 配置文件列表：
     - amqp：代表RabbitMQ的配置
     - common：代表通用的配置
     - mybatis：代表mybatis的配置项
     - redis：代表redis的配置项
     - bootstrap：代表项目Eureka的配置以及数据库的配置文件
     
   - 模块性质
     - 子模块，不可启动
     
   - 端口号
     - 无
     
   - 项目启动Profile说明
     - 无
      
 
 - xy-repository
   - 说明
     - 模块是项目的仓储控制中心
     - [SRC目录列表]
     - beans：数据库实体类
     - configuration：仓储配置中心
     - repository：仓储实际类
     - typehandlers：用于Mybatis类型转换包
     - [RESOURCES目录列表]
     - db.migration：用于Flyway数据库脚本的存放
     - mybatis：用于mybatis SQL文件的存放
     
   - 模块性质
     - 子模块，不可启动
     
   - 端口号
     - 无
     
   - 项目启动Profile说明
     - 无
  
 
  - xy-verify
    - 说明
      - 模块是项目的核心
      
    - 模块性质
      - 子模块，可启动
      
    - 端口号
      - 10000
      
    - 项目启动Profile说明
      - 需要从devel、test、prod中选取一个，分别对应开发、测试、生产环境