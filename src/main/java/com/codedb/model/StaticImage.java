package com.codedb.model;

import javafx.scene.image.Image;

public class StaticImage {
	public static Image db = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/db.png"), 16, 16, true,
			true);

	public static Image table = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/table.png"), 16, 16,
			true, true);

	public static Image function = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/function.png"), 18,
			18, true, true);

	public static Image select  = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/select.png"), 18,
			18, true, true);


	public static Image createCode = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/createCode.png"),
			18, 18, true, true);

	public static Image info = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/info.png"), 18, 18,
			true, true);

	public static Image add = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/add.png"), 18, 18, true,
			true);

	public static Image delete = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/delete.png"), 18, 18,
			true, true);
}
