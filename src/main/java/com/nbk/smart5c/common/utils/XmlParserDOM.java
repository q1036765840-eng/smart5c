package com.nbk.smart5c.common.utils;

import cn.hutool.core.text.CharSequenceUtil;
import com.nbk.smart5c.common.enums.LineDirectionEnum;
import com.nbk.smart5c.pojo.bo.XmlDataParseBO;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;

@Log4j2
public class XmlParserDOM {

    public static void main(String[] args) {
        try {
            // 方式1: 从文件读取
            File xmlFile = new File("/nbk/app/smart5c/data/杭州供电段/09/20260209000452_杭深线_苍南至福鼎_0249_K662_CRH4E4070/info.xml");
            parseXMLFile(xmlFile);

            // 方式2: 从字符串读取
//            String xmlContent = getXmlString();
//            parseXMLString(xmlContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 解析XML文件
    public static XmlDataParseBO parseDataByDirName(String dirName) {
        if (CharSequenceUtil.isBlank(dirName)) {
            return null;
        }
        String[] dataInfo = dirName.split("_");
        if (dataInfo.length < 6) {
            return null;
        }
        XmlDataParseBO xmlDataParseBO = new XmlDataParseBO();
        xmlDataParseBO.setLineName(dataInfo[1]).setStationName(dataInfo[2])
                .setPoleNo(dataInfo[3]).setKmSign(dataInfo[4]).setLocomotiveNumber(dataInfo[5]);
        try {
            xmlDataParseBO.setInspectionTime(DateUtil.yyyyMMddhhmmssToLocalDateTime(dataInfo[0]));
        } catch (Exception e) {
            try {
                xmlDataParseBO.setInspectionTime(DateUtil.yyyyMMddhhmmToLocalDateTime(dataInfo[0]));
            } catch (Exception ex) {
                log.error("巡检时间格式不对:目录名,巡检时间{},{}", dirName, dataInfo[0], ex);
            }
        }
        return xmlDataParseBO;
    }

    // 解析XML文件
    public static XmlDataParseBO parseXMLFile(File xmlFile) throws Exception {
        if (0 == xmlFile.length()) {
            return null;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        XmlDataParseBO xmlDataParseBO = parseDocument(document);
        if (xmlFile.getAbsolutePath().contains("段")) {
            String[] pathParts = xmlFile.getAbsolutePath().split("/");
            for (String part : pathParts) {
                if (part.endsWith("段")) {
                    xmlDataParseBO.setSectionName(part);
                    break;
                }
            }
        } else {
            xmlDataParseBO.setSectionName("未知段");
        }
        return xmlDataParseBO;
    }

    // 解析XML字符串
    public static XmlDataParseBO parseXMLString(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
        Document document = builder.parse(input);
        document.getDocumentElement().normalize();

        return parseDocument(document);
    }

    private static XmlDataParseBO parseDocument(Document document) {
        // 解析baseInfo部分
        Element baseInfo = (Element) document.getElementsByTagName("baseInfo").item(0);
        String trainNo = baseInfo.getElementsByTagName("trainNo").item(0).getTextContent();
        String date = baseInfo.getElementsByTagName("date").item(0).getTextContent();
        Node directionNode = baseInfo.getElementsByTagName("direction").item(0);
        Integer direction = directionNode == null ? null : LineDirectionEnum.getCodeByName(directionNode.getTextContent());
        Node speedNode = baseInfo.getElementsByTagName("speed").item(0);
        Double speed = null == speedNode ? null : Double.parseDouble(speedNode.getTextContent());

        // 解析appendInfo部分
        Element appendInfo = (Element) document.getElementsByTagName("appendInfo").item(0);
        String location = appendInfo.getElementsByTagName("location").item(0).getTextContent();
        String lineName = appendInfo.getElementsByTagName("lineName").item(0).getTextContent();
        String st = appendInfo.getElementsByTagName("st").item(0).getTextContent();
        String poleNo = appendInfo.getElementsByTagName("PoleNo").item(0).getTextContent();
        String km = appendInfo.getElementsByTagName("km").item(0).getTextContent();

        // 输出结果
//        System.out.println("=== XML解析结果 ===");
//        System.out.println("车次: " + trainNo);
//        System.out.println("日期: " + date);
//        System.out.println("位置: " + location);
//        System.out.println("线路: " + lineName);
//        System.out.println("车站: " + st);
//        System.out.println("支柱号: " + poleNo);
//        System.out.println("公里标: " + km);
        XmlDataParseBO xmlDataParseBO = new XmlDataParseBO();
        xmlDataParseBO.setLocomotiveNumber(trainNo).setFixLocation(location)
                .setLineName(lineName).setStationName(st).setPoleNo(poleNo)
                .setKmSign(km)
                .setDirection(direction).setSpeed(speed);
        try {
            xmlDataParseBO.setInspectionTime(DateUtil.yyyy_MM_ddhh_mm_ssToLocalDateTime(date));
        } catch (Exception e) {
            try {
                xmlDataParseBO.setInspectionTime(DateUtil.yyyy_MM_ddhh_mmToLocalDateTime(date));
            } catch (Exception ex) {
                log.error("巡检时间格式不对:目录名,巡检时间{},{}", document.getDocumentURI(), date, ex);
            }
        }
        return xmlDataParseBO;
    }

    private static String getXmlString() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<detect>\n" +
                "  <baseInfo>\n" +
                "    <trainNo>000000</trainNo>\n" +
                "    <date>2025-12-28 01:56:18</date>\n" +
                "  </baseInfo>\n" +
                "  <appendInfo>\n" +
                "    <location>庆元站82#支柱</location>\n" +
                "    <lineName>衢宁铁路</lineName>\n" +
                "    <st>庆元站</st>\n" +
                "    <PoleNo>82</PoleNo>\n" +
                "    <km>0</km>\n" +
                "  </appendInfo>\n" +
                "</detect>";
    }
}