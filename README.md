使用fragment和service通信的音乐播放器

2016/2/20
问题:在MyMusicFragment中添加下面两句代码，如果没有startService(),当在Fragment中unbindService()后，切换Fragment或者Fragment生命周期结束，在切回来执行播放等操作时将会重新创建MediaPlayer对象，造成同时播放两首曲子的错误。添加starService()后，服务已经创建，即使Fragment已经销毁
但是服务不会再重建，就不会造成有多个MediaPlayer对象的问题。
  getActivity().startService(intent);
  getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
可以在activity中starService(),在fragment中bindService()。
 
待添加功能:
  使用通知，退出功能，搜索功能，进度条，随机播放。
  
1.MediaPlayer虽然在服务中创建，但是若是服务停止后不释放MediaPlayer，音乐仍然播放。所以要在服务的onDestroy方法中使用release方法释放资源。
2.停止服务可以放在activity的onDestroy中，避免切换Fragment而停止服务。
