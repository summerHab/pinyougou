package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import until.FastDFSClient;

/**
 * 上传文件
 * @author ASUS
 *
 */
@RestController
public class UplodeController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;//文件服务器地址
	
	
	@RequestMapping("/uplode")
	public Result uplode(MultipartFile file){
		System.out.println("222222");
		String originalFilename = file.getOriginalFilename();//获取文件名
		
		//String	originalFilename="group1/M00/00/00/wKgZhVy_AueAYbmhAALg9OilrD0156.jpg";
		String filename= originalFilename.substring(originalFilename.lastIndexOf('.')+1);
		 try {
			FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
			String uploadFile = client.uploadFile(file.getBytes(),filename);
			String url=FILE_SERVER_URL+uploadFile;
			System.out.println(url);
			return new Result(true, url);	
			
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
		
		
	}
}
