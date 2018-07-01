package com.example.android.googlechallenge.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This model class implements parcelable so that
 * we don't have to make another network request to
 * FireBase database when we trying to view the details
 * of a thought. The less network requests the less data
 * consumption the better user experience.
 */
public class Thought implements Parcelable {
    private String id;
    private String title;
    private String content;

    public Thought() {
    }

    public Thought(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    private Thought(Parcel parcel) {
        id = parcel.readString();
        title = parcel.readString();
        content = parcel.readString();
    }

    public String getId() {
        return id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Thought> CREATOR = new Parcelable.Creator<Thought>() {

        @Override
        public Thought createFromParcel(Parcel parcel) {
            return new Thought(parcel);
        }

        @Override
        public Thought[] newArray(int size) {
            return new Thought[0];
        }
    };
}
