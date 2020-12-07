package com.telephone.coursetable.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ExamTotal {
    @NonNull
    public String total;
    @PrimaryKey
    public long sum;
    public boolean read = false;

    public ExamTotal(@NonNull String total) {
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
        return "ExamTotal{" +
                "total='" + total + '\'' +
                ", read=" + read +
                '}';
    }
}
