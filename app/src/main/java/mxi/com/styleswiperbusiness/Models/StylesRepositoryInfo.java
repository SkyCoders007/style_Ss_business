package mxi.com.styleswiperbusiness.Models;

/**
 * Created by parth on 21/12/16.
 */

import com.orm.SugarRecord;
import com.orm.annotation.Table;

@Table
public class StylesRepositoryInfo extends SugarRecord{

    String styleId;
    String style;
    String length;
    String color;
    String image;

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
