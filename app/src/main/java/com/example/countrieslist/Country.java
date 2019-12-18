package com.example.countrieslist;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {
    String name, nativeName, alpha3Code;
    String[] borders;
    double area;

    protected Country(Parcel in) {
        name = in.readString();
        nativeName = in.readString();
        alpha3Code = in.readString();
        borders = in.createStringArray();
        area = in.readDouble();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(nativeName);
        dest.writeString(alpha3Code);
        dest.writeStringArray(borders);
        dest.writeDouble(area);
    }
}
