package utils;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class ImagesUtils {

    public static byte[]  getByteFromUrl(String url){
        byte[] b = new byte[0];
        try {
            b = IOUtils.toByteArray((new URL(url)).openStream());
        } catch (IOException e) {

        }
        return b;
    }
}
