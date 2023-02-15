package com.xw.glue.properties;

import java.math.BigDecimal;

public class HighPrecision {
	//小数点保留位数
	private int scale;
	//约数模式
	private int roundingMode;
	
	public HighPrecision() {
		this(2);
	}
	
	public HighPrecision(int scale) {
		//保留小数点后位数，四舍五入
		this(scale, BigDecimal.ROUND_HALF_UP);
	}
	
	public HighPrecision(int scale, int roundingMode) {
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(int roundingMode) {
		this.roundingMode = roundingMode;
	}
	
}
