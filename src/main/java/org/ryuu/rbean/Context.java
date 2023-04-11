package org.ryuu.rbean;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Context {
    public Context(Class<?> scanClass) {
        if (!scanClass.isAnnotationPresent(PackageScan.class)) {
            throw new IllegalArgumentException("scanClass");
        }
        PackageScan packageScan = scanClass.getAnnotation(PackageScan.class);
        Path packagePath = getPackagePath(packageScan.packageName());
        ArrayList<Path> childrenPath = new ArrayList<>();
        try {
            getFiles(packagePath, childrenPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPackagePath(String packageName) {
        String packagePathString = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(packagePathString);
        File packageFile = new File(Objects.requireNonNull(url).getFile());
        return packageFile.toPath();
    }

    private static void getFiles(Path rootPath, List<Path> childrenPath) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootPath)) {
            for (Path innerPath : directoryStream) {
                if (Files.isDirectory(innerPath)) {
                    getFiles(innerPath, childrenPath);
                } else {
                    childrenPath.add(innerPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
