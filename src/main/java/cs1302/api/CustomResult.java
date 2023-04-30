package cs1302.api;

import javafx.scene.control.Button;
import javafx.scene.image.*;

public class CustomResult {
    ImageView image;
    String name;
    Button moreInfoButton;

    public CustomResult(String imgUrl, String name){
        this.image = new ImageView(new Image(imgUrl, 80, 80, false, false));
        this.name = name;
        moreInfoButton = new Button("More Info");
    } // CustumResult Constructor

} // CustumResult
