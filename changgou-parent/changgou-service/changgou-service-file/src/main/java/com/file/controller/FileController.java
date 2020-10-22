package com.file.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.file.pojo.FastDFSFile;
import com.file.utils.FastDFSFileUtils;
import org.apache.http.HttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result fileUpload(@RequestParam(value = "file") MultipartFile file){
        try {
            FastDFSFile fastDFSFile = new FastDFSFile(
                    file.getOriginalFilename(),//原来的文件名  1234.jpg
                    file.getBytes(),//文件本身的字节数组
                    StringUtils.getFilenameExtension(file.getOriginalFilename())
            );
            String[] fileupload = FastDFSFileUtils.fileupload(fastDFSFile);
            return new Result(true , StatusCode.OK ,"上传文件成功", fileupload);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/download")
    public void fileDownload(@RequestParam(value = "groupName")String groupName,
                             @RequestParam(value = "remoteFileName")String remoteFileName,
                               HttpServletResponse httpResponse){
        InputStream download = FastDFSFileUtils.download(groupName, remoteFileName);
        OutputStream outputStream = null;
        try {
            outputStream = httpResponse.getOutputStream();
            int b ;
            while ((b= download.read()) != -1){
                outputStream.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                download.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
