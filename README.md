# SecuritySetting
======<br/>
控制设备，软件可否卸载，应用加锁，相机，各类开关，锁屏，截屏，状态栏，基本信息等<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-044733.png" width = "30%" hight = "30%"/>
<br/>
功能1.控制各类开关，锁屏等<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045152.png" width = "30%" hight = "30%"/>
<br/>
功能2.开机密码，静音，状态栏下拉，重启，截图等<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045302.png" width = "30%" hight = "30%"/>
<br/>
功能3.应用锁，应用可否卸载，应用可否隐藏<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045316.png" width = "30%" hight = "30%"/>
<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045324.png" width = "30%" hight = "30%"/>
<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045343.png" width = "30%" hight = "30%"/>
<br/>

功能4.查看一些参数信息<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045405.png" width = "30%" hight = "30%"/>
<br/>

功能5 设备恢复出产设置<br/>
<img src = "https://github.com/Suguohei/SecuritySetting/blob/master/Screenshot_19700102-045349.png" width = "30%" hight = "30%"/>
<br/>

功能1，其实只要在应用中获取DeviceAdmin就可以控制这些开关，但是，功能2，应用可否卸载，恢复出产设置，等，都需要设备为，DeviceOwner<br/>
所以，设置DeviceOwner的方法，我只知道可以用 adb 命令来实现，<br/>
adb shell dpm remove-active-admin com.yhwxd.suguoqing.securitysetting2/com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin            移除<br/>
adb shell dpm set-device-owner com.yhwxd.suguoqing.securitysetting2/com.yhwxd.suguoqing.securitysetting2.bean.DeviceAdmin                   添加<br/>

应用锁功能，是通过，UsageStatsManager 来判断，某个应用是否正在被打开。还有其他方法也可以判断应用是否处于前台，还是后台，我们给他记录下来<br/>
https://github.com/wenmingvs/AndroidProcess<br/>



