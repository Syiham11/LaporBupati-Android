package id.go.pekalongankab.laporbupati.Util;

/**
 * Created by ERIK on 28-Jan-18.
 */

public class ServerAPI {

    public static String IP = "192.168.43.213";
    public static final String URL_OPD = "http://"+IP+"/laporbupati/api/opd";
    public static final String URL_ADUAN = "http://"+IP+"/laporbupati/api/aduan";
    public static final String URL_ADUAN_SAYA = "http://"+IP+"/laporbupati/api/aduansaya/";
    public static final String URL_Login = "http://"+IP+"/laporbupati/api/login";
    public static final String URL_DAFTAR = "http://"+IP+"/laporbupati/api/register";
    public static final String URL_TAMBAH_ADUAN = "http://"+IP+"/laporbupati/api/tambahaduan";
    public static final String URL_CARI_ADUAN = "http://"+IP+"/laporbupati/api/cariaduan/";
    public static final String URL_KOMENTAR = "http://"+IP+"/laporbupati/api/loadkomentar/";
    public static final String URL_TAMBAH_KOMENTAR = "http://"+IP+"/laporbupati/api/tambahkomentaruser/";
    public static final String URL_PEMBERITAHUAN = "http://"+IP+"/laporbupati/api/loadnotif/";
    public static final String URL_DETAIL_ADUAN = "http://"+IP+"/laporbupati/api/detail_aduan/";
    public static final String URL_CHECK_AKTIF = "http://"+IP+"/laporbupati/api/checkaktif/";
    public static final String URL_KIRIM_MASUKAN = "http://"+IP+"/laporbupati/api/kirim-masukan";


    public static final String URL_FOTO_OPD = "http://"+IP+"/laporbupati/files/opd/source/";
    public static final String URL_FOTO_OPD_THUMB = "http://"+IP+"/laporbupati/files/opd/thumb/";
    public static final String URL_FOTO_USER = "http://"+IP+"/laporbupati/files/user/source/";
    public static final String URL_FOTO_USER_THUMB = "http://"+IP+"/laporbupati/files/user/thumb/";
    public static final String URL_FOTO_ADUAN = "http://"+IP+"/laporbupati/files/aduan/source/";
    public static final String URL_FOTO_KOMEN = "http://"+IP+"/laporbupati/files/komentar/source/";

    public static int perLoadAduan = 10;
    public static int perLoadOpd = 10;
}
