package sensMatrix;

import org.eclipse.emf.common.util.Enumerator;

public enum NetworkVariables implements Enumerator
	{
	  Vmag, Vang;
	  public static final int Vmag_VALUE = 0;
	  public static final int Vang_VALUE = 1;

	  private static final NetworkVariables[] VALUES_ARRAY={ Vmag, Vang};
	  private  int value;
	  private  String name;
	  private  String literal;

	  public static NetworkVariables get(String literal)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	    	NetworkVariables result = VALUES_ARRAY[i];
	      if (result.toString().equals(literal))
	        return result;
	    }

	    return null;
	  }

	  public static NetworkVariables getByName(String name)
	  {
	    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
	      NetworkVariables result = VALUES_ARRAY[i];
	      if (result.getName().equals(name))
	        return result;
	    }

	    return null;
	  }

	  public static NetworkVariables get(int value)
	  {
	    switch (value) {
	    case 0:
	      return Vmag;
	    case 1:
	    	return Vang;
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
