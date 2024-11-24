package com.example.xllamaserver;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BotMapper {
    @Select("SELECT * FROM Bot;")
    List<Bot> getAllBots();

    @Insert("""
         INSERT INTO Bot(name,description,imgSrc,avatarUrl,price,version,highlight,created_by) 
         VALUES(#{name},#{discription},#{imgSrc},#{avatarUrl},#{price},#{version},#{highlight},#{created_by});""")
    void insertBot(Bot bot);

    @Select("""
        SELECT COUNT(*) FROM Bot WHERE Bot.name = #{name} AND Bot.version = #{version} AND created_by = #{author};""")
    boolean ifexist(String name,String version,Integer author);

    @Select("""
            SELECT * FROM Bot WHERE created_by = #{author});""")
    List<Bot> selectbyauthor(Integer author);

    @Select("""
            SELECT * FROM Bot WHERE created_by = #{author});""")
    Bot selectbyid(Integer id);

    @Update("UPDATE Bot SET name = #{name} WHERE id = #{botid};")
    void updateName(String name,Integer botid);

    @Update("UPDATE Bot SET views=views+1 WHERE id= #{botid};")
    void updateviews(Integer botid);

    @Update("UPDATE Bot SET discription = #{description} WHERE id = #{botid};")
    void updatediscription(String description,Integer botid);

    @Update("UPDATE Bot SET imgPath = #{imgSrc} WHERE id = #{botid};")
    void updateimgSrc(String imgSrc,Integer botid);

    @Update("UPDATE Bot SET avatarUrl = #{avatarUrl} WHERE id = #{botid};")
    void updateavatarUrl(String avatarUrl,Integer botid);

    @Update("UPDATE Bot SET price = #{price} WHERE id = #{botid};")
    void updateprice(float price,Integer botid);

    @Update("UPDATE Bot SET avatarUrl = #{highlight} WHERE id = #{botid};")
    void updatehighlight(String highlight,Integer botid);

    @Update("UPDATE Bot SET version = #{version}, created_at = CURRENT_TIMESTAMP WHERE id = #{botid};")
    void updateversion(String version,Integer botid);

    @Insert("INSERT INTO Paid(user,bot,ifpaid) VALUES (#{user_id},#{bot_id},TRUE);")
    void insertpaid(boolean ifpaid,Integer user_id,Integer botid);

    @Update("UPDATE Paid SET ifpaid = #{ifpaid} WHERE user = #{user_id} and bot=#{botid};")
    void updatepaid(boolean ifpaid,Integer user_id,Integer botid);

    @Select("""
        SELECT COUNT(*) FROM Paid WHERE bot = #{botid} AND user = #{user_id} AND ifpaid=TRUE;""")
    boolean ifpaid(Integer user_id,Integer botid);

    @Insert("INSERT INTO Reviews(user,bot,content,rating) VALUES (#{user},#{bot},#{content},#{rating});")
    void insertreviews(Review review);

    @Select("SELECT Round(AVG(rating),2) FROM Reviews WHERE bot=#{botid};")
    Integer ratingavg(Integer botid);

    @Select("""
    SELECT Reviews.user as user, Reviews.bot as bot, Reviews.content as content, Reviews.rating as rating, User.avatar_url as avatarUrl,Reviews.date as date FROM Reviews
    Join User in User.username = Reviews.user
    WHERE bot=#{botid};""")
    List<Review> showreviews(Integer botid);

    @Insert("INSERT INTO FAQs(bot,question,answer) VALUES (#{bot},#{question},#{answer});")
    void insertFAQs(FAQ faq);

    @Select("SELECT id,bot,question,answer FROM FAQs WHERE bot=#{botid};")
    List<FAQ> showFAQs(Integer botid);
}
