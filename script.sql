create table User
(
    user_id    int auto_increment
        primary key,
    username   varchar(50)                         not null,
    password   varchar(255)                        not null,
    email      varchar(100)                        unique ,
    userType   varchar(255)                        null,
    avatarUrl  varchar(255)                        null,
    bio        text                                null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    points     int       default 0                 null,
    tokens     int       default 0                 null,
    coverPhoto varchar(255)                        null,
    about      varchar(255)                        null,
    firstname  varchar(255)                        null,
    lastname   varchar(255)                        null,
    country    varchar(255)                        null
);

create table UserProfileComment
(
    comment_id       int auto_increment
        primary key,
    profile_owner_id int                                 null,
    commenter_id     int                                 null,
    comment_text     text                                null,
    rating           int                                 null,
    created_at       timestamp default CURRENT_TIMESTAMP null,
    constraint userprofilecomment_ibfk_1
        foreign key (profile_owner_id) references User (user_id),
    constraint userprofilecomment_ibfk_2
        foreign key (commenter_id) references User (user_id),
    constraint userprofilecomment_chk_1
        check (`rating` between 1 and 5)
);

CREATE TABLE Bot (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    views INT DEFAULT 0,
    description TEXT NOT NULL,
    isOfficial BOOLEAN DEFAULT FALSE,
    imgSrc TEXT,
    avatarUrl TEXT NOT NULL,
    price FLOAT DEFAULT 0.0 NOT NULL,
    version TEXT NOT NULL,
    highlight TEXT NOT NULL,
    state ENUM('Offline','Online','Error') DEFAULT 'Offline' NOT NULL,
    incentive FLOAT DEFAULT 0.0 NOT NULL ,
    createdBy VARCHAR(100) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (createdBy) REFERENCES User(email)
);

CREATE TABLE LT(
    user VARCHAR(100) NOT NULL,
    bot INT NOT NULL,
    lastTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user) REFERENCES User(email),
    FOREIGN KEY (bot) REFERENCES Bot(id)
);

CREATE TABLE Reviews(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(100),
    bot INT,
    content TEXT,
    rating FLOAT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    FOREIGN KEY (user) REFERENCES User(email),
    FOREIGN KEY (bot) REFERENCES Bot(id)
);

CREATE TABLE FAQs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    question TEXT,
    answer TEXT,
    bot INT,
    FOREIGN KEY (bot) REFERENCES Bot(id)
);
create table BotComment
(
    comment_id   int auto_increment
        primary key,
    bot_id       int                                 null,
    commenter_id int                                 null,
    comment_text text                                null,
    rating       int                                 null,
    created_at   timestamp default CURRENT_TIMESTAMP null,
    constraint botcomment_ibfk_1
        foreign key (bot_id) references Bot (id),
    constraint botcomment_ibfk_2
        foreign key (commenter_id) references User (user_id),
    constraint botcomment_chk_1
        check (`rating` between 1 and 5)
);


create table ChatSession
(
    session_id       int auto_increment
        primary key,
    user_id          int                                 null,
    bot_id           int                                 null,
    session_name     varchar(100)                        null,
    created_at       timestamp default CURRENT_TIMESTAMP null,
    last_interaction timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint chatsession_ibfk_1
        foreign key (user_id) references User (user_id),
    constraint chatsession_ibfk_2
        foreign key (bot_id) references Bot (id)
);


create table ChatInteraction
(
    interaction_id   int auto_increment
        primary key,
    session_id       int                                 null,
    user_id          int                                 null,
    bot_id           int                                 null,
    interaction_req  text                                null,
    interaction_res  text                                null,
    interaction_time timestamp default CURRENT_TIMESTAMP null,
    constraint chatinteraction_ibfk_1
        foreign key (session_id) references ChatSession (session_id),
    constraint chatinteraction_ibfk_2
        foreign key (user_id) references User (user_id),
    constraint chatinteraction_ibfk_3
        foreign key (bot_id) references Bot (id)
);


create definer = root@localhost trigger update_chat_summary
    after insert
    on ChatInteraction
    for each row
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
END;



create table ChatSummary
(
    summary_id        int auto_increment
        primary key,
    user_id           int           null,
    bot_id            int           null,
    interaction_count int default 0 null,
    last_interaction  timestamp     null,
    constraint chatsummary_ibfk_1
        foreign key (user_id) references User (user_id),
    constraint chatsummary_ibfk_2
        foreign key (bot_id) references Bot (id)
);

create table ChatTopic
(
    topic_id        int auto_increment
        primary key,
    session_id      int           null,
    user_id         int           null,
    bot_id          int           null,
    topic           varchar(100)  null,
    topic_frequency int default 1 null,
    constraint chattopic_ibfk_1
        foreign key (session_id) references ChatSession (session_id),
    constraint chattopic_ibfk_2
        foreign key (user_id) references User (user_id),
    constraint chattopic_ibfk_3
        foreign key (bot_id) references Bot (id)
);
CREATE TABLE UserBots (
      id INT AUTO_INCREMENT PRIMARY KEY,
      user_email VARCHAR(100) NOT NULL,
      bot_id INT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (user_email) REFERENCES User(email),
      FOREIGN KEY (bot_id) REFERENCES Bot(id)
);

-- 首先创建管理员用户
INSERT INTO User (email, username, password, userType)
VALUES ('admin@system.com', 'admin', '123123', 'admin');
INSERT INTO User (email, username, password, userType)
VALUES ('3163781466@qq.com', 'lg', '123123', 'regular');

-- 禁用自增和外键检查

-- 插入默认bot
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('GPT3.5-turbo', 'General purpose chat model with broad knowledge', TRUE, 'https://ehall.sustech.edu.cn/new/portal/custom/img/logo/logo-mini.png', 'https://cas.sustech.edu.cn/cas/images/sustech.png', 6.99, '1.0', 'highlight introduction', '3163781466@qq.com');
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('GPT4-turbo', 'Most capable GPT-4 model for general tasks', TRUE, 'https://cdn.pixabay.com/photo/2020/03/08/05/11/android-4911426_1280.png', 'https://cdn.pixabay.com/photo/2017/01/31/17/43/android-2025857_1280.png', 19.99, '1.0', 'highlight introduction', '3163781466@qq.com');
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('GPT4-mini', 'Smaller, faster version of GPT-4', FALSE, 'https://cdn.pixabay.com/photo/2020/03/07/08/16/android-4909082_1280.png', 'https://cdn.pixabay.com/photo/2020/03/08/05/07/android-4911417_1280.png', 16.99, '1.0', 'highlight introduction', '3163781466@qq.com');
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('DALL-E 3', 'Advanced image generation model', FALSE, 'https://cdn.pixabay.com/photo/2024/08/29/05/21/robot-9005785_1280.png', 'https://cdn.pixabay.com/photo/2016/08/23/05/45/bot-1613604_1280.png', 8.99, '1.0', 'highlight introduction', '3163781466@qq.com');
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('Translator', 'Specialized in language translation', FALSE, 'https://cdn.pixabay.com/photo/2020/02/13/02/34/robot-4844306_1280.png', 'https://cdn.pixabay.com/photo/2020/03/08/05/07/android-4911414_1280.png', 9.99, '1.0', 'highlight introduction', '3163781466@qq.com');
INSERT INTO Bot (name, description, is_official, imgSrc, avatarUrl, price, version, highlight, createdBy)
VALUES ('Math Solver', 'Specialized in solving mathematical problems', FALSE, 'https://pixabay.com/illustrations/ai-generated-brain-mind-technology-8563260/', 'https://cdn.pixabay.com/photo/2020/03/08/05/07/android-4911416_1280.png', 9.99, '1.0', 'highlight introduction', '3163781466@qq.com');

-- 重新启用自增和外键检查
ALTER TABLE Bot AUTO_INCREMENT = 4;
