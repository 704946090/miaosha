package com.ceshi.constanst;

import com.ceshi.dao.UserDOMapper;
import com.ceshi.dao.UserPasswordDOMapper;
import com.ceshi.dataobject.UserDO;
import com.ceshi.dataobject.UserPasswordDO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

public class CustomRealm  extends AuthorizingRealm {


    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private UserPasswordDOMapper userPasswordDOMapper;



    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> stringSet = new HashSet<>();
        stringSet.add("user:show");
        stringSet.add("user:admin");
        info.setStringPermissions(stringSet);
        return info;

    }


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //1.从主体传过来的认证信息中获得用户名(authenticationToken就是主体login(Token)传到realm的Token)
        String telphone=(String) authenticationToken.getPrincipal();

        //2.通过用户名到数据库中获取凭证
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO==null || "".equals(userDO)){
            throw new AccountException("用户信息不正确");
        }
        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());

        String password=userPasswordDO.getEncrptPassword();
        if(password==null || "".equals(userDO)){
            throw new AccountException("用户信息不正确");
        }

        //将数据库中查询到的信息存入SimpleAuthenticationInfo
        //通过AuthenticatingRealm的credentialsMatcher属性来进行密码比对
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo
                (telphone,password,getName());

        return authenticationInfo;
    }
}
