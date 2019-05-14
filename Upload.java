package com.qhit.utils;

import ch.qos.logback.core.util.FileUtil;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.AttachmentPart;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by tp on 2019/5/7.
 */
@Component
@Configuration
public class Upload  {

//    在启动类App.class文件中配置Bean来设置文件大小
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("10240KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("102400KB");
        return factory.createMultipartConfig();
    }
    //多文件上传
    public String handleFileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        通过request获取一个文件输入流
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        //返回动态文件路径
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
         path = path.substring(1, path.length());
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        String str="";
            for (int i = 0; i < files.size(); ++i) {
                file = files.get(i);
                if (!file.isEmpty()||files.get(i).getOriginalFilename()!=null) {
                    try {
                        byte[] bytes = file.getBytes();
                        String fileName = file.getOriginalFilename();  // 文件名
                        String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
                        fileName = UUID.randomUUID() + suffixName; // 新文件名 文件名称进行加密
                        stream=new BufferedOutputStream(new FileOutputStream(new File(path+"static/Upload/" + fileName)));
                        str=str+fileName+",";//拼接多个文件的名称
                        stream.write(bytes);
                        stream.close();
                    } catch (Exception e) {
                        stream = null;

                    }
                } else {
                    return null;
                }
        }
        return path+"static/Upload/"+","+str;
    }
    //文件下载
    //传一个文件名
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = request.getParameter("fileName");
        String[] split = fileName.split(",");
        //返回动态文件路径
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
       String fullpath= path+"static/Upload/";
        // 设置文件名，根据业务需要替换成要下载的文件名
        for (int i = 0; i <split.length ; i++) {
        if (split[i] != null) {
            //设置文件路径
            String realPath = fullpath;
            //指定下载文件的路径
                File file = new File(realPath , split[i]);
                if (file.exists()) {
                    // 设置强制下载不打开
                    response.setContentType("application/force-download");
                    // 设置文件名   attachment强制下载
                    response.addHeader("Content-Disposition", "attachment;fileName=" + split[i]);
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    try {
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        OutputStream os = response.getOutputStream();
                        int a = bis.read(buffer);
                        while (a != -1) {
                            os.write(buffer, 0, a);
                            a = bis.read(buffer);
                        }
                        System.out.println("下载成功！");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }


        }
        return null;
    }


}
