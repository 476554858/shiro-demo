package com.zjx.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class JdbcRealmTest {

    DruidDataSource dataSource = new DruidDataSource();

    {
        dataSource.setUrl("jdbc:mysql://localhost:3306/shiro-jdbc");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
    }

    @Test
    public void testAuthentication(){

        JdbcRealm jdbcRealm =  new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        //默认为false不去查权限，要设置为true
        jdbcRealm.setPermissionsLookupEnabled(true);

        //可以设置自己的查询sql
        String sql = "select password from test_user where user_name = ?";
        jdbcRealm.setAuthenticationQuery(sql);

        String roleSql = "select role_name from test_user_role where user_name = ?";
        jdbcRealm.setUserRolesQuery(roleSql);

        //1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        //2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zjx", "123456");
        subject.login(token);

        System.out.println("isAuthenticated:" + subject.isAuthenticated());

        subject.checkRoles("admin", "user");

        /* subject.checkPermission("user:select");*/

    }
}
