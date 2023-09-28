package backtool.service.zip;


import backtool.service.compare.DirCompareUtil;
import fly4j.common.file.FileAndDirPredicate;
import fly4j.common.test.util.TData;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Predicate;

/**
 * @author qryc
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDirZipService {
    static final Logger log = LoggerFactory.getLogger(TestDirZipService.class);


    @Before
    public void setup() throws Exception {
        TData.setup();
    }

    @Test
    public void zipDirWithVerify() throws Exception {
        var zipFilePath = TData.tPath.resolve("test.zip");
        var flyResult = DirZipService.zipDirWithVerify(TData.tDataDirPath.toFile(), zipFilePath.toFile(), "123", null);
        System.out.println(flyResult);
        Assert.assertTrue(flyResult.isSuccess());
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(TData.tPath.toFile());
    }


}
