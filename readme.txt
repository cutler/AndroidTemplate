项目：AndroidTemplate
作者：cutler
创建日期：2014-9-3 16:57


包结构介绍：
 -	common ： 存放当前项目中公用类。
 	- 依据功能来划分出不同的子包。  如dao、http、媒体下载、全局数据管理(manager)、广播接收者、自定义view、util(项目内部使用的工具类，即与当前项目的代码紧密耦合)等。
 	- Congif.java： 保存整个项目中所有的全局“常量”。
 	- SystemParams.java： 保存整个项目中所有的全局“变量”，以及与那些变量相关的操作方法。
 
 -	model  ：存放当前项目model类。
 	- 依据模块分为若干子包。	如model.user、model.user等。
 	- 在每个模块(block)的model包下面存放着：DAO、Entity等类。

 -	ui	：	存放当前项目的UI界面相关的类。
 	- 依据模块分为若干子包。	如ui.welcome（包含登录、注册、修改密码等Activity）、ui.message（包含消息相关的Activity）等。
 	- 在每个模块(block)的ui包下面存放着：Activity、Adapter、Fragment等类。
 
 -	util  ：	存放与当前项目无关的工具类，即这些类可以轻松的拿到其他项目中直接使用。
 	- 要始终保持本包下的类不会“依赖”到项目中的任何类。
 
 -	controller:  不是每个模块都需要用到controller的，如果activity中的业务在其被关闭后仍然需要继续，则就需要用到controller了，比如下载模块。通常controller是单例的。
 
 整体的分包思路：
 - 如果项目是这样的一个由行和列组成的表结构：
						model		ui		common		util
	message（消息模块）		xx			xx		xx			xx
	user（用户模块）		xx			xx		xx			xx
	friend（好友模块）		xx			xx		xx			xx
	shopping（商城）		xx			xx		xx			xx
	
 - 那么本人划分包的思路是：	先纵向划分出model、ui、common、util四个顶层包，然后在每个顶层包下面再横向的依据模块来划分子包。