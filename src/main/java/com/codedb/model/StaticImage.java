package com.codedb.model;

import javafx.scene.image.Image;

public class StaticImage {
	public static Image db = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/db.png"), 14, 14, true,
			true);

	public static Image table = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/table.png"), 14, 14,
			true, true);

	public static Image function = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/function.png"), 16,
			16, true, true);

	public static Image createCode = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/createCode.png"),
			16, 16, true, true);

	public static Image info = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/info.png"), 16, 16,
			true, true);

	public static Image add = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/add.png"), 16, 16, true,
			true);

	public static Image delete = new Image(StaticImage.class.getResourceAsStream("/com/codedb/img/delete.png"), 16, 16,
			true, true);
}
