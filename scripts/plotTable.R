library(ggplot2)
# read in vizTable
mcf<-read.delim("viralRTable.txt",header = F)

# sum number of reads
mcf$total<-apply(mcf[,c(3:6)],1,sum)

# find top allele frequency
mcf$vaf<-apply(mcf[,c(3:6)],1,max)/mcf$total

# identify which allele has maximum coverage
mcf$nt<-as.factor(c("A","T","C","G"))[apply(mcf[,c(3:6)],1,which.max)]

# identify loci with variant allele frequency >10 and coverage >10
mcf$var<-""
mcf$var[which(mcf$V9!=mcf$nt & mcf$vaf>0.9 & mcf$V2>10)]<-as.character(mcf$nt[which(mcf$V9!=mcf$nt & mcf$vaf>0.9 & mcf$V2>10)])

# plot results
ggplot(mcf,aes(V1,V2))+
  geom_line(color="gray")+
  geom_point(aes(color=var))+
  scale_color_manual("Variant allele",values=c("transparent","brown","darkolivegreen","royalblue","goldenrod"))+
  scale_y_continuous("Coverage")+
  scale_x_continuous("")+
  theme_bw(base_size=14)+
  theme(axis.text=element_text(color="black"),
        panel.background=element_rect(color="black"),
        strip.text = element_text(size=12),
        strip.background = element_rect(fill="white"))
