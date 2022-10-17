package com.ksh.service.sql;

import com.ksh.dao.UserDao;
import com.ksh.exception.SqlNotFoundException;
import com.ksh.exception.SqlRetrievalFailureException;
import com.ksh.service.jaxb.SqlType;
import com.ksh.service.jaxb.Sqlmap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService{
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
    private final BaseSqlService baseSqlService = new BaseSqlService();

    // oxmSqlService에서 사용
    public void setSqlRegistry(SqlRegistry sqlRegistry){
        this.sqlRegistry = sqlRegistry;
    }

    // oxmSqlService에서 DI받은것을 oxmSqlReader로 전달한다.
    // 내부클래스에 직접 전달할 수 없기 때문에 oxmSqlService를 거쳐 전달한다.
    public void setUnmarshaller(Unmarshaller unmarshaller){
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlMap(Resource sqlMap){
        this.oxmSqlReader.setSqlMap(sqlMap);
    }

    @PostConstruct
    public void loadSql(){
        // BaseSqlService에 필요한 정보를 DI해준다.
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);

        // BaseSqlService에 작업 위임
        this.baseSqlService.loadSql();
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        // BaseSqlService에 작업 위임
        return this.baseSqlService.getSql(key);
    }

    // OxmSqlService에서만 사용할 수 있도록 private로 선언
    private class OxmSqlReader implements SqlReader{
        private Unmarshaller unmarshaller;
//        private final static String DEFAULT_SQLMAP_FILE = "/sqlmap.xml";
        private Resource sqlMap = new ClassPathResource("/sqlmap.xml", UserDao.class);

        public void setUnmarshaller(Unmarshaller unmarshaller){
            this.unmarshaller = unmarshaller;
        }

        public void setSqlMap(Resource sqlMap){
            this.sqlMap = sqlMap;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try{
                Source source = new StreamSource(this.sqlMap.getInputStream());
                Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(source);

                for(SqlType sql : sqlmap.getSql()){
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            }catch(IOException e){
                throw new IllegalArgumentException(this.sqlMap.getFilename() + "을 가져올 수 없습니다.", e);
            }
        }
    }
}
