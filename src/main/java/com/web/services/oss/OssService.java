package com.web.services.oss;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
	String uploadImage(MultipartFile imageFile);
}