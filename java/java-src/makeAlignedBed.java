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
import java.util.Arrays;

public class makeAlignedBed {
    public static String inputFile = "";
    public static String outputFile = "";
    public static String faIn = "";
    public static void main(String[] args) {
	outputFile = "aligned.bed";
	faIn = "custom_virus.fa";
	if (args.length == 0) {
	    inputFile = "STAR_Aligned.out.sam";
	} else {
	    inputFile = args[0];
	    outputFile = args[1];
	    faIn = args[2];
	}

	
	//LinkedHashMap<String,ArrayList<Integer>> covered  = new LinkedHashMap<String,ArrayList<Integer>>();
	//LinkedHashMap<String,String> covered  = new LinkedHashMap<String,String>();
	//ArrayList<Integer> lengths = new ArrayList<Integer>();

	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFile)));
	    BufferedReader inputBam = new BufferedReader(new FileReader(new File(inputFile)));
	    BufferedReader inputFa = new BufferedReader(new FileReader(new File(faIn)));
	    //SAMFileReader inputBam = new SAMFileReader(new File(inputFile));
	    //inputBam.setValidationStringency(ValidationStringency.SILENT);
	    //List<SAMSequenceRecord> allSequences = inputBam.getFileHeader().getSequenceDictionary().getSequences();
	    //for (int i = 0; i < allSequences.size(); i++) {
	    //genomeCounts.put(allSequences.get(i).getSequenceName(),0);
	    //}
	    //SAMRecordIterator iter = inputBam.iterator();
	    String line = "";
	    int count = 0;
	    /*while ((line = inputFa.readLine()) != null) {
		if (count % 2 == 1) {
		    lengths.add(line.length());
		}
		count++;
		}*/
	    while ((line = inputBam.readLine()) != null) {
		if (line.indexOf("@") == 0) {
		    if (line.indexOf("@SQ") == 0) {
			/*StringTokenizer sqToken = new StringTokenizer(line);
			sqToken.nextToken();
			String strain = sqToken.nextToken();
			if (strain.indexOf("SN:") == 0) {
			    strain = strain.substring(3);
			}
			covered.put(strain,"");*/
		    }
		} else {
		    StringTokenizer token = new StringTokenizer(line);
		    String ID = token.nextToken();
		    token.nextToken();
		    String samStrain = token.nextToken();
		    String samStart = token.nextToken();
		    if (ID.indexOf("|") != -1 && ID.indexOf(":") != -1) {
			String[] pipeSplit = ID.split("\\|");
			String readStrain = "";
			for (int p = 0; p < 4; p++) {
			    readStrain += pipeSplit[p] + "|";
			}
			String[] colonSplit = ID.split(":");
			String start = "";
			if (colonSplit.length > 1) {
			    start = colonSplit[colonSplit.length - 2];
			}
			/*if (readStrain.equals(samStrain)) {
			    if (start.equals(samStart)) {
				covered.put(readStrain,covered.get(readStrain) + start + ",");
			    } else {
			    }
			    }*/
			//System.out.println(readStrain + "," + start);
			out.write(readStrain + "\t" + start + "\t" + start);
			out.newLine();
		    }
		}
            }
	    inputBam.close();
	    out.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }
    public static boolean isInteger(String str) {
	if (str == null) {
	    return false;
	}
	int length = str.length();
	if (length == 0) {
	    return false;
	}
	for (int i = 0; i < length; i++) {
	    char c = str.charAt(i);
	    if (c < '0' || c > '9') {
		return false;
	    }
	}
	return true;
    }
}
