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
    private ArrayList<String> opfManifestData;
    private ArrayList<String> opfSpineData;
    private ArrayList<String> opfReferenceData;
    private ArrayList<String> contentsOL1Data;
    private ArrayList<String> contentsOL2Data;

    public CumulativeData() {
        ncxInd = 1;
        opfInd = 1;
        contentsInd = 1;
        ncxNavPoints = new ArrayList<String>();
        opfManifestData = new ArrayList<String>();
        opfSpineData = new ArrayList<String>();
        opfReferenceData = new ArrayList<String>();
        contentsOL1Data = new ArrayList<String>();
        contentsOL2Data = new ArrayList<String>();
        ncxContentNavPoint = null;
        opfContentsElement = null;
        opfNCXElement = null;
        opfSpineToc = null;
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

    public ArrayList<String> getContentsOL2Data() {
        return contentsOL2Data;
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
            Pattern fallbackPattern = Pattern.compile("(fallback=\")(.*)(\")", Pattern.DOTALL);
            Matcher fallbackMatcher = fallbackPattern.matcher(ncxContentNavPoint);
            if (fallbackMatcher.find()) {
                ncxContentNavPoint = ncxContentNavPoint.replace(fallbackMatcher.group(0), "fallback=\"" + opfFallback + "\"");
            }
            ncxNavPoints.addFirst(ncxContentNavPoint);
            ncxContentNavPoint = null;
        }

        if (opfContentsElement != null) {
            opfManifestData.addFirst(opfContentsElement);
            opfContentsElement = null;
        }

        if (opfNCXElement != null) {
            opfManifestData.addFirst(opfNCXElement);
            opfNCXElement = null;
        }
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