eclipse=c(0.31,
          0.33,
          0.35,
          0.37,
          0.38,
          0.38,
          0.35,
          0.3
)
aspectj=c(0.42,
          0.43,
          0.43,
          0.44,
          0.43,
          0.41,
          0.37,
          0.33
)
swt=c(0.55,
      0.57,
      0.6,
      0.59,
      0.58,
      0.54,
      0.48,
      0.43
)
zxing=c(0.7,
        0.7,
        0.68,
        0.66,
        0.67,
        0.64,
        0.47,
        0.3
)

avalues=c(0,
          0.1,
          0.2,
          0.3,
          0.4,
          0.5,
          0.6,
          0.7
)


par( mgp=c(2,0.7,0), mar=c(6.1,4.1,1.1,2.1))

plot(eclipse, type="b", pch=6, ylim=range(c(eclipse,aspectj,swt,zxing)),
     ylab="MRR", xaxt="n",
     xlab=expression(alpha), lwd=2)
lines(aspectj, type = "b", pch=6, ylab = "", yaxt="n", xlab = "",lwd=2, col="blue", xaxt="n")
lines(swt, type = "b", pch=6, ylab = "", yaxt="n", xlab = "",lwd=2, col="gray", xaxt="n")
lines(zxing, type = "b", pch=6, ylab = "", yaxt="n", xlab = "",lwd=2, col="orange", xaxt="n")
axis(1, at=x<-seq(1,8,by=1), labels=avalues)
legend("topright", legend = c("Eclipse","AspectJ", "SWT","ZXing"), 
       col=c("black","blue","gray","orange"),  
       pch=6, lwd=1)



