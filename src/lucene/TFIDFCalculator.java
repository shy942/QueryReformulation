package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.PackedInts.Writer;

import config.StaticInfo;

public class TFIDFCalculator {

	String indexDir;
	HashMap<String, Double> tfMap;
	HashMap<String, Double> dfMap;
	HashMap<String, Double> tfidfMap;

	public TFIDFCalculator(String indexDir) {
		this.indexDir = indexDir;
		this.tfidfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Double>();
		this.dfMap = new HashMap<String, Double>();
	}

	public TFIDFCalculator() {
		//this.indexDir = StaticInfo.BUGDIR + "/" + year + "-index";
		this.indexDir = "/Users/user/Documents/workspace-2016/QueryReformulation/data" + "/"+"Index";
		this.tfidfMap = new HashMap<String, Double>();
		this.tfMap = new HashMap<String, Double>();
		this.dfMap = new HashMap<String, Double>();
	}

	public HashMap<String, Double> getDF(ArrayList<String> tokens)
			throws IOException {

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				indexDir).toPath()));
		String field = "contents";
		Bits liveDocs = MultiFields.getLiveDocs(reader);
		TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator();
		BytesRef term = null;
		TFIDFSimilarity tfidfSim = new DefaultSimilarity();
		int docCount = reader.numDocs();

		while ((term = termEnum.next()) != null) {
			String termText = term.utf8ToString();
			Term termInstance = new Term(field, term);
			// term and doc frequency in all documents

			long indexTf = reader.totalTermFreq(termInstance);

			long indexDf = reader.docFreq(termInstance);

			if (tokens.contains(termText)) {

				if (!dfMap.containsKey(termText)) {
					dfMap.put(termText, new Double(indexDf));
				}
			}

			System.out.println(termText+"\t"+indexTf+"\t"+indexDf);
		}
		return dfMap;
	}

	/*
	 * public void readingIndex(int docNbr) { try { IndexReader reader =
	 * DirectoryReader.open(FSDirectory .open(new File(indexDir).toPath()));
	 * 
	 * Document doc = reader.document(docNbr);
	 * System.out.println("Processing file: " + doc.get("id"));
	 * 
	 * Terms termVector = reader.getTermVector(docNbr, "contents"); TermsEnum
	 * itr = termVector.iterator(); BytesRef term = null;
	 * 
	 * while ((term = itr.next()) != null) { String termText =
	 * term.utf8ToString(); long termFreq = itr.totalTermFreq(); // FIXME: this
	 * only return // frequency in this doc long docCount = itr.docFreq(); //
	 * FIXME: docCount = 1 in all // cases
	 * 
	 * System.out.println("term: " + termText + ", termFreq = " + termFreq +
	 * ", docCount = " + docCount); if (!tfMap.containsKey(termText)) {
	 * tfMap.put(termText, new Double(termFreq)); } }
	 * 
	 * reader.close(); } catch (Exception exc) { exc.printStackTrace(); } }
	 */

	public static void main(String[] args) {
		//int year = 2011;
		//new TFIDFCalculator().getDF("Hello Masud");
	}

}
