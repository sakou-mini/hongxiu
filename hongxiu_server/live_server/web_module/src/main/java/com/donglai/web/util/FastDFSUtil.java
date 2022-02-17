package com.donglai.web.util;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

@Component
public class FastDFSUtil {

    private static ThumbImageConfig thumbImageConfig;

    private static FastFileStorageClient fastFileStorageClient;

    private static FdfsWebServer fdfsWebServer;

    public FastDFSUtil(ThumbImageConfig thumbImageConfig, FastFileStorageClient fastFileStorageClient, FdfsWebServer fdfsWebServer) {
        FastDFSUtil.thumbImageConfig = thumbImageConfig;
        FastDFSUtil.fastFileStorageClient = fastFileStorageClient;
        FastDFSUtil.fdfsWebServer = fdfsWebServer;
    }

    /**
     * @param multipartFile 文件对象
     * @return 返回文件地址
     * @author qbanxiaoli
     * @description: 上传文件
     */
    @SneakyThrows
    public static String uploadFile(MultipartFile multipartFile) {
        StorePath storePath = fastFileStorageClient.uploadFile(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), null);
        return storePath.getFullPath();
    }

    /**
     * @param multipartFile 图片对象
     * @return 返回图片地址
     * @author qbanxiaoli
     * @description: 上传缩略图
     */
    @SneakyThrows
    public static String uploadImageAndCrtThumbImage(MultipartFile multipartFile) {
        StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), null);
        return thumbImageConfig.getThumbImagePath(storePath.getFullPath());
    }

    /**
     * @param file 文件对象
     * @return 返回文件地址
     * @author qbanxiaoli
     * @description: 上传文件
     */
    @SneakyThrows
    public static String uploadFile(File file) {
        @Cleanup FileInputStream inputStream = new FileInputStream(file);
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);
        return storePath.getFullPath();
    }

    /**
     * @param file 图片对象
     * @return 返回图片地址
     * @author qbanxiaoli
     * @description: 上传缩略图
     */
    @SneakyThrows
    public static String uploadImageAndCrtThumbImage(File file) {
        @Cleanup FileInputStream inputStream = new FileInputStream(file);
        StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);
        return thumbImageConfig.getThumbImagePath(storePath.getFullPath());
    }

    /**
     * @param bytes         byte数组
     * @param fileExtension 文件扩展名
     * @return 返回文件地址
     * @author qbanxiaoli
     * @description: 将byte数组生成一个文件上传
     */
    @SneakyThrows
    public static String uploadFile(byte[] bytes, String fileExtension) {
        @Cleanup ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadFile(stream, bytes.length, fileExtension, null);
        return storePath.getFullPath();
    }

    /**
     * @param filePath 文件访问地址
     * @author qbanxiaoli
     * @description: 下载文件
     */
    @SneakyThrows
    public static byte[] downloadFile(String filePath) {
        StorePath storePath = StorePath.parseFromUrl(filePath);
        return fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
    }

    /**
     * @param filePath 文件访问地址
     * @author qbanxiaoli
     * @description: 删除文件
     */
    @SneakyThrows
    public static void deleteFile(String filePath) {
        StorePath storePath = StorePath.parseFromUrl(filePath);
        fastFileStorageClient.deleteFile(storePath.getGroup(), storePath.getPath());
    }

    // 获取文件服务器地址
    public static String getWebServerUrl() {
        return fdfsWebServer.getWebServerUrl();
    }

}