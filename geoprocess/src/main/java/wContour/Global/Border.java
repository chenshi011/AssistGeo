/*
 * Copyright 2012 Yaqiang Wang,
 * yaqiang.wang@gmail.com
 */
package wContour.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * Border class - contour line border
 *
 * @author Yaqiang Wang
 * @version $Revision: 1.6 $
 */
public class Border {
    public List<BorderLine> LineList = new ArrayList<BorderLine>();
    
    public Border()
    {
        
    }
    
    public int getLineNum(){
        return LineList.size();
    }
            
}
