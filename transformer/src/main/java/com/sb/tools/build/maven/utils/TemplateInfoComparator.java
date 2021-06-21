package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.model.TemplateInfo;

import java.util.Comparator;

public class TemplateInfoComparator implements Comparator<TemplateInfo> {
    @Override
    public int compare(TemplateInfo o1, TemplateInfo o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
