package com.dai.trust.common;

import com.dai.trust.exceptions.TrustException;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods to manage files.
 */
public class FileUtility {
    private static final Logger logger = LogManager.getLogger(FileUtility.class.getName());
    
    /**
     * Returns the byte array for the file.
     *
     * @param filePath The full path to the file
     * @return
     */
    public static byte[] getFileBinary(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            return readFile(file);
        } catch (IOException ex) {
            logger.error(ex);
            throw new TrustException(MessagesKeys.ERR_FILE_FAILED_READING, new Object[]{file.getName()});
        }
    }

    /**
     * Returns file's extention.
     *
     * @param fileName The name of the file.
     * @return
     */
    public static String getFileExtension(String fileName) {
        String ext = null;
        if (fileName.lastIndexOf(".") > 0 && fileName.lastIndexOf(".") < fileName.length()) {
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return ext;
    }

    /**
     * Returns file name excluding extention.
     *
     * @param fileName The name of the file.
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;
        if (fileName.lastIndexOf(".") > 0 && fileName.lastIndexOf(".") < fileName.length()) {
            name = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return name;
    }

    /*
     * Get the extension of a file.
     */
    public static String getFileExtension(File f) {
        if (f != null) {
            return getFileExtension(f.getName());
        }
        return null;
    }

    /**
     * Returns the size of the directory. This is done by summing the size of
     * each file in the directory. The sizes of all subdirectories can be
     * optionally included.
     *
     * @param directory The directory to calculate the size for.
     * @param recursive Indicates whether to drill down to the subdirectories or not.
     * @return 
     */
    public static long getDirectorySize(File directory, boolean recursive) {
        long length = 0;
        if (!directory.isFile()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    length += file.length();
                } else if (recursive) {
                    length += getDirectorySize(file, recursive);
                }

            }
        }
        return length;
    }

    /**
     * Writes the data from an input stream to the specified file using buffered
     * 8KB chunks. This method closes the input stream once the write is
     * completed.
     *
     * @param in The InputStream to write
     * @param file The file to write the input stream to
     * @param maxSize Maximum file size. If size is exceeded, exception will be thrown.
     * @throws IOException If an IO error occurs while attempting to write the
     * file.
     */
    public static void writeFile(InputStream in, File file, int maxSize) throws IOException {
        if (file == null || in == null) {
            // Nothing to write
            return;
        }
        OutputStream out = null;
        try {
            deleteFile(file);
            file.setLastModified(DateUtility.now().getTime());
            out = new FileOutputStream(file);
            // Use an 8K buffer for writing the file. This is usually the most effecient 
            // buffer size. 
            byte[] buf = new byte[8 * 1024];
            int len;
            int totalSize = 0;
            while ((len = in.read(buf)) != -1) {
                totalSize += len;
                if(maxSize > 0 && totalSize > maxSize){
                    throw new TrustException(MessagesKeys.ERR_FILE_TOO_BIG, new Object[]{maxSize / 1024});
                }
                out.write(buf, 0, len);
            }
            out.flush();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Reads a file from the file system into a byte array.
     *
     * @param file The file to read.
     * @return Byte array representing the file content. Returns null if the
     * file does not exist.
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException {
        byte[] result = null;
        if (file != null && file.exists()) {
            FileInputStream in = new FileInputStream(file);
            try {
                int length = (int) file.length();
                result = new byte[length];
                int offset = 0;
                int bytesRead = 1;

                while (offset < length && bytesRead > 0) {
                    bytesRead = in.read(result, offset, (length - offset));
                    offset = bytesRead > 0 ? (offset + bytesRead) : offset;
                }
                if (offset < length) {
                    throw new TrustException(MessagesKeys.ERR_FILE_FAILED_READING, new Object[]{file.getName()});
                }
            } finally {
                in.close();
            }
        }
        return result;
    }

    /**
     * Deletes the file from the file system if it exists.
     *
     * @param file The file to delete.
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * Formats file size, applying KB, MB, GB units.
     *
     * @param size Size to format
     * @return
     */
    public static String formatFileSize(long size) {
        if (size == 0) {
            return "0";
        }

        if (size < 1024) {
            return size + "B";
        }

        if (size >= 1024 && size < 1048576) {
            return Math.round((size / 1024) * 100.0) / 100.0 + "KB";
        }

        if (size >= 1048576 && size < 1073741824) {
            return Math.round((size / 1024 / 1024) * 100.0) / 100.0 + "MB";
        }

        if (size >= 1073741824 && size < 1099511627776L) {
            return Math.round((size / 1024 / 1024 / 1024) * 100.0) / 100.0 + "GB";
        }

        return Math.round((size / 1024 / 1024 / 1024 / 1024) * 100.0) / 100.0 + "TB";
    }
}
