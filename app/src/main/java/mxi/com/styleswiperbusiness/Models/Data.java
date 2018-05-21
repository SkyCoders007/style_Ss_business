package mxi.com.styleswiperbusiness.Models;

/**
 * Created by parth on 19/12/16.
 */
public class Data {
    private String description;

    private int imagePath;

    public Data(int imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getImagePath() {
        return imagePath;
    }
}
