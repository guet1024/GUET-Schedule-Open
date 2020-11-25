package com.telephone.coursetable.Gson.TeachersEvaluation;

public class Detail {
    private String term; // "2020-2021_1",
    private long lsh; // 1129,
    private String courseid; // "BT0300181X1",
    private long lb; // 1,
    private long score; // 100,
    private String teacherno; // "280011",
    private String courseno; // "2010486",
    private String dja; // "\u5b8c\u5168\u540c\u610f",
    private long afz; // 100,
    private String djb; // "\u5927\u90e8\u5206\u540c\u610f",
    private long bfz; // 85,
    private String djc; // "\u90e8\u5206\u540c\u610f",
    private long cfz; // 70,
    private String djd; // "\u5927\u90e8\u5206\u4e0d\u540c\u610f",
    private long dfz; // 40,
    private String dje; // "\u5b8c\u5168\u4e0d\u540c\u610f",
    private long efz; // 0,
    private String nr; // "1",
    private String zbnh; // "\u8001\u5e08\u4e0a\u8bfe\u4e0d\u8fdf\u5230\uff0c\u4e0d\u65e9\u9000\uff0c\u4e0d\u64c5\u81ea\u79bb\u5f00\u8bfe\u5802\uff0c\u4e0d\u5728\u8bfe\u5185\u63a5\u6253\u7535\u8bdd\uff0c\u4e0d\u968f\u4fbf\u8c03\u505c\u8bfe\uff0c\u65e0\u505c\u8bfe\u4e0d\u8865",
    private double qz; // 0.02,
    private long zp; // 0

    public Detail(String term, long lsh, String courseid, long lb, long score, String teacherno, String courseno, String dja, long afz, String djb, long bfz, String djc, long cfz, String djd, long dfz, String dje, long efz, String nr, String zbnh, double qz, long zp) {
        this.term = term;
        this.lsh = lsh;
        this.courseid = courseid;
        this.lb = lb;
        this.score = score;
        this.teacherno = teacherno;
        this.courseno = courseno;
        this.dja = dja;
        this.afz = afz;
        this.djb = djb;
        this.bfz = bfz;
        this.djc = djc;
        this.cfz = cfz;
        this.djd = djd;
        this.dfz = dfz;
        this.dje = dje;
        this.efz = efz;
        this.nr = nr;
        this.zbnh = zbnh;
        this.qz = qz;
        this.zp = zp;
    }

    public Detail encode_myself(){
        dja = encode(dja);
        djb = encode(djb);
        djc = encode(djc);
        djd = encode(djd);
        dje = encode(dje);
        nr = encode(nr);
        zbnh = encode(zbnh);
        return this;
    }

    public String getTerm() {
        return term;
    }

    public long getLsh() {
        return lsh;
    }

    public String getCourseid() {
        return courseid;
    }

    public long getLb() {
        return lb;
    }

    public long getScore() {
        return score;
    }

    public String getTeacherno() {
        return teacherno;
    }

    public String getCourseno() {
        return courseno;
    }

    public String getDja() {
        return dja;
    }

    public long getAfz() {
        return afz;
    }

    public String getDjb() {
        return djb;
    }

    public long getBfz() {
        return bfz;
    }

    public String getDjc() {
        return djc;
    }

    public long getCfz() {
        return cfz;
    }

    public String getDjd() {
        return djd;
    }

    public long getDfz() {
        return dfz;
    }

    public String getDje() {
        return dje;
    }

    public long getEfz() {
        return efz;
    }

    public String getNr() {
        return nr;
    }

    public String getZbnh() {
        return zbnh;
    }

    public double getQz() {
        return qz;
    }

    public long getZp() {
        return zp;
    }

    public static String encode(String text){
        StringBuilder sb = new StringBuilder();
        if (text != null){
            char[] chs = text.toCharArray();
            for (char ch : chs){
                if (ch < 128){
                    sb.append(ch);
                }else {
                    sb.append('\\').append('u').append(String.format("%04x", (int) ch).toLowerCase());
                }
            }
        }
        return sb.toString();
    }
}
