package testcases;

import apis.ApiPaths;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ApiUtils;
import utils.JsonUtils;

public class TCsRequestVerifyEmail extends BaseTest{

    @Test(groups = "Happy flow", priority = 1)
    public void verifyResponseSchemaAndPropertiesValueWithValidEmail() {
        Response response;
        randomNumber = getRandomNumber();
        JSONObject rawJsonObj = JsonUtils.convertJsonFileToJSONObject("/requestVerifyEmail_ValidData.json");
        String randomEmail = rawJsonObj.getJSONObject("body").getString("email").replace("randomIndex", Integer.toString(randomNumber));
        JSONObject updatedJsonObj = JsonUtils.updateValueInJSONObject(rawJsonObj, "email", randomEmail);
        response =  apiUtils.callAPIWithJsonObject(ApiPaths.REQUEST_VERIFY_EMAIL, updatedJsonObj);
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/requestVerifyEmail/sendOtpSucessResponseSchema.json"));
        JSONObject responseBodyObj = JsonUtils.convertStringToJSONObject(apiUtils.getResponseBodyAsString(response));
        // Verify fields value
        Assert.assertEquals(responseBodyObj.get("message"), "Captcha to verify your email was sent");
    }

    @Test(groups = "Independent cases")
    public void verifyResponseSchemaPropertiesValueWithInvalidEmail() {
        Response response = apiUtils.callAPIWithDataFile(ApiPaths.REQUEST_VERIFY_EMAIL, "/requestVerifyEmail_InvalidData.json");
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
}
