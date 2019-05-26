//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ecit.lucene.ik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import java.io.*;

public final class IKAnalyzer extends Analyzer {
    private boolean useSmart;

    public boolean useSmart() {
        return this.useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public IKAnalyzer() {
        this(false);
    }

    public IKAnalyzer(boolean useSmart) {
        this.useSmart = useSmart;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        Reader reader = null;
        reader=new StringReader(fieldName);
        Tokenizer _IKTokenizer = new IKTokenizer(reader, this.useSmart());
        return new TokenStreamComponents(_IKTokenizer);
    }
}
