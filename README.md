## FlyBackTool使用说明
## 一。打包

### 1.1 新的打包

#### Macair

jdk  安装：jdk-17_macos-aarch64_bin.dmg

查看安装路径：/usr/libexec/java_home



```
cd /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin

jpackage --input /Volumes/HomeWork/FlyCode/FlyBackTool/backtool-fx/target/lib/ \
  --name FlyBackTool \
  --main-jar backtool-fx-0.0.5f-SNAPSHOT.jar \
  --main-class backtool.fx.main.FxMain \
  --dest /Volumes/HomeWork/export/ \
  --type dmg \
  --app-version 20230921 \
  --java-options '--enable-preview'
```

原来

```
不要使用arm芯片版本，不支持
cd /Users/gw/Library/Java/JavaVirtualMachines/openjdk-19.0.1/Contents/Home/bin
jpackage --input /Volumes/GuanWork/gitHub/FlyBackTool/backtool-fx/target/lib/ \
--name FlyBackTool \
--main-jar backtool-fx-0.0.5f-SNAPSHOT.jar \
--main-class backtool.fx.main.FxMain \
--dest /Volumes/GuanWork/release \
--type dmg \
--app-version 20221030 \
--java-options '--enable-preview'
```
删除JDK
```
查看jdk路径
/usr/libexec/java_home -V

```

### 1.2 老的打包

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

## 二。使用说明

### 删除文件夹内重复文件

![在这里插入图片描述](https://img-blog.csdnimg.cn/3fc740e316ee424d92d5f5117b7919ac.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=800x)


### 删除双文件夹重复文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/eb12b0bf9b3d489c8d9931a7fcb2ac9e.jpg?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=800x)


### 文件夹对比
可以根据场景选择是否显示相同文件，也可以判断文件夹的包含关系。
![在这里插入图片描述](https://img-blog.csdnimg.cn/f1da8f02d69349699c418f1d236bbfa8.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=800x)


### 生成资产清单与检查资产清单
使用场景：防止光盘，压缩文件等重要资产文件通过网络传输后发生丢包或篡改
用户生成资产MD5清单，保存在文件或个人的网站或空间中，使用的时候可以检查是否改变。

生成资产清单：可以选择文件或文件夹

![在这里插入图片描述](https://img-blog.csdnimg.cn/4ae27c021b364b8da2b3150be3e05d1e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=800x)


检查资产清单：
![在这里插入图片描述](https://img-blog.csdnimg.cn/4fdb175c2c31477783ac852e2634213b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=800x)

## 三。安装说明

### MAC安装说明：

下载:[https://github.com/qryc/FlyBackTool/releases](https://github.com/qryc/FlyBackTool/releases)
MAC安装完成后，打开可能会报后如下错误：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2e1bd26e30e64de8b82c463b919caae7.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=300x)


解决：bigsur 为了安全, 新版本默认屏蔽开发者选项，可以手动开启，打开终端，输入以下命令：


```
sudo spctl --master-disable
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/9e1c029de15a41f09a1cbe5d7b15794f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAcXJ5Yw==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center=500x)

如果还是报错，可以执行如下命令
```
sudo xattr -d com.apple.quarantine /Applications/FlyBackTool.app
```



