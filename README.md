# jEverything

基于Java Swing的文件快速搜索软件

jEverything索引文件列表，保存到sqlite中，搜索时直接搜索sqlite

我手边没有Windows电脑，因此没有对Windows操作系统做适配，部分功能只在macOS下可用

而且Windows上已经有[Everything](https://www.voidtools.com/zh-cn/)了，完全没必要用我这玩意

## 功能

* 使用字符串模糊匹配文件路径
* 使用空格分隔多个字符串
* 使用`^`、`$`标记匹配路径开头或结尾
* 支持单选、多选拖拽文件到访达移动（直接拖拽）或拷贝（按住option）
* 支持按空格快速预览文件（仅支持macOS）
* 右键菜单支持打开文件、打开文件所在目录、拷贝文件路径

## 备注

* 只在用户主目录建立索引
* 索引建立时间与文件数量和磁盘速度有关，大约1分钟
* 需要将`/System/Library/CoreServices/JavaLauncher.app`加入`完全磁盘访问权限`

## 例子

* 使用`^/Users/xxx/Downloads .pdf$ book`可以搜索出`/Users/xxx/Downloads`目录下，路径中带有`book`的所有 pdf 文件。

## 详细使用指南（macOS）

### 1、将`/System/Library/CoreServices/JavaLauncher.app`加入`完全磁盘访问权限`

打开`系统设置`->`隐私与安全性`->`完全磁盘访问权限`。点击加号，按`command + shift + G`，输入`/System/Library/CoreServices/JavaLauncher.app`，按回车并选择打开

### 2、下载`jEverything.jar`并打开

### 3、等待索引建立完毕

### 4、开始搜索！