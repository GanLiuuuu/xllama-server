CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,  -- 用户ID，主键，自增
    username VARCHAR(50) NOT NULL,           -- 用户名
    password VARCHAR(255) NOT NULL,          -- 密码
    email VARCHAR(100),                      -- 邮箱
    user_type ENUM('普通用户', '管理员') DEFAULT '普通用户',  -- 用户类型
    avatar_url VARCHAR(255),                 -- 头像URL
    bio TEXT,                                -- 自我介绍
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 账户创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 账户最后更新时间
    points INT DEFAULT 0,                    -- 积分数
    tokens INT DEFAULT 0                     -- tokens
);

CREATE TABLE Bot (
    bot_id INT AUTO_INCREMENT PRIMARY KEY,   -- bot ID，主键，自增
    bot_name VARCHAR(100) NOT NULL,          -- bot 名称
    description TEXT,                        -- bot 描述
    is_official BOOLEAN DEFAULT FALSE,       -- 是否为官方mo默认bot
    created_by INT,                          -- 用户ID，外键（指向创建该bot的用户，可以为一个默认管理员代表默认bot）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- bot创建时间
    FOREIGN KEY (created_by) REFERENCES User(user_id)
);

-- 用于存储聊天会话的表
CREATE TABLE ChatSession (
    session_id INT AUTO_INCREMENT PRIMARY KEY,  -- 会话ID，主键，自增
    user_id INT,                                -- 用户ID，外键
    bot_id INT,                                 -- bot ID，外键
    session_name VARCHAR(100),                  -- 会话名称，可以用于区分不同会话
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 会话创建时间
    last_interaction TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 最后交互时间
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (bot_id) REFERENCES Bot(bot_id)
);

-- 用于存储每个会话下的具体交互记录
CREATE TABLE ChatInteraction (
    interaction_id INT AUTO_INCREMENT PRIMARY KEY,  -- 交互ID，主键，自增
    session_id INT,                                 -- 会话ID，外键
    user_id INT,                                    -- 用户ID，外键
    bot_id INT,                                     -- bot ID，外键
    interaction_req TEXT,                          -- 交互提问
    interaction_res TEXT,                          -- 交互回答
    interaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 交互时间
    FOREIGN KEY (session_id) REFERENCES ChatSession(session_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (bot_id) REFERENCES Bot(bot_id)
);

-- 用于用户主页的评论和评分
CREATE TABLE UserProfileComment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,   -- 评论ID，主键，自增
    profile_owner_id INT,                        -- 被评论用户的ID
    commenter_id INT,                            -- 评论者的用户ID
    comment_text TEXT,                           -- 评论内容
    rating INT CHECK (rating BETWEEN 1 AND 5),   -- 评分（1-5）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 评论时间
    FOREIGN KEY (profile_owner_id) REFERENCES User(user_id),
    FOREIGN KEY (commenter_id) REFERENCES User(user_id)
);

-- 用于自定义bot的评论和评分
CREATE TABLE BotComment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,   -- 评论ID，主键，自增
    bot_id INT,                                  -- 被评论的bot ID
    commenter_id INT,                            -- 评论者的用户ID
    comment_text TEXT,                           -- 评论内容
    rating INT CHECK (rating BETWEEN 1 AND 5),   -- 评分（1-5）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 评论时间
    FOREIGN KEY (bot_id) REFERENCES Bot(bot_id),   -- 关联被评论的bot
    FOREIGN KEY (commenter_id) REFERENCES User(user_id)
);


-- 汇总用户与每个机器人的交互次数
CREATE TABLE ChatSummary (
    summary_id INT AUTO_INCREMENT PRIMARY KEY,  -- 主键
    user_id INT,                                -- 用户ID
    bot_id INT,                                 -- bot ID
    interaction_count INT DEFAULT 0,            -- 交互次数
    last_interaction TIMESTAMP,                 -- 最后一次交互时间
    FOREIGN KEY (user_id) REFERENCES User(user_id),  -- 关联用户
    FOREIGN KEY (bot_id) REFERENCES Bot(bot_id)      -- 关联bot
);

-- 更新交互记录时更新ChatSummary表
DELIMITER //

CREATE TRIGGER update_chat_summary AFTER INSERT ON ChatInteraction
FOR EACH ROW
BEGIN
    DECLARE existing_count INT;
    -- 检查该用户与该bot的交互是否已有汇总
    SELECT interaction_count INTO existing_count
    FROM ChatSummary
    WHERE user_id = NEW.user_id AND bot_id = NEW.bot_id;

    IF existing_count IS NULL THEN
        -- 如果没有汇总，则插入新的记录
        INSERT INTO ChatSummary (user_id, bot_id, interaction_count, last_interaction)
        VALUES (NEW.user_id, NEW.bot_id, 1, NEW.interaction_time);
    ELSE
        -- 如果已有汇总，则更新交互次数和最后交互时间
        UPDATE ChatSummary
        SET interaction_count = interaction_count + 1,
            last_interaction = NEW.interaction_time
        WHERE user_id = NEW.user_id AND bot_id = NEW.bot_id;
    END IF;
END //

DELIMITER ;



-- 存储用户聊天的关键词及其频率
CREATE TABLE ChatTopic (
    topic_id INT AUTO_INCREMENT PRIMARY KEY,   -- 主键
    session_id INT,                            -- 关联会话ID
    user_id INT,                               -- 用户ID
    bot_id INT,                                -- bot ID
    topic VARCHAR(100),                        -- 话题/关键词
    topic_frequency INT DEFAULT 1,             -- 话题提到的频率
    FOREIGN KEY (session_id) REFERENCES ChatSession(session_id), -- 关联ChatSession
    FOREIGN KEY (user_id) REFERENCES User(user_id),              -- 关联用户
    FOREIGN KEY (bot_id) REFERENCES Bot(bot_id)                  -- 关联bot
);


