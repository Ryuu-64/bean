package org.ryuu.bean.file.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {
    @Test
    void toStringForwardSlash() {
        String testString = "foo\\bar";
        Path path = new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public boolean isAbsolute() {
                return false;
            }

            @Override
            public Path getRoot() {
                return null;
            }

            @Override
            public Path getFileName() {
                return null;
            }

            @Override
            public Path getParent() {
                return null;
            }

            @Override
            public int getNameCount() {
                return 0;
            }

            @Override
            public Path getName(int index) {
                return null;
            }

            @Override
            public Path subpath(int beginIndex, int endIndex) {
                return null;
            }

            @Override
            public boolean startsWith(Path other) {
                return false;
            }

            @Override
            public boolean startsWith(String other) {
                return false;
            }

            @Override
            public boolean endsWith(Path other) {
                return false;
            }

            @Override
            public boolean endsWith(String other) {
                return false;
            }

            @Override
            public Path normalize() {
                return null;
            }

            @Override
            public Path resolve(Path other) {
                return null;
            }

            @Override
            public Path resolve(String other) {
                return null;
            }

            @Override
            public Path resolveSibling(Path other) {
                return null;
            }

            @Override
            public Path resolveSibling(String other) {
                return null;
            }

            @Override
            public Path relativize(Path other) {
                return null;
            }

            @Override
            public URI toUri() {
                return null;
            }

            @Override
            public Path toAbsolutePath() {
                return null;
            }

            @Override
            public Path toRealPath(LinkOption... options) {
                return null;
            }

            @Override
            public File toFile() {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
                return null;
            }

            @Override
            public Iterator<Path> iterator() {
                return null;
            }

            @Override
            public int compareTo(Path other) {
                return 0;
            }

            @Override
            public String toString() {
                return testString;
            }
        };
        String stringForwardSlash = PathUtils.toStringForwardSlash(path);
        assertEquals("foo/bar", stringForwardSlash);
    }

    @Test
    void getChildren() throws URISyntaxException {
        URL url = PathUtilsTest.class.getClassLoader().getResource("foo");
        URI uri = Objects.requireNonNull(url).toURI();
        Path foo = Paths.get(uri);
        List<Path> childrenPath = new LinkedList<>();
        try {
            PathUtils.getChildren(foo, childrenPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> collect = childrenPath
                .stream()
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        List<String> expected = Arrays.asList("42.txt", "bar.txt");
        assertEquals(expected, collect);
    }
}