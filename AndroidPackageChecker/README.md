## AndroidPackageChecker

## 工具背景

主要用于项目上线前检查包体参数配置是否正确。

### 使用方式

- 把需要检查的Android应用包和`packagechecker-x.x.x.jar`放到同一个文件夹；
- 在命令行中进入上面的文件夹目录，运行命令`java -jar packagechecker-x.x.x.jar`，等待结果输出。结果会以`JSON`格式输出到`CheckResult.txt`文件中。
