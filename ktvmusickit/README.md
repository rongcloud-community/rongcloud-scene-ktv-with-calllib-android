# 模块简介

KTVMusicKit是一个工具模块，共分为三个功能模块：歌词屏幕，点歌台，混响音效弹框，均集成在KtvMusicKit这个模块中并进行了相应分组。
注意：歌词屏幕，点歌台，混响音效弹框都可以单独被开发者调用或使用，完全无相互依赖关系。

## 歌词屏幕

### 简介

首先歌词屏幕是一个自定义view控件 `SongScreenView`，里面包含了一个歌词组件LrcView和四个点击按钮，开发者可以传入歌词文件本地路径且连续传入时间来让歌词产生对应时间的动效，用户也可以点击四项按钮根据对应的点击回调方法在房间进行自己的需求处理

### 歌词屏幕使用方式：

1. 先保证成功依赖KTVMusicKit这个module，然后将SongScreenView作为普通组件放入自己的layout布局中：

```xml
    <cn.rongcloud.ktvmusickit.view.SongScreenView
        android:id="@+id/ssv_screen"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_margin="16dp"
        android:background="@drawable/ktv_ic_song_screen_bg"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

2. 然后在对应java中获取到SongScreenView控件对象。

```java
SongScreenView songScreenView = findViewById(R.id.xxx);
```

3. 获取歌词管理者
```java
//获取歌词管理者
LrcManager mLrcManager = mSongScreenView.getLrcManager();
```

4. 设置歌词文件路径
```java
//每首歌播放前必然要设置歌词本地路径
mLrcManager.setLrcFile(clickedSongBean.getLrcPath());
```

5. 设置当天播放时间进度
```java
//随着歌曲的播放连续传入进度时间（毫秒），歌词组件内部会根据时间来决定歌词的动效和位置
//进度传入的频率越高越密集越好，这会直接影响歌词动效卡不卡顿，建议100-200毫秒传一次进度
mLrcManager.setCurrentTime((long) (progress * duration));
```

6. 清空屏幕
```java
//播放完毕的时候可以调用此方法来刷新屏幕提示
mLrcManager.playEnd();
```

7. 显示控制
```java
//设置身份来决定屏幕四个按钮谁显示
mSongScreenView.setKtvScreenRole(KTV_SCREEN_ROLE_LEAD_SINGER);
```

8. 屏幕事件监听
```java
//设置四个按钮点击监听
mSongScreenView.setSongScreenListener( KtvSongScreenListener）
```

9. 播放暂停
```java
//主动设置屏幕的播放按钮图标
mSongScreenView.setPlay(true);
```

10. 原唱伴唱
```java
//主动切换屏幕的原伴唱图标
mSongScreenView.setOrigin(!isOrigin);
```

## 点歌台
### 简介：
点歌台的UI其实是弹框类型内容：总弹框为：`KtvSongPlatformDialog` ，普通音乐列表弹框为 `SongListFragment`，已点音乐列表弹框为 `ClickedSongListFragment` ，`KtvSongPlatformDialog`包含了2个列表，并涉及网络音乐列表，用户可以在普通列表点歌，下载点歌完成歌曲自动被置到已点列表，并通过 selectedMusic 回调自动传递给房间，开发者拿到已点列表后可以让房间进行歌曲资源播放，通过屏幕的按钮进行自己需求的音乐下一首，暂停等需求。
点歌台网络请求包含：普通音乐列表，已点音乐列表，点歌，置顶，删除，搜索，这些请求方法均在 `KtvMusicManager` 中，外部可以直接调用，而kit也是直接调用的这些方法来进行的网络请求

### 点歌台接入方式：
1. 由于网络请求要用到房间id，userid，用户头像，所以第一步必须在房间初始化的时候传入这些参数：

```java
KtvMusicManager.getInstance().init(mRoomId, UserManager.get().getUserId(), UserManager.get().getPortraitUrl(), ApiConfig.HOST);
```

2. 设置是否是房主

```java
//设置是否是房主，来决定置顶，删除，控麦的权限(默认是房主)
KtvMusicManager.getInstance().setHomeOwner(UserManager.get().getUserId().equals(voiceRoomBean.getCreateUserId()));
```

3. 点歌台弹框方式
```java
KTVConfig config = KTVConfig.get();
//已点列表还是普通列表
KtvMusicManager.SelectType selectType = isClickList ? KtvMusicManager.SelectType.CHOSEN : KtvMusicManager.SelectType.MUSIC;
//参数分别是独唱还是合唱，已点列表还是普通列表，当前正在播放的歌曲id
KtvMusicKitConfig ktvMusicKitConfig = new KtvMusicKitConfig(config.getKtvType(), selectType, mCurrentClickedSongBean == null ? null : mCurrentClickedSongBean.getMusicId());
//点歌台弹框
KtvMusicManager.getInstance().showMusicListDialog(getChildFragmentManager(), this, ktvMusicKitConfig);
```
4. 控制控麦开关

```java
// 点歌列表里的控麦按钮是否显示
KtvMusicManager.getInstance().setShowControlMic(false);
```
5. 刷新已点列表

```java
//主动刷新已点列表
KtvMusicManager.getInstance().refreshSelectedMusicList();
```

6. 关于这些列表获取，置顶，删除，搜索等都网络请求可以在KtvMusicManager---KtvMusicApi清楚的找得到并直接调用

注意：上述传入的roomid参数默认是要被服务器验证的，如果传随意自定义的roomid是验证不通过的，如果只想自定义roomid，则需要在获取房间已点列表的时候添加filter参数，值传1，表示服务器不验证房间id就可以通过：

## 混响音效弹框

### 简介

这个弹框是KtvMixDialog，内部包含混响音效，伴奏音量和人声音量和耳返，弹框内所有的点击和选择都会通过KtvKitMixDialogListener向房间通知，房间只需要注册并监听KtvKitMixDialogListener接口中的对应方法，来进行自己对应的需求操作即可。

### 混响音效弹框接入方式

```java
//混响音效弹框，REVERBER_LIST是混响音效集合列表，KTV_SCREEN_ROLE_LEAD_SINGER是身份来决定人生音量是否显示
KtvMixDialog dialog = new KtvMixDialog(KTVRoomFragment.this, REVERBER_LIST, KTV_SCREEN_ROLE_LEAD_SINGER);
dialog.show(getChildFragmentManager());
```

注意：
- 混响弹框中还有音准器和升降调被隐藏了，如果开发者需要可以去ktv_mix_dialog布局中解除隐藏
- 歌词屏幕，点歌台，混响音效弹框都可以单独被开发者调用或使用，完全无相互依赖关系。
