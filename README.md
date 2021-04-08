# UserPotrait

### 一、项目介绍

&emsp;&emsp;基于Flink的个人装扮商城群体用户画像与数据实时统计系统流式数据来源采用Java代码模拟生成，包括用户基本信息、用户浏览商品的信息、用户购买商品的信息。各信息包含字段如下：

- 用户基本信息
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210403010134997.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0OTkyNTU5,size_16,color_FFFFFF,t_70)
- 用户浏览商品的信息
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021040301024376.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0OTkyNTU5,size_16,color_FFFFFF,t_70)
- 用户购买商品的信息
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210403010311270.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0OTkyNTU5,size_16,color_FFFFFF,t_70)

### 二、需求介绍
- 首先是针对模拟生成的用户基本信息的需求集(记为A，用户后续文章指代，哈，高中英文阅读的it感觉有木有)
	- 群体用户画像之年代标签统计
	- 群体用户画像之手机网络使用偏好
	- 群体用户画像之电子邮件使用偏好
> 其实个人感觉怪怪的，这个不就是统计吗？害，就酱紫吧，无伤大雅。哎，也不雅。我好难！

- 其次是针对模拟生成的用户浏览商品的信息的需求集(记为B，原因同上)
	-	群体用户画像之当日实时品牌偏好
		-	实时包品牌偏好
		-	实时服装品牌偏好
		-	实时鞋品牌偏好
	-	各类产品(包类、服装类、鞋类)近一分钟浏览次数统计，每10s统计一次(哇，我最喜欢的一个需求，也是Flink代码最不一样的，统计图也最炫酷，我可太喜欢了，超级爱，Aa......)
	-	群体用户画像之当日实时终端偏好

- 最后是针对模拟生成的购买浏览商品的信息的需求集(记为C，原因同上)
	- 群体用户画像之当日支付类型偏好
	- 各类产品每日销售额实时Top5
	- 群体用户画像之每月实时消费水平&用户每月实时消费标签

emm......，就酱紫？就这样吧，不过大家应该也都能看出来有些需求的Flink实现基本无差异。实属就是想让结课作业看起来多一点(希望老师不要看到)。不过，大家可以自行增加，欢迎大家评论，有时间我会去完善项目的。其实我也还有一些，比如各类产品(包类、服装类、鞋类)近一分钟浏览购买统计、各类商品浏览时间TopN等。暂时这样吧，预知后事如何，请移步下一博文，即：
> 基于Flink的个人装扮商城群体用户画像与数据实时统计系统(三)-实验环境与项目结构
