package br.gov.frameworkdemoiselle.behave.regression.report.imagemagick.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BrowserResultColumn {

	private String name;
	private String pngFileName;
	private String gifFileName;
	private BigDecimal expectedTotalPixels;
	private BigDecimal actualTotalPixels;
	private String consoleOutput;
	private ComparisonStrategy strategy;

	public String getName() {
		return name;
	}

	public BrowserResultColumn(String name, String pngFileName, String gifFileName, BigDecimal expectedTotalPixels, BigDecimal actualTotalPixels, String consoleOutput, ComparisonStrategy strategy) {
		super();
		this.name = name;
		this.pngFileName = pngFileName;
		this.gifFileName = gifFileName;
		this.expectedTotalPixels = expectedTotalPixels;
		this.actualTotalPixels = actualTotalPixels;
		this.consoleOutput = consoleOutput;
		this.strategy = strategy;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPngFileName() {
		return pngFileName;
	}

	public void setPngFileName(String pngFileName) {
		this.pngFileName = pngFileName;
	}

	public String getGifFileName() {
		return gifFileName;
	}

	public void setGifFileName(String gifFileName) {
		this.gifFileName = gifFileName;
	}

	public BigDecimal getExpectedTotalPixels() {
		return expectedTotalPixels;
	}

	public void setExpectedTotalPixels(BigDecimal expectedTotalPixels) {
		this.expectedTotalPixels = expectedTotalPixels;
	}

	public BigDecimal getActualTotalPixels() {
		return actualTotalPixels;
	}

	public void setActualTotalPixels(BigDecimal actualTotalPixels) {
		this.actualTotalPixels = actualTotalPixels;
	}

	public BigDecimal getPercentageDeviation() throws IOException {
		String output = getFinalOutput(strategy, getConsoleOutput());

		BigDecimal percentageDeviation;
		if (getExpectedTotalPixels().longValue() > getActualTotalPixels().longValue()) {
			percentageDeviation = calculatePercentageDeviation(getExpectedTotalPixels(), output);
		} else {
			percentageDeviation = calculatePercentageDeviation(getActualTotalPixels(), output);
		}
		return percentageDeviation;
	}

	private BigDecimal calculatePercentageDeviation(BigDecimal totalImagePixels, String output) throws IOException {
		BigDecimal percentageDeviation;
		try {
			BigDecimal pixelDifference = new BigDecimal(output);
			percentageDeviation = pixelDifference.divide(totalImagePixels, 4, RoundingMode.HALF_UP);
		} catch (Exception e) {
			percentageDeviation = BigDecimal.valueOf(-1);
		}
		return percentageDeviation.multiply(new BigDecimal(100));
	}

	private String getFinalOutput(ComparisonStrategy strategy, String output) {
		if (strategy.equals(ComparisonStrategy.SUB_IMAGE)) {
			output = output.replace(output.substring(output.indexOf("@"), output.length()), "").trim();
		}
		return output;
	}

	public BigDecimal getDiffPixels() {
		return actualTotalPixels.subtract(expectedTotalPixels);
	}

	public String getConsoleOutput() {
		return consoleOutput;
	}

	public void setConsoleOutput(String consoleOutput) {
		this.consoleOutput = consoleOutput;
	}

}
