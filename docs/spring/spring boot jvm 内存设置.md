# 低配（8g内存）电脑如何设置spring-boot jvm内存

```
-XX:MetaspaceSize=64m
-XX:MaxMetaspaceSize=64m
-Xms100m
-Xmx100m
-Xmn100m
-Xss256k
-XX:SurvivorRatio=8
-XX:+UseG1GC
-XX:+PrintGCDetails
```



