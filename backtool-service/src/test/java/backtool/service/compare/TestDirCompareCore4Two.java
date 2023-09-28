package backtool.service.compare;

import backtool.service.FxConst;
import backtool.service.compare.DirCompareUtil;
import fly4j.common.file.FileUtil;
import fly4j.common.test.util.TData;
import fly4j.common.util.StringConst;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author qryc
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDirCompareCore4Two {


    @Before
    public void setup() throws Exception {
        FileUtils.deleteDirectory(new File("/Volumes/HomeWork/flyDataTest/"));
        FileUtils.copyDirectory(new File("/Volumes/HomeWork/FlyCode/FlyDoc/flyDataTest/"), new File("/Volumes/HomeWork/flyDataTest/"));
    }

    @Test
    public void deleteOneRepeatFile() throws Exception {

        DirCompareUtil.CompareResult compareResult = DirCompareUtil.compareTwoFull("/Volumes/HomeWork/flyDataTest/TwoDirCompare/LeftDir"
                , "/Volumes/HomeWork/flyDataTest/TwoDirCompare/RightDir", FxConst.fileAndDirPredicate);
        //验证左边多的
        System.out.println(compareResult.leftAloneFiles);
        Assert.assertEquals(2, compareResult.leftAloneFiles.size());
        Assert.assertTrue(compareResult.leftAloneFiles.contains(new File("/Volumes/HomeWork/flyDataTest/TwoDirCompare/LeftDir/img/ppt.png")));
        Assert.assertTrue(compareResult.leftAloneFiles.contains(new File("/Volumes/HomeWork/flyDataTest/TwoDirCompare/LeftDir/ck.png")));


        //验证右边多的
        System.out.println(compareResult.rightAloneFiles);
        Assert.assertEquals(2, compareResult.rightAloneFiles.size());
        Assert.assertTrue(compareResult.rightAloneFiles.contains(new File("/Volumes/HomeWork/flyDataTest/TwoDirCompare/RightDir/img/word.png")));
        Assert.assertTrue(compareResult.rightAloneFiles.contains(new File("/Volumes/HomeWork/flyDataTest/TwoDirCompare/RightDir/cd.gif")));

    }


    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("/Volumes/HomeWork/flyDataTest/"));
    }

}
