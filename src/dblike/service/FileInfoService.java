/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.service;

import dblike.server.service.FileListService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.joda.time.DateTime;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author haijun
 */
public class FileInfoService {
    
    private static String SERVER_USERS_FOLDER = "./users/";

    public static String getSERVER_USERS_FOLDER() throws Exception {
        return getAbsolutePathName(SERVER_USERS_FOLDER) + "/";
    }

    
    /**
     *
     * @param fileinfo
     * @return
     */
    public static String fileInfoToXMLString(FileInfo fileinfo) {
        String xmlstring = "";
        if (fileinfo == null || fileinfo.getFileHashCode().isEmpty()) {
            return xmlstring;
        }
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document fileInfoXML = docBuilder.newDocument();
            Element rootElement = fileInfoXML.createElement("fileInfo");
            fileInfoXML.appendChild(rootElement);

            Element versionElement = fileInfoXML.createElement("version");
            versionElement.appendChild(fileInfoXML.createTextNode(Integer.toString(fileinfo.getVersion())));
            rootElement.appendChild(versionElement);

            Element deviceIDElement = fileInfoXML.createElement("deviceID");
            deviceIDElement.appendChild(fileInfoXML.createTextNode(fileinfo.getDeviceID()));
            rootElement.appendChild(deviceIDElement);

            Element fileNameElement = fileInfoXML.createElement("fileName");
            fileNameElement.appendChild(fileInfoXML.createTextNode(fileinfo.getFileName()));
            rootElement.appendChild(fileNameElement);

            Element timestampElement = fileInfoXML.createElement("timestamp");
            timestampElement.appendChild(fileInfoXML.createTextNode(fileinfo.getTimestamp()));
            rootElement.appendChild(timestampElement);

            Element fileSizeElement = fileInfoXML.createElement("fileSize");
            fileSizeElement.appendChild(fileInfoXML.createTextNode(Long.toString(fileinfo.getFileSize())));
            rootElement.appendChild(fileSizeElement);

            Element fileHashCodeElement = fileInfoXML.createElement("fileHashCode");
            rootElement.appendChild(fileHashCodeElement);

            if (!fileinfo.getFileHashCode().isEmpty()) {
                for (String fileChunkName : fileinfo.getFileHashCode().keySet()) {
                    Element fileChunkHashCodeElement = fileInfoXML.createElement("fileChunkHashCode");
                    fileChunkHashCodeElement.appendChild(fileInfoXML.createTextNode(fileinfo.getFileHashCode().get(fileChunkName)));
                    fileChunkHashCodeElement.setAttribute("fileChunkName", fileChunkName);
                    fileHashCodeElement.appendChild(fileChunkHashCodeElement);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(fileInfoXML);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            transformer.transform(source, result);
            xmlstring = sw.toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return xmlstring;
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return xmlstring;
        }
        return xmlstring;
    }

    /**
     *
     * @param xmlstring
     * @return
     */
    public static FileInfo parseXMLStringToFileInfo(String xmlstring) {
        FileInfo fileinfo = new FileInfo();
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            InputStream fis = new ByteArrayInputStream(xmlstring.getBytes("UTF-8"));
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            NodeList fileInfoNode = root.getChildNodes();
            if (fileInfoNode != null) {
                Node versionNode = fileInfoNode.item(0);
                if (versionNode.getNodeName().equals("version")) {
                    fileinfo.setVersion(Integer.parseInt(versionNode.getFirstChild().getNodeValue()));
                }
                Node deviceIDNode = versionNode.getNextSibling();
                if (deviceIDNode.getNodeName().equals("deviceID")) {
                    fileinfo.setDeviceID(deviceIDNode.getFirstChild().getNodeValue());
                }
                Node fileNameNode = deviceIDNode.getNextSibling();
                if (fileNameNode.getNodeName().equals("fileName")) {
                    fileinfo.setFileName(fileNameNode.getFirstChild().getNodeValue());
                }
                Node timestampNode = fileNameNode.getNextSibling();
                if (timestampNode.getNodeName().equals("timestamp")) {
                    fileinfo.setTimestamp(timestampNode.getFirstChild().getNodeValue());
                }
                Node fileSizeNode = timestampNode.getNextSibling();
                if (fileSizeNode.getNodeName().equals("fileSize")) {
                    fileinfo.setFileSize(Long.parseLong(fileSizeNode.getFirstChild().getNodeValue()));
                }
                Node fileHashCodeNode = fileSizeNode.getNextSibling();
                Hashtable<String, String> fileHashCode = new Hashtable<>();

                for (Node node = fileHashCodeNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                    fileHashCode.put(((Attr) node.getAttributes().item(0)).getValue(), node.getFirstChild().getNodeValue());
                }
                fileinfo.setFileHashCode(fileHashCode);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileinfo;
    }

    public static FileInfo getFileInfoByFileName(String directory, String filename) throws Exception {
        FileInfo fileInfo = new FileInfo();
        File file = new File(directory + "/" + filename);
        if (file.exists()) {
            // this is the new file or updated file, don't know yet
            // set version = -1, deviceID = "" because do not know 
            fileInfo.setVersion(-1);
            fileInfo.setDeviceID("");
            fileInfo.setFileName(filename);
            fileInfo.setFileSize(file.length());
            fileInfo.setTimestamp(new DateTime(file.lastModified()).toString());
            fileInfo.setFileHashCode(MD5Service.getMD5StringTableFromSingleFile(directory, filename));
        } // this is a new file or a delete
        else {
            // this is a deleted file or never exsiting file
            fileInfo.setVersion(-1);
            fileInfo.setDeviceID("");
            fileInfo.setFileName(filename);
            fileInfo.setFileSize(0);
            fileInfo.setTimestamp(new DateTime().toString());
            fileInfo.setFileHashCode(new Hashtable<String, String>()); // null
        }

        return fileInfo;
    }

    public static String getAbsolutePathName(String pathName) throws Exception {
        File path = new File(pathName);
        String pathString = "";
        if (!path.exists()) {
            path.mkdir();
        }
        pathString = path.getCanonicalPath();
        return pathString;
    }
}
