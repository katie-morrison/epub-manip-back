package com.katiemorrison.epub_manip_back.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CumulativeData {
    private int ncxInd;
    private int opfInd;
    private int contentsInd;
    private String ncxContentNavPoint;
    private String opfContentsElement;
    private String opfNCXElement;
    private String opfSpineToc;
    private String opfFallback;
    private ArrayList<String> ncxNavPoints;
    private ArrayList<String> ncxNavPointsNonChapters;
    private ArrayList<String> opfManifestData;
    private ArrayList<String> opfSpineData;
    private ArrayList<String> opfSpineNonChapters;
    private String opfSpineContents;
    private ArrayList<String> opfReferenceData;
    private ArrayList<String> contentsOL1Data;
    private ArrayList<String> contentsOL1NonChapters;
    private ArrayList<String> contentsOL2Data;

    public CumulativeData() {
        ncxInd = 1;
        opfInd = 1;
        contentsInd = 1;
        ncxNavPoints = new ArrayList<String>();
        ncxNavPointsNonChapters = new ArrayList<String>();
        opfManifestData = new ArrayList<String>();
        opfSpineData = new ArrayList<String>();
        opfSpineNonChapters = new ArrayList<String>();
        opfReferenceData = new ArrayList<String>();
        contentsOL1Data = new ArrayList<String>();
        contentsOL1NonChapters = new ArrayList<String>();
        contentsOL2Data = new ArrayList<String>();
        ncxContentNavPoint = null;
        opfContentsElement = null;
        opfNCXElement = null;
        opfSpineToc = null;
        opfSpineContents = null;
        opfFallback = null;
    }

    public int getNcxInd() {
        return ncxInd;
    }

    public int getOpfInd() {
        return opfInd;
    }

    public int getContentsInd() {
        return contentsInd;
    }

    public ArrayList<String> getNcxNavPoints() {
        return ncxNavPoints;
    }

    public ArrayList<String> getNcxNavPointsNonChapters() {
        return ncxNavPointsNonChapters;
    }

    public String getNcxContentNavPoint() {
        return ncxContentNavPoint;
    }

    public String getOpfContentsElement() {
        return opfContentsElement;
    }

    public String getOpfNCXElement() {
        return opfNCXElement;
    }

    public String getOpfSpineToc() {
        return opfSpineToc;
    }

    public String getOpfFallback() {
        return opfFallback;
    }

    public ArrayList<String> getOpfManifestData() {
        return opfManifestData;
    }

    public ArrayList<String> getOpfSpineData() {
        return opfSpineData;
    }

    public ArrayList<String> getOpfReferenceData() {
        return opfReferenceData;
    }

    public ArrayList<String> getContentsOL1Data() {
        return contentsOL1Data;
    }

    public ArrayList<String> getContentsOL1NonChapters() {
        return contentsOL1NonChapters;
    }

    public ArrayList<String> getContentsOL2Data() {
        return contentsOL2Data;
    }

    public ArrayList<String> getOpfSpineNonChapters() {
        return opfSpineNonChapters;
    }

    public String getOpfSpineContents() {
        return opfSpineContents;
    }

    public void setNcxContentNavPoint(String ncxContentNavPoint) {
        this.ncxContentNavPoint = ncxContentNavPoint;
    }

    public void setOpfContentsElement(String opfContentsElement) {
        this.opfContentsElement = opfContentsElement;
    }

    public void setOpfNCXElement(String opfNCXElement) {
        this.opfNCXElement = opfNCXElement;
    }

    public void setOpfSpineToc(String opfSpineToc) {
        this.opfSpineToc = opfSpineToc;
    }

    public void setOpfFallback(String opfFallback) {
        this.opfFallback = opfFallback;
    }

    public void setOpfSpineContents(String opfSpineContents) {
        this.opfSpineContents = opfSpineContents;
    }

    public void incrementNCXInd() {
        ncxInd++;
    }

    public void incrementOPFInd() {
        opfInd++;
    }

    public void incrementContentsInd() {
        contentsInd++;
    }

    public void mergeData() {
        if (ncxContentNavPoint != null) {
            ncxNavPointsNonChapters.addFirst(ncxContentNavPoint);
            ncxContentNavPoint = null;
        }

        ArrayList<String> finalNavPoints = new ArrayList<String>();
        Dummy dummy = new Dummy() {
            @Override
            public String execute(String value) {
                int navPointInd = getNcxInd();
                incrementNCXInd();
                return "navPoint-" + navPointInd + "\" playOrder=\"" + navPointInd;
            }
        };
        for (String navPoint : ncxNavPointsNonChapters) {
            navPoint = FileUtils.processReplacements(navPoint, "(id=\")(.*?)(\")", 2, dummy);
            finalNavPoints.add(navPoint);
        }
        for (String navPoint : ncxNavPoints) {
            navPoint = FileUtils.processReplacements(navPoint, "(id=\")(.*?)(\")", 2, dummy);
            finalNavPoints.add(navPoint);
        }
        ncxNavPoints = finalNavPoints;
        ncxNavPointsNonChapters = null;

        if (opfContentsElement != null) {
            opfManifestData.addFirst(opfContentsElement);
            opfContentsElement = null;
        }

        if (opfNCXElement != null) {
            Pattern fallbackPattern = Pattern.compile("(fallback=\")(.*)(\")", Pattern.DOTALL);
            Matcher fallbackMatcher = fallbackPattern.matcher(opfNCXElement);
            if (fallbackMatcher.find()) {
                opfNCXElement = opfNCXElement.replace(fallbackMatcher.group(0), "fallback=\"" + opfFallback + "\"");
            }
            opfManifestData.addFirst(opfNCXElement);
            opfNCXElement = null;
        }

        if (opfSpineContents != null) {
            opfSpineNonChapters.addFirst(opfSpineContents);
            opfSpineContents = null;
        }

        ArrayList<String> finalSpineData = new ArrayList<String>();
        finalSpineData.addAll(opfSpineNonChapters);
        finalSpineData.addAll(opfSpineData);
        opfSpineData = finalSpineData;
        opfSpineNonChapters = null;

        ArrayList<String> finalcontentsOL1 = new ArrayList<String>();
        finalcontentsOL1.addAll(contentsOL1NonChapters);
        finalcontentsOL1.addAll(contentsOL1Data);
        contentsOL1Data = finalcontentsOL1;
        contentsOL1NonChapters = null;
    }

    public String toString() {
        return "\nncxInd: " + 
                ncxInd + 
                "\n, opfInd: " + 
                opfInd + 
                "\n, contentsInd: " + 
                contentsInd + 
                "\n, ncxContentNavPoint: " + 
                ncxContentNavPoint + 
                "\n, ncxNavPoints: " + 
                ncxNavPoints.toString() + 
                "\n, opfContentsElement: " + 
                opfContentsElement + 
                "\n, opfNCXElement: " + 
                opfNCXElement + 
                "\n, opfManifestData: " + 
                opfManifestData.toString() + 
                "\n, opfSpineData: " + 
                opfSpineData.toString() + 
                "\n, opfReferenceData: " + 
                opfReferenceData.toString() + 
                "\n, contentsOL1Data: " + 
                contentsOL1Data.toString() + 
                "\n, contentsOL2Data: " + 
                contentsOL2Data.toString();
    }
    
}