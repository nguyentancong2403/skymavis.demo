package apis;

import io.restassured.http.Method;

public enum Methods {
    GET(Method.GET),
    POST(Method.POST),
    PUT(Method.PUT),
    DELETE(Method.DELETE);

    private Method method;

    Methods(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
