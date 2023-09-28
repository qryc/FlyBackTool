package backtool.service.version;

import backtool.service.DirVersionGen;
import fly4j.common.test.util.TData;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.nio.file.Files;

/**
 * @author qryc
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDirVersionGen {


    @Before
    public void setup() throws Exception {
        TData.setup();
    }


    @Test
    public void deleteOneRepeatFile() throws Exception {
//        DirVersionGen.saveDirVersionModel2File(TData.tDataDirPath.toString(), null, TData.tPath.resolve("version.md5"));
//        System.out.println(Files.readString(TData.tPath.resolve("version.md5")));
    }


    @After
    public void tearDown() throws Exception {
        FileUtils.forceDelete(TData.tPath.toFile());
    }

}
