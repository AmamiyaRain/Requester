# JavawebFinal
\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/\NXY666/


# knife（部署为路径/javaweb的情况下）
http://localhost:8080/javaweb/doc.html#/home


# 所有service的书写规范！

```java
if (SyncUtil.start(*DTO)) {
  try{
  if (*DTO.get*() == null||...) {
    throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
  }
  log.info("【FunctionName】用户 {} 正在*：{}", userPO.getUserName(), *DTO);
  ...
  ...
  ...
  }finally {
    SyncUtil.finish(userDeleteDTO);
	}
} else {
    throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
}
    
```

# 部署相关
>
> * tomcat 启动
>   * 启动命令：sudo bash /usr/local/tomcat/bin/startup.sh
>   * 关闭命令：sudo bash /usr/local/tomcat/bin/shutdown.sh
