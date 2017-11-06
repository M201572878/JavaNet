package Client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

public class WindowUtil {
	public static void SetLocationCenter(Component component) {  
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  
	    Dimension compSize = component.getSize();  
	    if (compSize.height > screenSize.height) {  
	        compSize.height = screenSize.height;  
	    }  
	    if (compSize.width > screenSize.width) {  
	        compSize.width = screenSize.width;  
	    }  
	    component.setLocation((screenSize.width - compSize.width) / 2,  
	            (screenSize.height - compSize.height) / 2);  
	} 
	
	public static void SetLocationRight(Component component) {  
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  
	    Dimension compSize = component.getSize();  
	    if (compSize.height > screenSize.height) {  
	        compSize.height = screenSize.height;  
	    }  
	    if (compSize.width > screenSize.width) {  
	        compSize.width = screenSize.width;  
	    }  
	    component.setLocation(screenSize.width - compSize.width / 2 * 3,  
	            (screenSize.height - compSize.height) / 2);  
	} 
}
