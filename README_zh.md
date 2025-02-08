### 组织结构

``` lua
jagentmall
├── jagentmall-admin -- 后台管理系统服务
├── jagentmall-ai -- ai大模型服务
├── jagentmall-auth -- 基于Spring Security Oauth2的统一的认证中心
├── jagentmall-common -- 工具类及通用代码模块
├── jagentmall-entity -- MyBatisGenerator生成的数据库操作代码模块
├── jagentmall-feign -- 微服务远程调用服务
├── jagentmall-gateway -- 基于Spring Cloud Gateway的微服务API网关服务
├── jagentmall-portal -- 前端商城系统服务
└── jagentmall-search -- 基于Elasticsearch的商品搜索系统服务
```