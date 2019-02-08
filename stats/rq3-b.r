eclipse=c(0.22,
          0.23,
          0.25,
          0.27,
          0.27,
          0.27,
          0.24,
          0.26)

aspectj=c(0.25,
          0.25,
          0.24,
          0.24,
          0.23,
          0.21,
          0.2,
          0.17)

swt=c(0.48,
      0.5,
      0.51,
      0.51,
      0.5,
      0.46,
      0.39,
      0.24)

zxing=c(0.61,
        0.62,
        0.62,
        0.6,
        0.62,
        0.6,
        0.44,
        0.28)

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

plot(eclipse, type="b", pch=19, ylim=range(c(eclipse,aspectj,swt,zxing)),
     ylab="MAP", xaxt="n",
     xlab=expression(alpha), lwd=2)
lines(aspectj, type = "b", pch=19, ylab = "", yaxt="n", xlab = "",lwd=2, col="blue", xaxt="n")
lines(swt, type = "b", pch=19, ylab = "", yaxt="n", xlab = "",lwd=2, col="gray", xaxt="n")
lines(zxing, type = "b", pch=19, ylab = "", yaxt="n", xlab = "",lwd=2, col="orange", xaxt="n")
axis(1, at=x<-seq(1,8,by=1), labels=avalues)
legend("topright", legend = c("Eclipse","AspectJ", "SWT","ZXing"), 
       col=c("black","blue","gray","orange"),  
       pch=19, lwd=1)



