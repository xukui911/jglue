package com.xw.glue.properties;

public class JProperties {
	private HighPrecision highPrecision;
	private boolean isDefValue;
	private boolean isSalf;;

	public boolean isDefValue() {
		return isDefValue;
	}

	public JProperties setDefValue(boolean isDefValue) {
		this.isDefValue = isDefValue;
		return this;
	}

	public boolean isHighPrecision() {
		return highPrecision!=null;
	}

	public JProperties setHighPrecision(HighPrecision highPrecision) {
		this.highPrecision = highPrecision;
		return this;
	}
	
	public HighPrecision getHighPrecision() {
		return highPrecision;
	}
	
	public boolean isSalf() {
		return isSalf;
	}

	public JProperties setSalf(boolean isSalf) {
		this.isSalf = isSalf;
		return this;
	}

	public JProperties copy() {
		return new JProperties().setHighPrecision(highPrecision)
								.setDefValue(isDefValue)
								.setSalf(isSalf);
	}
}
