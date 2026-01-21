# JChatMind

> 基于 Spring AI 构建的 AI Agent 智能体平台，支持多模型切换、工具调用、知识库检索（RAG）

<!-- 项目展示区域 - 请在此处添加视频/图片 -->
<div align="center">

![演示视频](./assets/demo.gif)

<br/>

### 📸 项目截图

<img src="./assets/screenshot-chat.png" width="800" alt="智能对话界面" />

<br/>

<img src="./assets/screenshot-architecture.png" width="800" alt="系统架构图" />

</div>
<!-- 项目展示区域结束 -->

---

## 特性

- **Agent Loop** - 实现 Think-Execute 循环，Agent 可自主决策并执行多步骤任务
- **多模型支持** - 支持 DeepSeek、智谱 AI 等多种大语言模型，一键切换
- **工具调用 (Function Calling)** - 内置邮件发送、数据库查询、知识库检索等工具
- **RAG 知识库** - 上传文档自动向量化，基于语义相似度检索
- **实时流式响应** - 基于 SSE 的实时消息推送，体验流畅

## 技术栈

**后端**
- Java 17 + Spring Boot 3.5
- Spring AI 1.1
- PostgreSQL + pgvector
- MyBatis

**前端**
- React 19 + TypeScript
- Ant Design 6 + @ant-design/x
- Vite 7 + TailwindCSS 4

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- PostgreSQL 16 (with pgvector)
- Ollama (用于本地向量嵌入)

### 1. 启动数据库

```bash
docker-compose up -d
```

### 2. 初始化数据库

```bash
psql -U postgres -d jchatmind -f jchatmind.sql
```

### 3. 配置 API Key

编辑 `jchatmind/src/main/resources/application.yaml`，填入你的 API Key：

```yaml
spring:
  ai:
    deepseek:
      api-key: your-deepseek-api-key
    zhipuai:
      api-key: your-zhipu-api-key
```

### 4. 启动后端

```bash
cd jchatmind
mvn spring-boot:run
```

### 5. 启动前端

```bash
cd ui
npm install
npm run dev
```

访问 http://localhost:5173

## 项目结构

```
JChatMind/
├── jchatmind/                # 后端 Spring Boot 项目
│   ├── src/main/java/
│   │   └── com/kama/jchatmind/
│   │       ├── agent/        # Agent 核心（Loop、状态机、工具）
│   │       ├── controller/   # REST API
│   │       ├── service/      # 业务逻辑（RAG、SSE）
│   │       └── model/        # 数据模型
│   └── src/main/resources/
│       └── application.yaml  # 配置文件
│
├── ui/                       # 前端 React 项目
│   └── src/
│       ├── components/       # React 组件
│       ├── api/              # API 调用
│       └── types/            # TypeScript 类型
│
├── docker-compose.yml        # PostgreSQL + pgvector
└── jchatmind.sql             # 数据库初始化脚本
```

## 核心概念

### Agent Loop

Agent 采用 Think-Execute 循环模式：

```
┌─────────────────────────────────────────┐
│                                         │
│   ┌─────────┐    ┌─────────────────┐    │
│   │  Think  │───▶│ Tool Execution  │    │
│   │  (LLM)  │    │                 │    │
│   └─────────┘    └────────┬────────┘    │
│        ▲                  │             │
│        │                  │             │
│        └──────────────────┘             │
│                                         │
│   Loop until: terminate() or max_steps  │
└─────────────────────────────────────────┘
```

### 工具系统

| 工具 | 类型 | 描述 |
|------|------|------|
| `terminate` | 固定 | 终止 Agent 循环 |
| `knowledgeSearch` | 固定 | 知识库语义检索 |
| `sendEmail` | 可选 | 发送邮件 |
| `databaseQuery` | 可选 | 执行 SQL 查询（仅 SELECT） |

### RAG 流程

```
文档上传 → Markdown 解析 → 文本切片 → BGE-M3 向量化 → pgvector 存储
                                                          ↓
用户提问 → 问题向量化 → 相似度检索 → 上下文注入 → LLM 生成回答
```

## API 概览

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/agents` | GET/POST | 智能体管理 |
| `/api/chat-sessions` | GET/POST | 会话管理 |
| `/api/chat-messages` | POST | 发送消息 |
| `/api/knowledge-bases` | GET/POST | 知识库管理 |
| `/api/documents/upload` | POST | 文档上传 |
| `/sse/connect/{sessionId}` | GET | SSE 连接 |

## License

MIT
