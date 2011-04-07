package eu.xenit.move2alf.common;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ICC_Profile;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfICCBased;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class Tiff2Pdf {
	private static Logger logger = LoggerFactory.getLogger(Tiff2Pdf.class);

	private List<RenderedImage> imageList = null;

	public Tiff2Pdf() {
		init();
	}

	public void addImage(RenderedImage image) {
		imageList.add(image);
	}

	public void init() {
		imageList = new ArrayList<RenderedImage>();
	}

	public void createSingleTiff(File singleTifFile) {

		// jai approach
		try {
			OutputStream out = new FileOutputStream(singleTifFile);
			// ByteArrayOutputStream out = new ByteArrayOutputStream();
			TIFFEncodeParam param = new TIFFEncodeParam();

			Vector<RenderedImage> vector = new Vector<RenderedImage>();
			logger.debug("NbrOfImages {}", imageList.size());
			for (int i = 1; i < imageList.size(); i++) {
				vector.add(imageList.get(i));
			}
			param.setExtraImages(vector.iterator());

			ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", out,
					param);

			encoder.encode(imageList.get(0));
			// tempByteArray = out.toByteArray();
			out.close();
			logger.info("Single page tiff created");
		} catch (Exception e) {
			logger.error("Error creating single page tiff", e);
		}
	}

	public void tiff2Pdf(File singleTifFile, File pdfFile) {

		// based on tiff2pdf from itext toolbox 0.0.2
		// (cfr.http://itexttoolbox.sourceforge.net/doku.php?id=download&DokuWiki=
		// ecde1bfec0b8cca87dd8c6c042183992)
		try {
			RandomAccessFileOrArray ra = new RandomAccessFileOrArray(
					singleTifFile.getAbsolutePath());
			// RandomAccessFileOrArray ra = new
			// RandomAccessFileOrArray(tempByteArray);
			int comps = TiffImage.getNumberOfPages(ra);
			boolean adjustSize = false;
			Document document = new Document(PageSize.A4);
			float width = PageSize.A4.getWidth() - 40;
			float height = PageSize.A4.getHeight() - 120;
			Image img = TiffImage.getTiffImage(ra, 1);
			if (img.getDpiX() > 0 && img.getDpiY() > 0) {
				img.scalePercent(7200f / img.getDpiX(), 7200f / img.getDpiY());
			}
			document.setPageSize(new Rectangle(img.getScaledWidth(), img
					.getScaledHeight()));
			adjustSize = true;
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(pdfFile));

			// pdf/a
			// from
			// http://www.opensubscriber.com/message/itext-questions@lists.sourceforge
			// .net/7593470.html

			// check that it is really pdf/a:
			// http://www.intarsys.de/produkte/pdf-a-live/pdf-a-check-1
			// => 2 warnings
			// Keine eindeutige ID gefunden
			// Kein History-Eintrag vorhanden
			writer.setPDFXConformance(PdfWriter.PDFA1B);
			document.open();

			PdfDictionary outi = new PdfDictionary(PdfName.OUTPUTINTENT);
			outi.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(
					"sRGB IEC61966-2.1"));
			outi.put(PdfName.INFO, new PdfString("sRGB IEC61966-2.1"));
			outi.put(PdfName.S, PdfName.GTS_PDFA1);
			ICC_Profile icc = ICC_Profile.getInstance(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(
							"/srgb.profile"));
			PdfICCBased ib = new PdfICCBased(icc);
			ib.remove(PdfName.ALTERNATE);
			outi.put(PdfName.DESTOUTPUTPROFILE, writer.addToBody(ib)
					.getIndirectReference());
			writer.getExtraCatalog().put(PdfName.OUTPUTINTENTS,
					new PdfArray(outi));

			// BaseFont bf =
			// BaseFont.createFont("c:\\windows\\fonts\\arial.ttf",
			// BaseFont.WINANSI, true);
			// Font f = new Font(bf, 12);
			// doc.add(new Paragraph("hello", f));

			PdfContentByte cb = writer.getDirectContent();
			for (int c = 0; c < comps; ++c) {
				img = TiffImage.getTiffImage(ra, c + 1);
				if (img != null) {
					if (img.getDpiX() > 0 && img.getDpiY() > 0) {
						img.scalePercent(7200f / img.getDpiX(), 7200f / img
								.getDpiY());
					}
					if (adjustSize) {
						document.setPageSize(new Rectangle(
								img.getScaledWidth(), img.getScaledHeight()));
						document.newPage();
						img.setAbsolutePosition(0, 0);
					} else {
						if (img.getScaledWidth() > width
								|| img.getScaledHeight() > height) {
							if (img.getDpiX() > 0 && img.getDpiY() > 0) {
								float adjx = width / img.getScaledWidth();
								float adjy = height / img.getScaledHeight();
								float adj = Math.min(adjx, adjy);
								img.scalePercent(7200f / img.getDpiX() * adj,
										7200f / img.getDpiY() * adj);
							} else
								img.scaleToFit(width, height);
						}
						img.setAbsolutePosition(20, 20);
						document.newPage();
						document.add(new Paragraph(singleTifFile.getName()
								+ " - page " + (c + 1)));
					}
					cb.addImage(img);
					logger.debug("Finished page " + (c + 1));
				}
			}
			ra.close();

			writer.createXmpMetadata();// pdfa
			document.close();
		} catch (Throwable e) {
			// catch Throwable because we encountere a java.lang.InternalError
			// cfr. http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6503430
			// probably better to move to later java version for poller
			logger.error("Pdf not created", e);
		}
	}

}
