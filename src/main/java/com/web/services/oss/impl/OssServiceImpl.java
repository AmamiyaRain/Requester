package com.web.services.oss.impl;

import cn.hutool.log.Log;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.sun.istack.NotNull;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.enums.FileTypeEnum;
import com.web.base.exceptions.BusinessException;
import com.web.configs.aliyun.AliYunOSSConfig;
import com.web.services.oss.OssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	private static final String HTTPS_PREFIX = "https://";

	private static final Log log = Log.get();

	@Resource
	OSS ossClient;

	@Resource
	AliYunOSSConfig aliYunOSSConfig;

	public String byteToHex(byte[] bytes) {
		String strHex;
		StringBuilder sb = new StringBuilder();
		for (byte aByte : bytes) {
			strHex = Integer.toHexString(aByte & 0xFF);
			// 每个字节由两个字符表示，位数不够，高位补0
			sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
		}
		return sb.toString().trim();
	}

	private String generateRandomFileName(@NotNull String imageType) {
		// 新文件名称
		String newFileName = UUID.randomUUID().toString().replace("-", "") + imageType;
		// 构建日期路径，例如：OSS目标文件夹/2020/10/31/文件名
		String filePath = DATE_TIME_FORMATTER.format(LocalDateTime.now());
		// 文件上传的路径地址
		return filePath + "/" + newFileName;
	}

	private String generateAccessPath(String fileName) {
		return String.format("%s%s.%s/%s", HTTPS_PREFIX, aliYunOSSConfig.getBucketName(), aliYunOSSConfig.getEndpoint(), fileName);
	}

	@Override
	public String uploadImage(MultipartFile imageFile) {
		final String imageType = checkImageFormat(imageFile);
		String uploadImageUrl = generateRandomFileName(imageType);
		log.info("【UploadImage】将 {} 上传至 {} 。", imageFile.getOriginalFilename(), uploadImageUrl);
		try (InputStream inputStream = imageFile.getInputStream()) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("image/*");
			ossClient.putObject(aliYunOSSConfig.getBucketName(), uploadImageUrl, inputStream, objectMetadata);
			String imageAccessAddress = generateAccessPath(uploadImageUrl);
			log.info("【UploadMessage】文件 {} 上传成功：{}", imageFile.getOriginalFilename(), imageAccessAddress);
			return imageAccessAddress;
		} catch (Exception e) {
			log.error("【UploadMessage】文件 {} 上传失败：{}", imageFile.getOriginalFilename(), e);
			throw new BusinessException(BusinessErrorEnum.UPLOAD_IMAGE_ERROR);
		}
	}

	public String checkImageFormat(MultipartFile imageFile) {
		FileTypeEnum[] supportImageFormat = {
			FileTypeEnum.JPG,
			FileTypeEnum.PNG,
			FileTypeEnum.GIF,
			FileTypeEnum.WEBP,
		};
		final String[] imageFileSuffix = new String[1];
		try {
			byte[] fileStartBytes = new byte[14];
			//noinspection ResultOfMethodCallIgnored
			imageFile.getInputStream().read(fileStartBytes);
			String byteHex = byteToHex(fileStartBytes).toUpperCase(Locale.ROOT);
			if (Arrays.stream(supportImageFormat).noneMatch(fileTypeEnum -> {
				if (byteHex.startsWith(fileTypeEnum.getStart())) {
					imageFileSuffix[0] = fileTypeEnum.getTypeName();
					return true;
				} else {
					return false;
				}
			})) {
				throw new BusinessException(BusinessErrorEnum.FILE_TYPE_ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.UNRECOGNIZED_FILE_TYPE);
		}
		return imageFileSuffix[0];
	}
}