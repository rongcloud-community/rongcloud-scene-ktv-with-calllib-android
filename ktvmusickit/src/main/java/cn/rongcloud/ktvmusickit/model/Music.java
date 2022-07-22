package cn.rongcloud.ktvmusickit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Music {
    @SerializedName("musicId")
    private String musicId;
    @SerializedName("musicName")
    private String musicName;
    @SerializedName("albumId")
    private String albumId;
    @SerializedName("albumName")
    private String albumName;
    @SerializedName("duration")
    private Integer duration;
    @SerializedName("bpm")
    private Integer bpm;
    @SerializedName("auditionBegin")
    private Integer auditionBegin;
    @SerializedName("auditionEnd")
    private Integer auditionEnd;
    @SerializedName("intro")
    private String intro;
    @SerializedName("version")
    private List<Version> version;
    @SerializedName("cover")
    private List<Cover> cover;
    @SerializedName("artist")
    private List<Artist> artist;
    @SerializedName("author")
    private List<Author> author;
    @SerializedName("composer")
    private List<Composer> composer;
    @SerializedName("arranger")
    private List<?> arranger;

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public Integer getAuditionBegin() {
        return auditionBegin;
    }

    public void setAuditionBegin(Integer auditionBegin) {
        this.auditionBegin = auditionBegin;
    }

    public Integer getAuditionEnd() {
        return auditionEnd;
    }

    public void setAuditionEnd(Integer auditionEnd) {
        this.auditionEnd = auditionEnd;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<Version> getVersion() {
        return version;
    }

    public void setVersion(List<Version> version) {
        this.version = version;
    }

    public List<Cover> getCover() {
        return cover;
    }

    public void setCover(List<Cover> cover) {
        this.cover = cover;
    }

    public List<Artist> getArtist() {
        return artist;
    }

    public void setArtist(List<Artist> artist) {
        this.artist = artist;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public List<Composer> getComposer() {
        return composer;
    }

    public void setComposer(List<Composer> composer) {
        this.composer = composer;
    }

    public List<?> getArranger() {
        return arranger;
    }

    public void setArranger(List<?> arranger) {
        this.arranger = arranger;
    }


    public static class Version {
        @SerializedName("name")
        private String name;
        @SerializedName("musicId")
        private String musicId;
        @SerializedName("free")
        private Integer free;
        @SerializedName("price")
        private Integer price;
        @SerializedName("majorVersion")
        private Boolean majorVersion;
        @SerializedName("duration")
        private Integer duration;
        @SerializedName("auditionBegin")
        private Integer auditionBegin;
        @SerializedName("auditionEnd")
        private Integer auditionEnd;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMusicId() {
            return musicId;
        }

        public void setMusicId(String musicId) {
            this.musicId = musicId;
        }

        public Integer getFree() {
            return free;
        }

        public void setFree(Integer free) {
            this.free = free;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Boolean getMajorVersion() {
            return majorVersion;
        }

        public void setMajorVersion(Boolean majorVersion) {
            this.majorVersion = majorVersion;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Integer getAuditionBegin() {
            return auditionBegin;
        }

        public void setAuditionBegin(Integer auditionBegin) {
            this.auditionBegin = auditionBegin;
        }

        public Integer getAuditionEnd() {
            return auditionEnd;
        }

        public void setAuditionEnd(Integer auditionEnd) {
            this.auditionEnd = auditionEnd;
        }
    }

    public static class Cover {
        @SerializedName("url")
        private String url;
        @SerializedName("size")
        private String size;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    public static class Artist {
        @SerializedName("name")
        private String name;
        @SerializedName("code")
        private String code;
        @SerializedName("avatar")
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Author {
        @SerializedName("name")
        private String name;
        @SerializedName("code")
        private String code;
        @SerializedName("avatar")
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Composer {
        @SerializedName("name")
        private String name;
        @SerializedName("code")
        private String code;
        @SerializedName("avatar")
        private String avatar;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
