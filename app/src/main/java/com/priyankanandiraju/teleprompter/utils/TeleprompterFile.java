package com.priyankanandiraju.teleprompter.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by priyankanandiraju on 8/8/17.
 */

public class TeleprompterFile implements Parcelable {
    public static final Creator<TeleprompterFile> CREATOR = new Creator<TeleprompterFile>() {
        @Override
        public TeleprompterFile createFromParcel(Parcel in) {
            return new TeleprompterFile(in);
        }

        @Override
        public TeleprompterFile[] newArray(int size) {
            return new TeleprompterFile[size];
        }
    };
    private String id;
    private String title;
    private String content;

    public TeleprompterFile() {
    }

    protected TeleprompterFile(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TeleprompterFile{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
