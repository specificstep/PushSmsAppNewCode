package s.com.pushsmsapp.RestApi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {
    private Context mContext;
    private ProgressDialog dialog;
    private ApiResponseInterface mApiResponseInterface;

    public ApiManager(Context context, ApiResponseInterface apiResponseInterface) {
        this.mContext = context;
        this.mApiResponseInterface = apiResponseInterface;
        dialog = new ProgressDialog(mContext);
    }

    public void makeCompanyRequest(String mobile, String contents, final int requestCode) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (requestCode == AppConstant.ADD_AUTHORIZE_CONTACTS) {
            call = apiService.addAuthorizeContacts(mobile, contents);
        } else if (requestCode == AppConstant.GET_AUTHORIZE_CONTACTS) {
            call = apiService.getAuthorizeContacts(mobile);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    closeDialog();
                    String responseString = response.body().string();
                    System.out.println("Response: " + responseString);
                    if (!TextUtils.isEmpty(responseString) && responseString.length() > 0) {
                        mApiResponseInterface.isSuccess(responseString, requestCode);
                    } else {
                        mApiResponseInterface.isError("No Data found", AppConstant.ERROR_CODE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error", t.getMessage());
                mApiResponseInterface.isError("Network Error", AppConstant.NO_NETWORK_ERROR_CODE);
                //Toast.makeText(mContext, "Network Error", Toast.LENGTH_LONG).show();
                //Constants.showNoInternetDialog(mContext);
                closeDialog();
            }
        });

    }


    /**
     * The purpose of this method is to show the dialog
     *
     * @param message
     */
    private void showDialog(String message) {
        try {
            dialog.setMessage(message);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }
    }


    /**
     * The purpose of this method is to close the dialog
     */
    private void closeDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog.cancel();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }
    }
}
