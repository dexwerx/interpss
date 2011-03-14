package org.interpss.vstab.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import org.apache.commons.math.linear.RealVector;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfBusVisitor;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class VstabFuncOut {
	/**
	 * 
	 * @param m
	 * @param width width of column;
	 * @param d the number of digits after the decimal
	 */
   public static void printJmatix(SparseEqnMatrix2x2 m,int width,int d) {
	   double[][] temp=SparseMatrix2Ary(m);
	   StringBuffer sb=new StringBuffer();
	   DecimalFormat format = new DecimalFormat();
	   format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	   format.setMinimumIntegerDigits(1);
	   format.setMaximumFractionDigits(d);
	   format.setMinimumFractionDigits(d);
	   sb.append("\n");
	   for(int i=0;i<temp.length;i++) {
		   for(int j=0;j<temp[0].length;j++) {
			   String s=format.format(temp[i][j]);
			   sb.append(s);
			   int padding = Math.max(1,width-s.length()); // At _least_ 1 space
	           for (int k = 0; k < padding; k++) sb.append(" ");
	           
	            
		   }
		   sb.append("\n");
	   }
	   sb.append("\n");
	   System.out.print(sb);
	   


	   
   }
   public static void printRealVector(RealVector v) {
	   String s="\n";
	   for(double elem:v.toArray()) {
		   s=s+elem+"\n";
	   }
	   System.out.println(s);
   }
   
   public static double[][] SparseMatrix2Ary(SparseEqnMatrix2x2 m){
	   System.out.println("J-matrix Demension:"+m.getDimension());
	   double[][] mAry=new double[m.getDimension()][m.getDimension()];
	   for(int i=0;i<m.getDimension()/2;i++) { // index 1-N
		   for(int j=0;j<m.getDimension()/2;j++) {//index 1-N
			   Matrix_xy mxy=m.getA(i, j);
			   mAry[2 * i ][2 * j ]=mxy.xx;
			   mAry[2 * i ][2 * j+1]=mxy.xy;
			   mAry[2 * i+1 ][2 * j]=mxy.yx;
			   mAry[2 * i +1][2 * j+1]=mxy.yy;
		   }
	   }
	   return mAry;
	   
   }
   public static void printBVector(AclfNetwork net, final SparseEqnMatrix2x2 lfEqn){

	   net.forEachAclfBus(new IAclfBusVisitor(){
		@Override
		public void visit(AclfBus bus) {
			int i=bus.getSortNumber();
			Vector_xy bxy=lfEqn.getX(i);
			System.out.println("sortNumber"+bus.getSortNumber()+"  ,busId  "+bus.getId()+"  ,"+bus.getGenCode().getLiteral()+":  b.x="+bxy.x+",  b.y="+bxy.y);
		}
		   
	   });
	   if(lfEqn.getDimension()>net.getNoBus()*2){
		   Vector_xy v=lfEqn.getX(net.getNoBus());// just to get lambda parameter now;
		   System.out.println("B("+(net.getNoBus())+") :  b.x="+v.x+",  b.y="+v.y);
	   }
   }
   public static void pvResult2EXL(final CPFAlgorithm cpf,String filePath) throws BiffException, IOException, RowsExceededException, WriteException{
	   final List<Double> lambdaList=cpf.getCpfSolver().getLambdaList();
	   final List<Hashtable<Integer, Double>> busPVList=cpf.getCpfSolver().getBusPVCurveList();
	   WritableWorkbook wbook=Workbook.createWorkbook(new File(filePath));
	   WritableSheet sheet=wbook.createSheet("CPF Result",0);
	   
	   for(int row=0;row<=lambdaList.size();row++){
		   for(int col=0;col<=cpf.getAclfNetwork().getNoBus();col++){
			   if(row==0){  // the first row of the sheet is the name of every column;
				   if(col==0){  
					Label l=new Label(col, row, "Lambda");
					sheet.addCell(l);
				   }
				   else{
					Label l=new Label(col,row,cpf.getAclfNetwork().getBusList().get(col-1).getId());// busID indexed by original bus Index;
					sheet.addCell(l);
				   }
				   
			   }
			   else{
				   if(col==0){
					   Number n=new Number(col,row,cpf.getCpfSolver().getLambdaList().get(row-1));// Lambda(;0)
					   sheet.addCell(n);
				   }
				   else{
					   Number n= new Number(col,row,cpf.getCpfSolver().getBusPVCurveList().get(col-1).get(row-1));//bus Vmag of each bus  
					   sheet.addCell(n);
				   }
				   
			   }
			   
		   }
	   }
	   wbook.write();
	   wbook.close();
	   
   }
   public static void pvResutl2CSV(){
	   
   }
   
}
