package backtool.service;


import fly4j.common.file.FileAndDirPredicate;

import java.io.File;
import java.util.Set;
import java.util.function.Predicate;

/**
 * fx 常量
 * Created by qryc on 2020/3/8.
 */
public class FxConst {
    public static final String SPLITE_LINE_100 = "-".repeat(100);

    public static Predicate<File> fileAndDirPredicate;
    public static final String exceptionLog = "exceptionLog";
    public static final String version = "2021-12-12";

    public static void initBean() {
        fileAndDirPredicate = new FileAndDirPredicate(Set.of(".idea", "target", ".DS_Store", ".git"),
                Set.of("iml"));
    }

    static {
        initBean();
    }
}
