import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;

public class countStarViralAlignments {
    public static void main(String[] args) {
	String sampleID = "sampleID";
	String inputFile = "";
	String outFile = "";
	int chimericCount = 0;
	int mismatch_allowed = 5;
	if (args.length == 0) {
	    inputFile = "STAR_virus_Aligned.out.sam";
	    outFile = "viralReadCounts.txt";
	    mismatch_allowed = 5;
	} else if (args.length == 3) {
	    sampleID = args[0];
	    inputFile = args[1];
	    outFile = args[2];
	    mismatch_allowed = 5;
	} else if (args.length < 4) {
	    System.out.println("Usage: java countStarViralAlignments sampleID STAR_virus_Aligned.out.sam viralReadCounts.txt mismatches_allowed");
	    System.exit(1);
	} else {
	    sampleID = args[0];
	    inputFile = args[1];
	    outFile = args[2];
	    mismatch_allowed = Integer.parseInt(args[3]);
	}
	LinkedHashMap<String,Integer> genomeCounts = new LinkedHashMap<String,Integer>();
	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
	    SAMFileReader inputBam = new SAMFileReader(new File(inputFile));
	    inputBam.setValidationStringency(ValidationStringency.SILENT);
	    List<SAMSequenceRecord> allSequences = inputBam.getFileHeader().getSequenceDictionary().getSequences();
	    for (int i = 0; i < allSequences.size(); i++) {
		genomeCounts.put(allSequences.get(i).getSequenceName(),0);
	    }
	    SAMRecordIterator iter = inputBam.iterator();
	    String previousGenome = "";
	    String previousmmID = "";
	    boolean matchNext = false;
            while (iter.hasNext()) {
                SAMRecord s = iter.next();
		String samString = s.getSAMString().trim();
		String[] splitString = samString.split("\t");
		//only works with STAR multimap output
		String multiMapID = splitString[12];
		String misMatchID = "nM:i:0";
		for (int i = 13; i < splitString.length; i++) {
		    if (splitString[i].indexOf("nM:i:") != -1) {
			misMatchID = splitString[i];
		    }
		}
		int mismatches = Integer.parseInt(misMatchID.split("nM:i:")[1]);
		boolean passMismatch = true;
		if (mismatches > mismatch_allowed) {
		    passMismatch = false;
		}
		if (multiMapID.equals("HI:i:1")) {
		    if (genomeCounts.containsKey(splitString[2])) {
			if (passMismatch) {
			    genomeCounts.put(splitString[2],genomeCounts.get(splitString[2]) + 1);
			}
		    }
		    previousGenome = splitString[2];
		    previousmmID = multiMapID;
		} else {
		    if (!splitString[2].equals(previousGenome) || (multiMapID.equals(previousmmID) && matchNext)) {
			if (matchNext && multiMapID.equals(previousmmID) && !splitString[2].equals(previousGenome)) {
			    //chimeric read detected - read pair aligns to different genomes
			    chimericCount++;
			}
			if (genomeCounts.containsKey(splitString[2])) {
			    if (passMismatch) {
				genomeCounts.put(splitString[2],genomeCounts.get(splitString[2]) + 1);
			    }
			}
			if (!matchNext) {
			    matchNext = true;
			} else {
			    matchNext = false;
			}
		    }
		    previousGenome = splitString[2];
		    previousmmID = multiMapID;
		}
		//System.out.println(samString);
            }
	    iter.close();
	    inputBam.close();
	    //write genome counts
	    String tabValues = sampleID;
	    for (String key : genomeCounts.keySet()) {
		tabValues = tabValues + "\t" + genomeCounts.get(key);
	    }
	    //tabValues = tabValues + "\t" + chimericCount;
	    out.write(tabValues);
	    out.newLine();
	    out.close();
	} catch (Exception e) {
	    System.out.println(e);
	    e.printStackTrace();
	}
    }
}