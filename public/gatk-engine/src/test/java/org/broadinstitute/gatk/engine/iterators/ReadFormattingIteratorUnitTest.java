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

package org.broadinstitute.gatk.engine.iterators;

import htsjdk.samtools.*;
import org.broadinstitute.gatk.utils.BaseTest;
import org.broadinstitute.gatk.utils.iterators.GATKSAMIterator;
import org.broadinstitute.gatk.utils.iterators.GATKSAMIteratorAdapter;
import org.broadinstitute.gatk.utils.sam.ArtificialSAMUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;


public class ReadFormattingIteratorUnitTest extends BaseTest {

    @Test
    public void testIteratorConsolidatesCigars() {
        final Cigar unconsolidatedCigar = TextCigarCodec.decode("3M0M5M0M");
        final SAMRecord unconsolidatedRead = ArtificialSAMUtils.createArtificialRead(unconsolidatedCigar);

        final GATKSAMIterator readIterator = GATKSAMIteratorAdapter.adapt(Collections.singletonList(unconsolidatedRead).iterator());
        final ReadFormattingIterator formattingIterator = new ReadFormattingIterator(readIterator, false, (byte)-1);
        final SAMRecord postIterationRead = formattingIterator.next();

        Assert.assertEquals(postIterationRead.getCigarString(), "8M", "Cigar 3M0M5M0M not consolidated correctly by ReadFormattingIterator");
    }
}
