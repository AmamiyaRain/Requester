package com.web.base.enums;

/**
 * @author 22454
 */

public enum FileTypeEnum {
	JPG("jpg", "FFD8FF"),
	PNG("png", "89504E47"),
	GIF("gif", "47494638"),
	TIF("tif", "49492A00"),
	BMP("bmp", "424D"),
	DWG("dwg", "41433130"),
	HTML("html", "68746D6C3E"),
	RTF("rtf", "7B5C727466"),
	XML("xml", "3C3F786D6C"),
	ZIP("zip", "504B0304"),
	RAR("rar", "52617221"),
	PSD("psd", "38425053"),
	EML("eml", "44656C69766572792D646174653A"),
	DBX("dbx", "CFAD12FEC5FD746F"),
	PST("pst", "2142444E"),
	XLS("xls", "D0CF11E0"),
	XLSX("xlsx", "504b030414000600080000002100"),
	DOC("doc", "D0CF11E0"),
	MDB("mdb", "5374616E64617264204A"),
	WPD("wpd", "FF575043"),
	EPS("eps", "252150532D41646F6265"),
	PS("ps", "252150532D41646F6265"),
	PDF("pdf", "255044462D312E"),
	QDF("qdf", "AC9EBD8F"),
	PWL("pwl", "E3828596"),
	WAV("wav", "57415645"),
	AVI("avi", "41564920"),
	RAM("ram", "2E7261FD"),
	RM("rm", "2E524D46"),
	MPG("mpg", "000001BA"),
	MOV("mov", "6D6F6F76"),
	ASF("asf", "3026B2758E66CF11"),
	MID("mid", "4D546864"),
	SVG("svg", "3C737667"),
	WEBP("webp", "52494646");

	private final String typeName;

	private final String start;

	FileTypeEnum(String typeName, String start) {
		this.typeName = typeName;
		this.start = start;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getStart() {
		return start;
	}
}