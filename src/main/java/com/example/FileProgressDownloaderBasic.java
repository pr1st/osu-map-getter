package com.example;

public class FileProgressDownloaderBasic implements DownloaderProgress {
    private final String downloadFilePath;
    private final long sizeOfDownload;
    private long alreadyDownloaded;

    public FileProgressDownloaderBasic(String downloadId, long sizeOfFile) {
        this.downloadFilePath = downloadId;
        this.sizeOfDownload = sizeOfFile;
    }

    @Override
    public void updateDownloadState(long newDownloadedBytes) {
        alreadyDownloaded += newDownloadedBytes;
        displayDownloadState();
    }

    @Override
    public void displayDownloadState() {
        String percent = "%.2f%%".formatted((double) alreadyDownloaded / sizeOfDownload * 100);
        System.out.println(downloadFilePath + " - " + alreadyDownloaded + "/" + sizeOfDownload + "  [" + percent + "]");
    }

    @Override
    public boolean isDownloaded() {
        return alreadyDownloaded >= sizeOfDownload;
    }
}
