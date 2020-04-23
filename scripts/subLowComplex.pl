$file = shift;
open(IN,$file);
while($line = <IN>){
   chomp $line;	
   if($line =~ />/){
      $header = $line;
   }
   else{
      $seqHash{$header} = $seqHash{$header} . $line;
   }
}

foreach $virus (keys %seqHash){
   $seqHash{$virus} =~ s/((.)\2{10,})/"N" x length($1)/eg;
   $seqHash{$virus} =~ s/((..)\2{8,})/"N" x length($1)/eg;
   $seqHash{$virus} =~ s/((...)\2{5,})/"N" x length($1)/eg;
   $seqHash{$virus} =~ s/((....)\2{4,})/"N" x length($1)/eg;
   $seqHash{$virus} =~ s/((.....)\2{3,})/"N" x length($1)/eg;
   $seqHash{$virus} =~ s/((......)\2{3,})/"N" x length($1)/eg;
   @seq = split("",$seqHash{$virus});
 print "$virus\n";
   for($i=0;$i<scalar(@seq);$i++){
      $count = $count + 1;
      if($count < 80 && $i == scalar(@seq)-1){
	 print "$seq[$i]\n";
         $count = 0;
      }
      elsif($count < 80){
	 print "$seq[$i]";
      }
      else{
	 print "$seq[$i]\n";
	 $count = 0;
      }
   }
}
