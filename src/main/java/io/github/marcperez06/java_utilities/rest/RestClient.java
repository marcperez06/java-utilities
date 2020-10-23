package io.github.marcperez06.java_utilities.rest;

import io.github.marcperez06.java_utilities.request.Request;
import io.github.marcperez06.java_utilities.request.RequestProxy;
import io.github.marcperez06.java_utilities.request.Response;

public interface RestClient {

    public void verifySsl(boolean verifySsl);
    public void setCertificate(String certificateFilePath, String certificateFilePassword);
    public void setProxy(RequestProxy requestProxy);
    public void useCertificate();
    public void disableCertificate();
    public void useProxy();
    public void disableProxy();
    public <T> Response<T> send(Request request);
}