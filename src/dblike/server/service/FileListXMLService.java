/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dblike.server.service;

import dblike.server.ServerStart;
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

    public static void saveFileListToXML(FileListService filelist) throws IOException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document fileListXML = docBuilder.newDocument();
            Element rootElement = fileListXML.createElement("fileList");
            fileListXML.appendChild(rootElement);

            Element pathnameElement = fileListXML.createElement("pathname");
            pathnameElement.appendChild(fileListXML.createTextNode(filelist.getPathname()));
            rootElement.appendChild(pathnameElement);

            if (filelist.getFileHashTable().isEmpty()) {
                Element fileInfoElement = fileListXML.createElement("fileInfo");
                rootElement.appendChild(fileInfoElement);

            } else {
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
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(fileListXML);
            File file = new File(filelist.getPathname() + "filelist.xml");
            if (!file.exists())
                file.createNewFile();
            StreamResult result = new StreamResult(file);
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
                    if (pathNameNode.hasChildNodes()) {
                        filelist.setPathname(pathNameNode.getFirstChild().getNodeValue());
                    } else {
                        filelist.setPathname("");
                    }
                }

//                System.out.println(fileInfoNode.getNodeName());
                for (Node fileInfoNode = pathNameNode.getNextSibling(); fileInfoNode != null; fileInfoNode = fileInfoNode.getNextSibling()) {
                    FileInfo fileinfo = new FileInfo();
                    if (fileInfoNode.hasChildNodes()) {
                        Node versionNode = fileInfoNode.getFirstChild();
                        if (versionNode.getNodeName().equals("version")) {
                            if (versionNode.hasChildNodes()) {
                                fileinfo.setVersion(Integer.parseInt(versionNode.getFirstChild().getNodeValue()));
                            } else {
                                fileinfo.setVersion(0);
                            }
                        }
                        Node deviceIDNode = versionNode.getNextSibling();
                        if (deviceIDNode.getNodeName().equals("deviceID")) {
                            if (deviceIDNode.hasChildNodes()) {
                                fileinfo.setDeviceID(deviceIDNode.getFirstChild().getNodeValue());
                            } else {
                                fileinfo.setDeviceID("");
                            }
                        }
                        Node fileNameNode = deviceIDNode.getNextSibling();
                        if (fileNameNode.getNodeName().equals("fileName")) {
                            if (fileNameNode.hasChildNodes()) {
                                fileinfo.setFileName(fileNameNode.getFirstChild().getNodeValue());
                            } else {
                                fileinfo.setFileName("");
                            }
                        }
                        Node timestampNode = fileNameNode.getNextSibling();
                        if (timestampNode.getNodeName().equals("timestamp")) {
                            if (timestampNode.hasChildNodes()) {
                                fileinfo.setTimestamp(timestampNode.getFirstChild().getNodeValue());
                            } else {
                                fileinfo.setTimestamp("");
                            }
                        }
                        Node fileSizeNode = timestampNode.getNextSibling();
                        if (fileSizeNode.getNodeName().equals("fileSize")) {
                            if (fileSizeNode.hasChildNodes()) {
                                fileinfo.setFileSize(Long.parseLong(fileSizeNode.getFirstChild().getNodeValue()));
                            } else {
                                fileinfo.setFileSize(0);
                            }
                        }
                        Node fileHashCodeNode = fileSizeNode.getNextSibling();
                        Hashtable<String, String> fileHashCode = new Hashtable<>();

                        for (Node node = fileHashCodeNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node.hasChildNodes()) {
                                fileHashCode.put(((Attr) node.getAttributes().item(0)).getValue(), node.getFirstChild().getNodeValue());
                            }
                        }
                        fileinfo.setFileHashCode(fileHashCode);
                        if (fileInfoNode.hasAttributes()) {
                            filelist.getFileHashTable().put(((Attr) fileInfoNode.getAttributes().item(0)).getValue(), fileinfo);
                        }
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

    /**
     *
     * @return
     */
    public static boolean loadServerInfo() {
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            InputStream fis = new FileInputStream("ServerCfg/serverInfo.xml");
            System.out.println(fis);
            Document doc = dombuilder.parse(fis);
            Element root = doc.getDocumentElement();
            Node user = root.getFirstChild();
            if (user != null) {
                String id = "";
                String ip = "";
                String port = "";
                for (Node node = user; node != null; node = node.getNextSibling()) {
                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        if (node.getNodeName().equals("IP")) {
                            ip = node.getFirstChild().getNodeValue();
                        }

                        if (node.getNodeName().equals("port")) {
                            port = node.getFirstChild().getNodeValue();
                        }
                    }
                }
                ServerStart.setServerIP(ip);
                ServerStart.setPORT(Integer.parseInt(port));

                return true;
            } else {
                return false;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
