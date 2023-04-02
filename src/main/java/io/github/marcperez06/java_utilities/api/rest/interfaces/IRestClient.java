/**
 * @author Aleix Marques Casanovas
 */
package io.github.marcperez06.java_utilities.api.rest.interfaces;

import java.io.File;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.RequestProxy;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.credentials.RequestCredentials;

public interface IRestClient {

    public void verifySsl(boolean verifySsl);
    public void setCertificate(String certificateFilePath, String certificateFilePassword);
    public void setProxy(RequestProxy requestProxy);
    public void useCertificate();
    public void disableCertificate();
    public void useProxy();
    public void disableProxy();
    public void cookieManagement(boolean cookieManagement);
    public <T> Response<T> send(Request request);
    public File downloadFile(String url);
    public File downloadFile(String url, String fileName);
    public File downloadFile(String url, String fileName, String savePath);
    public File downloadFile(String url, String fileName, RequestCredentials credentials);
    public File downloadFile(String url, String fileName, String savePath, RequestCredentials credentials);
}