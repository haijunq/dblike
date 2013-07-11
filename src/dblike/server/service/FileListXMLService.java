/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.service.FileInfo;
import dblike.server.service.FileListService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
public class FileListXMLService {

    public static void saveFileListToXML(FileListService filelist) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document fileListXML = docBuilder.newDocument();
            Element rootElement = fileListXML.createElement("fileList");
            fileListXML.appendChild(rootElement);

            Element pathnameElement = fileListXML.createElement("pathname");
            pathnameElement.appendChild(fileListXML.createTextNode(filelist.getPathname()));
            rootElement.appendChild(pathnameElement);

            for (String filename : filelist.getFileHashTable().keySet()) {
                Element fileInfoElement = fileListXML.createElement("fileInfo");
                fileInfoElement.setAttribute("filename", filename);
                rootElement.appendChild(fileInfoElement);

                Element versionElement = fileListXML.createElement("version");
                versionElement.appendChild(fileListXML.createTextNode(Integer.toString(filelist.getFileHashTable().get(filename).getVersion())));
                fileInfoElement.appendChild(versionElement);

                Element deviceIDElement = fileListXML.createElement("deviceID");
                deviceIDElement.appendChild(fileListXML.createTextNode(filelist.getFileHashTable().get(filename).getDeviceID()));
                fileInfoElement.appendChild(deviceIDElement);

                Element fileNameElement = fileListXML.createElement("fileName");
                fileNameElement.appendChild(fileListXML.createTextNode(filename));
                fileInfoElement.appendChild(fileNameElement);

                Element timestampElement = fileListXML.createElement("timestamp");
                timestampElement.appendChild(fileListXML.createTextNode(filelist.getFileHashTable().get(filename).getTimestamp()));
                fileInfoElement.appendChild(timestampElement);

                Element fileSizeElement = fileListXML.createElement("fileSize");
                fileSizeElement.appendChild(fileListXML.createTextNode(Long.toString(filelist.getFileHashTable().get(filename).getFileSize())));
                fileInfoElement.appendChild(fileSizeElement);

                Element fileHashCodeElement = fileListXML.createElement("fileHashCode");
                fileInfoElement.appendChild(fileHashCodeElement);

                for (String fileChunkName : filelist.getFileInfoByFileName(filename).getFileHashCode().keySet()) {
                    Element fileChunkHashCodeElement = fileListXML.createElement("fileChunkHashCode");
                    fileChunkHashCodeElement.appendChild(fileListXML.createTextNode(filelist.getFileInfoByFileName(filename).getFileHashCode().get(fileChunkName)));
                    fileChunkHashCodeElement.setAttribute("fileChunkName", fileChunkName);
                    fileHashCodeElement.appendChild(fileChunkHashCodeElement);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(fileListXML);
            StreamResult result = new StreamResult(new File(filelist.getPathname() + "filelist.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static FileListService loadFileListFromXML(String username) {
        FileListService filelist = new FileListService(username);
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            InputStream fis = new FileInputStream(filelist.getPathname() + "filelist.xml");
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            NodeList fileListServiceNodeList = root.getChildNodes();
            if (fileListServiceNodeList != null) {
                Node pathNameNode = fileListServiceNodeList.item(0);
                if (pathNameNode.getNodeName().equals("pathname")) {
                    filelist.setPathname(pathNameNode.getFirstChild().getNodeValue());
                }

//                System.out.println(fileInfoNode.getNodeName());
                for (Node fileInfoNode = pathNameNode.getNextSibling(); fileInfoNode != null; fileInfoNode = fileInfoNode.getNextSibling()) {
                    FileInfo fileinfo = new FileInfo();
                    if (fileInfoNode.hasChildNodes()) {
                        Node versionNode = fileInfoNode.getFirstChild();
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
                        filelist.getFileHashTable().put(((Attr) fileInfoNode.getAttributes().item(0)).getValue(), fileinfo);
                    }
                }
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



        return filelist;
    }

    public static FileInfo getFileInfo(String userName, String directory, String fileName) // to do
    {
        return new FileInfo();
    }
    
    public static void setFileInfo(String userName, String directory, String fileName, FileInfo fileInfo) // to do
    {
        ;
    }
}
