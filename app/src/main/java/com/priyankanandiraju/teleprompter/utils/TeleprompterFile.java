package com.priyankanandiraju.teleprompter.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by priyankanandiraju on 8/8/17.
 */

public class TeleprompterFile implements Parcelable {
    private String title;
    private String content;

    public TeleprompterFile() {
    }

    protected TeleprompterFile(Parcel in) {
        title = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    @Override
    public String toString() {
        return "TeleprompterFile{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
