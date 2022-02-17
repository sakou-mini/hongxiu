package com.donglai.web.config.request;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xkcoding.http.config.HttpConfig;
import com.xkcoding.http.constants.Constants;
import com.xkcoding.http.support.HttpHeader;
import com.xkcoding.http.util.MapUtil;
import lombok.Data;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.GlobalAuthUtils;
import me.zhyd.oauth.utils.HttpUtils;
import me.zhyd.oauth.utils.UrlBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.*;

import static me.zhyd.oauth.config.AuthDefaultSource.TWITTER;
import static me.zhyd.oauth.utils.GlobalAuthUtils.generateTwitterSignature;
import static me.zhyd.oauth.utils.GlobalAuthUtils.urlEncode;

/**
 * @author Moon
 * @date 2021-12-06 16:28
 */
public class AuthTwitterRequest2 extends AuthDefaultRequest {

    private static final String PREAMBLE = "OAuth";

    public AuthTwitterRequest2(AuthConfig config) {
        super(config, TWITTER);
    }

    public AuthTwitterRequest2(AuthConfig config, AuthStateCache authStateCache) {
        super(config, TWITTER, authStateCache);
    }

    @Override
    public String authorize(String state) {
        AuthToken token = this.getRequestToken();
        return UrlBuilder.fromBaseUrl(super.authorize(state))
                .queryParam("oauth_token", token.getOauthToken())
                .build();
    }


    public AuthToken getRequestToken() {
        String baseUrl = "https://api.twitter.com/oauth/request_token";

        Map<String, String> oauthParams = buildOauthParams();
        oauthParams.put("oauth_callback", config.getRedirectUri());
        oauthParams.put("oauth_signature", generateTwitterSignature(oauthParams, "POST", baseUrl, config.getClientSecret(), null));
        String header = buildHeader(oauthParams);

        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", header);
        httpHeader.add("User-Agent", "themattharris' HTTP Client");
        //httpHeader.add("Host", "api.twitter.com");
        httpHeader.add("Accept", "*/*");
        HttpUtils post = new HttpUtils(config.getHttpConfig()).post(baseUrl, null, httpHeader);

        String requestToken = post.getBody();

        Map<String, String> res = MapUtil.parseStringToMap(requestToken, false);

        return AuthToken.builder()
                .oauthToken(res.get("oauth_token"))
                .oauthTokenSecret(res.get("oauth_token_secret"))
                .oauthCallbackConfirmed(Boolean.valueOf(res.get("oauth_callback_confirmed")))
                .build();
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        Map<String, String> oauthParams = buildOauthParams();
        oauthParams.put("oauth_token", authCallback.getOauth_token());
        oauthParams.put("oauth_verifier", authCallback.getOauth_verifier());
        oauthParams.put("oauth_signature", generateTwitterSignature(oauthParams, "POST", source.accessToken(), config.getClientSecret(), authCallback
                .getOauth_token()));
        String header = buildHeader(oauthParams);

        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", header);
        httpHeader.add(Constants.CONTENT_TYPE, "application/x-www-form-urlencoded");

        Map<String, String> form = new HashMap<>(3);
        form.put("oauth_verifier", authCallback.getOauth_verifier());
        String response = new HttpUtils(config.getHttpConfig()).post(source.accessToken(), form, httpHeader, false).getBody();

        Map<String, String> requestToken = MapUtil.parseStringToMap(response, false);

        return AuthToken.builder()
                .oauthToken(requestToken.get("oauth_token"))
                .oauthTokenSecret(requestToken.get("oauth_token_secret"))
                .userId(requestToken.get("user_id"))
                .screenName(requestToken.get("screen_name"))
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        Map<String, String> queryParams = new HashMap<>(5);
        queryParams.put("include_entities", Boolean.toString(true));
        queryParams.put("include_email", Boolean.toString(true));

        Map<String, String> oauthParams = buildOauthParams();
        oauthParams.put("oauth_token", authToken.getOauthToken());

        Map<String, String> params = new HashMap<>(oauthParams);
        params.putAll(queryParams);
        oauthParams.put("oauth_signature", generateTwitterSignature(params, "GET", source.userInfo(), config.getClientSecret(), authToken.getOauthTokenSecret()));
        String header = buildHeader(oauthParams);

        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", header);
        String response = new HttpUtils(config.getHttpConfig())
                .get(userInfoUrl(authToken), null, httpHeader, false).getBody();
        JSONObject userInfo = JSONObject.parseObject(response);

        return AuthUser.builder()
                .rawUserInfo(userInfo)
                .uuid(userInfo.getString("id_str"))
                .username(userInfo.getString("screen_name"))
                .nickname(userInfo.getString("name"))
                .remark(userInfo.getString("description"))
                .avatar(userInfo.getString("profile_image_url_https"))
                .blog(userInfo.getString("url"))
                .location(userInfo.getString("location"))
                .avatar(userInfo.getString("profile_image_url"))
                .email(userInfo.getString("email"))
                .source(source.toString())
                .token(authToken)
                .build();
    }

    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo())
                .queryParam("include_entities", true)
                .queryParam("include_email", true)
                .build();
    }

    private Map<String, String> buildOauthParams() {
        Map<String, String> params = new HashMap<>(12);
        params.put("oauth_consumer_key", config.getClientId());
        params.put("oauth_nonce", GlobalAuthUtils.generateNonce(32));
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_timestamp", GlobalAuthUtils.getTimestamp());
        params.put("oauth_version", "1.0");
        return params;
    }

    private String buildHeader(Map<String, String> oauthParams) {
        final StringBuilder sb = new StringBuilder(PREAMBLE + " ");

        for (Map.Entry<String, String> param : oauthParams.entrySet()) {
            sb.append(param.getKey()).append("=\"").append(urlEncode(param.getValue())).append('"').append(", ");
        }

        return sb.deleteCharAt(sb.length() - 2).toString();
    }


    public static void main(String[] args) throws IOException {

        @Data
        class Comment{
            @ExcelProperty({"评论"})
            private String comment;
        }

        HttpConfig config = new HttpConfig();
        config.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809)));
        config.setTimeout(15000);

        Map<String,List<String>> map = new HashMap<>();
        //map.put("赛事",Lists.newArrayList("c2yd1u-rNLA","gdTTn-W8e0g","ymPFptR3qxs","aOE-jKnS3A8","kir1The74wk","8ncw2ZZn6Iw","ZFGDE9v1nLc","1xEdoU_tZGM"));
        //map.put("美食",Lists.newArrayList("7_PlbdNJEuY","6JaVwpgSBdc","sGWZFcsyMgA","QnjtnzyTNoQ","wTnqY5O9IkM","pBPXxO9E-fQ","zV5b5SpnqAQ","PF2upGJu1Lo"));
        //map.put("武术",Lists.newArrayList("xe41-mlAqFw","TToCMj9zcos","ya_0nLFwxjU","oygjOqSlgpk","SD-t1nsHS9Y","Xdnjzqi1iIY","N-ss_OfxsgA","ByOyW0SAUWU"));
        //map.put("萌宠",Lists.newArrayList("X2lIovmNsUY","MIqfww1sCb0","8z_X3dWslj8","Bomb0-996JM","hYcY2HjVa3M","bb-vmCJD3Uc","uLYgyR_lBS4","iycR1og3m5Q"));
        //map.put("DJ",Lists.newArrayList("ybrA7SqocVs","1_4ELAxKrDc","TkvoJ5rTxb8","CLjVAgPM0A0","4Rv7EYQDX08","yoZy2E17-50","t1wYBAulzWU","EA6EBzd82LA"));
        //map.put("运动",Lists.newArrayList("99uaR7S9D6g","zQDelcHOhpM","uPXQIlZ9mls","w1iS0iiiMcA","ml6cT4AZdqI","ptdK6fvW1gQ","DQZKvHvVWHM","AT8FD-clvcg"));
        //
        //map.put("母婴",Lists.newArrayList("Hf9Ww3kLUmE","OYzdcanPQAo","rcF_O0uBpIE","XOySX6sE_8s","UA-Tk9qlG9A","8BA8CcEUP84","lx9sPUtZ_Ks","4oJpyxm-Cw4"));
        //map.put("帅哥",Lists.newArrayList("o1a1Tm1nxjU","BNgAuhl15QE","fLZwZyndc58","KSqLpTeRkrs","0EhjkbyiyhE","HXdcZBRxxLQ","8PHAFmblJtk","vkGKqQmII-Q"));
        //map.put("儿童",Lists.newArrayList("fCOWKsAaDbo","hy-JRoz6d1A","ecbNXcJ3IN8","8Ig2z5nMtoQ","ONPeCDnt8Qc","QcyYEN1j110","gONZsyo9Rdk","oOOkfVxkusc"));
        //map.put("护肤",Lists.newArrayList("f0RBUfGrngg","LBdbWiq5NrQ","-DV0W4SX_B4","L0tTTU7lwg4","SnMGgymKloU","lJ3KO-Cy-mc","V3yS3QLlmKQ","2kdERihz2oQ"));
        //map.put("乐器",Lists.newArrayList("-qIzvMCU8_o","dCvEgqePl84","IvUU8joBb1Q","foSJstDFDfg","xk3BvNLeNgw","9YcrMk_cZyI","3gZC5763wYk","O2K0ptoYpuc"));
        //map.put("潜水",Lists.newArrayList("0dDZ1dLDfyg","i4ZSMDWNXTg","gvf1vq8h-ec","L4qM1IEhtNQ","sN2WM3VwVwM","GswI5TjS8PU","Pcn4Bo-V6Fc","V8D-hU-d2Dk"));
        //
        //map.put("球类",Lists.newArrayList("WLWj2LXecHU","0xDB127vF9c","C_z16ZqBAqs","1jyxdh-hWyk","2evhd8VVzBs","BZJQXToF2rs","a9KB9UY6Mt8","rxslQS6KTS4"));
        //map.put("厨艺",Lists.newArrayList("qVa6LZBRWlg","IrgWCeGeDTQ","8zEE5-bVp2E","jNKnsRbT-ZU","mhDJNfV7hjk","5rjB5JT9RC0","8IUhIDgus-c","HQGBJDAoz10"));
        //map.put("游戏",Lists.newArrayList("Vl-MZXYvjhw","gmtZS0brRH4","30el9kWGPhY","x1z18Agvsbs","iu7a2emfXLU","rxW2hL3-Ylo","GCmSgSV4aYA","wZyYwjNZ4ls"));
        //map.put("跳舞",Lists.newArrayList("zUDXj8REpAI","Uvy_o058T40","LRXMn2zoXBc","jygSZ7x7l7M","SNCQsu5uXbo","gTL4CBY6-_c","kEXQ-ex6S7k","9CEnSTruBBg"));
        //map.put("钓鱼",Lists.newArrayList("5A4Ujo9bwFM","IPxqVHgyckM","0ds-_r_fnN4","41d7ewnEVM0","UJHXTvV4oQs","-vLancr5XMw","5Elz-B3wOSU","gH4WfTkICx8"));
        //map.put("跑步",Lists.newArrayList("skA1TU7rkWo","5umbf4ps0GQ","HBb5RYpzAD0","tgfAOOGr9vM","gF0rrpMH-Jo","CwSFeTwf3_E","8YWyac1ZdsU","bn7IjgD8gPo"));
        //
        //map.put("美女",Lists.newArrayList("xgj7Ynl9Ijs","zAcH3bjMSXs","5MTztTSo6HA","RmUc7hzsXmI","-DmAhfRF6Hk","mkv_UT21Ztg","jjFGq0AMjvk","WnSVLUP-WJc"));
        //map.put("健身",Lists.newArrayList("fcN37TxBE_s","-VQXbLXTxgc","HCONVBIOKj0","jD6TqYkgT2o","QAnwUnXNp_k","dWTqW6jLAE0","8DZktowZo_k","MCmFuiabZoU"));
        //map.put("音乐",Lists.newArrayList("KrcQHT1-ecQ","niG3YMU6jFk","hHjSNZAuDj4","yznImFnedmw","PO0vpohz53M","TgCqbq8p4eA","2i2khp_npdE","eMrh3wYb1mM"));
        //map.put("唱歌",Lists.newArrayList("tlYcUqEPN58","Yc7-krRX8uA","O3VPs9b_HZE","1nnndhSc4iw","X3wYCb2tACk","pRbxlpvXw2s","OboHz6Mc5E0","UmKuyui77vw"));
        //map.put("学习",Lists.newArrayList("1ex_bNIFR1A","3NSkk31vFbU","ATDCsY-VJPA","sUuNcYxocIQ","s_Chsfe7Ic8","niRNVxobm7s","tHOWHphesuU","mh-JTCAF3Ds"));
        //map.put("体育",Lists.newArrayList("ZMPQFlXvdkc","FGOJVqFKK1c","UxbHYfq6UA0","ssuI9W59PW4","AbnjSelQYV8","luR70V5gdS0","SEwAnxEcWFI","G57_qZwir4s"));
        //
        //map.put("游泳",Lists.newArrayList("C0uBYuBmvw4","CVzMMgNNAds","kJKhOLpv0Oo","HQ5c3IFzhc8","V1e-g12KZ90","yjPyL78GYb4","Xr-RmbasUn8","2yhDbz19iXc"));
        //map.put("手工",Lists.newArrayList("EIAElVhOcXA","8pRnJMBH9J8","LEtsgDsDo_g","gsul2cHHuSc","zw8rN3x5iLA","wbLnMlPGu34","Dh2x-0H1CNc","Jl0IDXkYKbk"));
        //map.put("美妆",Lists.newArrayList("EkGOC5gs1ao","JK-fFi92sTg","XsqvGtIr9Ak","pfVyPRtXQTM","OdlZ0mC2AN4","FVuU3D-MZY4","TmtQIs4WAB8","0GzaX2781Tg"));
        //map.put("旅行",Lists.newArrayList("Qmi-Xwq-MEc","7lvXbfNBIQg","Hmu4bQxfpDA","3SsK-cxlj_w","3U1wvyFipBA","aiYpDDHKy18","sRyslbdtT90","bMMBM6g7oTw"));
        //map.put("绘画",Lists.newArrayList("kNQCP3CtHvI","oQm8Df4UYNw","l4aVbY-tJWY","gKijkM15Fso","KC5iT6IS0T8","JaFWBPOIs7s","bFYWNDCAQdY","6VJmxd8l3YQ"));
        //
        //map.put("情感",Lists.newArrayList("DsDVCQnqcy4","b197XOd9S7U","xNY0AAUtH3g","hYZmK46--Mc","oID7Nff3VBM","j0RXXVi1kBs","0gks6ceq4eQ","QE5Cr5hdf4I"));
        map.put("搞笑",Lists.newArrayList("cCeSJdqYpRc","TyaNmcNeZ4o","dU61ioDvrTw","6zz_ti1JR5s","HqxXM3bpen4","8aQFwEYPCZc","3nYhXpWzXLs","f2n3y7VuDpY"));
        //map.put("影视综艺",Lists.newArrayList("G1wsCworwWk","Rq6aM-_7Se4","65xa8TG2G8o","-LgeCBK3daw","ML3qYHWRIZk","qXVF4a_oi3g","fakdsRcm8iA","VSjp2rZejR0"));
        //map.put("科技",Lists.newArrayList("9Hxt3GttF38","f3NWvUV8MD8","TxRIdL2CDBk","Oa9aWdcCC4o","9AMPsDXGAxY","ey7DqMbkxj4","hFewXtA_-Ts","ca-dC4BvSJE"));
        //map.put("摄影",Lists.newArrayList("nMjiDjUyops","AyB-u7T-EdE","Gdol2bzWGCs","J709tS10FtA","dA7VAZ9kHSM","6QpY8cO88Zg","gxKTyCoCXQU","J709tS10FtA"));
        //map.put("家居家装",Lists.newArrayList("EzihazF3Skg","EK9dKLEwle8","IwU-QXA8ybg","7ZCeqcrftDk","H9rKxmd9PWI","QxMFLe8jVbc","6pwBg0VLskM","cfLwgEC0w5g"));
        //
        //map.put("汽车",Lists.newArrayList("llRerQFP9kg","RMyzZz6CFDY","zTXdkLKUwdE","ba7JNTI17uo","1LFEHUMbL_Q","MwGqACbEZo0","pfVODjDBFxU","BuskROjBjuY"));
        //map.put("潮流",Lists.newArrayList("JC-qGzTIpEo","SoNu7gNl1I4","UwcaZ0wcGuo","4T6ZK_kc_6c","MK8WtiFqzHg","IAoB42rHUIk","b9VA0kkaQRM","QH00Shax-jg"));
        //map.put("发型",Lists.newArrayList("wiMbQ7b25Dc","g-lxFtOpJhA","tUIeUi-zYro","CAR9faF2So8","oNxr6wP-Ve0","XLCtFZudgbw","X24rEHTxUAo","m0K967bWMOE"));
        //map.put("明星",Lists.newArrayList("FcRBzUdULAw","YokEqGFCf0M","tQ0yjYUFKAE","QsIu8m-gKM8","FVkIsr52Lxw","8dLKJvJkrXs","X7KZzk6mmjs","8TodH5lAMbY"));
        //map.put("萌娃",Lists.newArrayList("myz0kxHLnLU","Ey-MiNgronM","Vw1l19io1JQ","lidYDN2YgKY","zQ1otABUuW8","K7-QhrOaewA","lKNGRUv4gHA","Ff4_B3wQqxk"));
        //map.put("美甲",Lists.newArrayList("1VHMh6XbRR0","66jMNxcw0A4","ABYbeEYZ6DQ","GJWDwvnxykk","xYAv4_xuHDg","JuCmMpwwOvI","5in4D1tRobs","dIlv0Wb1r_s"));
        //
        //map.put("风景",Lists.newArrayList("9ZfN87gSjvI","gao-A69jYEk","uLDFcVaX4M4","BHACKCNDMW8","RzVvThhjAKw","B5unCXpegAw","ITBMT-sUeH0","LVanPoKh7Ew"));
        //map.put("生活技巧",Lists.newArrayList("GOfAqfzGqhk","Og0iEyjC9uk","C67MSxr_Sds","sItq-DKVkoI","2wPeC4CLkLU","yJWqWe19mKU","uZEan_JWcTE","wNndar-fz98"));
        map.put("数码",Lists.newArrayList("Gpq0UFYqQ8o","5Ja3qZaucJQ","-pawrewCUVo","a67mm2wTK_c","i7RWCwXSp14","uTlg2-D6GG0","fGA4pW3kPEc","3scMZPiZWtA"));
        //map.put("才艺",Lists.newArrayList("uWldKib5oRQ","Jwto6Oj93K0","DuTqWZpDwsg","IKnruE4FpuE","kkRrq4P1NSs","653FWguoY1k","FOVRkYAn0SM","PFA-RmV_wG0"));
        //map.put("正能量",Lists.newArrayList("UaxMgTvghRg","4ramOatUY80","KNlkYlFBr34","KQsGPNKVXSY","itsfQZpGXhw","X0gA2mxbjSY","1fC6RP6tZ1Q","9r9HwcaHx8E"));
        //
        //map.put("魔术",Lists.newArrayList("G2A-185WViw","xvxc_f9otiE","rgDt4MkWlH8","dxi7Wh0TBQ8","ovaLaF6Rmhg","3Wl051VW4qU","XoGo_y7TY7M","lBbBD0qN4B4"));
        //map.put("娱乐",Lists.newArrayList("-DPr3PBNnZc","qpYF-xmNMew","NlFguTSypBQ","gD-FQBpT2o0","kC_wHYMNfuk","-2UUL_aKRy8","A5yja-3F3Fw","tZZH7jInpGM"));
        //map.put("资讯",Lists.newArrayList("gQoGL87UynE","qTOwnTXu8Lc","NRppHdHtuY8","tZZH7jInpGM","FHcXLKqiPjY","10ENb4GyIpU","IudVkrJ77yM","rdBGksKDwIg"));
        //map.put("亲子",Lists.newArrayList("RDemM6FjcSE","q8vk0T443o8","sWP0PzHIeBA","J5Os_UEk04Q","ApPA9iMB2gU","FsBXPKqI9TY","1NVJTxntfYg","kHvm1J9HVLo"));
        //map.put("财经",Lists.newArrayList("wvXDB9dMdEo","WEDIj9JBTC8","ZO8XGmUA0bM","bMXTGGxrQ3A","Mm9Ipdrtpbc","ffO3VxzQReM","WthuiKZtADs","Jwk1tuwa9xM"));
        //
        //map.put("励志",Lists.newArrayList("TNIYnbj5edw","bq8eOm0zEIs","YAzTIOy0ID0","x7qOFpgX28U","xu-WuJtOl0c","eBSeCp__xhI","i9UYbJ2xMTI","fLeJJPxua3E"));
        //map.put("动漫",Lists.newArrayList("N1lSTjClKfs","8fHXMhBnyQY","tAP36cinyVI","auj8EN_dq8k","ULgjrarg3yU","MWL3ullPReY","aVCvOfWQRYw","qz2g_QfZg_0"));
        //map.put("健康",Lists.newArrayList("4WiUQtOhfIc","y-Q9RAA27lU","oKHs_-6oR6s","3__CcskNkDs","8Zwlo9AkhY0","tqcuskaReug","fbeFn1Xcqo4","jwWpTAXu-Sg"));
        //map.put("故事",Lists.newArrayList("Eyj3agIuD7Q","Qt_gMjMJzTg","c8KKryDO0oE","85NlL7t9BfU","G6sVO5UXBSg","LFuw-KVGeCo","8SXisju6KRc","uHvg7pAfgEg"));

        int maxResults = 100;

        String apiKey = "AIzaSyDD-58vGlEr4z18kJGDUlvLXx4lGIG2L24";
        String url = "https://youtube.googleapis.com/youtube/v3/commentThreads";


        List<Comment> data = new ArrayList<>();
        for (Map.Entry<String, List<String>> stringListEntry : map.entrySet()) {

            List<String> value = stringListEntry.getValue();
            for (String s : value) {
                System.out.println(s);
                String params = "part=snippet%2Creplies&maxResults=" + maxResults + "&videoId=" + s + "&key=" + apiKey;
                String res = new HttpUtils(config).get(url + "?" + params).getBody();
                Map jsonpObject = JSONObject.parseObject(res, Map.class);

                List<Map> items = (List) jsonpObject.get("items");

                for (Map item : items) {
                    Map snippet = (Map) item.get("snippet");
                    Map topLevelComment = (Map) snippet.get("topLevelComment");
                    Map snippet1 = (Map) topLevelComment.get("snippet");
                    Object textDisplay = snippet1.get("textDisplay");
                    String comment = textDisplay.toString();
                    comment = comment.replaceAll("<br>", "\n");
                    if(!comment.contains("href") && !comment.contains("<a")){
                        Comment comment1 = new Comment();
                        comment1.setComment(StringEscapeUtils.unescapeHtml4(comment));
                        data.add(comment1);
                    }
                }
            }
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyExcel没有关系
            String fileName = stringListEntry.getKey() + ".xlsx";
            EasyExcel.write("C:\\Users\\Administrator\\Desktop\\爬虫评论\\" + fileName, Comment.class).sheet().doWrite(data);
            data.clear();
        }
    }
}
