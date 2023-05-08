package org.ryuu.bean.file.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PathUtils {
    private PathUtils() {
    }

    /**
     * When call the toString() method on a Path object,
     * the resulting string representation of the path will use the file separator character for the underlying operating system.
     * On Windows, for example, the file separator character is the backslash \.
     */
    public static String toStringForwardSlash(Path path) {
        return path.toString().replaceAll("\\\\", "/");
    }

    public static void getChildren(Path rootPath, List<Path> childrenPath) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootPath)) {
            for (Path innerPath : directoryStream) {
                if (Files.isDirectory(innerPath)) {
                    getChildren(innerPath, childrenPath);
                } else {
                    childrenPath.add(innerPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}