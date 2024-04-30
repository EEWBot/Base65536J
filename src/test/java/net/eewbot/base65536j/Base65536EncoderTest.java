package net.eewbot.base65536j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Base65536EncoderTest {
    @ParameterizedTest
    @MethodSource("successCaseProvider")
    void success(byte[] testCase, String expected) {
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actual = Base65536.getEncoder().encode(testCase);

        Assertions.assertArrayEquals(expectedBytes, actual);
        Assertions.assertEquals(expectedBytes.length, actual.length);
    }

    static List<Arguments> successCaseProvider() throws IOException {
        File baseDirectory = new File("src/test/resources/pairs/");
        List<File> files = collectAllFiles(baseDirectory);

        List<File> texts = files.stream().filter(file -> {
            String name = file.getName();

            int periodIndex = name.lastIndexOf('.');
            if (periodIndex <= 0) return false;

            String extension = name.substring(periodIndex + 1);
            return extension.equals("txt");
        }).collect(Collectors.toList());

        List<File> binaries = files.stream().filter(file -> {
            String name = file.getName();

            int periodIndex = name.lastIndexOf('.');
            if (periodIndex <= 0) return false;

            String extension = name.substring(periodIndex + 1);
            return extension.equals("bin");
        }).collect(Collectors.toList());

        if (texts.size() != binaries.size()) throw new RuntimeException("texts size not equal to binaries size");

        List<Arguments> arguments = new ArrayList<>();
        for (File textFile : texts) {
            String fileName;
            {
                String name = textFile.getName();
                int periodIndex = name.lastIndexOf('.');
                fileName = name.substring(0, periodIndex);
            }

            Optional<File> binaryFile = binaries.stream().filter(file -> {
                String name = file.getName();
                int periodIndex = name.lastIndexOf('.');
                return name.substring(0, periodIndex).equals(fileName);
            }).findFirst();
            if (!binaryFile.isPresent()) throw new RuntimeException("Can't find pair file.");

            String text = new String(Files.readAllBytes(textFile.toPath()), StandardCharsets.UTF_8);
            byte[] binary = Files.readAllBytes(binaryFile.get().toPath());

            arguments.add(Arguments.of(binary, text));
        }

        return arguments;
    }

    private static List<File> collectAllFiles(File baseDirectory) {
        File[] files = baseDirectory.listFiles();
        if (files == null) return Collections.emptyList();

        ArrayList<File> list = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(collectAllFiles(file));
                continue;
            }
            list.add(file);
        }

        return list;
    }
}
