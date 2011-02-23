package sensMatrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

public enum XVariablesEnum implements Enumerator
{
	  THETA, OMEGA,EQ1, EQ11,ED11;
	  public static final int THETA_VALUE = 0;
	  public static final int OMEGA_VALUE = 1;
	  public static final int EQ1_VALUE = 2;
	  public static final int EQ11_VALUE = 3;
	  public static final int ED11_VALUE = 4;
	  private static final XVariablesEnum[] VALUES_ARRAY={ THETA,OMEGA, EQ1,EQ11,ED11};
	  private  int value;
	  private  String name;
	  private  String literal;

	  public static XVariablesEnum get(String literal)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	    	XVariablesEnum result = VALUES_ARRAY[i];
	      if (result.toString().equals(literal))
	        return result;
	    }

	    return null;
	  }

	  public static XVariablesEnum getByName(String name)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	      XVariablesEnum result = VALUES_ARRAY[i];
	      if (result.getName().equals(name))
	        return result;
	    }

	    return null;
	  }

	  public static XVariablesEnum get(int value)
	  {
	    switch (value) {
	    case 0:
	      return THETA;
	    case 1:
	    	return OMEGA;
	    case 2:
	      return EQ1;
	    case 3:
	      return EQ11;
	    case 4:
	      return ED11;
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
