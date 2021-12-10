package com.example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OsuMapGetterJava11Client implements OsuMapGetter {

    private final HttpClient httpClient;

    public OsuMapGetterJava11Client(String osuSessionCookie) {
        var cookieHandler = createAndPresetCookies(Map.of("osu_session", osuSessionCookie));
        httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(cookieHandler)
                .build();
    }


    @Override
    public CompletableFuture<HttpResponse<?>> getMapAndSaveToFile(String mapId, Path directoryPath) {
        try {
            var osuMapPath = directoryPath.resolve(generateFileNameFromMapId(mapId));

            var httpRequest = createTemplateRequestForMap()
                    .header("referer", "https://osu.ppy.sh/beatmapsets/" + mapId)
                    .uri(new URI("https://osu.ppy.sh/beatmapsets/" + mapId + "/download"))
                    .build();

            return httpClient.sendAsync(httpRequest,
                            HttpResponse.BodyHandlers.ofInputStream())
                    .handleAsync((response, throwable) -> downloadingMapWithProgress(osuMapPath, response, throwable))
                    .handleAsync((response, throwable) -> {
                        if (response != null) System.out.println("Finished successfully");
                        if (throwable != null) System.err.println("Finished sadly");
                        return response;
                    });
        } catch (Exception e) {
            System.err.println(mapId + " - Operation is not started, cause: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<InputStream> downloadingMapWithProgress(Path osuMapPath, HttpResponse<InputStream> response, Throwable throwable) {
        if (response == null) {
            throwable.printStackTrace();
            return null;
        }
        var fileProgressDownload = new FileProgressDownloaderBasic(osuMapPath.getFileName().toString(),
                response.headers().firstValueAsLong("content-length").orElseThrow());
        try (var outputStream = Files.newOutputStream(osuMapPath);
             var bufInputStream = new BufferedInputStream(response.body())) {
            byte[] bytes;
            do {
                bytes = bufInputStream.readNBytes(131072);
                fileProgressDownload.updateDownloadState(bytes.length);
                outputStream.write(bytes);
            } while (bytes.length != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static CookieHandler createAndPresetCookies(Map<String, String> cookies) {
        var cookieManager = new CookieManager();

        cookies.entrySet()
                .stream()
                .map(cookieEntry -> {
                    var tempCookie = new HttpCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    tempCookie.setPath("/");
                    tempCookie.setSecure(true);
                    tempCookie.setHttpOnly(true);
                    tempCookie.setDomain(".ppy.sh");
                    tempCookie.setMaxAge(Instant.now().plus(30, ChronoUnit.DAYS).getEpochSecond());
                    return tempCookie;
                })
                .forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
        return cookieManager;
    }

    private String generateFileNameFromMapId(String mapId) {
        return "Downloaded from app - " + mapId + ".osz";
    }

    private static HttpRequest.Builder createTemplateRequestForMap() {
        return HttpRequest.newBuilder()
                .GET()
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    }
}
