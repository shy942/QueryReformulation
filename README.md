# QueryReformulation
A bug localization technique that applies historical data and query reformulation.

How to run the project?
===========================
1. Go bug.localization.SourceFilesCosineSimilarity.java file
2. Go to line 82,83,84. These three are input parameters.
    Here, noOfBugsForMap means at first mapping would be created for this number, 
          query will be (noOfBugsForMap+1) th no. bug
          then next query will (noOfBugsForMap+2) and 
          (noOfBugsForMap+1) th bug will be added to the mapping.
          This process will continue untill it reaches totalNoOfBugs or upperlimit
 3. Also go to line 129 to change the name of the result file
          
For Performance Calculation
=============================
1. Go to performance.calculator.CalculateLocalizationPerformance.java
2. Go to line 44, where you will need to put the file name of the result 



 
