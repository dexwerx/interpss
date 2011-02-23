/**
 * 
 */
package sensMatrix;

import org.eclipse.emf.common.util.Enumerator;

/**
 * @author Tony Huang
 *
 */
public enum DstabAlgoMethod implements Enumerator
{
	  TRAPEZOIDUAL_RULE;
	  public static final int TRAPEZOIDUAL_RULE_VALUE = 0;
//	  public static final int OMEGA_VALUE = 1;
//	  public static final int EQ1_VALUE = 2;
//	  public static final int EQ11_VALUE = 3;
//	  public static final int ED11_VALUE = 4;
	  private static final DstabAlgoMethod[] VALUES_ARRAY={ TRAPEZOIDUAL_RULE};
	  private  int value;
	  private  String name;
	  private  String literal;

	  public static DstabAlgoMethod get(String literal)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	    	DstabAlgoMethod result = VALUES_ARRAY[i];
	      if (result.toString().equals(literal))
	        return result;
	    }

	    return null;
	  }

	  public static DstabAlgoMethod getByName(String name)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	      DstabAlgoMethod result = VALUES_ARRAY[i];
	      if (result.getName().equals(name))
	        return result;
	    }

	    return null;
	  }

	  public static DstabAlgoMethod get(int value)
	  {
	    switch (value) {
	    case 0:
	      return TRAPEZOIDUAL_RULE;
//	    case 1:
//	    	return OMEGA;
//	    case 2:
//	      return EQ1;
//	    case 3:
//	      return EQ11;
//	    case 4:
//	      return ED11;
	    }
	    return null;
	  }

	  public int getValue()
	  {
	    return this.value;
	  }

	  public String getName()
	  {
	    return this.name;
	  }

	  public String getLiteral()
	  {
	    return this.literal;
	  }

	  public String toString()
	  {
	    return this.literal;
	  }
	}
