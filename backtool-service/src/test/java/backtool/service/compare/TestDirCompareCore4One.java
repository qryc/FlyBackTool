package backtool.service.compare;

import fly4j.common.test.util.TData;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author qryc
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDirCompareCore4One {


    @Before
    public void setup() throws Exception {
        TData.createTestFiles();
    }


    @Test
    public void deleteOneRepeatFile() throws Exception {
        File fileA = Path.of(TData.tDataDirPath.toString(), "readme.md").toFile();
        File fileACopy = Path.of(TData.tDataDirPath.toString(), "readmeCopy.md").toFile();
        FileUtils.copyFile(fileA, fileACopy);

        List<DirCompareUtil.OneSameObj> oneSameObjs = DirCompareUtil.compareOneDir(TData.tDataDirPath.toString(), null);
        Assert.assertEquals(1, oneSameObjs.size());
        List<File> files = oneSameObjs.get(0).sames();
        Assert.assertEquals(2, files.size());
        if (files.get(0).getAbsolutePath().equals(fileA.getAbsolutePath())) {
            Assert.assertEquals(fileA.getAbsolutePath(), files.get(0).getAbsolutePath());
            Assert.assertEquals(fileACopy.getAbsolutePath(), files.get(1).getAbsolutePath());
        } else {
            Assert.assertEquals(fileA.getAbsolutePath(), files.get(1).getAbsolutePath());
            Assert.assertEquals(fileACopy.getAbsolutePath(), files.get(0).getAbsolutePath());
        }
    }


    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(TData.tPath.toFile());
    }

}
