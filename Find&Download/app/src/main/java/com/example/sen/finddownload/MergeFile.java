package com.example.sen.finddownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Sen on 26/07/2016.
 */
public class MergeFile {

    public static int mergeFiles(List<File> listFile, OutputStream os) throws IOException {
        int totalRead = 0;
        byte[] buffer = new byte[2048];
        for (File file : listFile) {
            InputStream is = new FileInputStream(file);

            int read, totalPartRead = 0;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
                totalPartRead += read;
                totalRead += read;
            }
        }
        return totalRead;
    }
}
