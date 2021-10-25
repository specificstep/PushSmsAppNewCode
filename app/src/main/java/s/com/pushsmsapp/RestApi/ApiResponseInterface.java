package s.com.pushsmsapp.RestApi;

public interface ApiResponseInterface {
    public void isError(String errorMsg, int errorCode);

    public void isUserDisabled(String errorMsg, int errorCode);

    public void isSuccess(Object response, int ServiceCode);
}
