# BDMap
  地图目前实现了以下功能：当前位置定位、机头方向指示、三种定位模式（普通模式、指示模式、跟随模式）、普通卫星图切换、实时路况、室内图、热力图、个性地图。

  地图使用百度地图API实现（http://lbsyun.baidu.com/），基本定位实现在百度API的开发文档中有详细说明（http://lbsyun.baidu.com/index.php?title=androidsdk），此次需要说明的有以下几个关键点：
   一、百度地图API申请自己的APK并替换manifest中的API_KEY，注意申请时需要自己程序名称和秘钥，这里可以自行百度了解。
   二、侧滑栏的个性实现通过
   ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, 需要有侧滑的界面id, mToolbar, 侧滑进入动画, 侧滑退出动画);
   实现界面的侧滑监听中在onDrawerSlide（）方法中对缩放设置，代码有说明
   三、个性地图的加载需要在onCreate（）方法前加载个性地图皮肤（setMapCustomFile方法）
   四、关键词搜索，这里是通过一个自动提示文本框AutoCompleteTextView来实现的，通过监听AutoCompleteTextView的输入内容来完成suggestionSearch地点输入提示检索（百度提供的，API中有）在对AutoCompleteTextView的item写一个适配器来匹配获取的地点提示结果，在实现item的点击事件对地图的Mark操做，实现搜索的过程。
   
