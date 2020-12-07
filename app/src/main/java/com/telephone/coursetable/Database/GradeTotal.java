package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GradeTotal {
    @NonNull
    public String total;
    @PrimaryKey
    public long sum;
    public boolean read = false;

    public GradeTotal(@NonNull String total) {
        this.total = total;
        setSum();
    }

    private void setSum(){
        long res = 0;
        char[] chars = this.total.toCharArray();
        for (char ch : chars){
            res += ch;
        }
        this.sum = res;
    }

    @Override
    public String toString() {
        return "GradeTotal{" +
                "total='" + total + '\'' +
                ", read=" + read +
                '}';
    }
}
