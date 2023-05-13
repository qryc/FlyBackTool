package backtool.service.version;


import backtool.service.compare.DigestCalculate;
import fly4j.common.util.JsonUtils;
import fly4j.common.os.OsUtil;
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

import static fly4j.common.test.util.TData.tDataDirPath;

/**
 * @author qryc
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDirMd5Calculate {
    static final Logger log = LoggerFactory.getLogger(TestDirMd5Calculate.class);

    public TestDirMd5Calculate() {
//        FileAndDirFilter fileAndDirFilter = new FileAndDirFilter();
//        fileAndDirFilter.setFilterDirNames(Set.of(""));
    }

    @Before
    public void setup() throws Exception {
        TData.createTestFiles();
    }

    @Test
    public void genDirMd5VersionTagLen() throws Exception {
        if (OsUtil.isWindows()) {
            return;
        }
        String md5 = """
                {"Java/java语法.txt":"25","Java/Spring框架.txt":"33","readme.md":"24","李白/静夜思.txt":"15"}""";
        String md5FileStr = JsonUtils.writeValueAsString(DigestCalculate.getDirDigestMap(tDataDirPath.toString(), DigestCalculate.DigestType.LEN, null));
        Assert.assertEquals(md5, md5FileStr);
    }

    @Test
    public void genDirMd5VersionTagMd5() throws Exception {
        if (OsUtil.isWindows()) {
            return;
        }
        String md5 = """
                {"Java/java语法.txt":"f29a628831fe901bbdd3132bbdc2313e","Java/Spring框架.txt":"5fb14d685c15f75c92d243da709cc28b","readme.md":"a723b646fe33ab2f0c8e8ed4c4583f0b","李白/静夜思.txt":"7bae0c4e8ec3f7dededc3450137efa04"}""";
        String md5FileStr = JsonUtils.writeValueAsString(DigestCalculate.getDirDigestMap(tDataDirPath.toString(), DigestCalculate.DigestType.MD5, null));
        Assert.assertEquals(md5, md5FileStr);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.forceDeleteOnExit(TData.tPath.toFile());
    }


}
