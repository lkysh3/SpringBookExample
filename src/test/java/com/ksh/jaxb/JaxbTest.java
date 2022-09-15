package com.ksh.jaxb;

import com.ksh.service.jaxb.SqlType;
import com.ksh.service.jaxb.Sqlmap;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class JaxbTest {
    @Test
    public void readSqlMap() throws JAXBException, IOException{
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        // 클래스패스의 루트로 경로를 지정하고 resources에 파일 넣으면 찾는다.
        // 클래스패스의 개념 확인할것
        Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(getClass().getResourceAsStream("/sqlmap.xml"));
        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size(), is(3));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("insert"));
        assertThat(sqlList.get(1).getKey(), is("get"));
        assertThat(sqlList.get(1).getValue(), is("select"));
        assertThat(sqlList.get(2).getKey(), is("delete"));
        assertThat(sqlList.get(2).getValue(), is("delete"));
    }
}
