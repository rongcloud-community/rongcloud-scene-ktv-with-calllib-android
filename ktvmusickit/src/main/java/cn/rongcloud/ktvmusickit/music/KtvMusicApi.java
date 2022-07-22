package cn.rongcloud.ktvmusickit.music;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.wapper.IResultBack;

import cn.rongcloud.ktvmusickit.model.ChannelBean;
import cn.rongcloud.ktvmusickit.model.ChannelSheetBean;
import cn.rongcloud.ktvmusickit.model.ClickedMusic;
import cn.rongcloud.ktvmusickit.model.ClickedSongBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.ktvmusickit.model.Music;
import cn.rongcloud.ktvmusickit.model.MusicDetailBean;
import cn.rongcloud.ktvmusickit.model.MusicListBean;

/**
 * 各种音乐请求接口
 */
public class KtvMusicApi {
    private static String HOST = "";

    // 电台列表
    private final static String CHANNEL = "mic/music/channel";
    // 电台搜索音乐列表
    private final static String CHANNEL_SHEET = "mic/music/channelSheet";
    // 搜索音乐
    private final static String SEARCH_MUSIC = "mic/music/searchMusic";
    // 音乐列表
    private final static String MUSIC_LIST = "mic/music/sheetMusic";
    // 请求歌曲的详细信息
    private final static String MUSIC_DETAIL = "mic/music/kHQListen";
    // 点歌
    private final static String CLICK_MUSIC = "mic/ktv/song";
    // 已点音乐列表
    private final static String CLICKED_MUSICS = "mic/ktv/setting";
    // 置顶已点音乐
    private final static String TOPPING_MUSIC = "mic/ktv/song";
    // 删除已点歌曲
    private final static String DELETE_MUSIC = "mic/ktv/song/del";

    public static void setHost(String Host) {
        HOST = Host;
    }

    /**
     * 电台列表
     *
     * @param resultBack
     */
    public static void channel(IResultBack<List<ChannelBean>> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        OkApi.get(HOST + CHANNEL, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<ChannelBean> channelBeanList = result.getList(ChannelBean.class);
                if (null != resultBack) {
                    resultBack.onResult(channelBeanList);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "电台列表请求失败~" + msg);
            }
        });
    }


    /**
     * 电台获取歌单列表
     *
     * @param resultBack
     */
    public static void channelSheet(String groupId, int language, int page, int pageSize, IResultBack<List<ChannelSheetBean>> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("groupId", groupId);
        params.put("language", language);
        params.put("page", page);
        params.put("pageSize", pageSize);
        OkApi.get(HOST + CHANNEL_SHEET, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<ChannelSheetBean> channelSheetList = result.getList(ChannelSheetBean.class);
                if (null != resultBack) {
                    resultBack.onResult(channelSheetList);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "电台tag列表请求失败~" + msg);
            }
        });
    }


    /**
     * 搜索
     *
     * @param resultBack
     */
    public static void searchMusic(String keywords, int language, int page, int pageSize, IResultBack<List<Music>> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("keyword", keywords);
        params.put("language", language);
        params.put("page", page);
        params.put("pageSize", pageSize);
        OkApi.post(HOST + SEARCH_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                MusicListBean musicListBean = result.get(MusicListBean.class);
                if (null != resultBack) {
                    resultBack.onResult(musicListBean.getMusicList());
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "未找到搜索结果~" + msg);
            }
        });
    }


    /**
     * 请求音乐列表
     *
     * @param resultBack
     */
    public static void loadMusicList(String sheetId, int language, int page, int pageSize, IResultBack<List<Music>> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("sheetId", sheetId);
        params.put("language", language);
        params.put("page", page);
        params.put("pageSize", pageSize);
        OkApi.get(HOST + MUSIC_LIST, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                MusicListBean musicListBean = result.get(MusicListBean.class);
                if (null != resultBack) {
                    resultBack.onResult(musicListBean.getMusicList());
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "音乐列表请求失败~" + msg);
            }
        });
    }

    /**
     * 请求歌曲的详细信息
     *
     * @param musicId
     * @param resultBack
     */
    public static void loadDetail(String musicId, IResultBack<MusicDetailBean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("musicId", musicId);
        OkApi.get(HOST + MUSIC_DETAIL, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                MusicDetailBean musicDetailBean = result.get(MusicDetailBean.class);

                if (null != resultBack) {
                    if (result.ok()) {
                        resultBack.onResult(musicDetailBean);
                    } else {
                        resultBack.onResult(null);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "音乐详细信息请求失败~" + msg);
            }
        });
    }


    /**
     * 请求已点音乐列表
     *
     * @param roomId
     * @param resultBack
     */
    public static void loadClickedMusics(String roomId, IResultBack<List<ClickedMusic>> resultBack) {
        if (roomId == null) {
            resultBack.onResult(null);
            return;
        }
        Map<String, Object> params = new HashMap<>(8);
        params.put("roomId", roomId);
        params.put("filter", "1");
        OkApi.get(HOST + CLICKED_MUSICS, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                try {
                    List<ClickedMusic> clickedMusicList = result.get(ClickedSongBean.class).getClickedMusicList();
                    if (null != resultBack) {
                        if (clickedMusicList != null) {
                            for (int i = 0; i < clickedMusicList.size(); i++) {
                                //音乐本地存储路径
                                clickedMusicList.get(i).setMusicPath(KtvMusicManager.getInstance().getMusicPath() + "/" + clickedMusicList.get(i).getMusicId());
                                //歌词本地存储路径
                                clickedMusicList.get(i).setLrcPath(clickedMusicList.get(i).getMusicPath() + "lyric");
                            }
                        }
                        resultBack.onResult(clickedMusicList);
                    }
                } catch (Exception e) {
                    resultBack.onResult(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) {
                    resultBack.onResult(null);
                }
                KToast.show(code + "已点列表请求失败~" + msg);
            }
        });
    }


    /**
     * 点歌
     *
     * @param clickedSongBean
     * @param resultBack
     */
    public static void addMusic(ClickedMusic clickedSongBean, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("artistName", clickedSongBean.getArtistName());
        params.put("musicId", clickedSongBean.getMusicId());
        params.put("musicName", clickedSongBean.getMusicName());
        params.put("roomId", clickedSongBean.getRoomId());
        params.put("solo", clickedSongBean.getSolo());
        params.put("songId", clickedSongBean.getSongId());
        params.put("userId", clickedSongBean.getUserId());
        params.put("userPortrait", clickedSongBean.getUserPortrait());
        OkApi.post(HOST + CLICK_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                } else {
                    resultBack.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 删除
     *
     * @param resultBack
     */
    public static void deleteMusic(ClickedMusic clickedSongBean, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("artistName", clickedSongBean.getArtistName());
        params.put("musicId", clickedSongBean.getMusicId());
        params.put("musicName", clickedSongBean.getMusicName());
        params.put("roomId", clickedSongBean.getRoomId());
        params.put("solo", clickedSongBean.getSolo());
        params.put("songId", clickedSongBean.getSongId());
        params.put("userId", clickedSongBean.getUserId());
        params.put("userPortrait", clickedSongBean.getUserPortrait());
        OkApi.put(HOST + DELETE_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                } else {
                    resultBack.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 置顶已点音乐
     *
     * @param clickedSongBean
     * @param resultBack
     */
    public static void moveMusic(ClickedMusic clickedSongBean, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("artistName", clickedSongBean.getArtistName());
        params.put("musicId", clickedSongBean.getMusicId());
        params.put("musicName", clickedSongBean.getMusicName());
        params.put("roomId", clickedSongBean.getRoomId());
        params.put("solo", clickedSongBean.getSolo());
        params.put("songId", clickedSongBean.getSongId());
        params.put("userId", clickedSongBean.getUserId());
        params.put("userPortrait", clickedSongBean.getUserPortrait());
        OkApi.put(HOST + TOPPING_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                } else {
                    resultBack.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }


    /**
     * 控麦置顶
     *
     * @param resultBack
     */
    public static void controlMic(String roomId, String userId, String userName, String userPortrait, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("roomId", roomId);
        params.put("songId", "seat");
        params.put("userId", userId);
        params.put("userPortrait", userPortrait);
        params.put("artistName", userName);
        params.put("musicName", "控麦");
        OkApi.put(HOST + TOPPING_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                } else {
                    resultBack.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }
}
