package testcases;

import apis.ApiPaths;
import apis.Methods;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonUtils;

public class TCsRegister extends BaseTest{

    @Test(groups = {"Happy flow"}, priority = 3)
    public void verifyRegisterAccountSuccessfully() {
        Response response;
        JSONObject rawJsonObj = JsonUtils.convertJsonFileToJSONObject("/register_ValidData.json");
        String randomEmail = rawJsonObj.getJSONObject("body").getString("email").replace("randomIndex", Integer.toString(randomNumber));
        JSONObject updatedJsonObj = JsonUtils.updateValueInJSONObject(rawJsonObj, "email", randomEmail);
        response = apiUtils.callAPIWithJsonObject(ApiPaths.REGISTER_WITH_EMAIL, updatedJsonObj);
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/basicRegisterWithEmail/register_CreateAccountSuccessResponseSchema.json"));
    }

    @Test(groups = "Independent cases")
    public void verifyResponseSchemaPropertiesValueWithEmptyEmailAndPassword() {
        Response response;
        JSONObject jsonObject = JsonUtils.convertJsonFileToJSONObject("/register_EmptyEmailAndPassword.json");
        response = apiUtils.callAPIWithJsonObject(ApiPaths.REGISTER_WITH_EMAIL, jsonObject);
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
                .getString("email"), "email is a required field");
        Assert.assertEquals(responseBodyObj.getJSONObject("_errorDetails")
                .getString("password"), "password is a required field");
    }

    @Test(groups = "Independent cases")
    public void verifyResponseSchemaPropertiesValueWithInvalidEmailAndPassword() {
        Response response;
        JSONObject jsonObject = JsonUtils.convertJsonFileToJSONObject("/register_InvalidEmailAndPassword.json");
        response = apiUtils.callAPIWithJsonObject(ApiPaths.REGISTER_WITH_EMAIL, jsonObject);
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
        Assert.assertEquals(responseBodyObj.getJSONObject("_errorDetails")
                .getString("password"), "passwords must be at least 8 characters including a number and an alphabet");
    }

    @Test(groups = "Independent case")
    public void verifyResponseSchemaPropertiesValueWithUnverifiedEmail() {
        Response response;
        JSONObject jsonObject = JsonUtils.convertJsonFileToJSONObject("/register_UnverifiedEmail.json");
        response = apiUtils.callAPIWithJsonObject(ApiPaths.REGISTER_WITH_EMAIL, jsonObject);
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 403);
        // Verify Schema
        Assert.assertTrue(JsonUtils.validateResponseSchema(apiUtils.getResponseBodyAsString(response),
                "responseSchema/invalidRequestResponseSchema.json"));
        // Verify fields value
        JSONObject responseBodyObj = JsonUtils.convertStringToJSONObject(apiUtils.getResponseBodyAsString(response));
        Assert.assertEquals(responseBodyObj.getInt("_error"), 403007);
        Assert.assertEquals(responseBodyObj.getString("_errorMessage"), "Your email is not verified");
    }
}
