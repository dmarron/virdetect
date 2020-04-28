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

	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputFile)));
	    BufferedReader inputBam = new BufferedReader(new FileReader(new File(inputFile)));
	    BufferedReader inputFa = new BufferedReader(new FileReader(new File(faIn)));
	    String line = "";
	    int count = 0;
	    while ((line = inputBam.readLine()) != null) {
		if (line.indexOf("@") != 0) {
		    StringTokenizer token = new StringTokenizer(line);
		    String ID = token.nextToken();
		    token.nextToken();
		    String samStrain = token.nextToken();
		    String samStart = token.nextToken();
		    if (ID.indexOf("|") != -1 && ID.indexOf(":") != -1) {
			String[] pipeSplit = ID.split("\\|");
			String readStrain = "";
			for (int p = 0; p < pipeSplit.length; p++) {
                            if (p < pipeSplit.length-1) {
                                readStrain += pipeSplit[p] + "|";
                            } else {
                                readStrain += pipeSplit[p].split(":")[0];
                            }
			}
			String[] colonSplit = ID.split(":");
			String start = "";
			if (colonSplit.length > 1) {
			    start = colonSplit[colonSplit.length - 2];
			}
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
