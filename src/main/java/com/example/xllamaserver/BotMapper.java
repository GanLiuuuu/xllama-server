package com.example.xllamaserver;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BotMapper {
    @Select("""
        SELECT b.* FROM Bot b
        INNER JOIN UserBots ub ON b.id = ub.bot_id
        WHERE ub.user_email = #{email}
        ORDER BY ub.created_at DESC
    """)
    List<Bot> getBotsByUserEmail(String email);

    @Select("""
        SELECT COUNT(*) FROM UserBots ub
        WHERE ub.user_email = #{email} and ub.bot_id=#{bot};
    """)
    boolean ifUserBot(String email, Integer bot);

    @Insert("INSERT INTO UserBots(user_email, bot_id) VALUES(#{userEmail}, #{botId})")
    void addUserBot(@Param("userEmail") String userEmail, @Param("botId") Integer botId);

    @Delete("DELETE FROM UserBots WHERE user_email = #{userEmail} AND bot_id = #{botId}")
    void removeUserBot(@Param("userEmail") String userEmail, @Param("botId") Integer botId);
    @Select("SELECT * FROM Bot;")
    List<Bot> getAllBots();

    @Select("SELECT * FROM Bot WHERE state = 'Online';")
    List<Bot> getAllBotsOnline();

    @Insert("""
         INSERT INTO Bot(name,description,imgSrc,avatarUrl,version,highlight,createdBy) 
         VALUES(#{name},#{description},#{imgSrc},#{avatarUrl},#{version},#{highlight},#{createdBy});""")
    void insertBot(Bot bot);

    @Select("""
        SELECT COUNT(*) FROM Bot WHERE Bot.name = #{name} AND Bot.version = #{version} AND createdBy = #{author};""")
    boolean ifExist(String name, String version, String author);

    @Select("""
            SELECT * FROM Bot WHERE createdBy = #{author};""")
    List<Bot> selectByAuthor(String author);

    @Select("""
            SELECT * FROM Bot WHERE id = #{id};""")
    Bot selectById(Integer id);
    //TODO!!!
    @Select("""
            SELECT Bot.id, views, name, description, imgSrc, avatarUrl, price, version, state, highlight, createdBy, createdAt, lastTime FROM Bot JOIN LT ON LT.bot=Bot.id WHERE LT.user = #{user} desc LT.lastTime;""")
    List<lastUseTime> getRecent(String user);
    @Update("UPDATE Bot SET name = #{name} WHERE id = #{botid};")
    void updateName(String name,Integer botid);

    @Update("UPDATE Bot SET views=views+1 WHERE id= #{botid};")
    void updateViews(Integer botid);

    @Update("UPDATE Bot SET description = #{description} WHERE id = #{botid};")
    void updateDescription(String description, Integer botid);

    @Update("UPDATE Bot SET imgPath = #{imgSrc} WHERE id = #{botid};")
    void updateImgSrc(String imgSrc, Integer botid);

    @Update("UPDATE Bot SET avatarUrl = #{avatarUrl} WHERE id = #{botid};")
    void updateAvatarUrl(String avatarUrl, Integer botid);

    @Update("UPDATE Bot SET price = #{price} WHERE id = #{botid};")
    void updatePrice(float price, Integer botid);

    @Update("UPDATE Bot SET avatarUrl = #{highlight} WHERE id = #{botid};")
    void updateHighlight(String highlight, Integer botid);

    @Update("UPDATE Bot SET version = #{version}, createdAt = CURRENT_TIMESTAMP WHERE id = #{botid};")
    void updateVersion(String version, Integer botid);

    @Insert("INSERT INTO LT(user,bot) VALUES (#{user},#{bot});")
    void insertLT(String user,Integer bot);

    @Update("UPDATE LT SET lastTime=CURRENT_TIMESTAMP where user=#{user} and bot=#{bot}")
    void updateLT(String user,Integer bot);

    @Select("SELECT COUNT(*) FROM LT where user=#{user} and bot=#{bot}")
    boolean ifExistLT(String user,Integer bot);

    @Insert("INSERT INTO Reviews(user,bot,content,rating) VALUES (#{user},#{bot},#{content},#{rating});")
    void insertReviews(String user,Integer bot,String content,Float rating);

    @Select("SELECT IFNULL(Round(AVG(rating),2),0) FROM Reviews WHERE bot=#{botId};")
    Float ratingAvg(Integer botId);

    @Select("SELECT IFNULL(Round(AVG(rating),2),0) FROM Reviews WHERE bot=#{botId} AND TIMESTAMPDIFF(DAY, date, NOW()) < 31;")
    Float ratingAvgRecent(Integer botId);

    @Select("""
    SELECT User.username as user, Reviews.bot as bot, Reviews.content as content, Reviews.rating as rating, User.avatarURL as avatarUrl,Reviews.date as date FROM Reviews
    Join User on User.email = Reviews.user
    WHERE bot=#{botid};""")
    List<Review> showreviews(Integer botid);

    @Insert("INSERT INTO FAQs(bot,question,answer) VALUES (#{bot},#{question},#{answer});")
    void insertFAQs(Integer bot, String question,String answer);

    @Select("SELECT id,bot,question,answer FROM FAQs WHERE bot=#{botId};")
    List<FAQ> showFAQs(Integer botId);

    @Select("""
            WITH SelectedBots1 AS (
                SELECT bot_id
                FROM ChatSummary
                WHERE user_id = (SELECT user_id FROM User WHERE email = #{user})
                AND interaction_count > 30
                ORDER BY last_interaction DESC
                LIMIT 3
            ),
            RandomBots AS (
                SELECT id as bot_id
                FROM Bot
                WHERE id NOT IN (SELECT bot_id FROM SelectedBots1)
                ORDER BY RAND()
                LIMIT 3
            ),
            SelectedBots AS (
                SELECT bot_id
                FROM SelectedBots1
                UNION ALL
                SELECT bot_id
                FROM RandomBots
                LIMIT 3
            )
            , UsersOfSelectedBots AS (
                SELECT DISTINCT user_id
                FROM ChatSummary
                WHERE bot_id IN (SELECT bot_id FROM SelectedBots)
                AND interaction_count > 30
            )
            , BotUsageSummary AS (
                SELECT cs.bot_id, SUM(cs.interaction_count) AS total_interaction_count
                FROM ChatSummary cs
                JOIN UsersOfSelectedBots usb ON cs.user_id = usb.user_id
                GROUP BY cs.bot_id
            )
            SELECT b.id as id, views, name, description, imgSrc, avatarUrl, price, version, state, highlight, createdBy, createdAt
            FROM Bot b
            JOIN BotUsageSummary bus ON b.id = bus.bot_id
            ORDER BY bus.total_interaction_count DESC
            LIMIT 3;
            """)
    List<Bot> recommendBots(String user);

    @Update("""
            UPDATE User AS u
            -- 连接一个子查询，该子查询先对每个用户的calculated_value进行求和
            JOIN (
                SELECT
                    sub.createdBy,
                    SUM(sub.calculated_value) AS total_calculated_value
                FROM (
                    SELECT
                        b.createdBy,
                        (IFNULL(SUM(cs.interaction_count), 0) - b.incentive) * b.price * 0.1 AS calculated_value
                    FROM ChatSummary AS cs
                    JOIN Bot AS b ON cs.bot_id = b.id
                    GROUP BY b.id
                ) AS sub
                GROUP BY sub.createdBy
            ) AS sum_subquery ON u.email = sum_subquery.createdBy
            SET u.tokens = u.tokens + sum_subquery.total_calculated_value;
            """)
    void updateIncentive1 ();

    @Update("""
            UPDATE Bot AS b
            SET b.incentive = IFNULL((
                SELECT SUM(cs.interaction_count)
                FROM ChatSummary AS cs
                WHERE cs.bot_id = b.id
                GROUP BY cs.bot_id
            ),0);
            """)
    void updateIncentive2 ();

    @Select("SELECT id FROM Bot WHERE isOfficial = TRUE")
    List<Integer> getOfficialBotIds();
}
