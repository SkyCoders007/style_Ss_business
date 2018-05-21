package mxi.com.styleswiperbusiness.Models;

/**
 * Created by parth on 21/12/16.
 */

import com.orm.SugarRecord;
import com.orm.annotation.Table;

@Table
public class TagLenghts extends SugarRecord{

    String lenghtsID;
    String lenghtsTag;

    public String getLenghtsID() {
        return lenghtsID;
    }

    public void setLenghtsID(String lenghtsID) {
        this.lenghtsID = lenghtsID;
    }

    public String getLenghtsTag() {
        return lenghtsTag;
    }

    public void setLenghtsTag(String lenghtsTag) {
        this.lenghtsTag = lenghtsTag;
    }
}
