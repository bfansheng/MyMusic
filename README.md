使用fragment和service通信的音乐播放器

2016/2/20 
问题:在MyMusicFragment中添加下面两句代码，如果没有startService(),当在Fragment中unbindService()后，切换Fragment或者Fragment生命周期结束，在切回来执行播放等操作时将会重新创建MediaPlayer对象，造成同时播放两首曲子的错误。
  getActivity().startService(intent);
  getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
 
待添加功能:
  使用通知，退出功能，搜索功能，进度条，随机播放。
