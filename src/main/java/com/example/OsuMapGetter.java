package com.example;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


/**
 * Interface is used to download maps from osu <a href="https://osu.ppy.sh/"</> osu.ppy.sh
 * How it will be done depends on implementation
 */
public interface OsuMapGetter {
    /**
     * Downloads map from osu (via API or web) and saves it to file on the computer
     *
     * @param mapId         osu map id
     * @param directoryPath directory in which downloaded map will be saved (name of the file is not specified)
     * @return completable future which will indicate if download was completed
     */
    CompletableFuture<?> getMapAndSaveToFile(String mapId, Path directoryPath);
}
