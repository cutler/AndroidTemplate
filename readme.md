
## 是什么 ##
  每当公司要建立一个新项目时，我们都需要从头开始搭建项目结构，经历过几次之后就不难发现，各个项目之间其实有很多相同的模块，没必要每次都重写一遍。<br>

  虽然使用网上的各类开源库可以帮我们快速的完成任务，但是笔者认为这些库提供的功能往往都很全面，而我们却只会使用其内的几个小功能，同时若库里的方法数量过多则可能导致[《Building Apps with Over 65K Methods》](http://developer.android.com/tools/building/multidex.html)里所描述问题。
  因此笔者决定创建一个更适合自己的模板项目，每当新开启项目时都以它为起点。<br>

  `AndroidTemplate` 就是这个模板项目。

<br>
## 内容 ##
  为了防止整个项目过于臃肿，所以并没有把所有的代码都写在一个module里，而是将代码分别写在 `tp_base` 、 `tp_media` 、 `tp_translaod` 三个module里了。

     tp_base用于存放最基本的一些模块：http请求、对话框、RecyclerView、BaseActivity、各类Util类等。
     tp_media用于存放多媒体相关的类：目前只有一个流媒体播放器和GifView。
     tp_transload用于存放上传和下载模块，它支持上传下载队列。

  注意：在使用 `tp_base` 或 `tp_translaod` 里的类之前，需要调用 `Template.init(getApplication());` 方法初始化。

<br>
## 推荐阅读 ##
  如果你对笔者设计的项目架构感兴趣，可以参阅笔者写的下面三篇博文：

  [《架构篇　第一章 通用项目架构》](http://cutler.github.io/android-H01/)

  [《架构篇　第二章 Model数据管理》](http://cutler.github.io/android-H02/)

  [《架构篇　第三章 高效Android编程》](http://cutler.github.io/android-H03/)

  欢迎提出建议。

<br>
## 提示 ##
   本项目主要是笔者自己使用，所以也就并不打算写详细的文档。

   对此项目感兴趣的朋友可以任意修改、收藏，笔者不保留本项目的任何权利。