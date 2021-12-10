package com.example;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MainApplication {
    private static final String osuCookie;

    static {
        try {
            osuCookie = Files.readString(Path.of("C:\\home\\osu-settings\\myOsuCookie"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Path directoryForOsuMaps = Path.of(System.getProperty("user.home"), "Downloads", "osu-downloaded-maps");

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Starting the app...");
        var osuMapGetter = new OsuMapGetterJava11Client(osuCookie);
        getAllMaps().stream()
                .limit(10)
                .parallel()
                .map(mapId -> osuMapGetter.getMapAndSaveToFile(mapId, directoryForOsuMaps))
                .forEach(CompletableFuture::join);
        System.out.println("Finished...");
    }


    private static List<String> getAllMaps() throws IOException {
        return Files.readAllLines(Path.of("C:\\home\\osu-settings\\osuTmp.txt"));
    }
}
