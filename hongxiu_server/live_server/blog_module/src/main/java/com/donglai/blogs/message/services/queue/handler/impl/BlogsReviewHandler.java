package com.donglai.blogs.message.services.queue.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglai.blogs.message.services.queue.handler.TriggerHandler;
import com.donglai.blogs.process.QueueProcess;
import com.donglai.common.config.VideoConfig;
import com.donglai.common.video.VideoAsyncApi;
import com.donglai.common.video.model.VideoAsync;
import com.donglai.common.video.util.ConfigUtil;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Moon
 * @date 2021-11-10 11:32
 */
@Slf4j
@Service
public class BlogsReviewHandler implements TriggerHandler {

    @Autowired
    private QueueProcess queueProcess;
    @Autowired
    private VideoConfig videoConfig;
    @Autowired
    private com.donglai.model.db.service.blogs.BlogsService blogsService;
    @Value("${resource.urlHeader}")
    private String resourceUrlHeader;
    @Autowired
    QueueExecuteService queueExecuteService;

    @Override
    public void deal(QueueExecute queueExecute) {
        //查询所有未审核的视频
        List<Blogs> unapprovedBlogs = blogsService.findByBlogsTypeAndBlogsStatus(Constant.BlogsType.BLOGS_VIDEO, Constant.BlogsStatus.BLOGS_UNAPPROVED);
        unapprovedBlogs.addAll(blogsService.findByBlogsTypeAndBlogsStatus(Constant.BlogsType.BLOGS_VIDEO, Constant.BlogsStatus.BLOGS_APPROVED));

        //TODO 删除所有审核视频
        // byBlogsTypeAndBlogsStatus.clear();
        //修改需要审核的视频状态为审核中
        for (Blogs blogs : unapprovedBlogs) {
            //如果失败三次 就转人工
            if (blogs.getFailNum() >= 3) {
                blogs.audit(Constant.BlogsStatus.BLOGS_NO_PASS);
                blogsService.save(blogs);
                //添加一条失败记录
                continue;
            }

            if (blogs.getLastTime() == 0 || (System.currentTimeMillis() - blogs.getLastTime()) >= 21600000L) {
                if (blogs.getBlogsStatus().equals(Constant.BlogsStatus.BLOGS_APPROVED)) {
                    blogs.setFailNum(blogs.getFailNum() + 1);
                }
                reviewVideo("http://" + resourceUrlHeader + blogs.getResourceUrl().get(0), blogs.getId());
                blogs.setBlogsStatus(Constant.BlogsStatus.BLOGS_APPROVED);
                blogs.setLastTime(System.currentTimeMillis());
                blogsService.save(blogs);
            }
        }
        //创建下一个定时器
        queueExecuteService.del(queueExecute);
        //queueProcess.createReviewBlogsQueue();
    }

    public void reviewVideo(String videoUrl, long id) {

    }

    public static void main(String[] args) {

        //System.out.println("\033[1;" + "32" + "m" +"32" +"="+ "绿色" + "\033[0m \n");
        double all = 0.00;
        //白酒
        String liquor = "http://fundgz.1234567.com.cn/js/161725.js?rt=1463558676006";
        //医药
        String medicine = "http://fundgz.1234567.com.cn/js/161726.js?rt=1463558676006";
        //新能源
        String newEnergy = "http://fundgz.1234567.com.cn/js/398051.js?rt=1463558676006";
        double firstLiquor = 0.00;
        double firstMedicine = 0.00;
        double firstNewEnergy = 0.00;
        try {
            for (int i = 0; i < 900; i++) {
                all = 0.00;
                String liquorRes = sendGet(liquor, "");
                String medicineRes = sendGet(medicine, "");
                String newEnergyRes = sendGet(newEnergy, "");
                Map<String, Object> a = getRes(liquorRes, 6070, "招商中证白酒");
                Map<String, Object> b = getRes(medicineRes, 0, "招商生物医疗");
                Map<String, Object> c = getRes(newEnergyRes, 158.63, "中海新能源");
                all += (double) a.get("预计收益");
                all += (double) b.get("预计收益");
                all += (double) c.get("预计收益");

                System.out.println("----------------------分---------------割---------------线----------------------");
                firstLiquor = getFirst(firstLiquor, a);
                firstMedicine = getFirst(firstMedicine, b);
                firstNewEnergy = getFirst(firstNewEnergy, c);

                if (all > 0) {
                    System.out.println("\033[1;31m" + "=====>预计总收益" + all + "\033[0m");
                } else {
                    System.out.println("\033[1;32m" + "=====>预计亏损" + all + "\033[0m");
                }
                Thread.sleep(20000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static double getFirst(double firstMedicine, Map<String, Object> res) {
        double income = Double.parseDouble(res.get("涨幅").toString());
        if (firstMedicine < income) {
            System.out.println("\033[1;31m" + res + "\033[0m");
        } else {
            if (firstMedicine == Double.parseDouble(res.get("涨幅").toString())) {
                System.out.println(res);
            } else {
                System.out.println("\033[1;32m" + res + "\033[0m");
            }
        }
        return income;
    }

    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            //for (String key : map.keySet()) {
            //    System.out.println(key + "--->" + map.get(key));
            //}
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            //System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 组装可视化数据
     *
     * @param res   数据json
     * @param money 所投金额
     * @param name  基金名称
     * @return 返回可视化数据
     */
    public static Map<String, Object> getRes(String res, double money, String name) {
        Map<String, Object> result = new HashMap<>(6);
        try {
            String substring = res.substring(8, res.length() - 2);
            JSONObject jsonObject = JSON.parseObject(substring);
            result.put("名称", name);
            result.put("时间", jsonObject.get("gztime"));
            result.put("涨幅", jsonObject.get("gszzl"));
            result.put("今日单价", jsonObject.get("gsz"));
            result.put("昨日收盘", jsonObject.get("dwjz"));
            result.put("预计收益", money * Double.parseDouble(jsonObject.get("gszzl").toString()) / 100);
        } catch (Exception e) {
            System.out.println(res);
        }
        return result;
    }
}
