package com.telephone.coursetable.LogMe;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.telephone.coursetable.MyApp;

public class LogMe {

    public interface LogRunnable{
        void log(String tag, String msg);
    }

    public static StringBuilder log = new StringBuilder();

    private static LogRunnable e, w, i;
    private static LogRunnable no = (tag, msg) -> {};

    public static void init(){
        if (MyApp.isDebug()) {
            e = Log::e;
            w = Log::w;
            i = Log::i;
        }else {
            e = w = i = no;
        }
    }

    public static void setE(LogRunnable e){
        LogMe.e = e;
    }
    public static void setW(LogRunnable w){
        LogMe.w = w;
    }
    public static void setI(LogRunnable i){
        LogMe.i = i;
    }
    public static void setAll(LogRunnable logRunnable){
        LogMe.e = LogMe.w = LogMe.i = logRunnable;
    }

    private static void write_log(String tag, String msg){
        if (log.length() >= 33554432){
            log = new StringBuilder();
        }
        log.append(tag).append(": ").append(msg).append("\n");
    }

    public static void e(String tag, String msg){
        write_log(tag, msg);
        e.log(tag, msg);
    }
    public static void w(String tag, String msg){
        write_log(tag, msg);
        w.log(tag, msg);
    }
    public static void i(String tag, String msg){
        write_log(tag, msg);
        i.log(tag, msg);
    }
}
