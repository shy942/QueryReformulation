vmrr=c(0.33,
       0.33,
       0.38,
       0.38,
       0.31,
       0.35,
       0.35,
       0.28,
       0.33,
       0.33
)
pmrr=c(0.38,
       0.37,
       0.4,
       0.43,
       0.35,
       0.36,
       0.39,
       0.31,
       0.36,
       0.37
)
vmap=c(0.26,
       0.27,
       0.27,
       0.26,
       0.19,
       0.22,
       0.24,
       0.2,
       0.27,
       0.26
)
pmap=c(0.29,
       0.3,
       0.29,
       0.29,
       0.22,
       0.24,
       0.28,
       0.22,
       0.28,
       0.28)

par( mgp=c(2,0.7,0), mar=c(6.1,4.1,1.1,2.1))

plot(pmrr, type="b", pch=6, ylim=range(c(vmrr,vmap,pmrr,pmap)),
     ylab="Performance",
     xlab="Fold", lwd=2)
lines(pmap, type = "b", pch=19, ylab = "", yaxt="n", xlab = "",lwd=2)
lines(vmrr, type = "b", pch=6, ylab = "", yaxt="n", xlab = "",lwd=2, col="gray")
lines(vmap, type = "b", pch=19, ylab = "", col="gray", yaxt="n", xlab = "",lwd=2)
#axis(2, at=x<-seq(0,1,by=.1), labels=paste(x,"%",sep=""))
legend(0,.12, xpd=T, ncol=4, legend = c("MRR","MAP", "VSM","BLuAMIR"), col=c("black","black","gray","black"),  
       pch=c(6,19,NA,NA), lwd=2)

