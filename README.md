## Server

本项目采用 Spring Boot + Vue 前后端分离的架构，Spring Boot 负责处理后端逻辑并与 MySQL 数据库交互。该部分对后端部分进行说明，并提供开发指引。

### Quick Start

在本地下载并配置 MySQL，将初始密码设置为 `xllama123`。配置完成后，使用命令行或可视化工具（如 Navicat）创建数据库和表。

### 1. 创建数据库

使用以下 SQL 语句创建数据库：

```sql
CREATE DATABASE xllama_database;
```

### 2. 选择数据库

创建数据库后，使用以下语句选择你刚创建的数据库：

```sql
USE xllama_database;
```

### 3. 创建表

使用以下 SQL 语句在数据库中创建 `user` 表：

```sql
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    bio TEXT
);
```

### 4. 插入测试数据

在 `user` 表中插入一条测试数据，方便后续测试：

```sql
INSERT INTO user (username, password, bio) 
VALUES ('john_doe', 'secure_password', 'Hello, I am John Doe, a software developer.');
```

所有操作如下图所示：

![create_table](./imgs/create_table.png)

### 5. 运行后端程序

```
git clone https://github.com/GanLiuuuu/xllama-server.git
```

Clone 后端代码并在 IDE 中打开。成功启动项目后，访问 `localhost:8081/user/getAll`，你应该能够看到之前插入的测试数据，如下图所示：

![backend_get](./imgs/backend_get.png)

如遇到依赖问题，请在群里讨论。