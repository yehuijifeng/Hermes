package com.lh.hermes.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * user：LuHao
 * time：2019/8/15 11:26
 * describe：
 */
public class HermesBean implements Parcelable {
    private int id;
    private String name;
    private double money;

    public HermesBean() {
    }

    public HermesBean(int id, String name, double money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    protected HermesBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        money = in.readDouble();
    }

    public static final Creator<HermesBean> CREATOR = new Creator<HermesBean>() {
        @Override
        public HermesBean createFromParcel(Parcel in) {
            return new HermesBean(in);
        }

        @Override
        public HermesBean[] newArray(int size) {
            return new HermesBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(money);
    }

    @Override
    public String toString() {
        return "HermesBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
