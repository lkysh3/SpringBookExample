package com.ksh.oxm;

import com.ksh.service.jaxb.SqlType;
import com.ksh.service.jaxb.Sqlmap;
import com.ksh.service.sql.OxmSqlService;
import com.ksh.service.sql.SqlService;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/test-applicationContext.xml"})
public class OxmTest {
    @Autowired
    Unmarshaller unmarshaller;

    @Autowired
    SqlService sqlService;

    @Test
    public void unmarshallSqlMap() throws XmlMappingException, IOException {
        Source xmlSource = new StreamSource(getClass().getResourceAsStream("/sqlmap.xml"));
        Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(xmlSource);

        List<SqlType> sqlList = sqlmap.getSql();
        assertThat(sqlList.size(), is(3));
        assertThat(sqlList.get(0).getKey(), is("add"));
        assertThat(sqlList.get(0).getValue(), is("insert"));
        assertThat(sqlList.get(1).getKey(), is("get"));
        assertThat(sqlList.get(1).getValue(), is("select"));
        assertThat(sqlList.get(2).getKey(), is("delete"));
        assertThat(sqlList.get(2).getValue(), is("delete"));
    }

    @Test
    public void oxmSqlServiceTest(){
        // unmarshall을 OxmSqlService 내부클래스인 OxmSqlReader에서 했기 때문에 리스트로 접근할 수 없다.
        assertThat(sqlService.getSql("add"), is("insert"));
        assertThat(sqlService.getSql("get"), is("select"));
        assertThat(sqlService.getSql("delete"), is("delete"));
    }
}
