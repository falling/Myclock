#计时器

##自定义控件只是根据接收到的message信息里的 时间信息进行相应的显示。
##activity处理所有的时间信息和发送信息

*Clock_view 是计时显示的自定义控件，作用是接收时间信息，显示
里面有一个 继承Handler 的类，用于接收activity发送来的分、秒、毫秒（百位和十位）的信息
然后将它们显示在控件上面

*clock
是实现计时器功能的activity。

1. 开始计时。
开始计时，记录点击时候的系统时间（System.currentTimeMillis()）做开始时间，然后开启一个可控制结束的循环线程。
该线程的功能是，读取系统当前时间 减去 开始时间，就是过去的毫秒数，然后处理成 分、秒、毫秒的message，发送给自定义控件。

2. 暂停计时
暂停计时后，会记录点击暂停时候的系统时间，并且停止发送时间message的循环线程。

3. 继续计时
点击继续后，会读取当前的系统时间， 减去 暂停时候记录的 暂停时间，再加上 开始时间，重新赋值给 开始时间，更新开始时间。
去除暂停时候的时间影响。

3. 停止计时
停止发送时间message的循环线程。清除屏幕信息。

4. 跑圈
有一个ArrayList的集合，计时开始 的时候会加入一个数据 0 做初始化。
每次点击会加入 当前的时间-开始时间 的值到集合。然后最后一个数值减去前一个的值就是时间间隔。
转化为文本信息，加入到一个String的前端 保存起来，再把该String输出。

