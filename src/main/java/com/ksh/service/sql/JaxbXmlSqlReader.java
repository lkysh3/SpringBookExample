package com.ksh.service.sql;

import com.ksh.dao.UserDao;
import com.ksh.service.jaxb.SqlType;
import com.ksh.service.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class JaxbXmlSqlReader implements SqlReader{
    // sqlmapFile DI가 없어도 갖는 기본값
    private static final String DEFAULT_SQLMAP_FILE = "/sqlmap.xml";
    // 초기값을 기본값으로 지정해둔다.
    private String sqlmapFile = DEFAULT_SQLMAP_FILE;

    public void setSqlmapFile(String sqlmapFile){
        this.sqlmapFile = sqlmapFile;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
        String contextPath = Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
            Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);

            for(SqlType sql : sqlmap.getSql()){
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }
    }
}
