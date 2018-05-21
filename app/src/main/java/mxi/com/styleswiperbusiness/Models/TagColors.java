package mxi.com.styleswiperbusiness.Models;

/**
 * Created by parth on 21/12/16.
 */

import com.orm.SugarRecord;
import com.orm.annotation.Table;

@Table
public class TagColors extends SugarRecord{

    String colorID;
    String colorTag;

    public String getColorID() {
        return colorID;
    }

    public void setColorID(String colorID) {
        this.colorID = colorID;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }
}
