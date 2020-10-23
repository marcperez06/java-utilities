/**
 * @author Aleix Marques Casanovas
 */
package io.github.marcperez06.java_utilities.api.rest.interfaces;

import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.RequestProxy;
import io.github.marcperez06.java_utilities.api.request.Response;

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