# JAgentMall 

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/JAagentOrg/JAgentMall/blob/main/LICENSE)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023-brightgreen)

> 基于Spring Cloud 2023 & Alibaba构建的新一代智能微服务电商平台

## 🌟 项目概览
采用Spring Cloud 2023 + Spring Boot 3.2技术栈，集成注册中心、配置中心、监控中心、智能网关等基础设施，实现了商品检索、智能下单、分布式事务等电商核心场景。创新性整合Spring Alibaba AI实现自然语言交互式购物体验。

❗ **线上演示**：[演示视频](https://www.bilibili.com/video/BV1SH9ZYpEzR) | 📦 **源码地址**：[GitHub仓库](https://github.com/JAagentOrg/JAgentMall/tree/dev)

## 🚀 核心特性
### 高性能架构设计
- **万级QPS支撑**：Redis热数据缓存 + Elasticsearch分布式检索
- **智能流量管控**：Sentinel秒级限流/熔断，动态线程池保护
- **毫秒级搜索**：ES多级分词优化（IK+pinyin+同义词），search_after深度分页

### 智能业务实现
- **AI购物助手**：集成Alibaba Qwen大模型支持自然语言指令（示例："帮我找上个月买的Switch游戏"）
- **智能化事务**：Seata AT模式实现跨服务数据一致性，自动异常补偿机制
- **精准时效控制**：RabbitMQ延迟队列+定时任务实现30分钟未支付订单自动释放

### 企业级解决方案
- **亿级用户认证**：Sa-Token无状态会话管理，单日百万级登录支撑
- **全链路可观测**：ELK日志聚合 + SkyWalking链路追踪 + Grafana监控看板
- **柔性服务设计**：动态配置中心（Nacos） + 服务网格（Istio）灰度发布

## 🛠️ 技术矩阵
| 领域                | 技术组件                                                                                                                                 |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| 微服务框架          | Spring Cloud 2023、Spring Boot 3.2、OpenFeign 4.0                                                                                       |
| 分布式组件          | Nacos 2.2.3（注册/配置中心）、Sentinel 1.8.7（流量治理）、Seata 1.7.1（分布式事务）                                                     |
| 数据存储            | MySQL 8.0（业务主库）、Redis 7.0（缓存热数据）、Elasticsearch 8.12（商品搜索）                                                         |
| 消息队列            | RabbitMQ 3.12（订单状态同步/延迟消息）                                                                                                  |
| 智能交互            | Spring AI 1.0（自然语言处理）、Alibaba Qwen大模型                                                                                       |
| 可观测性            | Prometheus + Grafana（监控看板）、SkyWalking 10.0（链路追踪）、ELK Stack（日志分析）                                                   |

## 📂 模块架构
```bash
JAgentMall
├── jagentmall-admin        # 运营管理后台
├── jagentmall-ai           # AI交互服务（智能下单/查询）
├── jagentmall-auto         # 统一认证中心
├── jagentmall-common       # 公共组件库
├── jagentmall-entity       # 数据实体与MyBatis映射
├── jagentmall-gateway      # 智能网关（路由/限流）
├── jagentmall-portal       # 用户商城前端
└── jagentmall-search       # 商品搜索服务（ES优化引擎）
