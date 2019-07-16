package dk.ott.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class XmlStreamBuilderFactory {

    public static XmlStreamBuilderToString createXmlStreamBuilderToString(boolean prettyPrint) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (prettyPrint) {
            return new XmlStreamBuilderToString(new XmlStreamBuilderFactory(byteArrayOutputStream).withPrettyPrinter().build(), byteArrayOutputStream);
        }
        return new XmlStreamBuilderToString(new XmlStreamBuilderFactory(byteArrayOutputStream).build(), byteArrayOutputStream);
    }

    public static XmlStreamBuilder createXmlStreamBuilderToOutputStream(OutputStream outputStream, boolean prettyPrint) {
        if (prettyPrint) {
            return new XmlStreamBuilderFactory(outputStream).withPrettyPrinter().build();
        }
        return new XmlStreamBuilderFactory(outputStream).build();
    }

    private XmlStreamBuilder xmlStreamBuilder;

    public XmlStreamBuilderFactory(OutputStream outputStream) {
        this.xmlStreamBuilder = new XmlStreamWriterWrapper(outputStream);
    }

    public XmlStreamBuilderFactory withPrettyPrinter() {
        this.xmlStreamBuilder = new PrettyXmlStreamBuilder(xmlStreamBuilder);
        return this;
    }

    public XmlStreamBuilder build() {
        return xmlStreamBuilder;
    }
}
