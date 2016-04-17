package com.melkamar.deadlines;

import com.melkamar.deadlines.dao.group.GroupDAO;
import com.melkamar.deadlines.dao.group.GroupDAOHibernate;
import com.melkamar.deadlines.exceptions.AlreadyExistsException;
import com.melkamar.deadlines.exceptions.WrongParameterException;
import com.melkamar.deadlines.model.User;
import com.melkamar.deadlines.services.api.UserApi;
import com.melkamar.deadlines.services.api.implementation.UserApiImpl;
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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DeadlinesApplication.class)
@WebAppConfiguration
public class DeadlinesApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserApi userApi;
    @Autowired
    private GroupDAO groupDAO;


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

    @Test
    @Transactional
    public void nothing() throws AlreadyExistsException, WrongParameterException {
        User user = userApi.createUser("ahoj", "pa", null, null);
        groupDAO.findByMembers_User(user);
    }
}

// select group0_.group_id as group_id1_1_,
//        group0_.description as descript2_1_,
//        group0_.name as name3_1_
//        from group_table group0_
//          left outer join group_member members1_
//            on group0_.group_id=members1_.group_id
//          left outer join user_table user2_
//            on members1_.user_id=user2_.user_id
//        where user2_.user_id=?
