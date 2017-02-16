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

     参数	默认值	描述
     tinkerPatch		全局信息相关的配置项
     tinkerEnable	true	是否打开tinker的功能。
     oldApk	null	基准apk包的路径，必须输入，否则会报错。
     ignoreWarning	false	如果出现以下的情况，并且ignoreWarning为false，我们将中断编译。因为这些情况可能会导致编译出来的patch包带来风险：
     1. minSdkVersion小于14，但是dexMode的值为"raw";
     2. 新编译的安装包出现新增的四大组件(Activity, BroadcastReceiver...)；
     3. 定义在dex.loader用于加载补丁的类不在main dex中;
     4. 定义在dex.loader用于加载补丁的类出现修改；
     5. resources.arsc改变，但没有使用applyResourceMapping编译。
     useSign	true	在运行过程中，我们需要验证基准apk包与补丁包的签名是否一致，我们是否需要为你签名。
     buildConfig		编译相关的配置项
     applyMapping	null	可选参数；在编译新的apk时候，我们希望通过保持旧apk的proguard混淆方式，从而减少补丁包的大小。这个只是推荐设置，不设置applyMapping也不会影响任何的assemble编译。
     applyResourceMapping	null	可选参数；在编译新的apk时候，我们希望通过旧apk的R.txt文件保持ResId的分配，这样不仅可以减少补丁包的大小，同时也避免由于ResId改变导致remote view异常。
     tinkerId	null	在运行过程中，我们需要验证基准apk包的tinkerId是否等于补丁包的tinkerId。这个是决定补丁包能运行在哪些基准包上面，一般来说我们可以使用git版本号、versionName等等。
     keepDexApply	false	如果我们有多个dex,编译补丁时可能会由于类的移动导致变更增多。若打开keepDexApply模式，补丁包将根据基准包的类分布来编译。
     dex		dex相关的配置项
     dexMode	jar	只能是'raw'或者'jar'。
     对于'raw'模式，我们将会保持输入dex的格式。
     对于'jar'模式，我们将会把输入dex重新压缩封装到jar。如果你的minSdkVersion小于14，你必须选择‘jar’模式，而且它更省存储空间，但是验证md5时比'raw'模式耗时。默认我们并不会去校验md5,一般情况下选择jar模式即可。
     pattern	[]	需要处理dex路径，支持*、?通配符，必须使用'/'分割。路径是相对安装包的，例如assets/...
     loader	[]	这一项非常重要，它定义了哪些类在加载补丁包的时候会用到。这些类是通过Tinker无法修改的类，也是一定要放在main dex的类。
     这里需要定义的类有：
     1. 你自己定义的Application类；
     2. Tinker库中用于加载补丁包的部分类，即com.tencent.tinker.loader.*；
     3. 如果你自定义了TinkerLoader，需要将它以及它引用的所有类也加入loader中；
     4. 其他一些你不希望被更改的类，例如Sample中的BaseBuildInfo类。这里需要注意的是，这些类的直接引用类也需要加入到loader中。或者你需要将这个类变成非preverify。
     5. 使用1.7.6版本之后版本，参数1、2会自动填写。
     lib		lib相关的配置项
     pattern	[]	需要处理lib路径，支持*、?通配符，必须使用'/'分割。与dex.pattern一致, 路径是相对安装包的，例如assets/...
     res		res相关的配置项
     pattern	[]	需要处理res路径，支持*、?通配符，必须使用'/'分割。与dex.pattern一致, 路径是相对安装包的，例如assets/...，务必注意的是，只有满足pattern的资源才会放到合成后的资源包。
     ignoreChange	[]	支持*、?通配符，必须使用'/'分割。若满足ignoreChange的pattern，在编译时会忽略该文件的新增、删除与修改。 最极端的情况，ignoreChange与上面的pattern一致，即会完全忽略所有资源的修改。
     largeModSize	100	对于修改的资源，如果大于largeModSize，我们将使用bsdiff算法。这可以降低补丁包的大小，但是会增加合成时的复杂度。默认大小为100kb
     packageConfig		用于生成补丁包中的'package_meta.txt'文件
     configField	TINKER_ID, NEW_TINKER_ID	configField("key", "value"), 默认我们自动从基准安装包与新安装包的Manifest中读取tinkerId,并自动写入configField。在这里，你可以定义其他的信息，在运行时可以通过TinkerLoadResult.getPackageConfigByName得到相应的数值。但是建议直接通过修改代码来实现，例如BuildConfig。
     sevenZip		7zip路径配置项，执行前提是useSign为true
     zipArtifact	null	例如"com.tencent.mm:SevenZip:1.1.10"，将自动根据机器属性获得对应的7za运行文件，推荐使用。
     path	7za	系统中的7za路径，例如"/usr/local/bin/7za"。path设置会覆盖zipArtifact，若都不设置，将直接使用7za去尝试。


     二、命令行接入
        参考：https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97

