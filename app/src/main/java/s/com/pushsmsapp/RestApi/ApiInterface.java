package s.com.pushsmsapp.RestApi;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    /*@POST("store")
    @FormUrlEncoded
    Call<ResponseBody> addAuthorizeContacts(@FieldMap Map<String, String> params);*/

    @FormUrlEncoded
    @POST("store.php")
    Call<ResponseBody> addAuthorizeContacts(@Field("mobile") String mobile,
                                            @Field("contents") String contents);


    /*@POST("get")
    Call<ResponseBody> getAuthorizeContacts(@QueryMap Map<String, String> params);*/

    @FormUrlEncoded
    @POST("get.php")
    Call<ResponseBody> getAuthorizeContacts(@Field("mobile") String mobile);


}
