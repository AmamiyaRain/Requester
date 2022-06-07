package com.web.configs.aliyun;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@ToString
@EqualsAndHashCode
@Component
public class AliYunOSSConfig {
	@Value("${aliOSS.endpoint}")
	private String endpoint;

	@Value("${aliOSS.accessKeyId}")
	private String accessKeyId;

	@Value("${aliOSS.accessKeySecret}")
	private String accessKeySecret;

	@Value("${aliOSS.bucketName}")
	private String bucketName;

	@Bean(name = "ossClient")
	public OSS ossClient() {
		return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
	}
}