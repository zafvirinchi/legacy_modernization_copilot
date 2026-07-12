package com.ailegacy.modernization.copilot.infrastructure.report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.Closeable;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal text-layout helper over PDFBox's low-level content stream API:
 * word-wrapping, automatic pagination, and a handful of paragraph-style
 * building blocks (titles, headings, body text, bullets, code blocks).
 *
 * Report content ultimately comes from LLM-generated text, which can contain
 * "smart" punctuation (curly quotes, em-dashes) that the standard PDF fonts
 * used here can't encode - every string is sanitized to plain ASCII before
 * being measured or drawn so rendering never throws.
 */
class PdfReportWriter implements Closeable {

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    private final PDDocument document;
    private final PDFont titleFont;
    private final PDFont headingFont;
    private final PDFont bodyFont;
    private final PDFont monoFont;

    private PDPageContentStream contentStream;
    private float cursorY;

    PdfReportWriter() throws IOException {
        this.document = new PDDocument();
        this.titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        this.headingFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        this.bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        this.monoFont = new PDType1Font(Standard14Fonts.FontName.COURIER);
        startNewPage();
    }

    void addTitle(String text) throws IOException {
        writeParagraph(text, titleFont, 20f, 24f);
        cursorY -= 12f;
    }

    void addHeading(String text) throws IOException {
        cursorY -= 8f;
        writeParagraph(text, headingFont, 14f, 18f);
        cursorY -= 4f;
    }

    void addSubheading(String text) throws IOException {
        writeParagraph(text, headingFont, 10.5f, 14f);
    }

    void addParagraph(String text) throws IOException {
        if (text == null || text.isBlank()) {
            return;
        }
        writeParagraph(text, bodyFont, 10.5f, 14f);
        cursorY -= 8f;
    }

    void addKeyValue(String key, String value) throws IOException {
        writeParagraph(key + ": " + value, bodyFont, 10.5f, 14f);
    }

    void addBullet(String text) throws IOException {
        writeParagraph("- " + text, bodyFont, 10.5f, 14f);
    }

    void addCodeBlock(String code) throws IOException {
        if (code == null || code.isBlank()) {
            return;
        }
        String sanitized = sanitize(code);
        for (String line : sanitized.split("\n", -1)) {
            ensureSpace(11f);
            writeLine(line.isEmpty() ? " " : line, monoFont, 8.5f, 11f);
        }
        cursorY -= 8f;
    }

    void addSpacer(float height) throws IOException {
        cursorY -= height;
    }

    byte[] toByteArray() throws IOException {
        contentStream.close();
        contentStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        return out.toByteArray();
    }

    @Override
    public void close() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
        document.close();
    }

    private void writeParagraph(String text, PDFont font, float fontSize, float lineHeight) throws IOException {
        String sanitized = sanitize(text);
        List<String> lines = wrap(sanitized, font, fontSize, CONTENT_WIDTH);
        for (String line : lines) {
            ensureSpace(lineHeight);
            writeLine(line, font, fontSize, lineHeight);
        }
    }

    private void writeLine(String line, PDFont font, float fontSize, float lineHeight) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(MARGIN, cursorY);
        contentStream.showText(line);
        contentStream.endText();
        cursorY -= lineHeight;
    }

    private void ensureSpace(float neededHeight) throws IOException {
        if (cursorY - neededHeight < MARGIN) {
            startNewPage();
        }
    }

    private void startNewPage() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        cursorY = PAGE_HEIGHT - MARGIN;
    }

    private List<String> wrap(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\n", -1)) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            StringBuilder currentLine = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
                float width = font.getStringWidth(candidate) / 1000f * fontSize;
                if (width > maxWidth && !currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(candidate);
                }
            }
            lines.add(currentLine.toString());
        }
        return lines;
    }

    /**
     * Standard PDF fonts only encode plain ASCII (WinAnsi). LLM output often
     * contains curly quotes, em/en-dashes and ellipses that would otherwise
     * throw at render time, so those are normalized to ASCII equivalents and
     * anything else non-printable is replaced with '?'.
     */
    private static String sanitize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text
                .replace('‘', '\'').replace('’', '\'')
                .replace('“', '"').replace('”', '"')
                .replace('–', '-').replace('—', '-')
                .replace("…", "...");

        StringBuilder sanitized = new StringBuilder(normalized.length());
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c == '\n' || (c >= 0x20 && c <= 0x7E)) {
                sanitized.append(c);
            } else if (c == '\r' || c == '\t') {
                sanitized.append(' ');
            } else {
                sanitized.append('?');
            }
        }
        return sanitized.toString();
    }

}
