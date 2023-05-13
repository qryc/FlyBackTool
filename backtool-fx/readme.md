1.打包-windows
```
mvn  --offline -s D:\export\settings-my.xml jfx:jar
mvn  --offline -s D:\export\settings-my.xml jfx:run
exe包
mvn  --offline -s D:\export\settings-my.xml jfx:native

```

1.打包-mac
```
mvn  --offline -s /export/lib/settings-my-mac.xml jfx:jar
mvn  --offline -s /export/工作空间/杂货间-待整理/工具/maven/settings-local.xml jfx:jar

mvn  --offline -s /export/lib/settings-my-mac.xml jfx:run
exe包
mvn  --offline -s /export/lib/settings-my-mac.xml jfx:native


mvn  --offline -s /export/工作空间/杂货间-待整理/工具/maven/settings-local.xml  jfx:native
```

3.open jfx maven
```
mvn  --offline -s D:\export\settings-my.xml javafx:jlink
mvn  --offline -s D:\export\settings-my.xml javafx:run
```
4.执行jar
```
java -jar test.jar
```
