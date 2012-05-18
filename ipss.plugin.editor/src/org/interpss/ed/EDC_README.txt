Some of InterPSS' ED implementation is based on the program disk from the book
POWER GENERATION OPERATION AND CONTROL by A.J.Wood and B.F.Wollenberg, 
published by John Wiley & Sons, New York, ISBN 0-471-09182-0. The following
are information from the original READ file. 

============================================
November 10, 1994

This disk contains programs useful in teaching a course centered on the book
POWER GENERATION OPERATION AND CONTROL by A.J.Wood and B.F.Wollenberg, 
published by John Wiley & Sons, New York, ISBN 0-471-09182-0. The programs are 
intended for educational use and DO NOT come with any guarantee of 
correctness. They are provided free of charge and may be copied and 
distributed to your students. Copies of the documentation on the disks 
may also be copied freely. 

The programs are written in TURBO PASCAL 4.0 . Each program has a 
documentation file, a source file of the program, an executable file, 
several data files that contain cases to be run by the program and in 
some cases a file describing some exercises you may wish to give your 
students.  

For example if we had a program XYZ we might have:

XYZ.DOC - Documentation file
XYZ.PAS - PASCAL Source File 
XYZ.EXE - PASCAL Executable File
XYZ1.DAT - First data file 
XYZ2.DAT - Second data file 
XYZ.EXR - Student exercises etc. 

The documentation file should be printed and read before you attempt to run 
the program. It explains how the program works and how to set up data files 
for the homework problems. NOTE: the documentation file is an ASCII file and 
can be printed by entering TYPE after Ctrl-PrtSc or by reading it into a word 
processor that takes unformatted ASCII files and then using the word 
processor's print routines.  

The source files for the PASCAL programs are also ASCII files. They 
can be printed in the same way as as the documentation file if you care to see 
the programs. 

To run the programs enter XYZ, where XYZ is the name of the PASCAL 
executable file for the program. 

The PASCAL programs are written in TURBO PASCAL 4.0 and if you wish to 
make any changes to them you must obtain the TURBO PASCAL 4.00 from 
BORLAND INT. I highly recommend it! 

NOTE: TURBO PASCAL requires that real variables be formatted properly to 
be read directly into the program from a data file. By looking at any of 
the data files you can see that all real variables are formatted with a 
zero in front of the decimal in the case of a number less than one and a 
zero after the decimal for a number with no fractional part. Thus .001 
is not acceptable, you must put in 0.001 and 100. is not acceptable you 
must put in 100.0 ( also, 100 is not acceptable if the variable is a 
real number, it must be 100.0 ). Integers have no restrictions. Finally, 
separate all variables by spaces, not commas. 

The disk you received has the following programs and data files:

Economic Dispatch ( EDC )
Unit Commitment ( UNITCOM )
Linear Programming ( DUBLP )
Hydro scheduling via dynamic programming ( HYDRO )
Load flow ( LOADFLOW )
State Estimation ( STATEEST )

If you need help call me at (612) 626-7192 ( 8am-5pm ), leave a message 
if I am not in. FAX (612) 625-4583, email wollenbe@ee.umn.edu

Any comments on the usefulness of these programs and/or program bugs found 
would be greatly appreciated and should be sent to me via my office address:

Bruce Wollenberg 
Electrical Engineering Department
University of Minnesota
200 Union Street S.E.
Minneapolis MN 55455

If you have any programs useful in the subject area of the book and compatible 
with the IBM PC send them to me and I will add them to the collection and send 
out new disks when sufficient changes and additions warrant.  

