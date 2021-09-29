package io.github.birddevelper.salmos.db;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


public class JdbcQueryExcuter extends JdbcDaoSupport {


    public JdbcQueryExcuter(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public List<Map<String,Object>> getResultList(String sql){

        return getJdbcTemplate().queryForList(sql);
    }

}
