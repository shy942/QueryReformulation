vtop1=c(23.03,
        22.88,
        27.81,
        25.99,
        22.37,
        25.25,
        22.85,
        18.42,
        23.76,
        21.45
)
ptop1=c(26.38,
        26.71,
        30.29,
        30.94,
        24.1,
        24.76,
        28.01,
        21.82,
        25.41,
        26.38
)
vtop5=c(48.36,
        48.37,
        52.32,
        54.28,
        42.76,
        47.84,
        49.67,
        42.43,
        44.88,
        50.5
)
ptop5=c(53.75,
        50.16,
        55.37,
        58.63,
        47.55,
        50.81,
        54.4,
        44.95,
        50.16,
        51.14
)
vtop10=c(59.54,
         55.88,
         62.91,
         65.46,
         53.95,
         56.15,
         61.26,
         51.32,
         55.12,
         61.06
)
ptop10=c(61.56,
         60.26,
         64.17,
         71.34,
         60.26,
         62.87,
         64.5,
         52.77,
         58.63,
         62.54
)
par( mgp=c(2,0.7,0), mar=c(6.1,4.1,1.1,2.1))

plot(ptop10, type="b", pch=8, ylim=range(c(vtop1,ptop1,vtop5,ptop5,vtop10,ptop10)),
     ylab="Hit@K", yaxt="n",
     xlab="Fold", lwd=2, lty=1)
lines(ptop5, type = "b", pch=8, ylab = "", yaxt="n", xlab = "",lwd=2, lty=2)
lines(ptop1, type = "b", pch=8, ylab = "", yaxt="n", xlab = "",lwd=2, lty=3)
lines(vtop10, type = "b", pch=8, ylab = "", col="gray", yaxt="n", xlab = "",lwd=2, lty=1)
lines(vtop5, type = "b", pch=8, ylab = "", col="gray", yaxt="n", xlab = "",lwd=2, lty=2)
lines(vtop1, type = "b", pch=8, ylab = "", col="gray", yaxt="n", xlab = "",lwd=2, lty=3)

axis(2, at=x<-seq(0,100,by=10), labels=paste(x,"%",sep=""))
legend("top", xpd=T, ncol=3,   legend = c("Hit@1","Hit@5","Hit@10", "VSM","BLuAMIR"), col=c("black","black","black","gray","black"),  pch=c(8,8,8,NA,NA), lty=c(3,2,1,1,1), lwd=1)




