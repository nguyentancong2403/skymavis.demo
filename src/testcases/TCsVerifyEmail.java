package testcases;

import apis.ApiPaths;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ApiUtils;
import utils.JsonUtils;

public class TCsVerifyEmail extends BaseTest{

    @Test(groups = {"Happy flow"}, priority = 2)
    public void verifyResponseSchemaAndPropertiesValueWithValidEmailAndToken() throws InterruptedException {
        Response response;
        int i = 0;
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
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/verifyEmail/verifyEmailSuccessResponseSchema.json"));
        JSONObject responseBodyObj = JsonUtils.convertStringToJSONObject(apiUtils.getResponseBodyAsString(response));
        // Verify fields value
        Assert.assertEquals(responseBodyObj.get("message"), "Your email has been verified successfully");
    }

    @Test(groups = "Independent cases")
    public void verifyResponseSchemaAndPropertiesValueWithInvalidEmailFormat() {
        Response response = apiUtils.callAPIWithDataFile(ApiPaths.VERIFY_EMAIL, "/verifyEmail_InvalidEmailFormat.json");
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 400);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/invalidRequestResponseSchema.json"));
        // Verify fields value
        JSONObject responseBodyObj = JsonUtils.convertStringToJSONObject(apiUtils.getResponseBodyAsString(response));
        Assert.assertEquals(responseBodyObj.getInt("_error"), 400002);
        Assert.assertEquals(responseBodyObj.getString("_errorMessage"), "Input form is invalid");
        Assert.assertEquals(responseBodyObj.getJSONObject("_errorDetails")
                .getString("email"), "email must be a valid email address");
    }

    @Test(groups = "Independent cases")
    public void verifyResponseSchemaAndPropertiesValueWithTokenMismatch() {
        JSONObject rawJsonObj = JsonUtils.convertJsonFileToJSONObject("/verifyEmail_MismatchToken.json");
        String randomEmail = rawJsonObj.getJSONObject("body").getString("email").replace("randomIndex", Integer.toString(randomNumber));
        JSONObject updatedJsonObj = JsonUtils.updateValueInJSONObject(rawJsonObj, "email", randomEmail);
        Response response = apiUtils.callAPIWithJsonObject(ApiPaths.VERIFY_EMAIL, updatedJsonObj);
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 403);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/invalidRequestResponseSchema.json"));
        // Verify fields value
        JSONObject responseBodyObj = JsonUtils.convertStringToJSONObject(apiUtils.getResponseBodyAsString(response));
        Assert.assertEquals(responseBodyObj.getInt("_error"), 403008);
        Assert.assertEquals(responseBodyObj.getString("_errorMessage"), "Verify email token or email wrong");
    }
}
