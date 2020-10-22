package com.file;

import com.file.pojo.FastDFSFile;
import com.file.utils.FastDFSFileUtils;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageServer;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Arrays;

@SpringBootTest
public class FileTest {
    /**
     * 文件下载测试
     */
    @Test
    public void test01(){
        InputStream group1 = FastDFSFileUtils.download("group1", "M00/00/00/wKjogF8r_Q6AJex5AACPuTXOtQQ708.jpg");
        File file = new File("f:/ffff.jpg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            int b = -1 ;
            while ((b = group1.read())!= -1){
                fileOutputStream.write(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != fileOutputStream){
                    fileOutputStream.close();
                }
                if(null!= group1){
                    group1.close();
                }
            } catch (IOException e) {
            e.printStackTrace();
        }

        }
    }

    /**
     * 文件删除测试
     */
    @Test
    public void test02(){
        String group1 = FastDFSFileUtils.filedelete("group1", "M00/00/00/wKjogF8r_Q2APEneAACPuTXOtQQ282.jpg");
        System.out.println(group1);
    }

    /**
     * //根据组名 和文件名获取文件的信息
     */
    @Test
    public void test03(){
        FastDFSFileUtils.getGroupMsg("group1","M00/00/00/wKjogF8sHcWAQL3GAAkVb9hpJK4595.jpg");
        //source_ip_addr = 192.168.232.128, file_size = 595311, create_timestamp = 2020-08-06 23:12:05, crc32 = -664197970
        //ip192.168.232.128,文件大小595311
    }

    /**
     * //根据组名称 获取该组对应的组服务信息
     */
    @Test
    public void test04(){
        StorageServer storages = FastDFSFileUtils.getStorages("group1");
        System.out.println("ip:"+storages.getInetSocketAddress().getHostString()+",端口："+storages.getInetSocketAddress().getPort());
        //ip:192.168.232.128,端口：23000
    }

    /**
     * 根据文件名和组名和tracker 获取该文件所在的storage的服务器的数组信息
     */
    @Test
    public void test05(){
        ServerInfo[] group1s = FastDFSFileUtils.getServerInfo("group1", "M00/00/00/wKjogF8sHcWAQL3GAAkVb9hpJK4595.jpg");
        for (ServerInfo group : group1s){
            System.out.println("port:"+group.getPort()+",ip:"+group.getIpAddr());
            //port:23000,ip:192.168.232.128
        }
    }

    /**
     * //获取tracker的地址(ip 和端口)
     */
    @Test
    public void test06(){
        String trackerInfo = FastDFSFileUtils.getTrackerInfo();
        System.out.println(trackerInfo);
    }
}
