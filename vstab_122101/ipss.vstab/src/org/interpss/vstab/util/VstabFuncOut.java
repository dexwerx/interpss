package org.interpss.vstab.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.math.linear.RealVector;
import com.interpss.common.datatype.Matrix_xy;
import com.interpss.common.datatype.Vector_xy;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.sparse.SparseEqnMatrix2x2;

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
			   Matrix_xy mxy=m.getElement(i+1, j+1);
			   mAry[2 * i ][2 * j ]=mxy.xx;
			   mAry[2 * i ][2 * j+1]=mxy.xy;
			   mAry[2 * i+1 ][2 * j]=mxy.yx;
			   mAry[2 * i +1][2 * j+1]=mxy.yy;
		   }
	   }
	   return mAry;
	   
   }
   public static void printBVector(AclfNetwork net, final SparseEqnMatrix2x2 lfEqn){
	   String s="\n";
	   net.forEachAclfBus(new IAclfBusVisitor(){
		@Override
		public void visit(AclfBus bus) {
			int i=bus.getSortNumber();
			Vector_xy bxy=lfEqn.getBVect_xy(i);
			System.out.println(bus.getId()+"  "+bus.getGenCode().getLiteral()+":  dAng="+bxy.x+",  dMag="+bxy.y);
		}
		   
	   });
	   if(lfEqn.getDimension()>net.getNoBus()*2){
		   Vector_xy v=lfEqn.getBVect_xy(net.getNoBus()+1);
		   System.out.println("B("+(net.getNoBus()+1)+") :  dAng="+v.x+",  dMag="+v.y);
	   }
   }
   
}
