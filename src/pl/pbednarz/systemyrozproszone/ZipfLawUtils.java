package pl.pbednarz.systemyrozproszone; /**
 * @author Piotr Bednarz
 * @date 25.05.2015
 */

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZipfLawUtils {
    public static List<Multiset.Entry> readFile(File file) throws IOException {
        Multiset<String> multiset = HashMultiset.create();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        TokenStream stream = analyzer.tokenStream("content", new InputStreamReader(new FileInputStream(file), "UTF-8"));
        CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            multiset.add(new Term("content", cattr.toString()).text());
        }
        stream.end();
        stream.close();
        //Sorting the multiset by number of occurrences using a comparator on the Entries of the multiset
        List<Multiset.Entry> list = new ArrayList(multiset.entrySet());
        Comparator<Multiset.Entry> occurence_comparator = new Comparator<Multiset.Entry>() {
            public int compare(Multiset.Entry e1, Multiset.Entry e2) {
                return e2.getCount() - e1.getCount();
            }
        };
        Collections.sort(list, occurence_comparator);

        int rank = 1;
        for (Multiset.Entry e : list) {
            System.out.println(e.getElement() + "-> " + rank + " - " + e.getCount());
            rank++;
        }

        return list;
    }

    public static void writeToFile(File file, List<Multiset.Entry> list) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"
        ));
        for (int i = 0; i < list.size(); i++) {
            Multiset.Entry entry = list.get(i);
            outputWriter.write(String.format("%d\t%d\t%s", i + 1, entry.getCount(), entry.getElement()));
            if (i != list.size() - 1) {
                outputWriter.newLine();
            }
        }
        outputWriter.flush();
        outputWriter.close();
    }
}