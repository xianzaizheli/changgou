package com.file.utils;

import com.file.pojo.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class FastDFSFileUtils {

    static {
        //从classpath下获取文件对象获取路径
        String classPathResource = new ClassPathResource("fdfs_client.conf").getPath();
        try {
            ClientGlobal.init(classPathResource);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param fastDFSFile
     * @throws Exception
     */
    public static String[] fileupload(FastDFSFile fastDFSFile) {
        try {
            //1、创建trackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //2、创建trackerServer对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //3、创建storageClient对象
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //4、创建附加的一些参数
            NameValuePair[] nameValuePairs = new NameValuePair[1];
            //将作者加入进去
            nameValuePairs[0] = new NameValuePair("author",fastDFSFile.getAuthor());

            //调用上传文件方法，第一个参数字节流，第二个参数是后缀，最后一个是附加内容
            //参数1 字节数组
            //参数2 扩展名(不带点)
            //参数3 元数据( 文件的大小,文件的作者,文件的创建时间戳)
            String[] strings = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), nameValuePairs);
            return strings;// strings[0]==group1  strings[1]=M00/00/00/wKjogF8r_Q6AJex5AACPuTXOtQQ708.jpg
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static InputStream download(String groupName , String remoteFileName){
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);

            byte[] bytes = storageClient.download_file(groupName, remoteFileName);

            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            if(null != byteArrayInputStream){
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 删除
     * @param groupName
     * @param remoteFileName
     */
    public static String filedelete(String groupName , String remoteFileName){
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            int i = storageClient.delete_file(groupName, remoteFileName);
            if(i == 0){
                return "文件删除成功";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return "文件删除失败";
    }

    /**
     * //根据组名 和文件名获取文件的信息
     * @param groupName
     * @param remoteFileName
     */
    public static void getGroupMsg(String groupName ,String remoteFileName){
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            FileInfo file_info = storageClient.get_file_info(groupName, remoteFileName);
            System.out.println(file_info);
            System.out.println("ip"+file_info.getSourceIpAddr()+",文件大小"+file_info.getFileSize());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据组名称 获取该组对应的组服务信息
     * @param groupName
     * @return
     */
    public static StorageServer getStorages(String groupName ){
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);
            return storeStorage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ServerInfo[]  getServerInfo(String groupName ,String remoteFileName){
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            ServerInfo[] fetchStorages = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return fetchStorages;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取tracker 的ip和端口的信息
    //http://192.168.211.132:8080
    public static String getTrackerInfo(){
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            String hostString = trackerServer.getInetSocketAddress().getHostString();
            int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
            return "http://"+ hostString +":"+g_tracker_http_port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
