package com.yzx.springbootelasticsearch.utils;

import com.yzx.springbootelasticsearch.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//用来解析网页
public class HtmlParseUtil {

    //java书
    public List<Content> parseJD(String keyword) throws IOException {
        //1. 获取请求
        String url = "https://search.jd.com/Search?keyword="+keyword;
        String cookie = "shshshfpa=7aec3ad1-94dc-3990-7895-b9b5d7436009-1700652362; shshshfpx=7aec3ad1-94dc-3990-7895-b9b5d7436009-1700652362; __jdu=1705285298812288667386; pinId=dQFccJ-j-0v_iP3C2zABJbV9-x-f3wj7; unpl=JF8EALtnAGQiaExTVRtXGBERSgkHXlgAGxILOzdWAVkMQlNQG1UYFhFObVdfXg1XFgFzZAFFXVhAVwQQAh8iEEptVF9cCkwWCmpmDWRtX0JXAk8CHxMYJV06KT5LPlNbbmdePxloSFQ1GjIbFBFPXl1aXgBCHgNsZAxdWl9NVgwcMhoiEENZZG5tDUsWAm5vAlVaWXtVNRkDGhcUSlpVXVo4AHkCImcDVVlbQlAGEwsSEhNIVF1ZWg5JHgRfZjVX; qrsc=3; user-key=e4793f12-7ad4-4744-bf9e-79a8afcc3907; __jdv=176729966|cn.bing.com|-|referral|-|1712651736333; PCSYCityID=CN_320000_320100_0; TrackID=1-vcu8chKeKN9_RMlUUsNeGUtNFZdWY6Uwere7v8lckuW58Eta1nA5t9ED-YEgpV387H-lQW0KEsm0ohjQlB3Xf9RdEVYbTOahIu8F6KTh76kjvL_vs1blJ7lntBy9cP6; thor=D855B76BFA1E85381063D63224FD6C50FF08B4935834736406F59F3D50AD78D40378FDDF9712D8DFCB29BDB55621BCA26F90E821CDE90E9983FAEA5895E37FA7010021BDD576B382169C67750F15AE37AB4AFD410848807723CA70F5D20CB09D684FB1375BE8DA7F08DDDCBF7C2A6C2BEC5800FE25690034503DC2FBFC9472DFECF37CCC6C31417650EA8D34A3534BFB03512E76EE641FFE263C56D1B72CE712; flash=2_fTJj2f81x9nmY67cLNq2lp2Ktt81Fbx2Y6SoH4abRs-cHibYQuKPiuU_f4rGeSDECgsdlW0FvCNlOvQcauqYfxM5HxEQQtAtel5n1HSvxAM*; pin=jd_5e1a78a04fed9; unick=jd_138797vcl; ceshi3.com=000; _tp=HDY%2BMErwPwSVjHVZ8D1cdcsoRmcs%2BWItBOvvfJOXSAs%3D; _pst=jd_5e1a78a04fed9; xapieid=jdd03XJH6URPCMU7I4OCNCUTJ6D6IHH5SSJZNN4465HADQZB632SP3X5UZHAWFIQ5ZGY6O7PCQQL46YVSEFCW6DQ4WBZUQYAAAAMOYH724WAAAAAADCOWTT6NQOQZXYX; jsavif=1; jsavif=1; shshshfpb=BApXe6WX3wutAGpTBZpvXJMpc0MJshdHTBkIkJE1r9xJ1Mj3JQIO2; rkv=1.0; areaId=12; ipLoc-djd=12-978-0-0; avif=1; 3AB9D23F7A4B3CSS=jdd03XJH6URPCMU7I4OCNCUTJ6D6IHH5SSJZNN4465HADQZB632SP3X5UZHAWFIQ5ZGY6O7PCQQL46YVSEFCW6DQ4WBZUQYAAAAMOYICII4QAAAAACPRWTBLAJSR2EAX; _gia_d=1; 3AB9D23F7A4B3C9B=XJH6URPCMU7I4OCNCUTJ6D6IHH5SSJZNN4465HADQZB632SP3X5UZHAWFIQ5ZGY6O7PCQQL46YVSEFCW6DQ4WBZUQY; __jda=173673530.1705285298812288667386.1705285298.1711336816.1712651736.5; __jdb=173673530.8.1705285298812288667386|5.1712651736; __jdc=173673530";
        //2. 解析网页，返回的js对象页面
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 Edg/123.0.0.0")
                .header("cookie",cookie)
                .timeout(30000).get();
        //System.out.println(document.html());
        //3. 获取全部的div
        Element goodsList = document.getElementById("J_goodsList");
        //4. 获取div中的所有li标签
        Elements liElement = goodsList.getElementsByTag("li");
        //存储最后的对象
        ArrayList<Content> list = new ArrayList<>();
        //5. 遍历获取标签中每个数据的值
        for (Element el:liElement){
            // getElementsByTag 表示根据标签获取
            // data-lazy-img 表示图片采用懒加载，可通过输入document.html()在img中找到
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");// 获取li下 第一张图片
            // select表示查找p-name下的em标签
            String title = el.select(".p-name em").eq(0).text();
            // getElementsByClass 表示根据class获取
            String price = el.getElementsByClass("p-price").eq(0).text();
            Content content = new Content(title, img, price);
            list.add(content);
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        List<Content> javaList = new HtmlParseUtil().parseJD("java书");
        for (Content content:javaList){
            System.out.println(content);
        }
    }
}
