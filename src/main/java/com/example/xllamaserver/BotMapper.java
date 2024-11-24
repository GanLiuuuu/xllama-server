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
    boolean ifexist(String name,String version,int author);

    @Select("""
            SELECT * FROM Bot WHERE id = #{author});""")
    List<Bot> selectbyauthor(int author);

    @Update("UPDATE Bot SET name = #{name} WHERE id = #{botid};")
    void updateName(String name,int botid);

    @Update("UPDATE Bot SET views=views+1 WHERE id= #{botid};")
    void updateviews(int botid);

    @Update("UPDATE Bot SET discription = #{description} WHERE id = #{botid};")
    void updatediscription(String description,int botid);

    @Update("UPDATE Bot SET imgPath = #{imgSrc} WHERE id = #{botid};")
    void updateimgSrc(String imgSrc,int botid);

    @Update("UPDATE Bot SET avatarUrl = #{avatarUrl} WHERE id = #{botid};")
    void updateavatarUrl(String avatarUrl,int botid);

    @Update("UPDATE Bot SET price = #{price} WHERE id = #{botid};")
    void updateprice(float price,int botid);

    @Update("UPDATE Bot SET avatarUrl = #{highlight} WHERE id = #{botid};")
    void updatehighlight(String highlight,int botid);

    @Update("UPDATE Bot SET version = #{version}, created_at = CURRENT_TIMESTAMP WHERE id = #{botid};")
    void updateversion(String version,int botid);

    @Insert("INSERT INTO Paid(user,bot,ifpaid) VALUES (#{user_id},#{bot_id},TRUE);")
    void insertpaid(boolean ifpaid,int user_id,int botid);

    @Update("UPDATE Paid SET ifpaid = #{ifpaid} WHERE user = #{user_id} and bot=#{botid};")
    void updatepaid(boolean ifpaid,int user_id,int botid);

    @Select("""
        SELECT COUNT(*) FROM Paid WHERE bot = #{botid} AND user = #{user_id} AND ifpaid=TRUE;""")
    boolean ifpaid(int user_id,int botid);

    @Insert("INSERT INTO Reviews(user,bot,content,rating) VALUES (#{user_id},#{bot_id},#{content},#{rating});")
    void insertreviews(int user_id,int botid,String content,int rating);

    @Select("SELECT Round(AVG(rating),2) FROM Reviews WHERE bot=#{botid};")
    int ratingavg(int botid);

    @Select("SELECT id,user,content,rating FROM Reviews WHERE bot=#{botid};")
    List<Review> showreviews(int botid);

    @Insert("INSERT INTO FAQs(bot,question,answer) VALUES (#{bot_id},#{question},#{answer});")
    void insertFAQs(int botid,String question,String answer);

    @Select("SELECT id,bot,question,answer FROM FAQs WHERE bot=#{botid};")
    List<FAQ> showFAQs(int botid);
}
