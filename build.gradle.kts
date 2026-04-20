import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.vanniktech.publish) apply false
}

/////////////////////////////////////////////////////////////////////////////////////////
// [基于环境信息自动生成版本信息][Version: 20240902-01][start]/////////////////////////////////
// maven center 不支持发布 SNAPSHOT 版本
// 软件功能成熟过程支持如下 4 个阶段 "alpha、beta、rc、release"
fun execCommand(command: String): String {
    val buffer = ByteArrayOutputStream()
    exec {
        commandLine = command.split(" ")
        standardOutput = buffer
    }
    return buffer.toString().trim()
}

/**
 * 基于 git 信息计算版本号
 * @param versionCodeOffset 版本号的偏移量，默认0
 * @return git commit 数与 versionCodeOffset 的和
 */
fun genAutoVersionCode(versionCodeOffset: Int = 0): Int {
    val command = "git rev-list HEAD --count"
    val gitCommitCount = execCommand(command).toIntOrNull() ?: 0
    return gitCommitCount + versionCodeOffset
}

/**
 * 基于当前时间计算版本名
 * @param baseVersionName 业务指定的基础版本名信息
 * @param versionCode 版本号
 * @return 当前时间 baseVersionName-versionCode-yyyyMMddHHmm-commit[-SNAPSHOT]
 */
fun genAutoVersionName(baseVersionName: String, versionCode: Int): String {
    val builder = StringBuilder()
    builder.append(baseVersionName)
    builder.append("-")
    builder.append(versionCode)
    builder.append("-")
    builder.append(SimpleDateFormat("yyyyMMddHHmm").format(Date()))
    builder.append("-")
    builder.append(execCommand("git rev-parse --short HEAD"))
    return builder.toString()
}

/**
 * 生成适用于 maven 的版本名，例如 1.0.0, 1.0.0-alpha
 * @param baseVersionName 业务指定的基础版本名信息
 * @return baseVersionName
 */
fun genAutoMavenVersionName(baseVersionName: String): String {
    return baseVersionName
}
//[基于环境信息自动生成版本信息][Version: 20240902-01][end]///////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

// 版本号常见定义
// 1.0.0-alpha
// 1.0.0-alpha.1
// 1.0.0-beta
// 1.0.0-beta.1
// 1.0.0-rc
// 1.0.0-rc.1
// 1.0.0
val baseVersionName = "3.6.0"
val autoVersionCode = genAutoVersionCode()
val autoVersionName = genAutoVersionName(baseVersionName, autoVersionCode)
val autoMavenVersionName = genAutoMavenVersionName(baseVersionName)
val mavenArtifactIdSuffix = ""

println("autoVersionCode: $autoVersionCode")
println("autoVersionName: $autoVersionName")
println("autoMavenVersionName: $autoMavenVersionName")
println("mavenArtifactIdSuffix: $mavenArtifactIdSuffix")

rootProject.ext["autoVersionCode"] = autoVersionCode
rootProject.ext["autoVersionName"] = autoVersionName
rootProject.ext["autoMavenVersionName"] = autoMavenVersionName
rootProject.ext["mavenArtifactIdSuffix"] = mavenArtifactIdSuffix

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, TimeUnit.MINUTES)
    resolutionStrategy.cacheChangingModulesFor(10, TimeUnit.MINUTES)
}
