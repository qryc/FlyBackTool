package backtool.service;

import fly4j.common.file.store.FileJsonStrStore;
import backtool.domain.LocalConfig;
import fly4j.common.os.OsUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * 配置保存为辅助功能，暂关闭，关注核心功能
 * Created by qryc on 2020/3/7.
 */
public class LocalConfigService {
    public static final Path configDirPath;
    public static final Path configFilePath;

    static {
        if (OsUtil.isMac()) {
            configDirPath = Path.of(System.getProperty("user.home"), "Library", "FlyBackTool");
            try {
                if (Files.notExists(configDirPath))
                    Files.createDirectories(configDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            configDirPath = Path.of(System.getProperty("user.home"));
        }
        configFilePath = configDirPath.resolve("backToolFx.json");
    }

    public static void saveLocalBackConfig(Consumer<LocalConfig> consumer) throws IOException {
        LocalConfig localConfig = LocalConfigService.getLocalBackConfig();
        consumer.accept(localConfig);
        FileJsonStrStore.saveObject(configFilePath, localConfig);
    }

    public static LocalConfig getLocalBackConfig() throws IOException {

        if (Files.notExists(configFilePath)) {
            return new LocalConfig();
        }
        return FileJsonStrStore.getObject(configFilePath, LocalConfig.class);
    }

    public static void resetLocalConfig() throws IOException {
        Files.deleteIfExists(configFilePath);
    }

}
