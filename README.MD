# CatInject

一个自动注入依赖的框架

## 导入

mavenCentral 版本: [![Maven Central](https://img.shields.io/maven-central/v/io.github.zsqw123/cat-inject-lib)](https://search.maven.org/artifact/io.github.zsqw123/cat-inject-lib)  
mavenCentral 发布需要一定时间, 如果发现最新版本不能用, 建议稍等4小时左右

1. 在项目根 `build.gradle`:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.zsqw123:cat-inject-plugin:$version")
    }
}
```

2. 在需要用到注入的模块的 `build.gradle`:

```groovy
plugins {
    id "cat-inject"
}

dependencies {
    implementation "io.github.zsqw123:cat-inject-lib:$version"
}
```

## 使用

在需要自动注入的接口上添加注解 `@CatInject`

```kotlin
@CatInject
interface IUser {
    fun getName(): String
    fun getAge(): Int
}
```

在任意位置(任意内外部依赖均可)实现这个接口:

```kotlin
class UserImpl : IUser {
    override fun getName(): String = "my-injected-user"
    override fun getAge(): Int = 1
}

class UserImpl2 : IUser {
    override fun getName(): String = "my-injected-user2"
    override fun getAge(): Int = 2
}
```

然后在导入了依赖的库里面就可以自动注入依赖了!

```kotlin
val randomUser = CatInjects.getInstance<IUser>() // 随便获取一个实例
val age2user = CatInjects.getAllInstances<IUser>() // 获取全部实例, 筛选所需实例
	.first { it.getAge() == 2 }
```

## Thanks

[Gradle Transform + ASM 探索 (juejin.cn)](https://juejin.cn/post/6844904051331563528)

[App流畅度优化：利用字节码插桩实现一个快速排查高耗时方法的工具](https://juejin.cn/post/6844903975142047758)

[xiazunyang/discovery](https://github.com/xiazunyang/discovery)

[bytedance/ByteX: 字节码插件开发平台](https://github.com/bytedance/ByteX)