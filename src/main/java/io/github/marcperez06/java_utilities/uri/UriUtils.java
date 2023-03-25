package io.github.marcperez06.java_utilities.uri;

import java.net.URI;

public class UriUtils {
	
	private UriUtils() {}

	public static URI uriPath(final URI uri, final String path) {
		URI uriPath = null;
		String uriString = path(uri, path);
		
		if (uriString != null && !uriString.isEmpty()) {
			uriPath = URI.create(uriString);
		}
		
        return uriPath;
    }
	
	public static String path(final String host, final String path) {
		String completedPath = "";
		
		try {
			final URI uri = new URI(host);
			completedPath = path(uri, path);
		} catch (Exception e) {
			System.out.println("Can not transform host to URI");
		}

        return completedPath;
    }
	
	public static String path(final URI uri, final String path) {
		final String uriString = uri.toString();
        final StringBuilder stringBuilder = new StringBuilder(uriString);
        final String pathValue = path.startsWith("/") ? path.substring(1) : path;
        
        if (!uriString.endsWith("/")) {
        	stringBuilder.append('/');
        }
        
        stringBuilder.append(pathValue);

        return stringBuilder.toString();
    }
	
}
