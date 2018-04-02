package gmedia.net.id.semargres2018.Utils;

/**
 * Created by Shinmaul on 3/15/2018.
 */

public class ServerURL {

    private static final String baseURL = "https://semargres.gmedia.id/";

    public static final String login = baseURL + "auth";
    public static final String register = baseURL + "register";
    public static final String getGender = baseURL + "gender";
    public static final String getAgama = baseURL + "agama";
    public static final String getMarriage = baseURL + "marriage";
    public static final String getPekerjaan = baseURL + "pekerjaan";
    public static final String getProfile = baseURL + "profile/view";
    public static final String saveProfile = baseURL + "profile/edit";

    public static final String getPromo = baseURL + "promo/";
    public static final String getQrcode = baseURL + "qrcode";
    public static final String getKategori = baseURL + "kategori";
    public static final String getIklanHome = baseURL + "iklan/";
    public static final String getKuponList = baseURL + "kupon/history";

    public static final String getMerchant = baseURL + "merchant/all/";
    public static final String getNearbyMerchant = baseURL + "merchant/nearby";
    public static final String getMerchantHome = baseURL + "merchant/populer";
    public static final String getMerchantPerKategori = baseURL + "merchant/kategori";

    public static final String getEvent = baseURL + "event";
    public static final String getNewsPromo = baseURL + "news_promo";
    public static final String getLatestVersion = baseURL + "latest_version/user";
}
