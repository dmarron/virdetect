Virdetect

Contents

1. Getting started
2. Index genomes with STAR
3. Run workflow with command line
4. Visualization
5. Masking custom genomes
6. BLAST

Getting Started

The files needed for the virdetect workflow can be downloaded from
GitHub at https://github.com/dmarron/virdetect
The workflow can be run as several command line steps
Before the workflow can be run, the appropriate genome files (.fa) will need
to be downloaded and then indexed with the STAR aligner
(https://github.com/alexdobin/STAR). For human data, download
hg38_noEBV.fa and virus_masked_hg38.fa from the virdetect gitHub. For
mouse data, download mm10.fa from UCSC or ensembl and download
virus_masked_mm10.fa from the virdetect github. For the commands to
index this genomes with STAR, see Section 2.

Index Genomes with STAR

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
STAR --runThreadN 8 --genomeChrBinNbits 14 --runMode genomeGenerate --genomeDir hg38_star_dir --genomeFastaFiles hg38.fa --sjdbGTFfile hg38_gtf.gtf
STAR --runThreadN 1 --runMode genomeGenerate --genomeSAindexNbases 7 --genomeDir hg38_virus_dir --genomeFastaFiles virus_masked_hg38.fa
```

For mouse data:
```javascript
STAR --runThreadN 8 --genomeChrBinNbits 14 --runMode genomeGenerate --genomeDir mm10_star_dir --genomeFastaFiles mm10.fa --sjdbGTFfile mm10_gtf.gtf
STAR --runThreadN 1 --runMode genomeGenerate --genomeSAindexNbases 7 --genomeDir mm10_virus_dir --genomeFastaFiles virus_masked_mm10.fa
```

Run workflow with command line

After the genomes are indexed with STAR, the virdetect workflow is ready
to be run. To run virdetect from the command line, download all java files,
sh files, and jar files from the virdetect github. Then, run the following command line steps.
For human data the workflow is:
```javascript
STAR --runThreadN 16 --genomeDir hg38_star_dir --readFilesIn <input_R1.fastq.gz> <input_R2.fastq.gz> --readFilesCommand zcat --outFilterMultimapNmax 1000 --outSAMunmapped Within --outFileNamePrefix STAR_ #16 cpus, 32 G memory
sh awk_column3_star.sh STAR_Aligned.out.sam > unaligned.sam
sh awk_unalignedfq_1.sh unaligned.sam > unaligned_1.fastq && sh awk_unalignedfq_2.sh unaligned.sam > unaligned_2.fastq
STAR --genomeDir hg38_virus_dir --readFilesIn unaligned_1.fastq unaligned_2.fastq --runThreadN 16 --outFilterMismatchNmax 4 --outFilterMultimapNmax 1000 --limitOutSAMoneReadBytes 1000000 --outFileNamePrefix STAR_virus_ # 16 cpus, 32 G memory
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:countStarViralAlignments <sample_name> STAR_virus_Aligned.out.sam viralReadCounts.txt # 8 G memory
#remove intermediate files other than viralReadCounts.txt to clean up
```

For mouse data the workflow is:
```javascript
STAR --runThreadN 16 --genomeDir mm10_star_dir --readFilesIn <input_R1.fastq.gz> <input_R2.fastq.gz> --readFilesCommand zcat --outFilterMultimapNmax 1000 --outSAMunmapped Within --outFileNamePrefix <output_dir>/STAR_ #16 cpus, 32 G memory
sh awk_column3_star.sh <output_dir>/STAR_Aligned.out.sam > unaligned.sam
sh /home/dmarron/workspace/scripts/awk_unalignedfq_1.sh unaligned.sam > unaligned_1.fastq && sh awk_unalignedfq_2.sh unaligned.sam >
unaligned_2.fastq
STAR --genomeDir mm10_virus_dir --readFilesIn unaligned_1.fastq unaligned_2.fastq --runThreadN 16 --outFilterMismatchNmax 4 --outFilterMultimapNmax 1000 --limitOutSAMoneReadBytes 1000000 --outFileNamePrefix STAR_virus_ # 16 cpus, 32 G memory
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:countStarViralAlignments <sample_name> STAR_virus_Aligned.out.sam viralReadCounts.txt # 8 G memory
#remove intermediate files other than viralReadCounts.txt to clean up
```

Visualization

After running virdetect, it may be of interest to visualize coverage of specific
viruses. To run the visualization, download makeRTable.java, plotTable.R,
and the jar files from the virdetect github. The run the commands:
```javascript
java -Xmx4G -cp picard-1.92.jar:sam-1.92.jar:makeRTable virus_name STAR_virus.Aligned.out.bam viralRTable.txt
Rscript plotTable.R viralRTable.txt
```
Those commands will plot the coverage of the virus strain given by the
parameter virus_name.

Masking custom genomes

It may be of interest to run virdetect with custom virus strains rather than
the ones provided in virus_masked_hg38.fa and virus_masked_mm10.fa.
To do so, download simulateReads.java, makeAlignedBed.java and
maskGenome.java from the virdetect github. To mask your custom
genome fa (custom_virus.fa) for human data, run the following commands:
```javascript
java simulateReads custom_virus.fa sim.fastq
STAR --runThreadN 16 --genomeDir mm10_star_dir --readFilesIn sim.fastq --outFilterMismatchNmax 5 --outFilterMultimapNmax 1080 --outFileNamePrefix STAR_
java makeAlignedBed STAR_Aligned.out.sam aligned.bed
java maskGenome custom_virus.fa aligned.bed custom_masked_virus.fa
```
The resulting file, custom_masked_virus.fa, contains the masked genome
that can then be indexed with STAR (with command from section 2) and
used with virdetect.

BLAST

