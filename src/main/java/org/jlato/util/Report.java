package org.jlato.util;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.util.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Didier Villevalois
 */
public class Report {

	public static final Font CHAPTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD);
	public static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
	public static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	public static final Font NORMAL_FONT_WHITE = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

	static {
		NORMAL_FONT_WHITE.setColor(BaseColor.WHITE);
	}

	public void makeReport(Collection<RunResult> runResults) throws IOException {
		Map<String, Map<String, Map<String, RunResult>>> allResults =
				new LinkedHashMap<String, Map<String, Map<String, RunResult>>>();

		Map<String, BenchmarkParams> perTitleParams = new LinkedHashMap<String, BenchmarkParams>();

		for (RunResult runResult : runResults) {
			Result result = runResult.getPrimaryResult();
			String benchmark = runResult.getParams().getBenchmark();
			String label = result.getLabel();

			String[] sourceParser = label.split("_with_");
			String[] benchmarkSplit = benchmark.split("\\.");

			String name = benchmarkSplit[benchmarkSplit.length - 2];
			String source = sourceParser[0];
			String parser = sourceParser[1];

			perTitleParams.put(name, runResult.getParams());

			Map<String, Map<String, RunResult>> perSourceResults = allResults.get(name);
			if (perSourceResults == null) {
				perSourceResults = new LinkedHashMap<String, Map<String, RunResult>>();
				allResults.put(name, perSourceResults);
			}

			Map<String, RunResult> perParserResults = perSourceResults.get(source);
			if (perParserResults == null) {
				perParserResults = new LinkedHashMap<String, RunResult>();
				perSourceResults.put(source, perParserResults);
			}

			perParserResults.put(parser, runResult);
		}

		Map<String, StatisticalCategoryDataset> perTitleDatasets = new LinkedHashMap<String, StatisticalCategoryDataset>();
		for (Map.Entry<String, Map<String, Map<String, RunResult>>> results : allResults.entrySet()) {
			DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();
			for (Map.Entry<String, Map<String, RunResult>> perSource : results.getValue().entrySet()) {
				for (Map.Entry<String, RunResult> perParser : perSource.getValue().entrySet()) {
					Result result = perParser.getValue().getAggregatedResult().getPrimaryResult();
					dataset.add(result.getScore(), result.getScoreError(), perParser.getKey(), perSource.getKey());
				}
			}
			perTitleDatasets.put(results.getKey(), dataset);
		}

		makeReport(perTitleDatasets, perTitleParams);
	}

	public void makeReport(Map<String, StatisticalCategoryDataset> perTitleDatasets, Map<String, BenchmarkParams> perTitleParams) throws IOException {
		File directory = new File("results/");
		directory.mkdirs();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = df.format(new Date());

		PdfWriter writer = null;
		Document document = new Document(PageSize.A4, 32, 32, 32, 32);
		try {
			File file = new File(directory, date + ".pdf");
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			int chapterNumber = 1;
			for (Map.Entry<String, StatisticalCategoryDataset> entry : perTitleDatasets.entrySet()) {
				String title = entry.getKey();
				StatisticalCategoryDataset dataset = entry.getValue();
				BenchmarkParams params = perTitleParams == null ? null : perTitleParams.get(title);

				Chunk chunk = new Chunk(title, CHAPTER_FONT);
				Chapter chapter = new Chapter(new Paragraph(chunk), chapterNumber++);
				chapter.setNumberDepth(0);
				chapter.setTriggerNewPage(true);

				if (params != null) {
					chapter.add(makeHeaderParagraph("", org.openjdk.jmh.util.Version.getVersion()));
					chapter.add(makeHeaderParagraph("VM invoker", params.getJvm()));

					String opts = Utils.join(params.getJvmArgs(), " ");
					if (opts.trim().isEmpty()) {
						opts = "<none>";
					}
					chapter.add(makeHeaderParagraph("VM options", opts));

					chapter.add(makeHeaderParagraph("Forks", "" + params.getForks() + " " + getForksString(params.getForks())));

					IterationParams warmup = params.getWarmup();
					if (warmup.getCount() > 0) {
						chapter.add(makeHeaderParagraph("Warmup", "" + warmup.getCount() + " iterations, " + warmup.getTime() + " each" + (warmup.getBatchSize() <= 1 ? "" : ", " + warmup.getBatchSize() + " calls per op")));
					} else {
						chapter.add(makeHeaderParagraph("Warmup", "<none>"));
					}

					IterationParams measurement = params.getMeasurement();
					if (measurement.getCount() > 0) {
						chapter.add(makeHeaderParagraph("Measurement", "" + measurement.getCount() + " iterations, " + measurement.getTime() + " each" + (measurement.getBatchSize() <= 1 ? "" : ", " + measurement.getBatchSize() + " calls per op")));
					} else {
						chapter.add(makeHeaderParagraph("Measurement", "<none>"));
					}

					TimeValue timeout = params.getTimeout();
					boolean timeoutWarning = timeout.convertTo(TimeUnit.NANOSECONDS) <= measurement.getTime().convertTo(TimeUnit.NANOSECONDS) || timeout.convertTo(TimeUnit.NANOSECONDS) <= warmup.getTime().convertTo(TimeUnit.NANOSECONDS);
					chapter.add(makeHeaderParagraph("Timeout", "" + timeout + " per iteration" + (timeoutWarning ? ", ***WARNING: The timeout might be too low!***" : "")));
					chapter.add(makeHeaderParagraph("Threads", "" + params.getThreads() + " " + getThreadsString(params.getThreads()) + (params.shouldSynchIterations() ? ", will synchronize iterations" : (params.getMode() == Mode.SingleShotTime ? "" : ", ***WARNING: Synchronize iterations are disabled!***"))));
					chapter.add(makeHeaderParagraph("Benchmark mode", params.getMode().longLabel()));
				}

				Paragraph paragraph = new Paragraph();
				paragraph.setSpacingBefore(1);
				PdfPTable table = new PdfPTable(HEADERS.length);
				table.setWidthPercentage(100);
				table.setWidths(new float[]{100, 100, 70, 70, 50, 50, 50});
				for (int i = 0; i < HEADERS.length; i++) {
					table.addCell(makeCell(HEADERS[i], Element.ALIGN_CENTER, true));
				}
				for (Object column : dataset.getColumnKeys()) {
					Number javacMean = !dataset.getRowKeys().contains("javac") ? null : dataset.getMeanValue("javac", (Comparable) column);
					Number jlatoMean = !dataset.getRowKeys().contains("jlato") ? null : dataset.getMeanValue("jlato", (Comparable) column);

					for (Object row : dataset.getRowKeys()) {
						Number mean = dataset.getMeanValue((Comparable) row, (Comparable) column);
						Number stdDev = dataset.getStdDevValue((Comparable) row, (Comparable) column);

						if (mean != null) {
							table.addCell(makeCell((String) column, Element.ALIGN_LEFT, false));
							table.addCell(makeCell((String) row, Element.ALIGN_LEFT, false));
							table.addCell(makeCell(String.format("%.3f", mean), Element.ALIGN_RIGHT, false));
							table.addCell(makeCell(String.format("%.3f", stdDev), Element.ALIGN_RIGHT, false));
							table.addCell(makeCell("ms", Element.ALIGN_CENTER, false));
							table.addCell(makeCell(javacMean == null ? "" : String.format("%.2fx", mean.doubleValue() / javacMean.doubleValue()), Element.ALIGN_CENTER, false));
							table.addCell(makeCell(jlatoMean == null ? "" : String.format("%.2fx", mean.doubleValue() / jlatoMean.doubleValue()), Element.ALIGN_CENTER, false));
						}
					}
				}

				paragraph.add(table);
				chapter.add(paragraph);

				addChart(writer, document, chapter, title, dataset);

				document.add(chapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		document.close();
	}

	protected Paragraph makeHeaderParagraph(String name, String content) {
		Chunk chunk = new Chunk(name + (name.equals("") ? "" : ": "), BOLD_FONT);
		Paragraph paragraph = new Paragraph(chunk);
		paragraph.setSpacingAfter(-2);
		paragraph.add(new Chunk(content, NORMAL_FONT));
		return paragraph;
	}

	protected static String getForksString(int f) {
		return f > 1 ? "forks" : "fork";
	}

	protected static String getThreadsString(int t) {
		return t > 1 ? "threads" : "thread";
	}

	private PdfPCell makeCell(String string, int alignment, boolean header) {
		Phrase phrase = header ? new Phrase(string, NORMAL_FONT_WHITE) : new Phrase(string);

		PdfPCell cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(alignment);

		if (header) {
			cell.setPadding(4.0f);
			cell.setPaddingTop(1.0f);
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		} else {
			cell.setPadding(3.0f);
			cell.setPaddingTop(.0f);
		}

		return cell;
	}

	public static final NumberFormat MEAN_FORMAT = NumberFormat.getInstance();

	private String[] HEADERS = new String[]{"Source", "Parser", "Score", "Error (±)", "Units", "Ratio to Javac", "Ratio to JLaTo"};

	private static void addChart(PdfWriter writer, Document document, Chapter chapter, String title, StatisticalCategoryDataset dataset) throws DocumentException {
		JFreeChart chart = generateChart(title, dataset);

		PdfContentByte contentByte = writer.getDirectContent();

		float width = document.right() - document.left();
		float height = dataset.getColumnCount() * dataset.getRowCount() * 16 + 80;

		PdfTemplate template = contentByte.createTemplate(width, height);

		Graphics2D graphics2d = new PdfGraphics2D(template, width, height);
		Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(graphics2d, rectangle2d);
		graphics2d.dispose();

		Image image = Image.getInstance(template);
		image.scaleToFit(width, document.top() - document.bottom());
		chapter.add(image);
	}

	private static JFreeChart generateChart(String title, StatisticalCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createBarChart("", // title
				"Parsed Source", // category axis label
				"ms", // value axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
		);

		chart.setBackgroundPaint(Color.white);
		chart.setPadding(RectangleInsets.ZERO_INSETS);

		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(new Color(220, 220, 220));
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		StatisticalBarRenderer renderer = new StatisticalBarRenderer();
		plot.setRenderer(renderer);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", MEAN_FORMAT));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_LEFT));
		renderer.setItemLabelAnchorOffset(-5);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont().deriveFont(Font.NORMAL, 10));
		renderer.setBaseItemLabelPaint(Color.white);
		renderer.setItemMargin(0);

		populateColors(plot);

		CategoryAxis domainAxis = plot.getDomainAxis();

		domainAxis.setLabel("Source");
		domainAxis.setLabelFont(domainAxis.getLabelFont().deriveFont(Font.BOLD, 16));
		domainAxis.setLabelInsets(RectangleInsets.ZERO_INSETS);

		domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(Font.NORMAL, 12));

		domainAxis.setCategoryMargin(0.05);
		domainAxis.setUpperMargin(0.025);
		domainAxis.setLowerMargin(0.025);

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabel("ms");
		rangeAxis.setLabelInsets(RectangleInsets.ZERO_INSETS);

		return chart;
	}

	public static final Paint[] PAINTS = new Paint[]{
			new Color(196, 160, 0),
			new Color(206, 92, 0),
			new Color(143, 89, 2),
			new Color(78, 154, 6),
			new Color(32, 74, 135),
			new Color(92, 53, 102),
			new Color(164, 0, 0),
	};

	private static void populateColors(CategoryPlot plot) {
		plot.setDrawingSupplier(
				new DefaultDrawingSupplier(PAINTS,
						DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
	}
}
