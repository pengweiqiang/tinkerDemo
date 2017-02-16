# tinkerDemo
Android tinker热修复使用
参考文档：https://github.com/Tencent/tinker/wiki


一、Gradle接入：
Gradle是推荐的接入方式，在gradle插件tinker-patch-gradle-plugin中完成了proguard、multiDex以及Manifest处理等工作。
  1）添加gradle依赖
        a).在项目的build.gradle中，添加tinker-patch-gradle-plugin的依赖
        dependencies {
                classpath 'com.android.tools.build:gradle:2.2.3'
                classpath ('com.tencent.tinker:tinker-patch-gradle-plugin:1.7.7')
                // NOTE: Do not place your application dependencies here; they belong
                // in the individual module build.gradle files
            }
        b).然后在app的gradle文件app/build.gralde,添加对tinker库依赖以及apply tinker的gradle插件
        dependencies {
            ...

            //可选 用于生成application类
            provided('com.tencent.tinker:tinker-android-anno:1.7.7')
            //tinker的核心库
            compile('com.tencent.tinker:tinker-android-lib:1.7.7')
        }
        ...
        ...
        //apply tinker插件
        apply plugin: 'com.tencent.tinker.patch'
        tinkerPatch {
            //有问题的apk的地址
            oldApk = "old.apk"
            ignoreWarning = false
            useSign = true
            buildConfig{
                tinkerId = "1.1"
            }
            packageConfig{
                //写这个为了修复一个bug,详见github issue #22
                configField("TINKER_ID", "tinker_id_1.1")
            }
            dex{
                dexMode = "jar"
                pattern = ["classes*.dex", "assets/secondary-dex-?.jar"]
                loader = ["com.tencent.tinker.loader.*", "ook.yzx.tinker.Application"]
            }
            lib{
                pattern = ["lib/armeabi/*.so","lib/arm64-v8a/*.so","lib/armeabi-v7a/*.so","lib/mips/*.so","lib/mips64/*.so","lib/x86/*.so","lib/x86_64/*.so"]
            }
            res{
                pattern = ["res/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
                largeModSize = 100
            }
            sevenZip{
                zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
            }
        }
  2）gradle参数详解
     gradle参数详解

     我们将原apk包称为基准apk包，tinkerPatch直接使用基准apk包与新编译出来的apk包做差异，得到最终的补丁包。gradle配置的参数详细解释如下：

    


     二、命令行接入
        参考：https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97

