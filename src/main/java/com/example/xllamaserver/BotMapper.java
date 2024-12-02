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

    @Insert("INSERT INTO UserBots(user_email, bot_id) VALUES(#{userEmail}, #{botId})")
    void addUserBot(@Param("userEmail") String userEmail, @Param("botId") Integer botId);

    @Delete("DELETE FROM UserBots WHERE user_email = #{userEmail} AND bot_id = #{botId}")
    void removeUserBot(@Param("userEmail") String userEmail, @Param("botId") Integer botId);
    @Select("SELECT * FROM Bot;")
    List<Bot> getAllBots();

    @Insert("""
         INSERT INTO Bot(name,description,imgSrc,avatarUrl,price,version,highlight,createdBy) 
         VALUES(#{name},#{discription},#{imgSrc},#{avatarUrl},#{price},#{version},#{highlight},#{created_by});""")
    void insertBot(Bot bot);

    @Select("""
        SELECT COUNT(*) FROM Bot WHERE Bot.name = #{name} AND Bot.version = #{version} AND created_by = #{author};""")
    boolean ifExist(String name, String version, String author);

    @Select("""
            SELECT * FROM Bot WHERE createdBy = #{author});""")
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

    @Update("UPDATE Bot SET discription = #{description} WHERE id = #{botid};")
    void updateDiscription(String description, Integer botid);

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
    Integer ratingAvg(Integer botId);

    @Select("""
    SELECT User.username as user, Reviews.bot as bot, Reviews.content as content, Reviews.rating as rating, User.avatarURL as avatarUrl,Reviews.date as date FROM Reviews
    Join User on User.email = Reviews.user
    WHERE bot=#{botid};""")
    List<Review> showreviews(Integer botid);

    @Insert("INSERT INTO FAQs(bot,question,answer) VALUES (#{bot},#{question},#{answer});")
    void insertFAQs(Integer bot, String question,String answer);

    @Select("SELECT id,bot,question,answer FROM FAQs WHERE bot=#{botId};")
    List<FAQ> showFAQs(Integer botId);
}
