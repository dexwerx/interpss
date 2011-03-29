package com.interpss.test.sparse;

public class UMFPACKSparseEqnSolver
{
	public static enum DataType {Double, Complex};
	
	public static final int CompressedFormat = 1,
                             TripletFormat = 2;
    
    native double[] doubleSolve(int n, int dataFormat, int[] Ap, int[] Ai, double[] Ax, double[] b);
	native double[] complexSolve(int n, int dataFormat, int[] Ap, int[] Ai, double[] Ax, double[] Az, double[] b);

    static
    {
        //System.out.println(System.getProperties().get("java.library.path"));
		System.loadLibrary("UMFPACKSparseEqnSolver");
    }     
    
    private int n, dateFormat;
    private int[] Ap, Ai;
    private double[] Ax, Az, b;

    public UMFPACKSparseEqnSolver(int n, int m, DataType type, int dataFormat) {
    	this.n = n;

    	this.dateFormat = dataFormat;
    	if (dataFormat == CompressedFormat)
    		this.Ap = new int[n];
    	else
    		this.Ap = new int[m];
    	
    	this.Ai = new int[m];
    	this.Ax = new double[m];
    	if (type == DataType.Double) {
        	this.b = new double[n];
    	}
    	else { // complex type
        	this.b = new double[2*n];
    		this.Az = new double[m]; 
    	}
    }
    
    public void setAp(int i, int ap) {
    	this.Ap[i] = ap;
    }

    public void setAi(int i, int ai) {
    	this.Ai[i] = ai;
    }

    public void setAx(int i, double ax) {
    	this.Ax[i] = ax;
    }

    public void setAz(int i, double az) {
    	this.Az[i] = az;
    }

    public void setB(int i, double b) {
    	this.Az[i] = b;
    }
    
    public double[] doubleSolve() {
    	return doubleSolve(this.n, this.dateFormat, this.Ap, this.Ai, this.Ax, this.b);
    }

    public double[] complexSolve() {
    	return complexSolve(this.n, this.dateFormat, this.Ap, this.Ai, this.Ax, this.Az, this.b);
    }
}