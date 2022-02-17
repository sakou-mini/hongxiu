package com.donglaistd.jinli.util;

import com.donglaistd.jinli.http.entity.GuessBetInfo;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.Collectors;

public class PostUtil {

    public static String doPostWithFile(String url,String savefileName,String fileName, String param) {
        String result = "";
        try {
            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "232";
            // 服务器的域名
            URL realurl = new URL("http://localhost:18880/uploadImage");
            // 发送POST请求必须设置如下两行
            HttpURLConnection connection = (HttpURLConnection) realurl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestProperty("Charset","UTF-8");
            connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            // 头
            String boundary = BOUNDARY;
            // 传输内容
            StringBuffer contentBody =new StringBuffer("--" + BOUNDARY);
            // 尾
            String endBoundary ="\r\n--" + boundary + "--\r\n";
            //输出
            OutputStream out = connection.getOutputStream();
            // 1. 处理普通表单域(即形如key = value对)的POST请求（这里也可以循环处理多个字段，或直接给json）
            //这里看过其他的资料，都没有尝试成功是因为下面多给了个Content-Type
            //form-data  这个是form上传 可以模拟任何类型
            contentBody.append("\r\n")
                    .append("Content-Disposition: form-data; name=\"")
                    .append("param" + "\"")
                    .append("\r\n")
                    .append("\r\n")
                    .append(param)
                    .append("\r\n")
                    .append("--")
                    .append(boundary);
            String boundaryMessage1 =contentBody.toString();
            System.out.println(boundaryMessage1);
            out.write(boundaryMessage1.getBytes("utf-8"));

            // 2. 处理file文件的POST请求（多个file可以循环处理）
            contentBody = new StringBuffer();
            contentBody.append("\r\n")
                    .append("Content-Disposition:form-data; name=\"")
                    .append("file" +"\"; ")   // form中field的名称
                    .append("filename=\"")
                    .append(fileName +"\"")   //上传文件的文件名，包括目录
                    .append("\r\n")
                    .append("Content-Type:multipart/form-data")
                    .append("\r\n\r\n");
            String boundaryMessage2 = contentBody.toString();
            System.out.println(boundaryMessage2);
            out.write(boundaryMessage2.getBytes("utf-8"));

            // 开始真正向服务器写文件
            File file = new File(savefileName);
            DataInputStream dis= new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut =new byte[(int) file.length()];
            bytes =dis.read(bufferOut);
            out.write(bufferOut,0, bytes);
            dis.close();
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 4. 从服务器获得回答的内容
            String strLine="";
            String strResponse ="";
            InputStream in =connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while((strLine =reader.readLine()) != null)
            {
                strResponse +=strLine +"\n";
            }
            System.out.print(strResponse);
            return strResponse;
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return result;
    }

    @Test
    public void test(){
        String result = doPostWithFile("", "C:\\Users\\123\\Pictures\\Saved Pictures\\p3.jpg", "pic1", "p2");
    }


    public static String creatFile(String from,String to,String diaryId){
        FileChannel input = null;
        FileChannel output = null;
        try {
            input = new FileInputStream(new File(from)).getChannel();
            File toFile = new File(to);
            if(toFile.isDirectory()){
                toFile.mkdirs();
            }
            toFile = new File(toFile.toPath().toString() + "/" + diaryId + ".jpg");
            toFile.createNewFile();
            output = new FileOutputStream(toFile).getChannel();
            output.transferFrom(input, 0, input.size());
            return toFile.toPath().toString();
        } catch (Exception e) {
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Test
    public void testVerify(){
        Integer num = null;
        boolean b = verifyTxt(num);
        System.out.println(b);

    }

    public boolean verifyTxt(Integer num){
        return Objects.nonNull(num) && (Objects.equals(1, num.intValue()) || Objects.equals(2, num.intValue()));
    }

    @Test
    public void test2(){
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        ListIterator it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            System.out.println(it.previous());
        }
      /*  Collections.reverse(list);
        System.out.println(list);*/
      /*  ListIterator<Integer> li = list.listIterator();
        *//*for (li = list.listIterator(); li.hasNext();) {// 将游标定位到列表结尾
            Integer next = li.next();
            System.out.println(next);
        }*//*
        for (;li.hasPrevious();) {
            System.out.print(li.previous());
        }*/
    }

    //分组统计
    @Test
    public void streamGroupTest(){
        List<GuessBetInfo> guessBetInfos = new ArrayList<>();
        GuessBetInfo guessBetInfo = new GuessBetInfo();
        guessBetInfo.setBetNum(2l);
        guessBetInfo.setOptionContent("item1");
        guessBetInfo.setProfitLoss(200l);
        guessBetInfos.add(guessBetInfo);

        GuessBetInfo guessBetInfo2 = new GuessBetInfo();
        guessBetInfo2.setBetNum(5l);
        guessBetInfo2.setOptionContent("item1");
        guessBetInfo2.setProfitLoss(400l);
        guessBetInfos.add(guessBetInfo2);

        GuessBetInfo guessBetInfo3 = new GuessBetInfo();
        guessBetInfo3.setBetNum(5l);
        guessBetInfo3.setOptionContent("item2");
        guessBetInfo3.setProfitLoss(400l);
        guessBetInfos.add(guessBetInfo3);

        Map<String, Long> collect = guessBetInfos.parallelStream().collect(Collectors.groupingBy(GuessBetInfo::getOptionContent,
                Collectors.summingLong(betInfo -> betInfo.getBetNum() * betInfo.getProfitLoss())));

        System.out.println(collect);
    }

    @Test
    public void test4(){
        BigDecimal result = BigDecimal.valueOf(450050).divide(BigDecimal.valueOf(800050),56,RoundingMode.UP).multiply(BigDecimal.valueOf(1560030l));
        //System.out.println(result);
        System.out.println(450050/800050.0);
        // (本金/获胜方总金额* 失败方总金额 + 本金)*90%
        //
    }
}
