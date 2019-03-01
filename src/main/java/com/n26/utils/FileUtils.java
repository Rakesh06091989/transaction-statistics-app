package com.n26.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    private FileUtils() {

    }

    public static String readStringFromFile(String fileName) throws IOException {

        Class clazz = FileUtils.class;
        InputStream inputStream = clazz.getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString()
                ;
    }
}
