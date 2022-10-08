package com.codedb.model;

import javafx.scene.image.Image;

public class StaticImage
{
    public static Image db = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/db.png"), 14,
            14, true, true);

    public static Image table = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/table.png"), 14,
        14, true, true);
}
