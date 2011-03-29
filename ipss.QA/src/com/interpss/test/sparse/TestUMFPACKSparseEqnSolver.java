package com.interpss.test.sparse;

public class TestUMFPACKSparseEqnSolver
{
    public static void main(String[] args)
    {
    	UMFPACKSparseEqnSolver solver = new UMFPACKSparseEqnSolver(5, 12, 
    			UMFPACKSparseEqnSolver.DataType.Double, UMFPACKSparseEqnSolver.CompressedFormat);
    	
		solver.setAp(0, 0);
		solver.setAp(1, 2);
		solver.setAp(2, 5);
		solver.setAp(3, 9);
		solver.setAp(4, 10);
		
		solver.setAi(0, 0);
		solver.setAi(1, 1);
		solver.setAi(2, 0);
		solver.setAi(3, 2);
		solver.setAi(4, 4);
		solver.setAi(5, 1);
		solver.setAi(6, 2);
		solver.setAi(7, 3);
		solver.setAi(8, 4);
		solver.setAi(9, 2);
		solver.setAi(10, 1);
		solver.setAi(11, 4);

		solver.setAx(0, 2.0);
		solver.setAx(1, 3.0);
		solver.setAx(2, 3.0);
		solver.setAx(3, -1.0);
		solver.setAx(4, 4.0);
		solver.setAx(5, 4.0);
		solver.setAx(6,-3.0);
		solver.setAx(7, 1.0);
		solver.setAx(8, 2.0);
		solver.setAx(9, 2.0);
		solver.setAx(10, 6.0);
		solver.setAx(11, 1.0);

		solver.setB(0, 8.0);
		solver.setB(1, 45.0);
		solver.setB(2, -3.0);
		solver.setB(3, 3.0);
		solver.setB(4, 19.0);

        double[] x = solver.doubleSolve();

		for (int j = 0; j < 5; j++ )
		{
			System.out.println("x["+j+"] is "  + x[j]); 
		}
    }
}