/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.samples;

import org.h2.tools.DeleteDbFiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A very simple class that shows how to load the driver, create a database,
 * create a table, and insert some data.
 */
public class TestingGrounds {

    /**
     * Called when ran from command line.
     *
     * @param args ignored
     */
    public static void main(String... args) throws Exception {
        // delete the database named 'test' in the user home directory
        DeleteDbFiles.execute("~", "test", true);

        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/test;MODE=PostgreSQL");
        Statement stat = conn.createStatement();

        // this line would initialize the database
        // from the SQL script file 'init.sql'
        // stat.execute("runscript from 'init.sql'");

//        stat.execute("create table test(id int primary key, msg int4range)");
//        stat.execute("create table test(id int primary key, msg varchar(255))");
////        stat.execute("insert into test values (1, 'impostor')");
//        stat.execute("alter table test add constraint msg_range check ( msg ~ 'empty' or (" +
//                "msg ~ '[\\[,\\(][^\\[\\(\\,\\]\\)]*,[^\\[\\(\\,\\]\\)]*[\\],\\)]' and " +
//                "substr(msg, 1, position(',',msg)-1) ~ '()|([-]?[1-9][0-9]*)' and " +
//                "substr(msg, position(',',msg)+1, length(msg)-2) ~ '()|([-]?[1-9][0-9]*)'" +
//                ") )");
//        stat.execute("insert into test values (2, '[1,)')");
//        stat.execute("insert into test values (3, '[,2)')");
//        stat.execute("insert into test values (4, '[1,4)')");
//        stat.execute("insert into test values (5, '[-4,-1)')");
//        stat.execute("insert into test values (6, 'empty')");
        ResultSet rs;
//        rs = stat.executeQuery("select msg from test");
        rs = stat.executeQuery("select int4range(1,6,'[]') + int4range(4,9) + int4range(4,10)");
//        rs = stat.executeQuery("select daterange('2018-02-15','2019-03-22') && daterange('2018-08-26','2020-04-09')");
//        rs = stat.executeQuery("select int4range(upper(int4range(1,3)),5)");
//        rs = stat.executeQuery("select int4range(1,6) && int4range(4,9)");

        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        stat.close();
        conn.close();
    }

}
