## Android生成渠道包方法详解

### 什么是渠道包

因为国内Android应用分发市场的现状，在发布APP的时候，一般需要生成多个渠道包，上传到不同的应用分发市场，这些渠道包包含不同的渠道信息，在APP上报数据时候，会带上各自的渠道信息。这样，我们就可以获取到每个分发市场的下载数和用户数等关键数据。

### 生成渠道包方式

#### Gradle生成对应渠道包

- 实现原理

  预先在`AndroidManifest.xml`文件中的`<application>`标签内添加`<meta-data>`标签用作记录渠道信息，并使用占位符的方式，在`build.gradle`文件中添加`productFlavros`替换占位符信息，然后打包对应的渠道即可。

  Apk启动时候，读取`AndroidManifest.xml`文件即可获取渠道信息。

- 优点缺点

  - 优点

    实现方式最简单方便，并且可以支持V1和V2签名。

  - 缺点

    因为使用了`productFlavors`，所以会生成不同的`BuildConfig`文件，会导致包的DEX的CRC不同，对于使用热补丁方案（如微信Tinker）的应用，如果需要发布补丁，要针对不同的渠道包发布；

    每个渠道包都要打包生成，耗时很长。

- 实现方式

  ```
  // AndroidManifest.xml
  <manifest ...>
  	<application ...>
  		<meta-data
  			android:name="channel"
  			android:value="${channelValue}" />
  	</aplication>
  </manifest>
  
  // build.gradle
  android {
  		...
  		flavorDimensions "defalut"
      productFlavors {
          xiaomi {
              manifestPlaceholders = [channelValue: "xiaomi"]
          }
          huawei {
              manifestPlaceholders = [channelValue: "huawei"]
          }
          yingyongbao {
              manifestPlaceholders = [channelValue: "yingyongbao"]
          }
      }
  //    file("channels.txt").readLines().each {
  //        channel ->
  //            productFlavors.create(channel, {
  //                manifestPlaceholders = [channelValue: channel]
  //            })
  //    }
  }
  
  // 读取渠道信息
  	private static String getChannelFromManifest(Context context) {
          ApplicationInfo applicationInfo = null;
          try {
              applicationInfo = context.getPackageManager()
                      .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
          } catch (PackageManager.NameNotFoundException e) {
              e.printStackTrace();
          }
          if (null == applicationInfo) {
              return "default";
          }
  
          Bundle metaDataBundle = applicationInfo.metaData;
          if (null == metaDataBundle) {
              return "default";
          }
  
          String channel = metaDataBundle.getString("channel");
          if (TextUtils.isEmpty(channel)) {
              return "default";
          }
  
          return channel;
      }
  ```

#### ApkTool拆包替换渠道信息

- 实现原理

  预先在`AndroidManifest.xml`文件中的`<application>`标签内添加`<meta-data>`标签用作记录渠道信息，然后使用ApkTool拆包，找到`meta-data`并替换对应的渠道信息，重新合包即可。

  Apk启动时候，读取`AndroidManifest.xml`文件即可获取渠道信息。

- 优点缺点

  - 优点

    每个渠道包不需要重新打包，速度较快。

  - 缺点

    修改了AndroidManifest.xml文件内容，需要重新签名；

    需要拆包并重新合包，Apk较大时候生成渠道包耗时较长。

- 实现方式

  ```
  // AndroidManifest.xml
  <manifest ...>
  	<application ...>
  		<meta-data
  			android:name="channel"
  			android:value="customChannel" />
  	</aplication>
  </manifest>
  
  // build.sh
  # 拆包
  apktool d target.apk
  # 替换AndroidManifest.xml文件渠道信息
  sed ... AndroidManifest.xml
  # 合包
  apktool b target
  # 签名
  jarsigner ...
  
  // 读取渠道信息
  ApplicationInfo applicationInfo = null;
  try {
  	applicationInfo = getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
  } catch (PackageManager.NameNotFoundException e) {
  	e.printStackTrace();
  }
  if (applicationInfo == null)return;
  String value = applicationInfo.metaData.getString("channel");
  ```

#### META-INF添加渠道文件(V1签名)

- 实现原理

  Apk文件的签名信息是保存在`META-INF`目录下的，对于使用V1(Jar Signature)方式签名的Apk，校验时是不会对`META-INF`目录下的文件进行校验的。利用这一特性，可以在Apk的`META-INF`目录下新建一个包含渠道名称的空文件，Apk启动时候，读取该文件即可获取渠道名称。

  Apk文件实际也是Zip文件，所以对于Java来说，使用`ZipFile`、`ZipEntry`和`ZipOutputStream`等类就能操作Apk文件并往Apk文件里面添加内容。

- 优点缺点

  - 优点

    每个渠道包不需要重新打包也不需要拆包进行修改，速度较快。

  - 缺点

    因为是利用V1签名方式的特性，所以对于V2签名方式的Apk不支持；

    应用读取渠道文件时候，需要遍历整个Apk，Apk较大时候耗时较长。

- 实现方式

  ```
  // 添加渠道文件
  private static final String META_INF_PATH = "META-INF" + File.separator;
  private static final String CHANNEL_PREFIX = "channel_";
  private static final String CHANNEL_PATH = META_INF_PATH + CHANNEL_PREFIX;
  
  public static void addChannelFile(ZipOutputStream zos, String channel, String channelId)
              throws IOException {
      // Add Channel file to META-INF
      ZipEntry emptyChannelFile = new ZipEntry(CHANNEL_PATH + channel + "_" + channelId);
      zos.putNextEntry(emptyChannelFile);
      zos.closeEntry();
  }
  
  // 读取渠道文件
  public static String getChannelByMetaInf(File apkFile) {
      if (apkFile == null || !apkFile.exists()) return "";
  
      String channel = "";
      try {
          ZipFile zipFile = new ZipFile(apkFile);
          Enumeration<? extends ZipEntry> entries = zipFile.entries();
          while (entries.hasMoreElements()) {
              ZipEntry entry = entries.nextElement();
              String name = entry.getName();
              if (name == null || name.trim().length() == 0 || !name.startsWith(META_INF_PATH)) {
                  continue;
              }
              name = name.replace(META_INF_PATH, "");
              if (name.startsWith(CHANNEL_PREFIX)) {
                  channel = name.replace(CHANNEL_PREFIX, "");
                  break;
              }
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  
      return channel;
  }
  ```

#### Apk文件添加渠道信息(V1签名)

- 实现原理

  Apk文件也是个Zip文件，每个Zip文件末尾的字节Comment就是文件的注释，修改该区域并不会影响Zip的结构，也就是不会影响Apk的安装，所以可以在Zip的注释（Comment）里面写入渠道信息用来区分每个渠道包。但需要注意的是：**Comment Length** 所记录的注释长度必须跟实际所写入的注释字节数相等，否则Apk文件安装会失败。

  Apk启动的时候，读取该文件的Comment字段获取渠道信息即可。

- 优点缺点

  - 优点

    每个渠道包不需要重新打包也不需要拆包进行修改，速度快。

  - 缺点

    因为V2签名是对整个Apk文件进行签名，所以修改了Comment内容，也需要重新签名，否则会因为签名校验失败而导致无法安装。

- 实现方式

  ```java
  // 写入渠道信息
  public static void writeFileComment(File apkFile, String data) {
      if (apkFile == null) throw new NullPointerException("Apk file can not be null");
      if (!apkFile.exists()) throw new IllegalArgumentException("Apk file is not found");
  
      int length = data.length();
      if (length > Short.MAX_VALUE) throw new IllegalArgumentException("Size out of range: " + length);
  
      RandomAccessFile accessFile = null;
      try {
          accessFile = new RandomAccessFile(apkFile, "rw");
          long index = accessFile.length();
          index -= 2; // 2 = FCL
          accessFile.seek(index);
  
          short dataLen = (short) length;
          int tempLength = dataLen + BYTE_DATA_LEN + COMMENT_MAGIC.length();
          if (tempLength > Short.MAX_VALUE) throw new IllegalArgumentException("Size out of range: " + tempLength);
  
          short fcl = (short) tempLength;
          // Write FCL
          ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          byteBuffer.putShort(fcl);
          byteBuffer.flip();
          accessFile.write(byteBuffer.array());
  
          // Write data
          accessFile.write(data.getBytes(CHARSET));
  
          // Write data len
          byteBuffer = ByteBuffer.allocate(Short.BYTES);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          byteBuffer.putShort(dataLen);
          byteBuffer.flip();
          accessFile.write(byteBuffer.array());
  
          // Write flag
          accessFile.write(COMMENT_MAGIC.getBytes(CHARSET));
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          IOUtils.closeQuietly(accessFile);
      }
  }
  
  // 读取渠道信息
  public static String readFileComment(File apkFile) {
      if (apkFile == null) throw new NullPointerException("Apk file can not be null");
      if (!apkFile.exists()) throw new IllegalArgumentException("Apk file is not found");
  
      RandomAccessFile accessFile = null;
      try {
          accessFile = new RandomAccessFile(apkFile, "r");
          FileChannel fileChannel = accessFile.getChannel();
          long index = accessFile.length();
          
          // Read flag
          index -= COMMENT_MAGIC.length();
          fileChannel.position(index);
          ByteBuffer byteBuffer = ByteBuffer.allocate(COMMENT_MAGIC.length());
          fileChannel.read(byteBuffer);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          if (!new String(byteBuffer.array(), CHARSET).equals(COMMENT_MAGIC)) {
              return "";
          }
  
          // Read dataLen
          index -= BYTE_DATA_LEN;
          fileChannel.position(index);
          byteBuffer = ByteBuffer.allocate(Short.BYTES);
          fileChannel.read(byteBuffer);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          short dataLen = byteBuffer.getShort(0);
  
          // Read data
          index -= dataLen;
          fileChannel.position(index);
          byteBuffer = ByteBuffer.allocate(dataLen);
          fileChannel.read(byteBuffer);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          return new String(byteBuffer.array(), CHARSET);
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          IOUtils.closeQuietly(accessFile);
      }
      return "";
  }
  ```

#### Apk文件添加渠道信息(V2签名)

- 实现原理

  因为V2签名是对对整个Apk文件进行签名，修改文件的任何内容都会导致签名失效，只有一个地方`APK Signing Block`这个区域是不受签名校验规则保护的，可以利用这一特性，写入`ID-value`来保存我们的渠道信息。

- 优点缺点

  - 优点

    每个渠道包不需要重新打包也不需要拆包进行修改，速度快。

  - 缺点

    如果一个包没有支持V2签名的情况需要适配。

- 实现方式

  略。

### 常见问题

- 加固后是否会影响渠道信息获取

  需要验证先加固然后再生成渠道包是否还可以正常获取。

### 参考内容

[keytool](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html)

[apksigner](https://developer.android.com/studio/command-line/apksigner)

[应用签名](https://source.android.com/security/apksigning)

[为应用签名](https://developer.android.com/studio/publish/app-signing)

[Apk文件Comment区域写入信息](https://blog.csdn.net/kongpinde/article/details/51518466)

[Android多渠道包生成最佳实践（一）](https://juejin.cn/post/6844903591220609038)

[Android多渠道包生成最佳实践（二）](https://juejin.cn/post/6844903591392739342)

[Android-打包与快速打包](https://sq.163yun.com/blog/article/170607227994300416)

[新一代开源Android渠道包生成工具walle](https://tech.meituan.com/2017/01/13/android-apk-v2-signature-scheme.html)



[美团多渠道打包方案-Github](https://github.com/Meituan-Dianping/walle)

[腾讯多渠道打包方案-Github](https://github.com/Tencent/VasDolly)

