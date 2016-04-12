package com.melkamar.deadlines;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class DeadlinesApplicationTests {

    @Autowired
    private DataSource dataSource;

	@Test
	public void contextLoads() {

	}

	@Test
    public void printSqlCreateScript() throws SQLException {
        LocalSessionFactoryBuilder sessionFactory = new LocalSessionFactoryBuilder(dataSource);
        sessionFactory.scanPackages(this.getClass().getPackage().getName());
        Dialect dialect = new MySQL5Dialect();
        DatabaseMetadata metadata = new DatabaseMetadata(dataSource.getConnection(), dialect, sessionFactory);
        List<SchemaUpdateScript> scripts = sessionFactory.generateSchemaUpdateScriptList(dialect, metadata);

        Formatter formatter = FormatStyle.DDL.getFormatter();
        for (SchemaUpdateScript script : scripts) {
            System.err.println(formatter.format(script.getScript()) + ";");
        }
    }
}
