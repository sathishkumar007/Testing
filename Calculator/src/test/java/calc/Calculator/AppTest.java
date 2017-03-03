package calc.Calculator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    	public void testFindMax(){  
	        assertEquals(4,App.findMax(new int[]{1,3,4,2}));  
	        assertEquals(0,App.findMax(new int[]{-12,0,-3,-4,-2}));  
	    } }
