# AndroidMultiChannel

Android Channel （Android 渠道包）

### 目录说明

#### app

- 说明

  Android应用目录，用来展示当前应用的渠道信息。

- 使用

  替换`build.gradle`文件中的签名相关内容，然后使用`assemble`指令来生成签名后的应用，再调用`main`模块生成的`jar`包添加渠道信息，再运行即可查看添加的渠道信息；

  可以通过修改当前目录下的`build.gradle`文件内的`productFlavors`相关内容，然后通过`assemble`指令来生成对应渠道包应用。

#### channel

- 说明

  基于V1签名的渠道工具包，包括渠道信息的读取和写入。

- 使用

  依赖该模块后即可调用`ChannelUtil`对应的接口进行渠道信息的读取和写入。

#### main

- 说明

  Java程序，用来在PC端调用生成对应的渠道包应用。

- 使用

  调用`shadowJar`指令来生成`jar`包（如果使用`assemble`指令并不会把依赖模块代码添加进去），然后即可通过命令给指定应用添加渠道信息。

  >java -jar channelutil.jar -apk=xxx.apk -c=xiaomi,huawei
  >
  >java -jar channelutil.jar -apk=xxx.apk -f=channels.txt
  >
  >参数说明：
  >
  >-apk：表示需要添加渠道信息的应用
  >
  >-c：表示渠道，多个渠道用`,`进行分割
  >
  >-f：表示使用文件作为渠道来源，每个渠道作为单独一行

### 参考内容

[应用签名](https://source.android.com/security/apksigning)

[Android 新一代多渠道打包神器](https://zhuanlan.zhihu.com/p/26674427)

[新一代开源Android渠道包生成工具walle](https://tech.meituan.com/2017/01/13/android-apk-v2-signature-scheme.html)

[美团多渠道打包方案-Github](https://github.com/Meituan-Dianping/walle)

[腾讯多渠道打包方案-Github](https://github.com/Tencent/VasDolly)

[packer-ng-plugin-Github](https://github.com/mcxiaoke/packer-ng-plugin)