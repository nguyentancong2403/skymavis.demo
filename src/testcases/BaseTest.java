package testcases;

import apis.ApiPaths;
import config.ConfigData;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.*;
import org.testng.asserts.Assertion;
import org.testng.asserts.LoggingAssert;
import org.testng.log4testng.Logger;
import utils.ApiUtils;
import utils.JsonUtils;
import utils.MailUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseTest {
    public MailUtils mailUtils;
    public ApiUtils apiUtils = new ApiUtils();

    public static int randomNumber;

    protected String getOtpFromEmail(String otp_type){
        String otp = "";
        try {
            mailUtils = new MailUtils();
            Message[] messages = mailUtils.getEmails(ConfigData.MAIL_HOST, ConfigData.MAIL_PORT,
                    ConfigData.IS_ENABLE_TLS, ConfigData.STORE_TYPE, ConfigData.MAIL_USERNAME, ConfigData.MAIL_PASSWORD);
            List<Message> numberOfFilteredMail = mailUtils.filterEmailsBySenderAndSubject(messages,
                    ConfigData.MAIL_SENDER, ConfigData.MAIL_SUBJECT);
            String bodyContent = numberOfFilteredMail.get(numberOfFilteredMail.size() - 1).getContent().toString();
            switch (otp_type){
                case "From Request Verify Email":
                    Pattern pattern = Pattern.compile("<span style=\"display: block;\">(\\d{6})");
                    Matcher matcher = pattern.matcher(bodyContent);
                    if (matcher.find()){
                        otp = (String) matcher.group().subSequence(matcher.group().length() - 6, matcher.group().length());
                    }
            }
        }
        catch (MessagingException | IOException e){
            e.printStackTrace();
        }
        return otp;
    }

    protected static int getRandomNumber(){
        Random random = new Random();
        return random.ints(1000, 9999).findFirst().getAsInt();
    }
    protected Response callApiRequestVerifyEmailWithValidData(int randomNumber){
        apiUtils = new ApiUtils();
        JSONObject rawJsonObj = JsonUtils.convertJsonFileToJSONObject("/requestVerifyEmail_ValidData.json");
        String randomEmail = rawJsonObj.getJSONObject("body").getString("email").replace("randomIndex", Integer.toString(randomNumber));
        JSONObject updatedJsonObj = JsonUtils.updateValueInJSONObject(rawJsonObj, "email", randomEmail);
        return apiUtils.callAPIWithJsonObject(ApiPaths.REQUEST_VERIFY_EMAIL, updatedJsonObj);
    }

    protected Response callApiVerifyEmailWithValidData(int randomNumber){
        Response response = null;
        int i = 0;
        try {
            callApiRequestVerifyEmailWithValidData(randomNumber);
            JSONObject rawJsonObj = JsonUtils.convertJsonFileToJSONObject("/verifyEmail_ValidData.json");
            String randomEmail = rawJsonObj.getJSONObject("body").getString("email").replace("randomIndex", Integer.toString(randomNumber));
            JSONObject updatedJsonObj = JsonUtils.updateValueInJSONObject(rawJsonObj, "email", randomEmail);
            JsonUtils.updateValueInJSONObject(updatedJsonObj, "token", getOtpFromEmail("From Request Verify Email"));
            response = apiUtils.callAPIWithJsonObject(ApiPaths.VERIFY_EMAIL, updatedJsonObj);
            while (!JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),"responseSchema/verifyEmail/verifyEmailSuccessResponseSchema.json")){
                Thread.sleep(5000);
                JsonUtils.updateValueInJSONObject(updatedJsonObj, "token", getOtpFromEmail("From Request Verify Email"));
                response = apiUtils.callAPIWithJsonObject(ApiPaths.VERIFY_EMAIL, updatedJsonObj);
                if (i == 5){
                    break;
                }
                i++;
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return response;
    }
}
