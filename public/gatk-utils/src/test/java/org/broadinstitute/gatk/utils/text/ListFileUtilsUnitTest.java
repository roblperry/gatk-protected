/*
* Copyright 2012-2016 Broad Institute, Inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
* THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package org.broadinstitute.gatk.utils.text;

import org.broadinstitute.gatk.utils.BaseTest;
import org.broadinstitute.gatk.utils.commandline.ParsingEngine;
import org.broadinstitute.gatk.utils.commandline.Tags;
import org.broadinstitute.gatk.utils.sam.SAMReaderID;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

/**
 * Tests selected functionality in the CommandLineExecutable class
 */
public class ListFileUtilsUnitTest extends BaseTest {

    @Test
    public void testIgnoreBlankLinesInBAMListFiles() throws Exception {
        File tempListFile = createTempListFile("testIgnoreBlankLines",
                                               "",
                                               publicTestDir + "exampleBAM.bam",
                                               "         "
                                              );

        List<SAMReaderID> expectedBAMFileListAfterUnpacking = new ArrayList<SAMReaderID>();
        expectedBAMFileListAfterUnpacking.add(new SAMReaderID(new File(publicTestDir + "exampleBAM.bam"), new Tags()));

        performBAMListFileUnpackingTest(tempListFile, expectedBAMFileListAfterUnpacking);
    }

    @Test
    public void testCommentSupportInBAMListFiles() throws Exception {
        File tempListFile = createTempListFile("testCommentSupport",
                                               "#",
                                               publicTestDir + "exampleBAM.bam",
                                               "#" + publicTestDir + "foo.bam",
                                               "      # " + publicTestDir + "bar.bam"
                                              );

        List<SAMReaderID> expectedBAMFileListAfterUnpacking = new ArrayList<SAMReaderID>();
        expectedBAMFileListAfterUnpacking.add(new SAMReaderID(new File(publicTestDir + "exampleBAM.bam"), new Tags()));

        performBAMListFileUnpackingTest(tempListFile, expectedBAMFileListAfterUnpacking);
    }

    @Test
    public void testUnpackSet() throws Exception {
        Set<String> expected = new HashSet<String>(Collections.singletonList(publicTestDir + "exampleBAM.bam"));
        Set<String> actual;

        actual = ListFileUtils.unpackSet(Collections.singletonList(publicTestDir + "exampleBAM.bam"));
        Assert.assertEquals(actual, expected);

        File tempListFile = createTempListFile("testUnpackSet",
                "#",
                publicTestDir + "exampleBAM.bam",
                "#" + publicTestDir + "foo.bam",
                "      # " + publicTestDir + "bar.bam"
        );
        actual = ListFileUtils.unpackSet(Collections.singletonList(tempListFile.getAbsolutePath()));
        Assert.assertEquals(actual, expected);
    }

    @DataProvider(name="includeMatchingTests")
    public Object[][] getIncludeMatchingTests() {
        return new Object[][] {
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("a"), true, asSet("a") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("a"), false, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("b"), true, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("b"), false, asSet("ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "b"), true, asSet("a") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "b"), false, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "ab"), true, asSet("a", "ab") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "ab"), false, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*b.*"), true, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*b.*"), false, asSet("ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*"), true, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*"), false, asSet("a", "ab", "abc") }
        };
    }

    @Test(dataProvider = "includeMatchingTests")
    public void testIncludeMatching(Set<String> values, Collection<String> filters, boolean exactMatch, Set<String> expected) {
        Set<String> actual = ListFileUtils.includeMatching(values, ListFileUtils.IDENTITY_STRING_CONVERTER, filters, exactMatch);
        Assert.assertEquals(actual, expected);
    }

    @DataProvider(name="excludeMatchingTests")
    public Object[][] getExcludeMatchingTests() {
        return new Object[][] {
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("a"), true, asSet("ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("a"), false, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("b"), true, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList("b"), false, asSet("a") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "b"), true, asSet("ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "b"), false, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "ab"), true, asSet("abc") },
                new Object[] { asSet("a", "ab", "abc"), Arrays.asList("a", "ab"), false, Collections.EMPTY_SET },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*b.*"), true, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*b.*"), false, asSet("a") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*"), true, asSet("a", "ab", "abc") },
                new Object[] { asSet("a", "ab", "abc"), Collections.singletonList(".*"), false, Collections.EMPTY_SET }
        };
    }

    @Test(dataProvider = "excludeMatchingTests")
    public void testExcludeMatching(Set<String> values, Collection<String> filters, boolean exactMatch, Set<String> expected) {
        Set<String> actual = ListFileUtils.excludeMatching(values, ListFileUtils.IDENTITY_STRING_CONVERTER, filters, exactMatch);
        Assert.assertEquals(actual, expected);
    }

    private static <T> Set<T> asSet(T... args){
        return new HashSet<T>(Arrays.asList(args));
    }

    private void performBAMListFileUnpackingTest( File tempListFile, List<SAMReaderID> expectedUnpackedFileList ) throws Exception {
        List<String> bamFiles = new ArrayList<String>();
        bamFiles.add(tempListFile.getAbsolutePath());

        List<SAMReaderID> unpackedBAMFileList = ListFileUtils.unpackBAMFileList(bamFiles,new ParsingEngine(null));

        Assert.assertEquals(unpackedBAMFileList.size(), expectedUnpackedFileList.size(),
                            "Unpacked BAM file list contains extraneous lines");
        Assert.assertEquals(unpackedBAMFileList, expectedUnpackedFileList,
                            "Unpacked BAM file list does not contain correct BAM file names");
    }
}
