package com.ksh.oxm;

import com.ksh.service.jaxb.SqlType;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class JibxTarget {
    @XmlElement(required = true)
    protected List<SqlType> sql;
}
