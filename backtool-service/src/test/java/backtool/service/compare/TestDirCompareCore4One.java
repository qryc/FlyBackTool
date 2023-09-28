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
        FileUtils.deleteDirectory(new File("/Volumes/HomeWork/flyDataTest/"));
        FileUtils.copyDirectory(new File("/Volumes/HomeWork/FlyCode/FlyDoc/flyDataTest/"), new File("/Volumes/HomeWork/flyDataTest/"));
    }


    @Test
    public void deleteOneRepeatFile() throws Exception {

        List<DirCompareUtil.OneSameObj> sameObjs = DirCompareUtil.compareOneDir("/Volumes/HomeWork/flyDataTest/oneDirCompre", null);
        Assert.assertEquals(1, sameObjs.size());
        Assert.assertEquals(2, sameObjs.get(0).sames().size());
        Assert.assertTrue(sameObjs.get(0).sames().contains(new File("/Volumes/HomeWork/flyDataTest/oneDirCompre/img/ck.png")));
        Assert.assertTrue(sameObjs.get(0).sames().contains(new File("/Volumes/HomeWork/flyDataTest/oneDirCompre/ck.png")));


    }


    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("/Volumes/HomeWork/flyDataTest/"));
    }

}
