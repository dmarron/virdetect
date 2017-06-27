perl /datastore/nextgenout2/share/labs/UNCseq/tools/mergeFastqs.pl -f $fq1,$fq2,$fq3,$fq4,$fq5,$fq6,$fq7,$fq8 -o $outdir/working/temp # cpus:9 iobound

#align to mm10
/datastore/nextgenout4/seqware-analysis/lmose/software/star/STAR_2.4.2a/STAR-STAR_2.4.2a/source/STAR --genomeDir /datastore/nextgenout4/seqware-analysis/lmose/ref/mouse/mm10/star/ --readFilesIn $outdir/working/temp_R1.fastq $outdir/working/temp_R2.fastq --runThreadN 16 --outFilterMultimapNmax 1080 --outSAMunmapped Within --outFileNamePrefix $outdir/working/STAR_mouse_ # cpus:16 mem:2

#Get unaligned reads from realigned sam
/home/dmarron/workspace/scripts/awk_column3_star.sh $outdir/working/STAR_mouse_Aligned.out.sam > $outdir/working/unaligned_mm.sam

#put unaligned reads in fastq
/home/dmarron/workspace/scripts/awk_unalignedfq_1.sh $outdir/working/unaligned_mm.sam > $outdir/working/unaligned_mm_1.fastq

/home/dmarron/workspace/scripts/awk_unalignedfq_2.sh $outdir/working/unaligned_mm.sam > $outdir/working/unaligned_mm_2.fastq

#align with masked genome - changed to 4 mismatches on 5/4/17
/datastore/nextgenout4/seqware-analysis/lmose/software/star/STAR_2.4.2a/STAR-STAR_2.4.2a/source/STAR --genomeDir /datastore/nextgenout4/share/labs/bioinformatics/seqware/virus_genomes/viral_broad_masked_mm10_4_28/Genomes --readFilesIn $outdir/working/unaligned_mm_1.fastq $outdir/working/unaligned_mm_2.fastq --runThreadN 16 --outFilterMismatchNmax 4 --outFilterMultimapNmax 52 --limitOutSAMoneReadBytes 1000000 --outFileNamePrefix $outdir/STAR_virus_ # cpus:16 mem:2

#make a row of a table of viral read counts.  Row is sample and column is genome
java -Xmx4G -cp /home/dmarron/workspace/scripts/picard-1.92.jar:/home/dmarron/workspace/scripts/sam-1.92.jar:/home/dmarron/workspace/scripts/ countStarViralAlignments $sample $outdir/STAR_virus_Aligned.out.sam $outdir/viralReadCounts.txt #mem:8

ls -l $outdir/working/ > $outdir/output/fileSizes.txt

rm -rfv $outdir/working/* # cleanup
