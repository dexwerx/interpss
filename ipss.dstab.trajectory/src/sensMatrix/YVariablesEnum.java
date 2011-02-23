package sensMatrix;

import org.eclipse.emf.common.util.Enumerator;

public enum YVariablesEnum implements Enumerator
	{
	  UX, UY, IX,IY;
	  public static final int UX_VALUE = 0;
	  public static final int UY_VALUE = 1;
	  public static final int IX_VALUE = 2;
	  public static final int IY_VALUE = 3;
	  private static final YVariablesEnum[] VALUES_ARRAY={ UX, UY, IX,IY};
	  private  int value;
	  private  String name;
	  private  String literal;

	  public static YVariablesEnum get(String literal)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	    	YVariablesEnum result = VALUES_ARRAY[i];
	      if (result.toString().equals(literal))
	        return result;
	    }

	    return null;
	  }

	  public static YVariablesEnum getByName(String name)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	      YVariablesEnum result = VALUES_ARRAY[i];
	      if (result.getName().equals(name))
	        return result;
	    }

	    return null;
	  }

	  public static YVariablesEnum get(int value)
	  {
	    switch (value) {
	    case 0:
	      return UX;
	    case 1:
	    	return UY;
	    case 2:
	      return IX;
	    case 3:
	      return IY;

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
