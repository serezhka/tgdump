package com.github.serezhka.tgdump.telegram.impl;

import org.apache.log4j.Logger;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
public class FileDownloader implements Consumer<TdApi.Object> {

    private static final Logger LOGGER = Logger.getLogger(FileDownloader.class);

    private final Map<Integer, AtomicReference<TdApi.File>> loadingFiles = new HashMap<>();

    public TdApi.File waitFileDownload(TdApi.File file) {
        final AtomicReference<TdApi.File> notifier = new AtomicReference<>();
        loadingFiles.put(file.id, notifier);
        //noinspection all
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return notifier.get();
    }

    @Override
    public void accept(TdApi.Object object) {
        if (object.getConstructor() == TdApi.UpdateFile.CONSTRUCTOR) {
            TdApi.UpdateFile fileUpdate = (TdApi.UpdateFile) object;
            for (Integer fileId : loadingFiles.keySet()) {
                if (fileUpdate.file.id == fileId && fileUpdate.file.local.isDownloadingCompleted) {
                    AtomicReference<TdApi.File> notifier = loadingFiles.get(fileId);
                    //noinspection all
                    synchronized (notifier) {
                        notifier.set(fileUpdate.file);
                        notifier.notify();
                    }
                }
            }
        }
    }
}
