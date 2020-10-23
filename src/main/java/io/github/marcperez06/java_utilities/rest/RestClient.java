package io.github.marcperez06.java_utilities.rest;

import sogeti.jira_api_wrapper.core.utils.request.Request;
import sogeti.jira_api_wrapper.core.utils.request.RequestProxy;
import sogeti.jira_api_wrapper.core.utils.request.Response;

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