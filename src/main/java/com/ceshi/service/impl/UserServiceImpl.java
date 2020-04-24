package com.ceshi.service.impl;

import com.ceshi.dao.UserDOMapper;
import com.ceshi.dao.UserPasswordDOMapper;
import com.ceshi.dataobject.UserDO;
import com.ceshi.dataobject.UserPasswordDO;
import com.ceshi.error.BusinessException;
import com.ceshi.error.EmBusinessError;
import com.ceshi.service.UserService;
import com.ceshi.service.model.UserModel;
import com.ceshi.validator.ValidationResult;
import com.ceshi.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {


    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private UserPasswordDOMapper userPasswordDOMapper;

    @Resource
    private ValidatorImpl validator;


    @Override
    public UserModel getUserById(Integer id) {
       //调用UserDOMapper获取到对应的用户dataObject
       UserDO userDO=userDOMapper.selectByPrimaryKey(id);
       if(userDO==null){
           return null;
       }
       //通过用户Id获取对应的用户加密密码信息
       UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());

       return convertFromDataObject(userDO,userPasswordDO);

    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//            ||userModel.getGender()==null
//            ||userModel.getAge()==null
//            ||StringUtils.isEmpty(userModel.getTelphone())){
//
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }

        ValidationResult result = validator.validae(userModel);
        if(result.isHasError()){

            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }


        //实现model-->dataObject方法
        UserDO userDO=convertFromUserModel(userModel);

        try {
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){

            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复注册");

        }
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO=convertPasswordFromUserModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过用户的手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO==null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel=convertFromDataObject(userDO,userPasswordDO);


        //对比用户信息内加密的密码是否和传输进来的密码相匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){

            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;


    }


    private UserPasswordDO convertPasswordFromUserModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserPasswordDO userPasswordDO=new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }


    private UserDO convertFromUserModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserDO userDO=new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){

        if(userDO==null){
            return null;
        }

        UserModel userModel=new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO!=null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());

        }
        return userModel;
    }
}
