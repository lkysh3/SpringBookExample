package com.ksh.service.sql;

import com.ksh.dao.UserDao;
import com.ksh.exception.SqlNotFoundException;
import com.ksh.exception.SqlRetrievalFailureException;
import com.ksh.service.jaxb.SqlType;
import com.ksh.service.jaxb.Sqlmap;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// 하나의 클래스에 3가지 인터페이스를 모두 구현
// 각 인터페이스에서 할 일의 경
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;
    private Map<String, String> sqlMap = new HashMap<String, String>();
    private String sqlMapFile;

    public XmlSqlService(){
    }

    public void setSqlReader(SqlReader sqlReader){
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry){
        this.sqlRegistry = sqlRegistry;
    }

    public void setSqlMapFile(String sqlMapFile){
        this.sqlMapFile = sqlMapFile;
    }

    @PostConstruct
    public void loadSql(){
        this.sqlReader.read(this.sqlRegistry);
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try{
            return sqlRegistry.findSql(key);
        }catch(SqlNotFoundException e){
            throw new SqlRetrievalFailureException(e.getMessage());
        }
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if(sql == null){
            throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
        }
        return sql;
    }

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
        String contextPath = Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(this.sqlMapFile);
            Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);

            for(SqlType sql : sqlmap.getSql()){
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }
    }
}
