package mxi.com.styleswiperbusiness.Models;

/**
 * Created by parth on 21/12/16.
 */

import com.orm.SugarRecord;
import com.orm.annotation.Table;

@Table
public class TagStyles extends SugarRecord{

    String styleID;
    String styleTag;

    public String getStyleID() {
        return styleID;
    }

    public void setStyleID(String styleID) {
        this.styleID = styleID;
    }

    public String getStyleTag() {
        return styleTag;
    }

    public void setStyleTag(String styleTag) {
        this.styleTag = styleTag;
    }
}
