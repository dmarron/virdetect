# Virdetect

## Contents

1. Getting started
2. Index genomes with STAR
3. Run workflow with command line
4. Visualization
5. Masking custom genomes
6. BLAST

## Getting Started

The files needed for the virdetect workflow can be downloaded from
GitHub at https://github.com/dmarron/virdetect.
The workflow can be run as several command line steps.
Before the workflow can be run, the appropriate genome files (.fa) will need
to be downloaded and then indexed with the STAR aligner
(https://github.com/alexdobin/STAR). For human data, download
hg38_noEBV.fa and virus_masked_hg38.fa from the virdetect github reference folder. For
mouse data, download mm10.fa from UCSC or ensembl and download
virus_masked_mm10.fa from the virdetect github reference folder. For the commands to
index this genomes with STAR, see Section 2.

## Index Genomes with STAR

To index the genome .fa files, download the STAR aligner and the
necessary files for the human and mouse workflows.
For human data, download hg38_noEBV.fa and a hg38 gtf file (Example -
ftp://ftp.ensembl.org/pub/release-89/gtf/homo_sapiens).
For mouse data, download mm10.fa and a mm10 gtf file (Example -
ftp://ftp.ensembl.org/pub/release-89/gtf/mus_musculus).
Then run the following commands to index the main genome and virus
genomes:

For human data:
```javascript
STAR --runThreadN 8 --genomeChrBinNbits 14 --runMode genomeGenerate --genomeDir hg38_star_dir --genomeFastaFiles hg38_noEBV.fa --sjdbGTFfile hg38_gtf.gtf
STAR --runThreadN 1 --runMode genomeGenerate --genomeSAindexNbases 7 --genomeDir hg38_virus_dir --genomeFastaFiles virus_masked_hg38.fa
```

For mouse data:
```javascript
STAR --runThreadN 8 --genomeChrBinNbits 14 --runMode genomeGenerate --genomeDir mm10_star_dir --genomeFastaFiles mm10.fa --sjdbGTFfile mm10_gtf.gtf
STAR --runThreadN 1 --runMode genomeGenerate --genomeSAindexNbases 7 --genomeDir mm10_virus_dir --genomeFastaFiles virus_masked_mm10.fa
```

## Run workflow with command line

After the genomes are indexed with STAR, the virdetect workflow is ready
to be run. To run virdetect from the command line, download all jar files from the java folder
and sh files from the scripts folder in the virdetect github. Then, run the following command line steps.

### For human data the workflow is:
```javascript
STAR --runThreadN 16 --genomeDir hg38_star_dir --readFilesIn <input_R1.fastq.gz> <input_R2.fastq.gz> --readFilesCommand zcat --outFilterMultimapNmax 1000 --outSAMunmapped Within --outFileNamePrefix STAR_ #16 cpus, 32 G memory
sh awk_column3_star.sh STAR_Aligned.out.sam > unaligned.sam
sh awk_unalignedfq_1.sh unaligned.sam > unaligned_1.fastq && sh awk_unalignedfq_2.sh unaligned.sam > unaligned_2.fastq
STAR --genomeDir hg38_virus_dir --readFilesIn unaligned_1.fastq unaligned_2.fastq --runThreadN 16 --outFilterMismatchNmax 4 --outFilterMultimapNmax 1000 --limitOutSAMoneReadBytes 1000000 --outFileNamePrefix STAR_virus_ # 16 cpus, 32 G memory
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:countStarViralAlignments.jar countStarViralAlignments <sample_name> STAR_virus_Aligned.out.sam viralReadCounts.txt # 8 G memory
#remove intermediate files other than viralReadCounts.txt and STAR_virus_Aligned.out.sam to clean up
```

### For mouse data the workflow is:
```javascript
STAR --runThreadN 16 --genomeDir mm10_star_dir --readFilesIn <input_R1.fastq.gz> <input_R2.fastq.gz> --readFilesCommand zcat --outFilterMultimapNmax 1000 --outSAMunmapped Within --outFileNamePrefix <output_dir>/STAR_ #16 cpus, 32 G memory
sh awk_column3_star.sh <output_dir>/STAR_Aligned.out.sam > unaligned.sam
sh awk_unalignedfq_1.sh unaligned.sam > unaligned_1.fastq && sh awk_unalignedfq_2.sh unaligned.sam >
unaligned_2.fastq
STAR --genomeDir mm10_virus_dir --readFilesIn unaligned_1.fastq unaligned_2.fastq --runThreadN 16 --outFilterMismatchNmax 4 --outFilterMultimapNmax 1000 --limitOutSAMoneReadBytes 1000000 --outFileNamePrefix STAR_virus_ # 16 cpus, 32 G memory
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:countStarViralAlignments.jar countStarViralAlignments <sample_name> STAR_virus_Aligned.out.sam viralReadCounts.txt # 8 G memory
#remove intermediate files other than viralReadCounts.txt and STAR_virus_Aligned.out.sam to clean up
```

## Visualization

After running virdetect, it may be of interest to visualize coverage of specific
viruses. To run the visualization, download the jar files from the java folder and run the following command
to make a table that is plottable in R:
```javascript
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:makeRTable.jar makeRTable <sample_name> <virus_name> STAR_virus.Aligned.out.sam viralRTable.txt
```
Then, use R to run the commands in plotTable.R in the scripts folder on the produced viralRTable.txt.
These commands will plot the coverage of the virus strain given by the parameter <virus_name>.

## Masking custom genomes

It may be of interest to run virdetect with custom virus strains rather than
the ones provided in virus_masked_hg38.fa and virus_masked_mm10.fa.
To do so, download simulateReads.jar, makeAlignedBed.jar and
maskGenome.jar from the java folder and subLowComplex.pl from
the scripts folder. To mask your custom genome fa (custom_virus.fa)
for human data, run the following commands:
```javascript
(Replace spaces with another character such as '|' in the custom genome)
sed -i 's/ /|/g' custom_virus.fa
java -Xmx4G -cp simulateReads.jar simulateReads custom_virus.fa sim.fastq
STAR --runThreadN 16 --genomeDir hg38_star_dir --readFilesIn sim.fastq --outFilterMismatchNmax 5 --outFilterMultimapNmax 1080 --outFileNamePrefix STAR_
perl subLowComplex.pl custom_virus.fa > virus_mask_1.fa
java -Xmx4G -cp makeAlignedBed.jar makeAlignedBed STAR_Aligned.out.sam aligned.bed custom_virus.fa
java -Xmx4G -cp maskGenome.jar maskGenome aligned.bed virus_mask_1.fa custom_masked_virus.fa > mask_stats.txt
```
Run the STAR command with --genomeDir mm10_star_dir instead for mouse data.
The resulting file, custom_masked_virus.fa, contains the masked genome
that can then be indexed with STAR (with command from section 2) and
used with virdetect.

## BLAST

After the virdetect workflow is complete, it is possible to run BLAST on a representative subset of the virus reads.  The following script will select up to four reads of maximal genomic distance from each virus strain and run BLAST on each one of them.  To do this, download and install BLAST from ncbi, then run the following commands:
```javascript
java -Xmx8G -cp picard-1.92.jar:sam-1.92.jar:printBlastViralReads.jar printBlastViralReads STAR_virus_Aligned.out.sam output_dir > blast_commands.sh
sh blast_commands.sh
```
