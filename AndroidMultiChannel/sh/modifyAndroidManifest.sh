#!/bin/bash

# 应用名称，调用脚本时候传进来
apkName=$1

# 渠道信息
channels=(
huawei
xiaomi
oppo
vivo
)

init () {
	# 清除残余文件
	rm -rf target/
	rm -rf outputs/

	mkdir outputs

	# 解压应用
	./apktool d $apkName -o target

	# 获取应用名称(去除.apk后缀)
	apkName=(${apkName//./ })
}

buildPackage () {
	# -e 开启转义
	echo -e "\n========== build ${apkName}-$1.apk =========="

	# 清空上次打包残余文件
	rm -rf ./target/build/
	rm -rf ./target/dist/

	# 重新打包
	./apktool b target

	# 签名应用，替换your_keystore your_pass your_alias为对应签名信息
	jarsigner -keystore your_keystore -storepass your_pass -signedjar ./outputs/${apkName}-$1.apk ./target/dist/${apkName}.apk your_alias

	#mv ./target/dist/${apkName}.apk ./outputs/${apkName}-$1.apk
	echo "========== build ${apkName}-$1.apk done =========="
}

generatePackage () {
	# @表示数组全部元素
	for channel in ${channels[@]}
	do
		#echo "s/defaultChannel/${channel}/g"
		sed -i "" "s/defaultChannel/${channel}/g" ./target/AndroidManifest.xml
		#cat ./target/AndroidManifest.xml

		buildPackage $channel

		# 重新修改为默认，避免后续无法替换
		sed -i "" "s/${channel}/defaultChannel/g" ./target/AndroidManifest.xml
	done
}


init
generatePackage