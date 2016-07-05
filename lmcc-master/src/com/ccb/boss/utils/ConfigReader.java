package com.ccb.boss.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class ConfigReader {

    private static Logger logger = Logger.getLogger(ConfigReader.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }

    public static String CFG_HOME = System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator;
    public String xmlFile;
    public InputSource inputSource = null;
    public XPath xpath = null;

    private static HashMap<String, Double> commandDataHM = new HashMap<String, Double>();

    public double getCommandData(String nodeName, String tagName) {
        double commandData = 0.0;
        if (commandDataHM.size() == 0) {
            HashMap testHM = new HashMap();
            try {
                Document document = parse(xmlFile);
                Element element = (Element) document.selectSingleNode("boss/commonData");
                treeWalk(element, testHM);
            } catch (DocumentException documentException) {
                documentException.printStackTrace();
            }

            HashMap defaultHM = (HashMap) testHM.get(nodeName);
            Iterator iterate = defaultHM.keySet().iterator();
            while (iterate.hasNext()) {
                String keyString = iterate.next().toString();
                String valueString = defaultHM.get(keyString).toString();
                double valueDouble = Double.parseDouble(valueString);
                if (tagName.equals(keyString)) {
                    commandData = valueDouble;
                }
                commandDataHM.put(keyString, valueDouble);
            }
        } else {
            commandData = commandDataHM.get(tagName);
        }
        return commandData;
    }

    public static void main(String[] args) throws Exception {
        ConfigReader xmlReader = new ConfigReader();
        String fileLocation = System.getenv("BOSS_HOME") + File.separator + "gui"
                    + File.separator;
        File folder = new File(fileLocation);
        File[] listOfFiles = folder.listFiles();
        String xPath = "BorderPane";
        String attrName = "@fx:controller";
        String fxController = "";
        for (int i = 0; i < listOfFiles.length; i++) {
            InputStream in = null;
            File fxml = listOfFiles[i];
            String fileName = fxml.getAbsolutePath();
            Document document = xmlReader.parse( fileName);
            xmlReader.treeWalk(document);
        }
        
        /*
        
        XMLReader xmlReader = new XMLReader();

        logger.debug("lite=" + xmlReader.getCommandData("default", "lite"));
        logger.debug("internal=" + xmlReader.getCommandData("default", "internal"));

        String tagName = "/boss/output/itemOrderOI";
        String sortField = "index";

        HashMapWrapper hashMapWrapper = new HashMapWrapper();
        HashMap[] vt = xmlReader.getSortedConf(tagName, sortField);
        /*
         for (int i=0; i<vt.length; i++) {
         hashMapWrapper.display(vt[i]);
         }
         //XPathExpression expr = xpath.compile("/BSQCUBS/Class/Type[@type_id=4218]");

         /*
         XMLReader xmlReader = new XMLReader();
         String xmlFile = xmlReader.xmlFile;
         //Document document = xmlReader.parse(xmlFile);
         //xmlReader.bar(document);
         String fileName = xmlReader.getSingleTCByxPath("boss/databases/connection", "@userName");
		
         logger.info("userName=" + xmlReader.getSingleTCByxPath("boss/Invoice/connection", "@userName"));
         logger.info("ISN.column=" + xmlReader.getSingleTCByxPath("boss/Invoice/Date", "@row"));
		
         String[] nodeName = xmlReader.getMutilTCByxPath("boss/Invoice", null);
         logger.info("fileName=" + nodeName.length);
         for (int i=0; i<nodeName.length; i++)
         logger.info("nodeName[" + i + "]=" + nodeName[i]);
		
         /*
         String connectURL = xmlReader.getSingleTCByxPath(xmlFile, "spp/databases/connectURL", null);
		
         String dbname = xmlReader.getSingleTCByxPath(xmlFile, "spp/databases/connection", "@dbname");
         String userName = xmlReader.getSingleTCByxPath(xmlFile, "spp/databases/connection", "@userName");
         String password = xmlReader.getSingleTCByxPath(xmlFile, "spp/databases/connection", "@password");
         logger.info("driverName = " + driverName);
         logger.info("connectURL = " + connectURL);
         logger.info("dbname     = " + dbname);
         logger.info("userName   = " + userName);
         logger.info("password   = " + password);
		
         String[] roleTextContentByxPat = xmlReader.getMutilTCByxPath(xmlFile, "spp/user/role", null);
         String[] actionTextContentByxPat = xmlReader.getMutilTCByxPath(xmlFile, "spp/user/role", "@action");
		
		
         XMLReader xmlReader = new XMLReader();
         String xmlFile = xmlReader.xmlFile;
         Document document = null;
         HashMap<String, String> testHM = new HashMap<String, String>();
         try {
         document = xmlReader.parse(xmlFile);
         //xmlReader.bar(document);
         Element element = (Element)document.selectSingleNode( "boss/gui/Menu" );
         xmlReader.treeWalk(element, testHM);
         } catch(DocumentException documentException) {
         documentException.printStackTrace();
         }

         //logger.info("xpath = " + xpath);
         //Element element = (Element)document.selectSingleNode( "boss/Quotation" );
         //xmlReader.treeWalkElement(element);
         //String xpathExpression = "boss/Quotation/Quotation";
         //logger.debug(xmlFile);
         //String dbname = xmlReader.getSingleTCByxPath("boss/databases/connection", "@userName");
         //HashMap hashMap = xmlReader.getMAByxPath(xmlFile, xpathExpression);
         //logger.debug(dbname);
         //HashMap actionTextContentByxPat = xmlReader.getMutilTCByxPath(xmlFile, "boss/gui/Menu", "@id", "sr_MI");
         //actionTextContentByxPat = xmlReader.getMutilTCByxPath(xmlFile, "boss/gui/Menu/MenuItem", "@id", "sr_MI");
         */
    }

    /**
     * If you ever have to walk a large XML document tree then for performance
     * we recommend you use the fast looping method which avoids the cost of
     * creating an Iterator object for each loop. For example
     *
     * @param element
     */
    public void treeWalkElement(Element element) {
        logger.debug("element.nodeCount()=" + element.nodeCount());
        for (int i = 0; i < (element.nodeCount() + 1) / 2; i++) {
            Element e = (Element) element.getXPathResult(i * 2 + 1);
            if (e == null) {
                logger.debug("null");
            } else {
                logger.debug(element.getName() + " = " + element.getPath());
            }
        }
    }

    public ConfigReader() {
        this.xmlFile = CFG_HOME + "boss.xml";
        inputSource = new InputSource(xmlFile);
        xpath = XPathFactory.newInstance().newXPath();
    }

    public ConfigReader(String xmlFile) {
        this.xmlFile = xmlFile;
        inputSource = new InputSource(xmlFile);
        xpath = XPathFactory.newInstance().newXPath();
    }

    /**
     * One of the first things you'll probably want to do is to parse an XML
     * document of some kind. This is easy to do in dom4j. The following code
     *
     * @return
     * @throws DocumentException
     */
    public Document parse() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlFile);
        return document;
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public String getSingleTCByxPath(String xpathExpression,
            String attributeName) {
        String textContent = null;

        if (attributeName != null) {
            xpathExpression = xpathExpression + "/" + attributeName;
        }
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }
        int j = nodes.getLength();
        if (j > 0) {
            textContent = nodes.item(0).getTextContent();
        }
        return textContent;
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public String[] getMutilTCByxPath(String xpathExpression,
            String attributeName) {
        if (attributeName != null) {
            xpathExpression = xpathExpression + "/" + attributeName;
        }
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }

        int j = nodes.getLength();
        String[] textContent = new String[j];
        for (int i = 0; i < j; i++) {
            textContent[i] = nodes.item(i).getTextContent();
        }
        return textContent;
    }

    /**
     * One of the first things you'll probably want to do is to parse an XML
     * document of some kind. This is easy to do in dom4j. The following code
     *
     * @param fileName
     * @return
     * @throws DocumentException
     */
    public Document parse(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(fileName);
        return document;
    }

    /**
     * A document can be navigated using a variety of methods that return
     * standard Java Iterators. For example
     *
     * @param document
     * @throws DocumentException
     */
    public void bar(Document document) throws DocumentException {

        Element root = document.getRootElement();

        // iterate through child elements of root
        int index = 0;
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();

            logger.debug("1: " + element.getName() + " = " + element.getPath()
                    + " = " + element.getXPathResult(index).getStringValue());

            index++;
            // do something
        }

        // iterate through child elements of root with element name "foo"
        Iterator iterator = root.elementIterator("Quotation");
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();

            logger.debug("100: " + element.getName() + " = " + element.getPath() + "; "
                    + " 1= " + element.getXPathResult(1).getName()
                    + " 2= " + element.getXPathResult(2).getName()
                    + " 3= " + element.getXPathResult(3).getName());
        }
        index = 0;
        for (Iterator i = root.elementIterator("Quotation"); i.hasNext();) {
            Element databases = (Element) i.next();
            logger.debug("2: " + databases.getName() + " = " + databases.getPath() + "; "
                    + databases.getName() + " = " + databases.getPath()
                    + " = " + databases.getXPathResult(1).getName());

            // do something
        }

        // iterate through attributes of root 
        for (Iterator i = root.attributeIterator(); i.hasNext();) {
            Attribute attribute = (Attribute) i.next();
            logger.debug("3: " + attribute.getName() + " = " + attribute.getText());
            // do something
        }
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public String getSingleTCByxPath(String xmlFile, String xpathExpression,
            String attributeName) {
        String textContent = null;

        if (attributeName != null) {
            xpathExpression = xpathExpression + "/" + attributeName;
        }
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }
        int j = nodes.getLength();

        if (j > 0) {
            textContent = nodes.item(0).getTextContent();
        }
        return textContent;
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public String[] getMutilTCByxPath(String xmlFile, String xpathExpression,
            String attributeName) {
        if (attributeName != null) {
            xpathExpression = xpathExpression + "/" + attributeName;
        }
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }

        int j = nodes.getLength();
        String[] textContent = new String[j];
        for (int i = 0; i < j; i++) {
            textContent[i] = nodes.item(i).getTextContent();
            //logger.debug("textContent[" + i + "]=" + textContent[i]);
        }
        return textContent;
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public HashMap getMAByxPath(String xmlFile, String xpathExpression) {
        HashMap<String, HashMap> hashMap = new HashMap<String, HashMap>();
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }

        int j = nodes.getLength();
        String[] textContent = new String[j];
        for (int i = 0; i < j; i++) {
            NamedNodeMap nameNodeMap = nodes.item(i).getAttributes();
            String id = "";
            HashMap<String, String> menuItemHM = new HashMap<String, String>();
            for (int ai = 0; ai < nameNodeMap.getLength(); ai++) {
                String attName = nameNodeMap.item(ai).getNodeName();
                String attValue = nameNodeMap.item(ai).getNodeValue();
                logger.debug("attName=" + attName + "; attValue" + attValue);
                if ("id".equals(attName)) {
                    id = attValue;
                } else {
                    menuItemHM.put(attName, attValue);
                }
                hashMap.put(id, menuItemHM);
            }

        }
        return hashMap;
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public void xPathBar(Document document, String xpath, String attributeName) {
        //List list = document.selectNodes( xpath );
        //Node node = null
        if (attributeName != null) {
            xpath = xpath + "/" + attributeName;
        }
        //logger.info("xpath = " + xpath);
        Node node = document.selectSingleNode(xpath);

        String name = node.valueOf(attributeName);
        logger.debug("name=" + name + ", getNodeTypeName=" + node.getNodeTypeName()
                + ", getStringValue=" + node.getStringValue()
                + ", getText=" + node.getText()
                + ", getUniquePath=" + node.getUniquePath()
                + ", getName=" + node.getName()
                + ", getNodeType=" + node.getNodeType());

    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public void xPathBar(Document document) {
        List list = document.selectNodes("/DataMapper/databases");
        Node node = document.selectSingleNode("/DataMapper/databases/table");

        String name = node.valueOf("@cname");
        //logger.info("name=" + name);
    }

    /**
     * For example if you wish to find all the hypertext links in an XHTML
     * document the following code would do the trick.
     *
     * @param document
     * @throws DocumentException
     */
    public void findLinks(Document document) throws DocumentException {

        List list = document.selectNodes("//a/@href");

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();
        }
    }

    /**
     * If you ever have to walk a large XML document tree then for performance
     * we recommend you use the fast looping method which avoids the cost of
     * creating an Iterator object for each loop. For example
     *
     * @param document
     */
    public void treeWalk(Document document) {
        treeWalk(document.getRootElement());
    }

    /**
     * Often in dom4j you will need to create a new document from scratch.
     * Here's an example of doing that.
     *
     * @return
     */
    public Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");

        Element author1 = root.addElement("author")
                .addAttribute("name", "James")
                .addAttribute("location", "UK")
                .addText("James Strachan");

        Element author2 = root.addElement("author")
                .addAttribute("name", "Bob")
                .addAttribute("location", "US")
                .addText("Bob McWhirter");

        return document;
    }

    /**
     * A quick and easy way to write a Document (or any Node) to a Writer is via
     * the write() method. FileWriter out = new FileWriter( "foo.xml" );
     * document.write( out );
     *
     * If you want to be able to change the format of the output, such as pretty
     * printing or a compact format, or you want to be able to work with Writer
     * objects or OutputStream objects as the destination, then you can use the
     * XMLWriter class.
     *
     * import org.dom4j.Document; import org.dom4j.io.OutputFormat; import
     * org.dom4j.io.XMLWriter;
     *
     * @param document
     * @throws IOException
     */
    public void write(Document document) throws IOException {

        // lets write to a file
        XMLWriter writer = new XMLWriter(
                new FileWriter("output.xml")
        );
        writer.write(document);
        writer.close();

        // Pretty print the document to System.out
        OutputFormat format = OutputFormat.createPrettyPrint();
        writer = new XMLWriter(System.out, format);
        writer.write(document);

        // Compact format to System.out
        format = OutputFormat.createCompactFormat();
        writer = new XMLWriter(System.out, format);
        writer.write(document);
    }

    /**
     * Applying XSLT on a Document is quite straightforward using the JAXP API
     * from Sun. This allows you to work against any XSLT engine such as Xalan
     * or SAXON. Here is an example of using JAXP to create a transformer and
     * then applying it to a Document. import javax.xml.transform.Transformer;
     * import javax.xml.transform.TransformerFactory; import org.dom4j.Document;
     * import org.dom4j.io.DocumentResult; import org.dom4j.io.DocumentSource;
     *
     * @param document
     * @param stylesheet
     * @return
     * @throws Exception
     */
    public Document styleDocument(
            Document document,
            String stylesheet
    ) throws Exception {

        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
                new StreamSource(stylesheet)
        );

        // now lets style the given document
        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }

    /**
     * If you ever have to walk a large XML document tree then for performance
     * we recommend you use the fast looping method which avoids the cost of
     * creating an Iterator object for each loop. For example
     *
     * @param element
     */
    public void treeWalk(Element element) {
        logger.debug(element.getName() + " = " + element.getPath() + "; element.nodeCount()=" + element.nodeCount());
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                logger.debug("\t" + i + "; " + element.getName() + " = " + element.getPath());
                treeWalk((Element) node);
            } else {
                logger.debug("\t" + i + "; node instanceof Element ? false; " + node.getClass().getName() + "; " + node.getName());
            }
        }
    }

    /**
     * If you ever have to walk a large XML document tree then for performance
     * we recommend you use the fast looping method which avoids the cost of
     * creating an Iterator object for each loop. For example
     *
     * @param element
     */
    public void treeWalk(Element element, HashMap hashMap) {
        if (element == null) {
            return;
        }
        String xpathExpression = element.getPath();
        //logger.debug("element.getName()=" + element.getName() + "; xpathExpression = " + xpathExpression);
        getMutilTCByxPath(xpathExpression, hashMap);
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                //logger.debug("treeWalk: "+i+"; element.getName()=" + element.getName() + " element.getPath() = " + element.getPath() );
                treeWalk((Element) node, hashMap);
            } else {
                //logger.debug("\t\t\t"+i+"; node instanceof Element ? false; " + node.getClass().getName() + "; " + node.getName());
            }
        }
    }

    /**
     * In dom4j XPath expressions can be evaluated on the Document or on any
     * Node in the tree (such as Attribute, Element or ProcessingInstruction).
     *
     * This allows complex navigation throughout the document with a single line
     * of code. For example.
     *
     * @param document
     */
    public void getMutilTCByxPath(String xpathExpression, HashMap hashMap) {
        NodeList nodes = null;
        try {
            nodes = (NodeList) xpath.evaluate(xpathExpression, inputSource, XPathConstants.NODESET);
        } catch (XPathExpressionException xPathExpressionException) {
            xPathExpressionException.printStackTrace();
        }

        int j = nodes.getLength();
        String[] textContent = new String[j];
        for (int i = 0; i < j; i++) {
            NamedNodeMap nameNodeMap = nodes.item(i).getAttributes();
            String id = "";
            HashMap<String, String> menuItemHM = new HashMap<String, String>();
            for (int ai = 0; ai < nameNodeMap.getLength(); ai++) {
                String attName = nameNodeMap.item(ai).getNodeName();
                String attValue = nameNodeMap.item(ai).getNodeValue();
	    		//logger.debug("\tid=" + id+ "; attName=" + attName + "; attValue=" + attValue + " in new HM");

                if ("id".equals(attName)) {
                    id = attValue;
                } else {
                    if (menuItemHM.containsKey(attName)) {
                        logger.warn("Duplicated key was be defined xml " + this.xmlFile);
                        logger.warn("\t The Duplicated Key is " + attName);
                        break;
                    }
                    menuItemHM.put(attName, attValue);
                }
                //logger.debug("\tid=" + id+ "; attName=" + attName + "; attValue=" + attValue + " in new HM");
            }
            if ("".equals(id)) {
                String[] xpaths = xpathExpression.split("/");
                id = xpaths[xpaths.length - 1];
            }

            if (menuItemHM.size() > 0) {
	    		//logger.debug("id=" + id+ " with new hashMap");
                //logger.debug("id=" + id+ " with new hashMap");
                hashMap.put(id, menuItemHM);
            } else {
                //logger.debug("id=" + id+ " without new hashMap");
                logger.info("id=" + id + " without new hashMap");
            }

        }
    }

   

    public HashMap<String, HashMap> getConf(String tagName) {
        HashMap<String, HashMap> confHM = new HashMap<String, HashMap>();
        try {
            Document document = parse(xmlFile);
            Element element = (Element) document.selectSingleNode(tagName);
            treeWalk(element, confHM);
        } catch (DocumentException documentException) {
            documentException.printStackTrace();
        }

        return confHM;
    }

}
