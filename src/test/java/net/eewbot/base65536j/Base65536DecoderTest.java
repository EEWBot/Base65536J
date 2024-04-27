package net.eewbot.base65536j;

import net.eewbot.base65536j.exception.Base65536Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

class Base65536DecoderTest {
    @ParameterizedTest
    @MethodSource("failCaseProvider")
    void fail(String testCase) {
        Assertions.assertThrows(Base65536Exception.class, () -> Base65536.getDecoder().decode(testCase));
    }

    static List<String> failCaseProvider() {
        File baseDirectory = new File("src/test/resources/bad/");

        File[] files = baseDirectory.listFiles();
        if (files == null) throw new IllegalStateException("No test resources.");

        return Arrays.stream(files).map(file -> {
            byte[] bytes;
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }).collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("successCaseProvider")
    void success(String testCase, byte[] expected) {
        Assertions.assertArrayEquals(expected, Base65536.getDecoder().decode(testCase));
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

            arguments.add(Arguments.of(text, binary));
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
