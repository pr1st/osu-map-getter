package com.example;

/**
 * Interface help to see progress made while downloading something (for example file threw web)
 * Classes that uses this interface
 */
public interface DownloaderProgress {

    /**
     * Updates current progress
     * This method should invoke {@link #displayDownloadState} automatically after update completion
     *
     * @param newDownloadedBytes bytes that was downloaded starting from previous invocation
     */
    void updateDownloadState(long newDownloadedBytes);

    /**
     * Displays current status of download
     * How it will be display depends on implementation
     */
    void displayDownloadState();


    /**
     * Checks if download is completed (sum of all updates equal or greater than object size)
     *
     * @return true if downloaded otherwise false
     */
    boolean isDownloaded();
}
