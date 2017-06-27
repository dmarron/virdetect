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

public class makeVirusRTableHPVAllele {
    public static void main(String[] args) {
	String sampleID = "sampleID";
	String inputFile = "";
	String outFile = "";
	if (args.length == 0) {
	    inputFile = "STAR_virus_Aligned.out.sam";
	    outFile = "viralRTable.txt";
	} else {
	    sampleID = args[0];
	    inputFile = args[1];
	    outFile = args[2];
	}
	//LinkedHashMap<String,Integer> genomeCounts = new LinkedHashMap<String,Integer>();
	LinkedHashMap<String,ArrayList<Integer>> strainCov = new LinkedHashMap<String,ArrayList<Integer>>();
	LinkedHashMap<String,ArrayList<String>> strainAllele = new LinkedHashMap<String,ArrayList<String>>();
	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(new File(outFile)));
	    SAMFileReader inputBam = new SAMFileReader(new File(inputFile));
	    inputBam.setValidationStringency(ValidationStringency.SILENT);
	    List<SAMSequenceRecord> allSequences = inputBam.getFileHeader().getSequenceDictionary().getSequences();
	    for (int i = 0; i < allSequences.size(); i++) {
		String seqName = allSequences.get(i).getSequenceName();
		boolean HPVonly = true;
		if (!HPVonly || seqName.indexOf("Human_herpesvirus_4_complete_wild_type_genome") != -1) {
		    strainCov.put(allSequences.get(i).getSequenceName(),new ArrayList<Integer>());
		    strainAllele.put(allSequences.get(i).getSequenceName(),new ArrayList<String>());
		}
	    }
	    SAMRecordIterator iter = inputBam.iterator();
	    String previousGenome = "";
	    String previousmmID = "";
	    boolean matchNext = false;
            while (iter.hasNext()) {
                SAMRecord s = iter.next();
		String samString = s.getSAMString().trim();
		String[] splitString = samString.split("\t");
		String strain = splitString[2];
		String start = splitString[3];
		String cigar = splitString[5];
		String sequence = s.getReadString();
		ArrayList<Integer> covArr = strainCov.get(strain);
		ArrayList<String> allArr = strainAllele.get(strain);
		if (covArr != null) {
		    updateArray(covArr,allArr,cigar,sequence,Integer.parseInt(start));
		}
		//String multiMapID = splitString[12];
		/*if (multiMapID.equals("HI:i:1")) {
		    genomeCounts.put(splitString[2],genomeCounts.get(splitString[2]) + 1);
		    previousGenome = splitString[2];
		    previousmmID = multiMapID;		    
		} else {
		    if (!splitString[2].equals(previousGenome) || (multiMapID.equals(previousmmID) && matchNext)) {
			if (matchNext && multiMapID.equals(previousmmID) && !splitString[2].equals(previousGenome)) {
			    //chimeric read detected - read pair aligns to different genomes
			    chimericCount++;
			}
			genomeCounts.put(splitString[2],genomeCounts.get(splitString[2]) + 1);
			if (!matchNext) {
			    matchNext = true;
			} else {
			    matchNext = false;
			}
		    }
		    previousGenome = splitString[2];
		    previousmmID = multiMapID;
		    }*/
		//System.out.println(samString);
            }
	    iter.close();
	    inputBam.close();
	    //write genome counts
	    String tabValues = "";
	    for (String key : strainCov.keySet()) {
		ArrayList<Integer> cov = strainCov.get(key);
		ArrayList<String> alle = strainAllele.get(key);
		for (int a = 0; a < cov.size(); a++) {
		    tabValues = (a+1) + "\t" + cov.get(a) + "\t" + alle.get(a) + "\t" + sampleID + "\t" + key;
		    out.write(tabValues);
		    out.newLine();
		}
	    }
	    out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    private static void updateArray(ArrayList<Integer> toUpdate, ArrayList<String> aUpdate, String cigar, String sequence, int start) {
        String currentNum = "";
        int sequenceIndex = 0;
        //int sequenceIndex = trimEnd;
        //start = start + trimEnd;
        int tempTrimEnd = 0;
        int baseNum = 0;
	int min = 0;
        for (int i = 0; i < cigar.length(); i++) {
            char c = cigar.charAt(i);
            if (Character.isDigit(c)) {
                currentNum = currentNum + c;
            } else {
                if (c == 'M' || c == 'I') {
                    int mEnd = Integer.parseInt(currentNum);
		    if (toUpdate.size() == 0) {
			for (int a = 0; a < start + mEnd - min; a++) {
			    toUpdate.add(0);
			    aUpdate.add("0,0,0,0");
			}
		    }
		    if (toUpdate.size() < start + mEnd - min) {
			for (int a = toUpdate.size(); a < start + mEnd - min; a++) {
			    toUpdate.add(0);
			    aUpdate.add("0,0,0,0");
			}
		    }
                    for (int j = 0; j < mEnd; j++) {
                        //if (start + j - min < toUpdate.length) {
                            if (start + j - min > 0) {
				Integer currentInt = toUpdate.get(start + j - min);
                                int pos = start + j;
				currentInt = currentInt + 1;
				toUpdate.set(start+j-min,currentInt);
				String currentAlleleCount = aUpdate.get(start+j-min);
				String[] aSplit = currentAlleleCount.split(",");
				String newString = currentAlleleCount;
				char nucleotide = sequence.charAt(sequenceIndex);
				if (nucleotide == 'A' || nucleotide == 'a') {
				    int newCount = Integer.parseInt(aSplit[0]);
				    newCount = newCount + 1;
				    newString = newCount + "," + aSplit[1] + "," + aSplit[2] + "," + aSplit[3];
				} else if (nucleotide == 'T' || nucleotide == 't') {
				    int newCount = Integer.parseInt(aSplit[1]);
				    newCount = newCount + 1;
				    newString = aSplit[0] + "," + newCount + "," + aSplit[2] + "," + aSplit[3];
				} else if (nucleotide == 'C' || nucleotide == 'c') {
				    int newCount = Integer.parseInt(aSplit[2]);
				    newCount = newCount + 1;
				    newString = aSplit[0] + "," + aSplit[1] + "," + newCount + "," + aSplit[3];
				} else if (nucleotide == 'G' || nucleotide == 'g') {
				    int newCount = Integer.parseInt(aSplit[3]);
				    newCount = newCount + 1;
				    newString = aSplit[0] + "," + aSplit[1] + "," + aSplit[2] + "," + newCount;
				}
				aUpdate.set(start+j-min,newString);
			    }
			    sequenceIndex++;
			    //}
		    } 
		} else if (c == 'I') {
		    //add insertion
			int insertNum = Integer.parseInt(currentNum);
			/*if (start - min > 0 && start - min < toUpdate.length) {
			    String currentString = toUpdate[start - min];
			    if (sequenceIndex != 0) {
				String insertion = "";
				for (int j = 0; j < insertNum; j++) {
				    char nucleotide = sequence.charAt(sequenceIndex);
				    insertion = insertion + nucleotide;
				    sequenceIndex++;
				}
				if (currentString.equals("")) {
				    currentString = start + ",0,0,0,0,0";
				}
				String updateString;
				int insIndex = currentString.indexOf(insertion);
				if (insIndex == -1) {
				    updateString = currentString + "," + insertion + ",1";
				} else {
				    String[] split = currentString.substring(insIndex).split(",");
				    int currentCount = Integer.parseInt(split[1]);
				    currentCount++;
				    updateString = currentString.substring(0,insIndex) + split[0] + "," + currentCount;
				    for (int s = 2; s < split.length; s++) {
					updateString = updateString + "," + split[s];
				    }
				}
				toUpdate[start - min] = updateString;
			    } else {
				sequenceIndex += insertNum;
			    }
			} else {
			    sequenceIndex += insertNum;
			    }*/
			sequenceIndex += insertNum;
		    } else if (c == 'S') {
			//these characters are softclipped so do not update the array
			//skip over the softclipped characters in the sequence
			sequenceIndex += Integer.parseInt(currentNum);
		    } else if (c == '*' || c == 'D' || c == 'N') {
			//unaligned, do not update
		    } else {
			System.out.println("Warning: Ignoring unsupported cigar character in " + cigar);
		    }
		    if (!currentNum.equals("")) {
			if (c != 'I' && c != 'S') {
			    //don't change ref position if insertion or softclip
			    start += Integer.parseInt(currentNum);
			}
			currentNum = "";
		    }
		
	    }
	}
    }
}